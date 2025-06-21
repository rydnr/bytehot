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
 *
 * Collaborators:
 *   - InstrumentationProvider: Singleton for instrumentation access
 *   - Instrumentation: JVM API for class redefinition
 */
package org.acmsl.bytehot.infrastructure.agent;

import org.acmsl.bytehot.application.ByteHotApplication;
import org.acmsl.bytehot.domain.InstrumentationProvider;
import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;

import org.acmsl.commons.patterns.DomainResponseEvent;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * JVM agent entry point for ByteHot instrumentation setup
 * @author Claude Code
 * @since 2025-06-17
 */
public class ByteHotAgent {

    /**
     * Agent entry point called when ByteHot is loaded as -javaagent at JVM startup
     * @param agentArgs arguments passed to the agent
     * @param inst instrumentation instance provided by JVM
     */
    public static void premain(final String agentArgs, final Instrumentation inst) {
        InstrumentationProvider.setInstrumentation(inst);
        
        // Initialize ByteHot hexagonal architecture
        ByteHotApplication.initialize(inst);
        
        // Create and process the agent attachment request through domain logic
        try {
            final WatchConfiguration config = new WatchConfiguration(8080); // Default port
            final ByteHotAttachRequested attachRequest = new ByteHotAttachRequested(config, inst);
            final List<DomainResponseEvent<ByteHotAttachRequested>> responses = ByteHotApplication.getInstance().accept(attachRequest);
            
            // Print the response events to stdout for testing verification
            for (final DomainResponseEvent<ByteHotAttachRequested> response : responses) {
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
}