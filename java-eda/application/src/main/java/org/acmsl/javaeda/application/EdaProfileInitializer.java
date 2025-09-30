package org.acmsl.javaeda.application;

import org.acmsl.javaeda.domain.EdaProfile;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
  Initializes the EDA profile and synchronizes it with the active Spring profile.

  Class name: EdaProfileInitializer

  Responsibilities:
    - Propagate the EDA profile as Spring profile.

  Collaborators:
    - SpringBoot
 */
@SuppressWarnings({"PMD.LawOfDemeter","PMD.AtLeastOneConstructor"})
public class EdaProfileInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * Initializes the EDA profile.
     * @param context the {@link ConfigurableApplicationContext} instance.
     */
    @Override
    public void initialize(@NonNull final ConfigurableApplicationContext context) {
        final Optional<String> edaProfile = new EdaProfile() {}.getValue();
        edaProfile.ifPresent(profile -> context.getEnvironment().addActiveProfile(profile));
    }
}
