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
 * Filename: ByteHotAgent.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotAgent
 *
 * Responsibilities:
 *   - JVM agent entry point for ByteHot instrumentation setup
 *   - Initialize the InstrumentationProvider for hot-swap operations
 *   - Discover and initialize Application layer through reflection
 *
 * Collaborators:
 *   - InstrumentationProvider: Singleton for instrumentation access
 *   - Instrumentation: JVM API for class redefinition
 *   - Application: Interface from java-commons for hexagonal architecture
 */
package org.acmsl.bytehot.infrastructure.agent;

import org.acmsl.bytehot.domain.InstrumentationProvider;
import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;

import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * JVM agent entry point for ByteHot instrumentation setup
 * @author Claude Code
 * @since 2025-06-17
 */
public class ByteHotAgent {
    
    /**
     * The discovered application instance (cached after first discovery)
     */
    private static Application applicationInstance;

    /**
     * Agent entry point called when ByteHot is loaded as -javaagent at JVM startup
     * @param agentArgs arguments passed to the agent
     * @param inst instrumentation instance provided by JVM
     */
    public static void premain(final String agentArgs, final Instrumentation inst) {
        InstrumentationProvider.setInstrumentation(inst);
        
        // Create and process the agent attachment request through domain logic
        try {
            // Discover and initialize the Application layer through reflection
            final Application application = discoverApplication(inst);
            
            // Load the actual configuration instead of using default
            final WatchConfiguration config = WatchConfiguration.load();
            final ByteHotAttachRequested attachRequest = new ByteHotAttachRequested(config, inst);
            final List<? extends DomainResponseEvent<?>> responses = application.accept(attachRequest);
            
            // Print the response events to stdout for testing verification
            for (final DomainResponseEvent<?> response : responses) {
                System.out.println(response.getClass().getSimpleName());
            }
            
        } catch (final Exception e) {
            System.err.println("Failed to process agent attachment: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("ByteHot agent initialized successfully");
    }

    /**
     * Agent entry point called when ByteHot is attached to running JVM
     * @param agentArgs arguments passed to the agent
     * @param inst instrumentation instance provided by JVM
     */
    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        // Runtime agent attachment uses the same initialization as premain
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