/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: SpringPluginConfiguration.java
 *
 * Author: Claude Code
 *
 * Class name: SpringPluginConfiguration
 *
 * Responsibilities:
 *   - Provide Spring-specific plugin configuration
 *   - Define Spring framework integration settings
 *   - Configure Spring context discovery and management options
 *   - Set Spring hot-swap behavior parameters
 *
 * Collaborators:
 *   - PluginConfiguration: Base configuration interface
 *   - ByteHotSpringPlugin: Uses this configuration
 */
package org.acmsl.bytehot.spring;

import org.acmsl.bytehot.plugin.core.PluginConfiguration;

/**
 * Configuration for ByteHot Spring plugin.
 * Defines Spring-specific settings and behavior parameters.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class SpringPluginConfiguration implements PluginConfiguration {

    /**
     * Configuration name for Spring plugin.
     */
    protected static final String SPRING_PLUGIN_CONFIG_NAME = "bytehot-spring-plugin-config";

    /**
     * Enable Spring Boot auto-detection.
     */
    protected final boolean enableSpringBootDetection = true;

    /**
     * Enable Spring AOP proxy refresh.
     */
    protected final boolean enableAopProxyRefresh = true;

    /**
     * Enable Spring transaction management integration.
     */
    protected final boolean enableTransactionIntegration = true;

    /**
     * Enable Spring caching integration.
     */
    protected final boolean enableCachingIntegration = true;

    /**
     * Maximum context refresh time in milliseconds.
     */
    protected final long maxContextRefreshTimeMs = 5000L;

    @Override
    public String getConfigurationName() {
        return SPRING_PLUGIN_CONFIG_NAME;
    }

    @Override
    public boolean isValid() {
        // Basic validation - more sophisticated validation can be added later
        return maxContextRefreshTimeMs > 0 && maxContextRefreshTimeMs < 30000L;
    }

    /**
     * Checks if Spring Boot auto-detection is enabled.
     * 
     * @return true if Spring Boot detection is enabled, false otherwise
     */
    public boolean isSpringBootDetectionEnabled() {
        return enableSpringBootDetection;
    }

    /**
     * Checks if AOP proxy refresh is enabled.
     * 
     * @return true if AOP proxy refresh is enabled, false otherwise
     */
    public boolean isAopProxyRefreshEnabled() {
        return enableAopProxyRefresh;
    }

    /**
     * Checks if transaction management integration is enabled.
     * 
     * @return true if transaction integration is enabled, false otherwise
     */
    public boolean isTransactionIntegrationEnabled() {
        return enableTransactionIntegration;
    }

    /**
     * Checks if caching integration is enabled.
     * 
     * @return true if caching integration is enabled, false otherwise
     */
    public boolean isCachingIntegrationEnabled() {
        return enableCachingIntegration;
    }

    /**
     * Gets the maximum context refresh time in milliseconds.
     * 
     * @return the maximum context refresh time
     */
    public long getMaxContextRefreshTimeMs() {
        return maxContextRefreshTimeMs;
    }
}