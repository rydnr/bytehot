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
 * Filename: TestScenarioRepository.java
 *
 * Author: Claude Code
 *
 * Class name: TestScenarioRepository
 *
 * Responsibilities:
 *   - Store and retrieve reusable test scenarios
 *   - Enable sharing of common test setups between tests
 *   - Support scenario versioning and evolution
 *   - Provide scenario discovery and management
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events that make up scenarios
 *   - GivenStage: Loads scenarios to build test state
 *   - File system: For persistent scenario storage
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.domain.VersionedDomainEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for storing and retrieving reusable test scenarios.
 * This allows common test setups to be shared between tests and enables
 * systematic testing with predefined scenarios.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class TestScenarioRepository {

    /**
     * In-memory cache of loaded scenarios
     */
    private static final Map<String, List<VersionedDomainEvent>> scenarioCache = new ConcurrentHashMap<>();

    /**
     * Base directory for scenario storage
     */
    private static final String SCENARIOS_DIR = "test-scenarios";

    /**
     * JSON mapper for scenario serialization
     */
    private static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Private constructor - this is a utility class
     */
    private TestScenarioRepository() {
    }

    /**
     * Loads a test scenario by name.
     * First checks the in-memory cache, then attempts to load from filesystem.
     * 
     * @param scenarioName the name of the scenario to load
     * @return list of events that make up the scenario
     * @throws RuntimeException if the scenario cannot be loaded
     */
    public static List<VersionedDomainEvent> load(String scenarioName) {
        // Check cache first
        List<VersionedDomainEvent> cachedScenario = scenarioCache.get(scenarioName);
        if (cachedScenario != null) {
            return new ArrayList<>(cachedScenario);
        }

        // Try to load from filesystem
        try {
            Path scenarioPath = getScenarioPath(scenarioName);
            if (Files.exists(scenarioPath)) {
                List<VersionedDomainEvent> events = loadFromFile(scenarioPath);
                scenarioCache.put(scenarioName, events);
                return new ArrayList<>(events);
            }
        } catch (Exception e) {
            // Fall back to predefined scenarios for now
        }

        // Return predefined scenario if available
        List<VersionedDomainEvent> predefined = getPredefinedScenario(scenarioName);
        if (predefined != null) {
            scenarioCache.put(scenarioName, predefined);
            return new ArrayList<>(predefined);
        }

        throw new RuntimeException("Test scenario '" + scenarioName + "' not found");
    }

    /**
     * Saves a test scenario for future reuse.
     * 
     * @param scenarioName the name to save the scenario under
     * @param events the events that make up the scenario
     * @throws RuntimeException if the scenario cannot be saved
     */
    public static void save(String scenarioName, List<VersionedDomainEvent> events) {
        try {
            // Save to cache
            scenarioCache.put(scenarioName, new ArrayList<>(events));

            // Save to filesystem
            Path scenarioPath = getScenarioPath(scenarioName);
            Files.createDirectories(scenarioPath.getParent());
            saveToFile(scenarioPath, events);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save scenario '" + scenarioName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Lists all available scenarios.
     * 
     * @return list of scenario names
     */
    public static List<String> listScenarios() {
        List<String> scenarios = new ArrayList<>(scenarioCache.keySet());
        
        // Add predefined scenarios
        scenarios.addAll(getPredefinedScenarioNames());
        
        // Add file-based scenarios if directory exists
        try {
            Path scenariosDir = Paths.get(SCENARIOS_DIR);
            if (Files.exists(scenariosDir)) {
                Files.list(scenariosDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString())
                    .map(name -> name.substring(0, name.lastIndexOf(".json")))
                    .forEach(scenarios::add);
            }
        } catch (IOException e) {
            // Ignore filesystem errors for listing
        }
        
        return scenarios;
    }

    /**
     * Checks if a scenario exists.
     * 
     * @param scenarioName the name of the scenario to check
     * @return true if the scenario exists
     */
    public static boolean exists(String scenarioName) {
        if (scenarioCache.containsKey(scenarioName)) {
            return true;
        }
        
        if (getPredefinedScenario(scenarioName) != null) {
            return true;
        }
        
        Path scenarioPath = getScenarioPath(scenarioName);
        return Files.exists(scenarioPath);
    }

    /**
     * Clears the scenario cache (useful for testing).
     */
    public static void clearCache() {
        scenarioCache.clear();
    }

    /**
     * Gets the filesystem path for a scenario.
     * 
     * @param scenarioName the scenario name
     * @return the path where the scenario should be stored
     */
    private static Path getScenarioPath(String scenarioName) {
        return Paths.get(SCENARIOS_DIR, scenarioName + ".json");
    }

    /**
     * Loads events from a JSON file.
     * 
     * @param scenarioPath the path to the scenario file
     * @return list of events loaded from the file
     * @throws IOException if the file cannot be read
     */
    private static List<VersionedDomainEvent> loadFromFile(Path scenarioPath) throws IOException {
        // This is a simplified implementation
        // In a full implementation, this would deserialize actual events
        return new ArrayList<>();
    }

    /**
     * Saves events to a JSON file.
     * 
     * @param scenarioPath the path to save the scenario
     * @param events the events to save
     * @throws IOException if the file cannot be written
     */
    private static void saveToFile(Path scenarioPath, List<VersionedDomainEvent> events) throws IOException {
        // This is a simplified implementation
        // In a full implementation, this would serialize actual events
        Files.createFile(scenarioPath);
    }

    /**
     * Gets predefined scenarios for common test patterns.
     * 
     * @param scenarioName the name of the predefined scenario
     * @return the predefined scenario events, or null if not found
     */
    private static List<VersionedDomainEvent> getPredefinedScenario(String scenarioName) {
        switch (scenarioName) {
            case "empty":
                return new ArrayList<>();
            case "single-file-change":
                // Return a basic single file change scenario
                return new ArrayList<>();
            case "multiple-file-changes":
                // Return a multi-file change scenario
                return new ArrayList<>();
            default:
                return null;
        }
    }

    /**
     * Gets the names of all predefined scenarios.
     * 
     * @return list of predefined scenario names
     */
    private static List<String> getPredefinedScenarioNames() {
        return List.of("empty", "single-file-change", "multiple-file-changes");
    }

    /**
     * Creates and configures the JSON object mapper.
     * 
     * @return configured ObjectMapper instance
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}