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
 * Filename: AgentJarBuilder.java
 *
 * Author: Claude Code
 *
 * Class name: AgentJarBuilder
 *
 * Responsibilities:
 *   - Ensure the ByteHot agent JAR exists for tests that need it
 *   - Build the agent JAR on-demand using Maven
 *   - Provide reusable utility for agent-dependent tests
 *
 * Collaborators:
 *   - Maven build system: For creating the shaded agent JAR
 */
package org.acmsl.bytehot.testing.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class to ensure the ByteHot agent JAR exists for tests.
 * Some tests need to run Java processes with the agent JAR, but Maven
 * typically builds the shaded JAR after tests run. This utility solves
 * the circular dependency by building the JAR on-demand during test setup.
 * 
 * @author Claude Code
 * @since 2025-06-18
 */
public class AgentJarBuilder {

    /**
     * Expected location of the agent JAR
     */
    private static final String AGENT_JAR_PATH = "/target/bytehot-latest-SNAPSHOT-agent.jar";

    /**
     * Ensures the agent JAR exists, building it if necessary.
     * This method checks if the agent JAR exists and builds it using
     * Maven if it's missing. It's safe to call multiple times.
     * 
     * @throws RuntimeException if the JAR cannot be built
     */
    public static void ensureAgentJarExists() {
        try {
            Path agentJar = Path.of(System.getProperty("user.dir") + AGENT_JAR_PATH);
            
            if (!Files.exists(agentJar)) {
                buildAgentJar(agentJar);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to ensure agent JAR exists", e);
        }
    }

    /**
     * Gets the path to the agent JAR, building it if necessary.
     * 
     * @return the path to the agent JAR
     * @throws RuntimeException if the JAR cannot be built
     */
    public static Path getAgentJarPath() {
        ensureAgentJarExists();
        return Path.of(System.getProperty("user.dir") + AGENT_JAR_PATH);
    }

    /**
     * Builds the agent JAR using Maven.
     * 
     * @param agentJar the expected path of the agent JAR
     * @throws IOException if process creation fails
     * @throws InterruptedException if the process is interrupted
     * @throws RuntimeException if the build fails
     */
    private static void buildAgentJar(Path agentJar) throws IOException, InterruptedException {
        System.out.println("Building agent JAR for test...");
        
        ProcessBuilder builder = new ProcessBuilder("mvn", "package", "-DskipTests=true", "-q");
        builder.directory(Path.of(System.getProperty("user.dir")).toFile());
        
        Process process = builder.start();
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new RuntimeException("Failed to build agent JAR for test. Exit code: " + exitCode);
        }
        
        if (!Files.exists(agentJar)) {
            throw new RuntimeException("Agent JAR was not created: " + agentJar);
        }
        
        System.out.println("Agent JAR built successfully: " + agentJar);
    }
}