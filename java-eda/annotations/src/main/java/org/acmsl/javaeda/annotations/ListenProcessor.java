package org.acmsl.javaeda.annotations;

import com.google.auto.service.AutoService;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Messager;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  A processor for @Listen annotations.

  Class name: ListenProcessor

  Responsibilities:
    - Discover and process any @Listen-annotated methods.

  Collaborators:
    - None
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("org.acmsl.javaeda.annotations.Listen")
@SuppressWarnings({"unused","PMD.AtLeastOneConstructor"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ListenProcessor extends EventHandlerProcessor {

    /**
      * Retrieves the index file location.
      * @return such location.
      */
    @Override
    @NonNull
    public String getIndexFileLocation() {
        return "META-INF/listen.index";
    }

    /**
      * Retrieves the annotation class.
      * @return such class.
      */
    @Override
    @NonNull
    public Class<? extends Annotation> getAnnotationClass() {
        return Listen.class;
    }

    /**
     * Validates the annotated methods have valid signatures.
     * @param annotations the annotations.
     * @param roundEnv the {@code RoundEnv} instance.
     * @return {@code true} if all annotated methods are valid.
     */
    @Override
    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition","PMD.LawOfDemeter"})
    public boolean validate(@NonNull final Set<? extends TypeElement> annotations, @NonNull final RoundEnvironment roundEnv) {
        final Messager messager = processingEnv.getMessager();
        final Types typeUtils = processingEnv.getTypeUtils();
        final Elements elementUtils = processingEnv.getElementUtils();

        final TypeMirror rawEventType = elementUtils.getTypeElement("org.acmsl.javaeda.domain.Event").asType();
        final TypeMirror entityType = elementUtils.getTypeElement("org.acmsl.javaeda.domain.Entity").asType();
        final TypeMirror listType = elementUtils.getTypeElement("java.util.List").asType();

        for (final Element element : roundEnv.getElementsAnnotatedWith(Listen.class)) {
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(Kind.ERROR, "@Listen can only annotate methods.", element);
                continue;
            }

            final ExecutableElement method = (ExecutableElement) element;

            // Check method is not static.
            final boolean isStatic = method.getModifiers().contains(Modifier.STATIC);
            if (isStatic) {
                messager.printMessage(Kind.ERROR, "Method annotated with @Listen cannot be static.", method);
                continue;
            }

            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() != 1) {
                messager.printMessage(Kind.ERROR, "Method annotated with @Listen must have exactly one parameter.", method);
                continue;
            }

            final VariableElement param = parameters.get(0);
            if (!isEventSubtype(param.asType(), typeUtils, elementUtils, rawEventType)) {
                messager.printMessage(Kind.ERROR, "Method parameter must be of type Event.", param);
            }

            // Check method return type (List<Event>)
            final TypeMirror returnType = method.getReturnType();

            if (!isListOfEvents(returnType, typeUtils, elementUtils, rawEventType)) {
                messager.printMessage(Kind.ERROR,
                    "Method return type must be List<Event>.", method);
            }
        }
        return true;
    }
}
