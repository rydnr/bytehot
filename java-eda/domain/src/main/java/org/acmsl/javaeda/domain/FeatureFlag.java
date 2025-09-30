package org.acmsl.javaeda.domain;

/**
  A feature flag.

  Class name: FeatureFlag

  Responsibilities:
    - Check if a feature flag is enabled or not.

  Collaborators:
    - org.acmsl.javaeda.annotations.FeatureToggle
 */
public interface FeatureFlag extends Port {

     /**
      * Checks if given feature flag is enabled.
      * @param name the name of the feature flag.
      * @return {code true} in such case.
      */
     boolean isEnabled(String name);
}
