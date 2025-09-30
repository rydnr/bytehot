package org.acmsl.javaeda.annotations;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.CRC32;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  A processor to discover event -> entity associations.

  Class name: EventEntityClassProcessor

  Responsibilities:
    - Discover and process any @Listen or @Apply-annotated methods to keep track of the event -> entity relationship.

  Collaborators:
    - None
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"org.acmsl.javaeda.annotations.Listen","org.acmsl.javaeda.annotations.Apply"})
@SuppressWarnings({"unused","PMD.ExcessiveImports","PMD.AtLeastOneConstructor","PMD.CognitiveComplexity","PMD.CyclomaticComplexity","PMD.LawOfDemeter"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class EventEntityClassProcessor extends AbstractProcessor implements Processor {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean process(@NonNull final Set<? extends TypeElement> annotations, @NonNull final RoundEnvironment roundEnv) {
        generateEventEntityClassMappings(annotations, roundEnv);
        return false;
    }

    /**
     * Generates the META-INF/services/org.acmsl.javaeda.domain.EventEntityClassResolver file.
     * @param annotations the annotations.
     * @param roundEnv the {@code RoundEnvironment} instance.
     * @return {@code true} if the file could be generated.
     */
    @SuppressWarnings({"PMD.LawOfDemeter"})
    protected boolean generateEventEntityClassMappings(@NonNull final Set<? extends TypeElement> annotations, @NonNull final RoundEnvironment roundEnv) {
        boolean result = false;

        final String pkg = "org.acmsl.javaeda.domain";
        final String cls = "GeneratedEventEntityClassResolver_" + stableSuffix(collectEventToEntity(roundEnv));

        if (!annotations.isEmpty() && !roundEnv.processingOver() && !alreadyExists(StandardLocation.SOURCE_OUTPUT, pkg, cls + ".java")) {
            final Types types = processingEnv.getTypeUtils();
            final Elements elements = processingEnv.getElementUtils();
            final TypeElement entityBase = elements.getTypeElement("org.acmsl.javaeda.domain.Entity");
            final TypeMirror eventBase  = elements.getTypeElement("org.acmsl.javaeda.domain.Event").asType();

            final Map<String,String> eventToEntity = new LinkedHashMap<>();
            final List<String> errors = new ArrayList<>();

            final Set<Element> annotatedElements = new LinkedHashSet<>();
            annotatedElements.addAll(roundEnv.getElementsAnnotatedWith(Listen.class));
            annotatedElements.addAll(roundEnv.getElementsAnnotatedWith(Apply.class));
            for (final Element element : annotatedElements) {
                if (element.getKind() != ElementKind.METHOD) {
                    continue;
                }
                final ExecutableElement method = (ExecutableElement) element;
                final TypeElement entityType = (TypeElement) method.getEnclosingElement();

                // Only index real Entities
                if (!types.isAssignable(entityType.asType(), entityBase.asType())) {
                    continue;
                }

                // Grab first parameter as the Event type
                if (method.getParameters().isEmpty()) {
                    continue;
                }

                final VariableElement firstParameter = method.getParameters().get(0);
                final TypeMirror evType = firstParameter.asType();
                if (!types.isAssignable(evType, eventBase)) {
                    continue;
                }

                final String evFqn = eraseFqn(evType);
                final String entFqn = entityType.getQualifiedName().toString();

                final String existing = eventToEntity.putIfAbsent(evFqn, entFqn);
                if (existing != null && !existing.equals(entFqn)) {
                    errors.add("Event " + evFqn + " is listened by " + existing + " and " + entFqn);
                }
            }

            if (!errors.isEmpty()) {
                 errors.forEach(msg -> processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg));
            }

            try {
                writeResolverClass(pkg, cls, eventToEntity);
                writeSpiFile(pkg + "." + cls);
            } catch (final IOException ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write resolver: " + ex);
            }

             result = true;
        }
        return result;
    }

    /**
     * Trims the fully-qualified name.
     * @param type the {@link TypeMirror}.
     * @return the trimmed name.
     */
    @NonNull
    protected String eraseFqn(@NonNull final TypeMirror type) {
        return processingEnv.getTypeUtils().erasure(type).toString();
    }

    /**
     * Retrieves the unique suffix of given environment.
     * It would use -Aorg.acmsl.javaeda.module; fallback to CRC32 of sorted entries.
     * @param map the map of event -> entity
     * @return the suffix.
     */
    @NonNull
    protected String stableSuffix(@NonNull final Map<String,String> map) {
        final String result;
        final String moduleId = processingEnv.getOptions().get("org.acmsl.javaeda.module");
        if (moduleId != null && !moduleId.isBlank()) {
            result = moduleId.replaceAll("[^A-Za-z0-9_$]", "_");
        } else {
            final List<Map.Entry<String,String>> keys = new ArrayList<>(map.entrySet());
            keys.sort(Map.Entry.comparingByKey());
            final CRC32 crc = new CRC32();
            for (final Map.Entry<String,String> entry : keys) {
                crc.update(entry.getKey().getBytes(StandardCharsets.UTF_8));
                crc.update((byte) ':');
                crc.update(entry.getValue().getBytes(StandardCharsets.UTF_8));
                crc.update((byte) ';');
            }
            result = Long.toUnsignedString(crc.getValue());
        }
        return result;
    }

    /**
     * Collects a map whose keys are the types of the parametes of the "@Listen" methods, and the values the entity classes.
     * @param round the round environment.
     * @return such map.
     */
    @SuppressWarnings("PMD.LongVariable")
    protected Map<String,String> collectEventToEntity(@NonNull final RoundEnvironment round) {
        final Map<String,String> result = new TreeMap<>(); // TreeMap => deterministic order
        final Types types = processingEnv.getTypeUtils();
        final Elements elements = processingEnv.getElementUtils();
        final TypeElement entityBase = elements.getTypeElement("org.acmsl.javaeda.domain.Entity");
        final TypeMirror eventBase   = elements.getTypeElement("org.acmsl.javaeda.domain.Event").asType();
        for (final Element element : round.getElementsAnnotatedWith(elements.getTypeElement("org.acmsl.javaeda.annotations.Listen"))) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            final ExecutableElement method = (ExecutableElement) element;
            final TypeElement owner = (TypeElement) method.getEnclosingElement();
            if (!types.isAssignable(owner.asType(), entityBase.asType())) {
                continue;
            }
            if (method.getParameters().isEmpty()) {
                continue;
            }
            final TypeMirror firstParameterType = method.getParameters().get(0).asType();
            if (!types.isAssignable(firstParameterType, eventBase)) {
                continue;
            }
            final String eventType = types.erasure(firstParameterType).toString();
            final String entityType = owner.getQualifiedName().toString();
            final String prev = result.putIfAbsent(eventType, entityType);
            if (prev != null && !prev.equals(entityType)) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Event " + eventType + " is listened by both " + prev + " and " + entityType + " (unique owner required)");
            }
        }
        return result;
    }

    /**
     * Writes the resolver class.
     * @param pkg the package.
     * @param cls the class name.
     * @param map the mappings.
     * @throws IOException if the disk operation fails.
     */
    protected void writeResolverClass(
        @NonNull final String pkg, @NonNull final String cls, @NonNull final Map<String,String> map)
        throws IOException {
        final Filer filer = processingEnv.getFiler();
        final JavaFileObject file = filer.createSourceFile(pkg + "." + cls);
        try (Writer writer = file.openWriter()) {
            writer.append("package ").append(pkg).append(";\n\n");
            writer.append("import java.util.Map;\n");
            writer.append("import java.util.Optional;\n\n");
            writer.append("@SuppressWarnings({\"PMD.AtLeastOneConstructor\",\"PMD.ClassNamingConventions\",\"PMD.UnnecessaryFullyQualifiedName\"})\n");
            writer.append("public final class ").append(cls).append(" implements EventEntityClassResolver {\n");
            writer.append("    private static final Map<Class<? extends Event>, Class<? extends Entity>> MAP = Map.ofEntries(\n");
            int index = 0;
            for (final Map.Entry<String, String> e: map.entrySet()) {
                writer.append("        Map.entry(").append(e.getKey()).append(".class, ").append(e.getValue()).append(".class)");
                writer.append(++index < map.size() ? ",\n" : "\n");
            }
            if (map.isEmpty()) {
                writer.append("        // empty\n");
            }
            writer.append("    );\n\n");
            writer.append("    /**\n");
            writer.append("     * {@inheritDoc}\n");
            writer.append("     */\n");
            writer.append("    @Override\n");
            writer.append("    public int priority() {\n");
            writer.append("        return 0;\n");
            writer.append("    }\n\n");
            writer.append("    /**\n");
            writer.append("     * {@inheritDoc}\n");
            writer.append("     */\n");
            writer.append("    @Override\n");
            writer.append("    public Optional<Class<? extends Entity>> resolveExact(final Class<? extends Event> clazz) {\n");
            writer.append("        return Optional.ofNullable(MAP.get(clazz));\n");
            writer.append("    }\n\n");
            writer.append("    /**\n");
            writer.append("     * {@inheritDoc}\n");
            writer.append("     */\n");
            writer.append("    @Override\n");
            writer.append("    public Map<Class<? extends Event>, Class<? extends Entity>> entries() {\n");
            writer.append("        return MAP;\n");
            writer.append("    }\n");
            writer.append("}\n");
        }
    }

    /**
     * Checks if the SPI file already exists.
     * @param loc the location.
     * @param pkg the package.
     * @param relName the relative name.
     * @return {@code true} if the file exists.
     */
    protected boolean alreadyExists(@NonNull final StandardLocation loc, @NonNull final String pkg, @NonNull final String relName) {
        boolean result;
        try {
            processingEnv.getFiler().getResource(loc, pkg, relName).openInputStream().close();
            result = true;
        } catch (final IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Writes given content as an ServiceProvider file.
     * @param implFqn The implementation fully-qualified mappings.
     * @throws IOException if the file cannot be written to disk.
     */
    protected void writeSpiFile(@NonNull final String implFqn) throws IOException {
        final FileObject fileObject =
            processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                "", "META-INF/services/org.acmsl.javaeda.domain.EventEntityClassResolver");
        try (Writer writer = fileObject.openWriter()) {
            writer.write(implFqn);
            writer.write("\n");
        }
    }
}
