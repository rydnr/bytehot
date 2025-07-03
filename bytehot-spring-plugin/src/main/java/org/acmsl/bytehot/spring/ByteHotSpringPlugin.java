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
 * Filename: ByteHotSpringPlugin.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotSpringPlugin
 *
 * Responsibilities:
 *   - Provide Spring Framework integration for ByteHot
 *   - Manage Spring application context hot-swapping
 *   - Handle Spring bean lifecycle during hot-swap operations
 *   - Coordinate Spring-specific configuration and annotation processing
 *
 * Collaborators:
 *   - PluginBase: Foundation plugin infrastructure
 *   - SpringContextManager: Manages Spring application context
 *   - SpringBeanHotSwapHandler: Handles Spring bean hot-swapping
 *   - SpringConfigurationDetector: Detects Spring configuration changes
 *   - SpringAnnotationProcessor: Processes Spring annotation changes
 */
package org.acmsl.bytehot.spring;

import org.acmsl.bytehot.plugin.core.PluginBase;
import org.acmsl.bytehot.plugin.core.PluginConfiguration;

/**
 * ByteHot plugin for Spring Framework integration.
 * Provides seamless hot-swapping of Spring beans, configurations, and components.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class ByteHotSpringPlugin extends PluginBase {

    /**
     * Spring context manager for application context operations.
     */
    protected final SpringContextManager springContextManager;

    /**
     * Spring bean hot-swap handler for bean-specific operations.
     */
    protected final SpringBeanHotSwapHandler springBeanHotSwapHandler;

    /**
     * Spring configuration detector for configuration changes.
     */
    protected final SpringConfigurationDetector springConfigurationDetector;

    /**
     * Spring annotation processor for annotation changes.
     */
    protected final SpringAnnotationProcessor springAnnotationProcessor;

    /**
     * Creates a new ByteHot Spring plugin with initialized Spring components.
     */
    public ByteHotSpringPlugin() {
        super();
        this.springContextManager = new SpringContextManager();
        this.springBeanHotSwapHandler = new SpringBeanHotSwapHandler();
        this.springConfigurationDetector = new SpringConfigurationDetector();
        this.springAnnotationProcessor = new SpringAnnotationProcessor();
    }

    @Override
    public String getPluginName() {
        return "ByteHot Spring Plugin";
    }

    @Override
    public String getPluginVersion() {
        return "1.0.0-SNAPSHOT";
    }

    @Override
    public PluginConfiguration getDefaultConfiguration() {
        return new SpringPluginConfiguration();
    }

    @Override
    protected boolean onInitialize() {
        // Spring-specific initialization
        return initializeSpringIntegration();
    }

    /**
     * Gets the Spring context manager.
     * 
     * @return the Spring context manager
     */
    public SpringContextManager getSpringContextManager() {
        return springContextManager;
    }

    /**
     * Gets the Spring bean hot-swap handler.
     * 
     * @return the Spring bean hot-swap handler
     */
    public SpringBeanHotSwapHandler getSpringBeanHotSwapHandler() {
        return springBeanHotSwapHandler;
    }

    /**
     * Gets the Spring configuration detector.
     * 
     * @return the Spring configuration detector
     */
    public SpringConfigurationDetector getSpringConfigurationDetector() {
        return springConfigurationDetector;
    }

    /**
     * Gets the Spring annotation processor.
     * 
     * @return the Spring annotation processor
     */
    public SpringAnnotationProcessor getSpringAnnotationProcessor() {
        return springAnnotationProcessor;
    }

    /**
     * Initializes Spring-specific integration.
     * 
     * @return true if Spring integration was initialized successfully, false otherwise
     */
    protected boolean initializeSpringIntegration() {
        try {
            // 1. Discover Spring application context
            if (!springContextManager.discoverSpringContext()) {
                // Not a failure - might not be a Spring application
                return true;
            }

            // 2. Initialize Spring-specific components
            springBeanHotSwapHandler.initialize(springContextManager);
            springConfigurationDetector.initialize(springContextManager);
            springAnnotationProcessor.initialize(springContextManager);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}