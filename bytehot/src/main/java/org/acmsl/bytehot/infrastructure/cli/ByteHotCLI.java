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
 * event, and send it to the application layer.
 *
 * Collaborators:
 *   - ByteHot: the application layer.
 */
package org.acmsl.bytehot.infrastructure.cli;

import java.lang.instrument.Instrumentation;

import java.io.IOException;
import java.nio.file.Path;

import org.acmsl.bytehot.application.ByteHotApplication;
import org.acmsl.bytehot.domain.events.ByteHotStartRequested;
import org.acmsl.bytehot.domain.WatchConfiguration;

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
    public static void premain(final String agentArgs, final Instrumentation inst) {
        String configPath = System.getProperty("hsconfig");
        if (configPath == null || configPath.isBlank()) {
            throw new IllegalStateException("Missing required system property hsconfig");
        }

        WatchConfiguration config;
        try {
            config = WatchConfiguration.load(Path.of(configPath));
        } catch (final IOException exception) {
            throw new IllegalStateException("Failed to load configuration", exception);
        }

        ByteHotApplication.getInstance().accept(new ByteHotStartRequested(config, inst));
    }

    /**
     * Called when we are attached to a running JVM.
     * @param agentArgs Arguments passed to the agent.
     * @param inst Instrumentation instance for the JVM.
     */
    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        premain(agentArgs, inst);
    }
}
