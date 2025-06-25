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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Flow statistics domain object providing analysis and insights about system flows.
 * Encapsulates statistical calculations and performance assessment logic.
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
    protected final int totalFlows;

    /**
     * Average confidence level across all flows.
     */
    @Getter
    protected final double averageConfidence;

    /**
     * Highest confidence level among all flows.
     */
    @Getter
    protected final double highestConfidence;

    /**
     * Lowest confidence level among all flows.
     */
    @Getter
    protected final double lowestConfidence;

    /**
     * Distribution of flows by confidence ranges.
     */
    @Getter
    protected final Map<String, Integer> confidenceDistribution;

    /**
     * Average number of events per flow.
     */
    @Getter
    protected final double averageEventCount;

    /**
     * Most common flow patterns by name.
     */
    @Getter
    protected final Map<String, Integer> flowPatternCounts;

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

    /**
     * Generates performance recommendations based on current statistics.
     * @return List of actionable recommendations
     */
    public List<String> generateRecommendations() {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (totalFlows == 0) {
            recommendations.add("No flows detected. Verify flow detection configuration and event sources.");
            return recommendations;
        }
        
        if (averageConfidence < 0.5) {
            recommendations.add("Low average confidence detected. Review flow patterns and consider tuning detection algorithms.");
        } else if (averageConfidence < 0.7) {
            recommendations.add("Moderate confidence levels. Consider refining flow detection criteria for better accuracy.");
        }
        
        if (getHighConfidencePercentage() < 30.0) {
            recommendations.add("Few high-confidence flows detected. Review detection thresholds and pattern matching rules.");
        }
        
        if (averageEventCount < 3.0) {
            recommendations.add("Flows have few events on average. Consider expanding detection window or reviewing event capture.");
        } else if (averageEventCount > 50.0) {
            recommendations.add("Flows contain many events. Consider segmenting long flows or adjusting detection boundaries.");
        }
        
        String mostCommon = getMostCommonFlowPattern();
        if (!"N/A".equals(mostCommon) && flowPatternCounts.get(mostCommon) > totalFlows * 0.8) {
            recommendations.add("One pattern dominates (" + mostCommon + "). Verify detection diversity and pattern coverage.");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Flow detection appears to be performing well. Monitor trends for optimization opportunities.");
        }
        
        return recommendations;
    }

    /**
     * Calculates the confidence standard deviation for variability analysis.
     * @return Standard deviation of confidence levels
     */
    public double getConfidenceStandardDeviation() {
        if (totalFlows <= 1) {
            return 0.0;
        }
        
        // Estimate standard deviation from distribution ranges
        double variance = 0.0;
        int totalCount = 0;
        
        for (Map.Entry<String, Integer> entry : confidenceDistribution.entrySet()) {
            String range = entry.getKey();
            int count = entry.getValue();
            double rangeMidpoint = parseRangeMidpoint(range);
            
            variance += count * Math.pow(rangeMidpoint - averageConfidence, 2);
            totalCount += count;
        }
        
        return totalCount > 1 ? Math.sqrt(variance / (totalCount - 1)) : 0.0;
    }

    /**
     * Determines the overall quality grade for flow detection performance.
     * @return Quality grade (A, B, C, D, F)
     */
    public String getQualityGrade() {
        if (totalFlows == 0) {
            return "F";
        }
        
        double score = 0.0;
        
        // Average confidence (40% weight)
        if (averageConfidence >= 0.9) score += 40;
        else if (averageConfidence >= 0.8) score += 35;
        else if (averageConfidence >= 0.7) score += 30;
        else if (averageConfidence >= 0.6) score += 20;
        else if (averageConfidence >= 0.5) score += 10;
        
        // High confidence percentage (30% weight)
        double highConfPct = getHighConfidencePercentage();
        if (highConfPct >= 80) score += 30;
        else if (highConfPct >= 60) score += 25;
        else if (highConfPct >= 40) score += 20;
        else if (highConfPct >= 20) score += 10;
        
        // Flow diversity (20% weight)
        int uniquePatterns = flowPatternCounts.size();
        if (uniquePatterns >= 5) score += 20;
        else if (uniquePatterns >= 3) score += 15;
        else if (uniquePatterns >= 2) score += 10;
        else if (uniquePatterns >= 1) score += 5;
        
        // Average event count reasonableness (10% weight)
        if (averageEventCount >= 5 && averageEventCount <= 20) score += 10;
        else if (averageEventCount >= 3 && averageEventCount <= 30) score += 7;
        else if (averageEventCount >= 2) score += 5;
        
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }

    /**
     * Creates a comprehensive analysis report of the flow statistics.
     * @return Detailed analysis report
     */
    public String generateAnalysisReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== Flow Detection Statistics Analysis ===\n\n");
        
        // Overview
        report.append("Overview:\n");
        report.append(String.format("- Total Flows: %d\n", totalFlows));
        report.append(String.format("- Quality Grade: %s\n", getQualityGrade()));
        report.append(String.format("- System Health: %s\n\n", isHealthy() ? "HEALTHY" : "NEEDS ATTENTION"));
        
        // Confidence Analysis
        report.append("Confidence Analysis:\n");
        report.append(String.format("- Average: %.2f\n", averageConfidence));
        report.append(String.format("- Range: %.2f - %.2f\n", lowestConfidence, highestConfidence));
        report.append(String.format("- High Confidence Flows: %.1f%%\n", getHighConfidencePercentage()));
        report.append(String.format("- Standard Deviation: %.3f\n\n", getConfidenceStandardDeviation()));
        
        // Flow Patterns
        report.append("Flow Patterns:\n");
        report.append(String.format("- Unique Patterns: %d\n", flowPatternCounts.size()));
        report.append(String.format("- Most Common: %s\n", getMostCommonFlowPattern()));
        report.append(String.format("- Average Events per Flow: %.1f\n\n", averageEventCount));
        
        // Distribution
        report.append("Confidence Distribution:\n");
        confidenceDistribution.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> report.append(String.format("- %s: %d flows\n", entry.getKey(), entry.getValue())));
        
        // Recommendations
        report.append("\nRecommendations:\n");
        List<String> recommendations = generateRecommendations();
        for (int i = 0; i < recommendations.size(); i++) {
            report.append(String.format("%d. %s\n", i + 1, recommendations.get(i)));
        }
        
        return report.toString();
    }

    /**
     * Determines if the system is performing above baseline expectations.
     * @return true if performance exceeds baseline thresholds
     */
    public boolean isAboveBaseline() {
        return totalFlows >= 10 
            && averageConfidence >= 0.6 
            && getHighConfidencePercentage() >= 25.0
            && flowPatternCounts.size() >= 2;
    }

    /**
     * Calculates trend indicators compared to a previous statistics snapshot.
     * @param previous Previous statistics for comparison
     * @return Map of trend indicators (improving, degrading, stable)
     */
    public Map<String, String> calculateTrends(final FlowStatistics previous) {
        Map<String, String> trends = new java.util.HashMap<>();
        
        if (previous == null || previous.totalFlows == 0) {
            trends.put("overall", "initial");
            return trends;
        }
        
        // Total flows trend
        double flowGrowth = (double) (totalFlows - previous.totalFlows) / previous.totalFlows;
        trends.put("totalFlows", flowGrowth > 0.1 ? "growing" : flowGrowth < -0.1 ? "declining" : "stable");
        
        // Confidence trend
        double confDiff = averageConfidence - previous.averageConfidence;
        trends.put("confidence", confDiff > 0.05 ? "improving" : confDiff < -0.05 ? "degrading" : "stable");
        
        // Pattern diversity trend
        int patternDiff = flowPatternCounts.size() - previous.flowPatternCounts.size();
        trends.put("diversity", patternDiff > 0 ? "increasing" : patternDiff < 0 ? "decreasing" : "stable");
        
        // Overall trend
        long improvingCount = trends.values().stream()
            .mapToLong(value -> ("improving".equals(value) || "growing".equals(value) || "increasing".equals(value)) ? 1 : 0)
            .sum();
        long degradingCount = trends.values().stream()
            .mapToLong(value -> ("degrading".equals(value) || "declining".equals(value) || "decreasing".equals(value)) ? 1 : 0)
            .sum();
        
        if (improvingCount > degradingCount) {
            trends.put("overall", "improving");
        } else if (degradingCount > improvingCount) {
            trends.put("overall", "degrading");
        } else {
            trends.put("overall", "stable");
        }
        
        return trends;
    }

    /**
     * Parses the midpoint value from a confidence range string.
     * @param range Range string like "0.8-1.0"
     * @return Midpoint value of the range
     */
    protected double parseRangeMidpoint(final String range) {
        try {
            if (range.contains("-")) {
                String[] parts = range.split("-");
                double lower = Double.parseDouble(parts[0]);
                double upper = Double.parseDouble(parts[1]);
                return (lower + upper) / 2.0;
            } else {
                return Double.parseDouble(range);
            }
        } catch (NumberFormatException e) {
            return averageConfidence; // Fallback to average
        }
    }
}