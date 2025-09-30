package org.acmsl.javaeda.infrastructure;

import org.acmsl.javaeda.domain.Application;
import org.acmsl.javaeda.domain.BaseObject;
import org.acmsl.javaeda.domain.ConfigurationSettings;
import org.acmsl.javaeda.domain.FeatureFlag;
import org.acmsl.javaeda.domain.Ports;
import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
  Unleash implementation of feature flags.

  Class name: UnleashFeatureFlag

  Responsibilities:
    - Check if a feature flag is enabled or not.

  Collaborators:
    - org.acmsl.javaeda.annotations.FeatureToggle
    - Unleash SDK
 */
@SuppressWarnings({"PMD.LongVariable", "PMD.AvoidFieldNameMatchingMethodName"})
public class UnleashFeatureFlag extends BaseObject implements FeatureFlag {
    /**
     * The app name entry.
     */
    public static final String APP_NAME_KEY = "unleash.app-name";

    /**
     * The instance id entry.
     */
    public static final String INSTANCE_ID_KEY = "unleash.instance-id";

    /**
     * The API url.
     */
    public static final String API_URL_KEY = "unleash.api-url";

    /**
     * The API key environment variable.
     */
    public static final String API_KEY = "UNLEASH_API_KEY";

    /**
     * The fetch interval in seconds.
     */
    public static final String FETCH_INTERVAL_IN_SECONDS = "unleash.fetch-interval-in-seconds";

    /**
     * The application instance.
     */
    private final Application application;

    /**
     * The {@code Unleash} instance.
     */
    private Unleash unleash;

    /**
     * Whether this adapter is enabled or not.
     */
    private boolean enabled;

    /**
     * Creates a new instance.
     * @param application the {@link Application} instance.
     */
    public UnleashFeatureFlag(@NonNull final Application application) {
        super();
        this.application = application;
    }

    /**
     * Initializes this adapter.
     */
    @Override
    public void initialize() {
        final ConfigurationSettings settings = Ports.getInstance().resolveFirst(ConfigurationSettings.class);
        if (settings == null) {
            if (getLogger().isErrorEnabled()) {
                getLogger().error("Configuration settings not available");
            }
        } else {
            @Nullable final String appName = settings.getString(APP_NAME_KEY);
            @Nullable final String instanceId = settings.getString(INSTANCE_ID_KEY);
            @Nullable final String apiUrl = settings.getString(API_URL_KEY);
            @Nullable final String apiKey = System.getenv(API_KEY);
            @Nullable final Long fetchIntervalInSeconds = settings.getLong(FETCH_INTERVAL_IN_SECONDS);
            if (validate(appName, instanceId, apiUrl, fetchIntervalInSeconds)) {
                final UnleashConfig config = UnleashConfig.builder()
                    .appName(appName)
                    .instanceId(instanceId)
                    .unleashAPI(apiUrl)
                    .apiKey(apiKey)
                    .fetchTogglesInterval(fetchIntervalInSeconds)
                    .build();

                this.unleash = new DefaultUnleash(config);
                this.enabled = true;
            } else {
                 if (getLogger().isWarnEnabled()) {
                    getLogger().warn(
                        String.format(
                            "⚠️ Unleash disabled due to missing configuration: (appName: %s, instanceId: %s, apiUrl:%s, apiKey: %s, fetchInterval: %s)",
                            appName, instanceId, apiUrl, apiKey == null ? "(missing)" : "(provided)", fetchIntervalInSeconds));
                }
                this.enabled = false;
            }
        }
    }

    /**
     * Checks if required configuration settings are available and valid.
     * @param appName the application's name.
     * @param instanceId the id of the instance.
     * @param apiUrl the url of the Unleash API.
     * @param fetchIntervalInSeconds how long to wait to refresh the settings.
     * @return {@code true} if the settings are valid.
     */
    protected final boolean validate(
        @Nullable final String appName, @Nullable final String instanceId, @Nullable final String apiUrl, @Nullable final Long fetchIntervalInSeconds) {
        final boolean result;
        if ((appName == null) || (instanceId == null) || (apiUrl == null) || (fetchIntervalInSeconds == null)) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }
    /**
     * Checks if this adapter is enabled or not.
     * @return {code true} in any case.
     */
    @Override
    public boolean enabled() {
        return this.enabled;
    }

    /**
     * Checks if given feature flag is enabled.
     * @param featureName the name.
     * @return {@code true} if enabled; {@code false} if disabled.
     */
    @Override
    public boolean isEnabled(@NonNull final String featureName) {
        final boolean result;
        if (this.unleash == null) {
            if (getLogger().isWarnEnabled()) {
                getLogger().warn("Unleash not available. Rejecting feature flag " + featureName);
            }
            result = false;
        } else {
            result = this.unleash.isEnabled(featureName);
        }
        return result;
    }
}
