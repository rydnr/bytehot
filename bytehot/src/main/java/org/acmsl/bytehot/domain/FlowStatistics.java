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
 * Filename: FlowStatistics.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowStatistics
 *
 * Responsibilities:
 *   - Provide statistical information about stored flows
 *
 * Collaborators:
 *   - FlowDetectionPort: Returns statistics from storage operations
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.dao.ValueObject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * Statistical information about flows in the system.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class FlowStatistics implements ValueObject {

    /**
     * Total number of flows stored.
     */
    @Getter
    private final int totalFlows;

    /**
     * Average confidence level across all flows.
     */
    @Getter
    private final double averageConfidence;

    /**
     * Highest confidence level among all flows.
     */
    @Getter
    private final double highestConfidence;

    /**
     * Lowest confidence level among all flows.
     */
    @Getter
    private final double lowestConfidence;

    /**
     * Distribution of flows by confidence ranges.
     */
    @Getter
    private final Map<String, Integer> confidenceDistribution;

    /**
     * Average number of events per flow.
     */
    @Getter
    private final double averageEventCount;

    /**
     * Most common flow patterns by name.
     */
    @Getter
    private final Map<String, Integer> flowPatternCounts;

    /**
     * Creates empty statistics for when no flows exist.
     * @return FlowStatistics with all zero values
     */
    public static FlowStatistics empty() {
        return FlowStatistics.builder()
            .totalFlows(0)
            .averageConfidence(0.0)
            .highestConfidence(0.0)
            .lowestConfidence(0.0)
            .confidenceDistribution(Map.of())
            .averageEventCount(0.0)
            .flowPatternCounts(Map.of())
            .build();
    }

    /**
     * Gets the percentage of high-confidence flows (confidence >= 0.8).
     * @return Percentage of high-confidence flows
     */
    public double getHighConfidencePercentage() {
        if (totalFlows == 0) {
            return 0.0;
        }
        
        int highConfidenceFlows = confidenceDistribution.getOrDefault("0.8-1.0", 0);
        return (double) highConfidenceFlows / totalFlows * 100.0;
    }

    /**
     * Gets the most common flow pattern name.
     * @return The name of the most common flow pattern, or "N/A" if none
     */
    public String getMostCommonFlowPattern() {
        return flowPatternCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    /**
     * Checks if the statistics indicate a healthy flow detection system.
     * @return true if statistics indicate good performance
     */
    public boolean isHealthy() {
        return totalFlows > 0 
            && averageConfidence >= 0.7 
            && getHighConfidencePercentage() >= 50.0;
    }
}