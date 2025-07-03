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
 * Filename: AgentDiscovery.java
 *
 * Author: Claude Code
 *
 * Class name: AgentDiscovery
 *
 * Responsibilities:
 *   - Locate ByteHot agent JAR across different environments
 *   - Validate agent JAR integrity and compatibility
 *   - Provide fallback discovery strategies
 *   - Cache discovered agent path for performance
 *
 * Collaborators:
 *   - PluginBase: Uses agent discovery during initialization
 *   - JarFile: Validates agent JAR structure
 *   - Manifest: Checks required agent attributes
 *   - Files: File system operations for discovery
 */
package org.acmsl.bytehot.plugin.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

/**
 * Discovers and validates ByteHot agent JAR files across different environments.
 * Implements multiple discovery strategies with automatic fallback and caching.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class AgentDiscovery {

    /**
     * Pattern for ByteHot agent JAR files.
     */
    protected static final String AGENT_JAR_PATTERN = "bytehot-application-*-agent.jar";

    /**
     * System property key for agent path override.
     */
    protected static final String AGENT_PATH_PROPERTY = "bytehot.agent.path";

    /**
     * Cached agent path after successful discovery.
     */
    protected Optional<Path> agentPath = Optional.empty();

    /**
     * Flag indicating if discovery has been attempted.
     */
    protected boolean discoveryAttempted = false;

    /**
     * Discovers the ByteHot agent JAR using multiple strategies.
     * Caches the result for subsequent calls.
     * 
     * @return true if agent was successfully discovered and validated, false otherwise
     */
    public boolean discoverAgent() {
        if (discoveryAttempted && agentPath.isPresent()) {
            return true;
        }

        discoveryAttempted = true;

        // Strategy 1: Check system property
        agentPath = checkSystemProperty();
        if (agentPath.isPresent()) return true;

        // Strategy 2: Check Maven local repository
        agentPath = checkMavenRepository();
        if (agentPath.isPresent()) return true;

        // Strategy 3: Check Gradle cache
        agentPath = checkGradleCache();
        if (agentPath.isPresent()) return true;

        // Strategy 4: Check project target/build directories
        agentPath = checkProjectDirectories();
        if (agentPath.isPresent()) return true;

        // Strategy 5: Check PATH and common locations
        agentPath = checkCommonLocations();
        return agentPath.isPresent();
    }

    /**
     * Gets the discovered agent path.
     * 
     * @return Optional containing the agent path if discovered, empty otherwise
     */
    public Optional<Path> getAgentPath() {
        return agentPath;
    }

    /**
     * Validates that a JAR file is a valid ByteHot agent.
     * 
     * @param agentJar the path to the potential agent JAR
     * @return true if the JAR is a valid ByteHot agent, false otherwise
     */
    public boolean validateAgent(final Path agentJar) {
        if (agentJar == null || !Files.exists(agentJar)) {
            return false;
        }

        try (JarFile jar = new JarFile(agentJar.toFile())) {
            // Check for required manifest entries
            final Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return false;
            }

            // Verify it has the required Agent-Class attribute
            final String agentClass = manifest.getMainAttributes().getValue("Agent-Class");
            return agentClass != null && !agentClass.trim().isEmpty();

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks system property for agent path override.
     * 
     * @return Optional containing the agent path if found and valid, empty otherwise
     */
    protected Optional<Path> checkSystemProperty() {
        final String agentPathStr = System.getProperty(AGENT_PATH_PROPERTY);
        if (agentPathStr != null) {
            final Path path = Paths.get(agentPathStr);
            if (Files.exists(path) && validateAgent(path)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks Maven local repository for agent JAR.
     * 
     * @return Optional containing the agent path if found and valid, empty otherwise
     */
    protected Optional<Path> checkMavenRepository() {
        final String userHome = System.getProperty("user.home");
        if (userHome == null) return Optional.empty();

        final Path mavenRepo = Paths.get(userHome, ".m2", "repository", "org", "acmsl");
        if (!Files.exists(mavenRepo)) return Optional.empty();

        return findAgentInDirectory(mavenRepo);
    }

    /**
     * Checks Gradle cache for agent JAR.
     * 
     * @return Optional containing the agent path if found and valid, empty otherwise
     */
    protected Optional<Path> checkGradleCache() {
        final String userHome = System.getProperty("user.home");
        if (userHome == null) return Optional.empty();

        final Path gradleCache = Paths.get(userHome, ".gradle", "caches");
        if (!Files.exists(gradleCache)) return Optional.empty();

        return findAgentInDirectory(gradleCache);
    }

    /**
     * Checks project target/build directories for agent JAR.
     * 
     * @return Optional containing the agent path if found and valid, empty otherwise
     */
    protected Optional<Path> checkProjectDirectories() {
        final Path currentDir = Paths.get(System.getProperty("user.dir"));
        
        // Check Maven target directories
        Optional<Path> result = findAgentInDirectory(currentDir.resolve("target"));
        if (result.isPresent()) return result;

        // Check Gradle build directories
        result = findAgentInDirectory(currentDir.resolve("build"));
        if (result.isPresent()) return result;

        // Check parent project structures
        final Path parent = currentDir.getParent();
        if (parent != null) {
            result = findAgentInDirectory(parent);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    /**
     * Checks common system locations for agent JAR.
     * 
     * @return Optional containing the agent path if found and valid, empty otherwise
     */
    protected Optional<Path> checkCommonLocations() {
        // Check current directory
        Optional<Path> result = findAgentInDirectory(Paths.get("."));
        if (result.isPresent()) return result;

        // Check common installation directories
        final String[] commonDirs = {
            "/usr/local/lib/bytehot",
            "/opt/bytehot",
            System.getProperty("java.io.tmpdir")
        };

        for (String dir : commonDirs) {
            final Path path = Paths.get(dir);
            if (Files.exists(path)) {
                result = findAgentInDirectory(path);
                if (result.isPresent()) return result;
            }
        }

        return Optional.empty();
    }

    /**
     * Searches for agent JAR files in a directory and its subdirectories.
     * 
     * @param directory the directory to search
     * @return Optional containing the first valid agent found, empty if none found
     */
    protected Optional<Path> findAgentInDirectory(final Path directory) {
        if (directory == null || !Files.exists(directory) || !Files.isDirectory(directory)) {
            return Optional.empty();
        }

        try (Stream<Path> paths = Files.walk(directory, 3)) { // Limit depth to avoid deep recursion
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().matches("bytehot-application-.*-agent\\.jar"))
                .filter(this::validateAgent)
                .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}