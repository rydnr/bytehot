package org.acmsl.javaeda.annotations;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  A processor for event handler annotations.

  Class name: EventHandlerProcessor

  Responsibilities:
    - Discover and process any event handler methods.

  Collaborators:
    - None
 */
@SuppressWarnings({"unused","PMD.AtLeastOneConstructor"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public abstract class EventHandlerProcessor extends AbstractProcessor implements Processor {

    /**
     * The lines to write to the index file.
     */
    private final Set<String> lines = new LinkedHashSet<>();

    /**
      * Retrieves the index file location.
      * @return such location.
      */
    @NonNull
    public abstract String getIndexFileLocation();

    /**
      * Retrieves the annotation class.
      * @return such class.
      */
    @NonNull
    public abstract Class<? extends Annotation> getAnnotationClass();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean process(@NonNull final Set<? extends TypeElement> annotations, @NonNull final RoundEnvironment roundEnv) {
        return validate(annotations, roundEnv) && generateIndex(annotations, roundEnv);
    }

    /**
     * Validates the annotated methods have valid signatures.
     * @param annotations the annotations.
     * @param roundEnv the {@code RoundEnv} instance.
     * @return {@code true} if all annotated methods are valid.
     */
    protected abstract boolean validate(@NonNull Set<? extends TypeElement> annotations, @NonNull RoundEnvironment roundEnv);

    /**
     * Generates the index file.
     * @param annotations the annotations.
     * @param roundEnv the {@code RoundEnvironment} instance.
     * @return {@code true} if the file could be generated.
     */
    @SuppressWarnings({"PMD.LawOfDemeter"})
    protected boolean generateIndex(@NonNull final Set<? extends TypeElement> annotations, @NonNull final RoundEnvironment roundEnv) {

        for (final Element element : roundEnv.getElementsAnnotatedWith(getAnnotationClass())) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            final ExecutableElement method = (ExecutableElement) element;

            // Get class name
            final String className = ((TypeElement) method.getEnclosingElement()).getQualifiedName().toString();

            // Get method name
            final String methodName = method.getSimpleName().toString();

            // Get parameter type
            final TypeMirror type = method.getParameters().get(0).asType();
            final String paramType = ((TypeElement) processingEnv.getTypeUtils().asElement(type)).getQualifiedName().toString();

            final boolean isStatic = method.getModifiers().contains(Modifier.STATIC);

            this.lines.add(className + ":" + methodName + ":" + paramType + ":" + (isStatic ? "static" : "instance"));
        }

         if (roundEnv.processingOver()) {
            try {
                final Filer filer = processingEnv.getFiler();
                final FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", getIndexFileLocation());
                try (Writer writer = file.openWriter()) {
                    for (final String line : this.lines) {
                        writer.write(line + "\n");
                    }
                }
            } catch (final IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write " + getIndexFileLocation() + ": " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * Checks if it's an event subtype.
     * @param mirror the {@link TypeMirror} type.
     * @param typeUtils the {@link Types} instance.
     * @param elementUtils the {@link Elements} instance.
     * @param rawEventType the {@link TypeMirror} event type.
     * @return {code true} if it's a parameterized event.
     */
    @SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts","PMD.CognitiveComplexity","PMD.CyclomaticComplexity"})
    protected boolean isEventSubtype(
        @NonNull final TypeMirror mirror,
        @NonNull final Types typeUtils,
        @NonNull final Elements elementUtils,
        @NonNull final TypeMirror rawEventType) {

        boolean result = false;

        // Ensure the type is assignable to Event
        if (typeUtils.isSubtype(typeUtils.erasure(mirror), typeUtils.erasure(rawEventType)) && (mirror instanceof DeclaredType declaredType)) {
            final Element element = declaredType.asElement();
            if (element instanceof TypeElement typeElement) {
                // Look through interfaces first
                for (final TypeMirror iface : typeElement.getInterfaces()) {
                    if (typeUtils.isSameType(typeUtils.erasure(iface), typeUtils.erasure(rawEventType)) && iface instanceof DeclaredType eventInterface) {
                        final List<? extends TypeMirror> typeArgs = eventInterface.getTypeArguments();
                        if (typeArgs.isEmpty()) {
                            result = true;
                            break;
                        }
                    }
                }

                // Check superclass if interface isn't matched
                final TypeMirror superclass = typeElement.getSuperclass();
                if (superclass != null && !superclass.getKind().equals(TypeKind.NONE) && typeUtils.isSubtype(typeUtils.erasure(superclass), typeUtils.erasure(rawEventType)) && superclass instanceof DeclaredType superDeclared) {
                    final List<? extends TypeMirror> typeArgs = superDeclared.getTypeArguments();
                    if (typeArgs.isEmpty()) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Checks if the type represents an event.
     * @param type the {@link TypeMirror} type.
     * @param typeUtils the {@link Types} instance.
     * @param elementUtils the {@link Elements} instance.
     * @param rawEventType the {@link TypeMirror} event type.
     * @return {code true} if it's an event.
     */
    protected boolean isEvent(
        @NonNull final TypeMirror type,
        @NonNull final Types typeUtils,
        @NonNull final Elements elementUtils,
        @NonNull final TypeMirror rawEventType) {

        return     type instanceof DeclaredType declaredType
                && typeUtils.isSubtype(typeUtils.erasure(declaredType), typeUtils.erasure(rawEventType))
                && declaredType.getTypeArguments().isEmpty();
    }

    /**
     * Checks if the return type is {@code List<Event>}.
     * @param returnType the return type to check.
     * @param typeUtils a {@code Types} instance.
     * @param elementUtils a {@code Elements} instance.
     * @param rawEventType the {@code TypeMirror} instance representing the event type.
     * @return {@code true} if it's a list of events.
     */
    @SuppressWarnings({"PMD.LongVariable","PMD.AvoidLiteralsInIfCondition","PMD.AvoidDeeplyNestedIfStmts"})
    protected boolean isListOfEvents(
        @NonNull final TypeMirror returnType,
        @NonNull final Types typeUtils,
        @NonNull final Elements elementUtils,
        @NonNull final TypeMirror rawEventType) {

        boolean result = false;

        if (returnType instanceof DeclaredType declaredReturnType) {
            final TypeMirror rawList = elementUtils.getTypeElement("java.util.List").asType();
            if (typeUtils.isAssignable(typeUtils.erasure(returnType), typeUtils.erasure(rawList))) {
                final List<? extends TypeMirror> typeArgs = declaredReturnType.getTypeArguments();
                if (typeArgs.size() == 1) {
                    final TypeMirror listArg = typeArgs.get(0);
                    if (listArg.getKind() == TypeKind.WILDCARD) {
                        final WildcardType wildcard = (WildcardType) listArg;
                        final TypeMirror extendsBound = wildcard.getExtendsBound();
                        result = extendsBound != null && isEvent(extendsBound, typeUtils, elementUtils, rawEventType);
                    } else {
                        result = isEvent(listArg, typeUtils, elementUtils, rawEventType);
                    }
                }
            }
        }

        return result;
    }
}
