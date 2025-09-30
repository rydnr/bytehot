package org.acmsl.javaeda.annotations;

import com.google.auto.service.AutoService;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  A processor to validate use of @Apply at compile time.

  Class name: ApplyProcessor

  Responsibilities:
    - Check @Apply-annotated method are correctly defined.

  Collaborators:
    - org.acmsl.javaeda.annotations.Apply
*/
@AutoService(Processor.class)
@SuppressWarnings({"PMD.AtLeastOneConstructor","PMD.LawOfDemeter","PMD.AvoidLiteralsInIfCondition","PMD.AvoidInstantiatingObjectsInLoops"})
@SupportedAnnotationTypes("org.acmsl.javaeda.annotations.Apply")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ApplyProcessor extends EventHandlerProcessor {

    /**
      * Retrieves the index file location.
      * @return such location.
      */
    @Override
    @NonNull
    public String getIndexFileLocation() {
        return "META-INF/apply.index";
    }

    /**
      * Retrieves the annotation class.
      * @return such class.
      */
    @Override
    @NonNull
    public Class<? extends Annotation> getAnnotationClass() {
        return Apply.class;
    }

    /**
     * Validates the annotations found.
     * @param annotations the annotations.
     * @param roundEnv the {@code RoundEnvironment} instance.
     * @return {@code true} if all annotated methods are valid.
     */
    @Override
    protected boolean validate(@NonNull final Set<? extends TypeElement> annotations, @NonNull final RoundEnvironment roundEnv) {
        final Map<TypeElement, Map<EventKey, ExecutableElement>> registry = new HashMap<>();

        for (final Element element : roundEnv.getElementsAnnotatedWith(Apply.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            final ExecutableElement method = (ExecutableElement) element;

            final List<? extends VariableElement> parameters = method.getParameters();

            if (parameters.size() != 1) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@Apply methods must have exactly one parameter",
                    method);
                continue;
            }

            final VariableElement param = parameters.get(0);
            final TypeElement eventClass = (TypeElement) processingEnv.getTypeUtils().asElement(param.asType());

            final Optional<? extends Element> versionFieldOpt = eventClass.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .filter(e -> e.getSimpleName().contentEquals("VERSION"))
                .filter(e -> e.getModifiers().containsAll(Set.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)))
                .findFirst();

            if (versionFieldOpt.isEmpty()) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Event class must declare `public static final long VERSION`",
                    eventClass);
                continue;
            }

            final VariableElement versionField = (VariableElement) versionFieldOpt.get();
            final Object value = versionField.getConstantValue();

            if (!(value instanceof Long)) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "`VERSION` field must be a constant long",
                    versionField);
                continue;
            }

            final long version = (Long) value;
            final TypeMirror eventType = param.asType();
            final EventKey key = new EventKey(eventType.toString(), version);
            final TypeElement enclosingClass = (TypeElement) method.getEnclosingElement();

            registry
                .computeIfAbsent(enclosingClass, k -> new HashMap<>())
                .compute(key, (k, existing) -> {
                    if (existing != null) {
                        processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            String.format("Duplicate @HandlesEvent handler for event type '%s' and version %d",
                                k.eventClass, k.version),
                            method
                        );
                    }
                    return method;
                });
        }

        return true;
    }

    /**
      Companion class to store events as keys.

      Class name: EventKey

      Responsibilities:
        - Act as a key for events in maps.
        - Know how to compare to other instances using event semantics.
    */
    protected static class EventKey {
        /**
         * The event class.
         */
        private final String eventClass;

        /**
         * The event version.
         */
        private final long version;

        /**
         * Creates a new instance.
         * @param eventClass the event class.
         * @param version the event version.
         */
        protected EventKey(@NonNull final String eventClass, final long version) {
            this.eventClass = eventClass;
            this.version = version;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(@Nullable final Object object) {
            final boolean result;
            if (this == object) {
                result = true;
            } else if (object instanceof EventKey that) {
                result = version == that.version && Objects.equals(eventClass, that.eventClass);
            } else {
                result = false;
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(eventClass, version);
        }
    }
}
