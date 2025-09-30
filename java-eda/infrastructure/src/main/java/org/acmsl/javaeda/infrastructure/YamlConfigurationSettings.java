package org.acmsl.javaeda.infrastructure;

import org.acmsl.javaeda.domain.BaseObject;
import org.acmsl.javaeda.domain.ConfigurationSettings;
import java.util.Properties;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/**
  Configuration settings read from a yaml file.

  Class name: YamlConfigurationSettings

  Responsibilities:
    - Read configuration settings from a yaml file.

  Collaborators:
    - None
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConstructorCallsOverridableMethod","PMD.UnusedAssignment"})
public abstract class YamlConfigurationSettings extends BaseObject implements ConfigurationSettings {

    /**
     * The yaml file.
     */
    private final String yamlFile;

    /**
     * The properties.
     */
    private Properties properties;

    /**
     * Whether it's initialized already.
     */
    private boolean initialized;

    /**
     * Creates a new instance to read given file.
     * @param yamlFile the yaml file.
     */
    public YamlConfigurationSettings(@NonNull final String yamlFile) {
        super();
        this.yamlFile = yamlFile;
        this.initialized = false;
        initialize();
        this.initialized = true;
    }

    /**
     * Checks if this adapter is enabled or not.
     * @return {code true} in any case.
     */
    @Override
    public boolean enabled() {
        return this.getProperties() != null;
    }

    /**
     * Initializes the adapter.
     */
    @Override
    public void initialize() {
        if (!this.initialized) {
            final YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(new ClassPathResource(this.yamlFile));
            this.properties = yamlFactory.getObject();
        }
    }

    /**
     * Retrieves the properties.
     * @return such instance.
     */
    @Nullable
    protected Properties getProperties() {
        return this.properties;
    }

    /**
     * Retrieves a string value.
     * @param key the key.
     * @return the value, or {@code null} if non-existent.
     */
    @Override
    @Nullable
    public String getString(@NonNull final String key) {
        final String result;
        final Properties props = this.getProperties();
        if (props == null) {
            result = null;
        } else {
            final Object aux = props.get(key);
            if (aux == null) {
                result = null;
            } else {
                result = aux.toString();
            }
        }
        return result;
    }

    /**
     * Retrieves an integer value.
     * @param key the key.
     * @return the value, or {@code null} if non-existent.
     */
    @Override
    @Nullable
    public Integer getInteger(@NonNull final String key) {
        Integer result = null;
        final Object prop = this.getString(key);
        if (prop != null) {
            try {
                result = Integer.parseInt(prop.toString());
            } catch (@NonNull final NumberFormatException ex) {
                if (this.getLogger("config").isErrorEnabled()) {
                    this.getLogger("config").error("Invalid number " + prop + " (" + key + ") in " + this.yamlFile, ex);
                }
            }
        }
        return result;
    }

    /**
     * Retrieves a long value.
     * @param key the key.
     * @return the value, or {@code null} if non-existent.
     */
    @Override
    @Nullable
    public Long getLong(@NonNull final String key) {
        Long result = null;
        final Object prop = this.getString(key);
        if (prop != null) {
            try {
                result = Long.parseLong(prop.toString());
            } catch (@NonNull final NumberFormatException ex) {
                if (this.getLogger("config").isErrorEnabled()) {
                    this.getLogger("config").error("Invalid number " + prop + " (" + key + ") in " + this.yamlFile, ex);
                }
            }
        }
        return result;
    }

    /**
     * Retrieves an array of strings.
     * @param key the key.
     * @return the value, or an empty array if non-existent.
     */
    @Override
    @NonNull
    public String[] getStringArray(@NonNull final String key) {
        final String[] result;
        final String prop = this.getString(key);
        if (prop == null) {
            result = new String[0];
        } else {
            result = prop.split(",");
        }
        return result;
    }
}
