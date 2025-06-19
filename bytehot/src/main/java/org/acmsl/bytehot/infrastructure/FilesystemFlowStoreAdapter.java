/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/******************************************************************************
 *
 * Filename: FilesystemFlowStoreAdapter.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FilesystemFlowStoreAdapter
 *
 * Responsibilities:
 *   - Implement filesystem-based storage for discovered flows
 *   - Provide flow detection capabilities using stored patterns
 *   - Serialize and deserialize flows to/from JSON format
 *
 * Collaborators:
 *   - FlowDetectionPort: Interface this adapter implements
 *   - Flow: Domain flows to be stored and retrieved
 *   - FlowDetector: Uses this adapter for flow pattern matching
 */
package org.acmsl.bytehot.infrastructure;

import org.acmsl.bytehot.domain.Flow;
import org.acmsl.bytehot.domain.FlowDetector;
import org.acmsl.bytehot.domain.FlowId;
import org.acmsl.bytehot.domain.FlowSearchCriteria;
import org.acmsl.bytehot.domain.FlowStatistics;
import org.acmsl.bytehot.domain.FlowStorageResult;
import org.acmsl.bytehot.domain.VersionedDomainEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Filesystem-based adapter for storing and retrieving flows.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
public final class FilesystemFlowStoreAdapter implements FlowDetectionPort {

    @NonNull
    private final Path flowStorePath;
    @NonNull
    private final ObjectMapper objectMapper;

    /**
     * Default constructor that sets up filesystem storage in user's home directory.
     */
    public FilesystemFlowStoreAdapter() {
        this.flowStorePath = Paths.get(System.getProperty("user.home"), ".bytehot", "flows");
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        
        // Ensure storage directory exists
        try {
            Files.createDirectories(flowStorePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create flow storage directory: " + flowStorePath, e);
        }
    }

    /**
     * Constructor with custom storage path.
     * @param flowStorePath Custom path for flow storage
     */
    public FilesystemFlowStoreAdapter(@NonNull final Path flowStorePath) {
        this.flowStorePath = flowStorePath;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        
        try {
            Files.createDirectories(flowStorePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create flow storage directory: " + flowStorePath, e);
        }
    }

    @Override
    @NonNull
    public CompletableFuture<List<Flow>> detectFlows(@NonNull final List<VersionedDomainEvent> events) {
        return CompletableFuture.supplyAsync(() -> {
            // Use the FlowDetector to analyze events against known patterns
            List<Flow> detectedFlows = new ArrayList<>();
            
            // This is a simplified implementation that uses the pre-defined patterns
            // In a more sophisticated implementation, we would also load custom patterns
            // from the filesystem and apply machine learning techniques
            
            if (events.isEmpty()) {
                return detectedFlows;
            }
            
            // Get known patterns from storage
            List<Flow> storedPatterns = getAllFlows().join();
            
            // For each pattern, check if the events match
            for (Flow pattern : storedPatterns) {
                if (pattern.matchesByName(events.stream()
                    .map(VersionedDomainEvent::getEventType)
                    .collect(Collectors.toList()))) {
                    detectedFlows.add(pattern);
                }
            }
            
            return detectedFlows;
        });
    }

    @Override
    @NonNull
    public CompletableFuture<FlowStorageResult> storeFlow(@NonNull final Flow flow) {
        return CompletableFuture.supplyAsync(() -> {
            if (flow == null || !flow.isValid()) {
                return FlowStorageResult.failure(
                    flow != null ? flow.getFlowId() : FlowId.random(),
                    "Invalid flow provided for storage"
                );
            }
            
            try {
                Path flowFile = flowStorePath.resolve(flow.getFlowId().getValue() + ".json");
                JsonFlow jsonFlow = JsonFlow.fromDomain(flow);
                String jsonContent = objectMapper.writeValueAsString(jsonFlow);
                Files.writeString(flowFile, jsonContent);
                
                return FlowStorageResult.success(
                    flow.getFlowId(),
                    "Flow stored successfully at: " + flowFile.toString()
                );
            } catch (IOException e) {
                return FlowStorageResult.failure(
                    flow.getFlowId(),
                    "Failed to store flow: " + e.getMessage(),
                    e.getClass().getSimpleName() + " occurred during file write"
                );
            }
        });
    }

    @Override
    @NonNull
    public CompletableFuture<List<Flow>> getAllFlows() {
        return CompletableFuture.supplyAsync(() -> {
            List<Flow> flows = new ArrayList<>();
            
            try {
                if (!Files.exists(flowStorePath)) {
                    return flows;
                }
                
                try (Stream<Path> files = Files.list(flowStorePath)) {
                    flows = files
                        .filter(file -> file.toString().endsWith(".json"))
                        .map(this::loadFlowFromFile)
                        .filter(flow -> flow != null)
                        .collect(Collectors.toList());
                }
            } catch (IOException e) {
                // Log error but return empty list rather than failing
                System.err.println("Error reading flows from storage: " + e.getMessage());
            }
            
            return flows;
        });
    }

    @Override
    @NonNull
    public CompletableFuture<List<Flow>> searchFlows(@NonNull final FlowSearchCriteria criteria) {
        return getAllFlows().thenApply(flows -> 
            flows.stream()
                .filter(criteria::matches)
                .collect(Collectors.toList())
        );
    }

    @Override
    @NonNull
    public CompletableFuture<List<Flow>> getFlowsByConfidence(final double minimumConfidence) {
        return getAllFlows().thenApply(flows ->
            flows.stream()
                .filter(flow -> flow.getConfidence() >= minimumConfidence)
                .collect(Collectors.toList())
        );
    }

    @Override
    @NonNull
    public CompletableFuture<FlowStorageResult> deleteFlow(@NonNull final FlowId flowId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path flowFile = flowStorePath.resolve(flowId.getValue() + ".json");
                
                if (!Files.exists(flowFile)) {
                    return FlowStorageResult.failure(
                        flowId,
                        "Flow file not found: " + flowFile.toString()
                    );
                }
                
                Files.delete(flowFile);
                return FlowStorageResult.success(
                    flowId,
                    "Flow deleted successfully: " + flowFile.toString()
                );
            } catch (IOException e) {
                return FlowStorageResult.failure(
                    flowId,
                    "Failed to delete flow: " + e.getMessage()
                );
            }
        });
    }

    @Override
    @NonNull
    public CompletableFuture<FlowStorageResult> updateFlow(@NonNull final Flow flow) {
        // For filesystem storage, update is the same as store (overwrite)
        return storeFlow(flow);
    }

    @Override
    @NonNull
    public CompletableFuture<FlowStatistics> getFlowStatistics() {
        return getAllFlows().thenApply(flows -> {
            if (flows.isEmpty()) {
                return FlowStatistics.empty();
            }
            
            // Calculate statistics
            double totalConfidence = flows.stream()
                .mapToDouble(Flow::getConfidence)
                .sum();
            
            double averageConfidence = totalConfidence / flows.size();
            
            double highestConfidence = flows.stream()
                .mapToDouble(Flow::getConfidence)
                .max()
                .orElse(0.0);
            
            double lowestConfidence = flows.stream()
                .mapToDouble(Flow::getConfidence)
                .min()
                .orElse(0.0);
            
            // Confidence distribution
            Map<String, Integer> confidenceDistribution = new HashMap<>();
            confidenceDistribution.put("0.0-0.2", (int) flows.stream().mapToDouble(Flow::getConfidence).filter(c -> c < 0.2).count());
            confidenceDistribution.put("0.2-0.4", (int) flows.stream().mapToDouble(Flow::getConfidence).filter(c -> c >= 0.2 && c < 0.4).count());
            confidenceDistribution.put("0.4-0.6", (int) flows.stream().mapToDouble(Flow::getConfidence).filter(c -> c >= 0.4 && c < 0.6).count());
            confidenceDistribution.put("0.6-0.8", (int) flows.stream().mapToDouble(Flow::getConfidence).filter(c -> c >= 0.6 && c < 0.8).count());
            confidenceDistribution.put("0.8-1.0", (int) flows.stream().mapToDouble(Flow::getConfidence).filter(c -> c >= 0.8).count());
            
            // Average event count
            double averageEventCount = flows.stream()
                .mapToInt(flow -> flow.getEventSequence().size())
                .average()
                .orElse(0.0);
            
            // Flow pattern counts
            Map<String, Integer> flowPatternCounts = flows.stream()
                .collect(Collectors.groupingBy(
                    Flow::getName,
                    Collectors.summingInt(flow -> 1)
                ));
            
            return FlowStatistics.builder()
                .totalFlows(flows.size())
                .averageConfidence(averageConfidence)
                .highestConfidence(highestConfidence)
                .lowestConfidence(lowestConfidence)
                .confidenceDistribution(confidenceDistribution)
                .averageEventCount(averageEventCount)
                .flowPatternCounts(flowPatternCounts)
                .build();
        });
    }

    @Nullable
    private Flow loadFlowFromFile(@NonNull final Path file) {
        try {
            String jsonContent = Files.readString(file);
            JsonFlow jsonFlow = objectMapper.readValue(jsonContent, JsonFlow.class);
            return jsonFlow.toDomain();
        } catch (IOException e) {
            System.err.println("Error loading flow from file " + file + ": " + e.getMessage());
            return null;
        }
    }
}