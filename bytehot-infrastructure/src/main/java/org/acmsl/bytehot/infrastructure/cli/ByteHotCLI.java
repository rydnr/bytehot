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
 * Filename: ByteHotCLI.java
 *
 * Author: rydnr
 *
 * Class name: ByteHotCLI
 *
 * Responsibilities: Extract parameters from the command line into a domain
 * event, and send it to the application layer through the Application interface.
 *
 * Collaborators:
 *   - Application: Interface from java-commons for hexagonal architecture
 */
package org.acmsl.bytehot.infrastructure.cli;

import java.lang.instrument.Instrumentation;

import java.io.IOException;
import java.nio.file.Path;

import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.WatchConfiguration;

import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainResponseEvent;

/**
 * Primary port from the command line, when a JVM is started with the agent enabled.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
public class ByteHotCLI {
    /**
     * Protected constructor to prevent instantiation.
     */
    protected ByteHotCLI() {}

    /**
     * Called when we are attached at JVM startup.
     * @param agentArgs Arguments passed to the agent.
     * @param inst Instrumentation instance for the JVM.
     */
    @SuppressWarnings("unchecked")
    public static void premain(final String agentArgs, final Instrumentation inst) {
        String configPath = System.getProperty("bhconfig");
        if (configPath == null || configPath.isBlank()) {
            throw new IllegalStateException("Missing required system property bhconfig");
        }

        try {
            // Discover and initialize the Application layer through reflection
            final Application application = discoverApplication(inst);
            
            WatchConfiguration config = WatchConfiguration.load();
            
            // Process the attach request
            application.accept(ByteHotAttachRequested.withUserContext(config, inst));
            
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to initialize ByteHot CLI", e);
        }
    }

    /**
     * Called when we are attached to a running JVM.
     * @param agentArgs Arguments passed to the agent.
     * @param inst Instrumentation instance for the JVM.
     */
    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        premain(agentArgs, inst);
    }
    
    /**
     * Discovers and initializes the Application layer through reflection
     * Following hexagonal architecture, infrastructure should not directly depend on application
     * @param inst the instrumentation instance to pass to application
     * @return the discovered Application instance
     * @throws Exception if application discovery fails
     */
    @SuppressWarnings("unchecked")
    protected static Application discoverApplication(final Instrumentation inst) throws Exception {
        try {
            // Try to find ByteHotApplication in the application layer
            final String applicationClassName = "org.acmsl.bytehot.application.ByteHotApplication";
            final Class<?> applicationClass = Class.forName(applicationClassName);
            
            // Initialize the application with instrumentation
            final var initializeMethod = applicationClass.getMethod("initialize", Instrumentation.class);
            initializeMethod.invoke(null, inst);
            
            // Get the singleton instance
            final var getInstanceMethod = applicationClass.getMethod("getInstance");
            final Object applicationInstance = getInstanceMethod.invoke(null);
            
            // Verify it implements the Application interface
            if (!(applicationInstance instanceof Application)) {
                throw new IllegalStateException("Discovered application class does not implement Application interface");
            }
            
            return (Application) applicationInstance;
            
        } catch (final ClassNotFoundException e) {
            throw new Exception("ByteHotApplication not found in classpath. Ensure bytehot-application module is included.", e);
        } catch (final Exception e) {
            throw new Exception("Failed to discover and initialize Application layer: " + e.getMessage(), e);
        }
    }
}
