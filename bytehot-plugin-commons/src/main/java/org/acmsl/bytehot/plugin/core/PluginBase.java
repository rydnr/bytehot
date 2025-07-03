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
 * Filename: PluginBase.java
 *
 * Author: Claude Code
 *
 * Class name: PluginBase
 *
 * Responsibilities:
 *   - Provide abstract base class for all ByteHot plugins
 *   - Manage plugin lifecycle and initialization
 *   - Coordinate agent discovery and configuration
 *   - Establish communication with ByteHot agent
 *
 * Collaborators:
 *   - AgentDiscovery: Locates ByteHot agent JAR
 *   - ConfigurationManager: Handles plugin configuration
 *   - PluginCommunicationHandler: Manages agent communication
 *   - HealthMonitor: Monitors plugin health status
 */
package org.acmsl.bytehot.plugin.core;

/**
 * Abstract base class for all ByteHot plugins.
 * Provides common functionality for plugin lifecycle management,
 * agent discovery, configuration, and communication.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public abstract class PluginBase {

    /**
     * Agent discovery component for locating ByteHot agent.
     */
    protected final AgentDiscovery agentDiscovery;

    /**
     * Configuration manager for loading plugin configuration.
     */
    protected final ConfigurationManager configManager;

    /**
     * Communication handler for agent interaction.
     */
    protected final PluginCommunicationHandler communicationHandler;

    /**
     * Health monitor for tracking plugin status.
     */
    protected final HealthMonitor healthMonitor;

    /**
     * Creates a new plugin base with initialized components.
     */
    protected PluginBase() {
        this.agentDiscovery = new AgentDiscovery();
        this.configManager = new ConfigurationManager();
        this.communicationHandler = createCommunicationHandler();
        this.healthMonitor = new HealthMonitor();
        
        // Register default configuration adapter
        this.configManager.registerAdapter(createDefaultConfigurationAdapter());
    }

    /**
     * Gets the name of this plugin.
     * 
     * @return the plugin name
     */
    public abstract String getPluginName();

    /**
     * Gets the version of this plugin.
     * 
     * @return the plugin version
     */
    public abstract String getPluginVersion();

    /**
     * Gets the default configuration for this plugin.
     * 
     * @return the default plugin configuration
     */
    public abstract PluginConfiguration getDefaultConfiguration();

    /**
     * Creates the communication handler for this plugin.
     * Subclasses can override to provide specific communication implementations.
     * 
     * @return the communication handler instance
     */
    protected PluginCommunicationHandler createCommunicationHandler() {
        // Default implementation - can be overridden by subclasses
        return new DefaultPluginCommunicationHandler();
    }

    /**
     * Creates the default configuration adapter for this plugin.
     * Subclasses can override to provide specific configuration sources.
     * 
     * @return the default configuration adapter
     */
    protected ConfigurationAdapter createDefaultConfigurationAdapter() {
        return new DefaultConfigurationAdapter(getDefaultConfiguration());
    }

    /**
     * Called during plugin initialization after all components are ready.
     * Subclasses should override this method to perform plugin-specific initialization.
     * 
     * @return true if initialization succeeded, false otherwise
     */
    protected abstract boolean onInitialize();

    /**
     * Initializes the plugin by setting up all required components.
     * This method coordinates the initialization sequence:
     * 1. Agent discovery
     * 2. Configuration loading
     * 3. Communication handler setup
     * 4. Health monitoring
     * 5. Plugin-specific initialization
     * 
     * @return true if initialization succeeded, false otherwise
     */
    public final boolean initialize() {
        try {
            // 1. Discover ByteHot agent
            if (!agentDiscovery.discoverAgent()) {
                return false;
            }
            
            // 2. Load configuration
            if (!configManager.loadConfiguration()) {
                return false;
            }
            
            // 3. Setup communication handler
            if (!communicationHandler.connect()) {
                return false;
            }
            
            // 4. Start health monitoring
            healthMonitor.start();
            
            // 5. Plugin-specific initialization
            final boolean result = onInitialize();
            
            if (result) {
                healthMonitor.recordSuccessfulCommunication();
            } else {
                healthMonitor.recordPluginError();
            }
            
            return result;
            
        } catch (Exception e) {
            healthMonitor.recordPluginError();
            return false;
        }
    }

    /**
     * Shuts down the plugin and cleans up resources.
     */
    public void shutdown() {
        try {
            healthMonitor.stop();
            communicationHandler.disconnect();
        } catch (Exception e) {
            // Log error but continue shutdown
        }
    }

    /**
     * Gets the agent discovery component.
     * 
     * @return the agent discovery instance
     */
    protected AgentDiscovery getAgentDiscovery() {
        return agentDiscovery;
    }

    /**
     * Gets the configuration manager.
     * 
     * @return the configuration manager instance
     */
    protected ConfigurationManager getConfigurationManager() {
        return configManager;
    }

    /**
     * Gets the communication handler.
     * 
     * @return the communication handler instance
     */
    protected PluginCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    /**
     * Gets the health monitor.
     * 
     * @return the health monitor instance
     */
    protected HealthMonitor getHealthMonitor() {
        return healthMonitor;
    }
}

