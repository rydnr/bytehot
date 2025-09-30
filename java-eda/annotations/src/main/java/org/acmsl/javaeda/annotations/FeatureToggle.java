package org.acmsl.javaeda.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  An annotation to replace a method with a different implementation if a feature flag is enabled.

  Class name: FeatureToggle

  Responsibility:
    - Define a @FeatureToggle annotation, with two parameters: feature (the name of the feature); and replaceWith (the replacement if the feature flag is enabled).

  Collaborators:
    - org.acmsl.javaeda.domain.FeatureFlag
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureToggle {
    /**
     * The name of the feature.
     */
    String feature();

    /**
     * The substitute method if the feature flag is active.
     */
    String replaceWith();
}
