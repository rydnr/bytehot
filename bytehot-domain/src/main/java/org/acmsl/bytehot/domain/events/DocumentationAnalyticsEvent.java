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
 * Filename: DocumentationAnalyticsEvent.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationAnalyticsEvent
 *
 * Responsibilities:
 *   - Represent aggregated documentation system analytics
 *   - Track patterns and trends in documentation usage
 *   - Enable data-driven documentation system optimization
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Event triggered for periodic documentation system analytics reporting.
 * This event aggregates metrics from documentation introspection events
 * and provides insights for system optimization.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class DocumentationAnalyticsEvent 
    extends AbstractVersionedDomainEvent {

    /**
     * Time period covered by this analytics report.
     */
    @Getter
    private final Duration reportingPeriod;

    /**
     * Total number of documentation requests processed.
     */
    @Getter
    private final long totalRequests;

    /**
     * Cache hit rate (0.0 to 1.0).
     */
    @Getter
    private final double cacheHitRate;

    /**
     * Average documentation generation time.
     */
    @Getter
    private final Duration averageGenerationTime;

    /**
     * Flow detection success rate (0.0 to 1.0).
     */
    @Getter
    private final double flowDetectionSuccessRate;

    /**
     * Distribution of documentation types requested.
     */
    @Getter
    private final Map<String, Long> documentationTypeDistribution;

    /**
     * Distribution of generation strategies used.
     */
    @Getter
    private final Map<String, Long> strategyDistribution;

    /**
     * Top requested classes for documentation.
     */
    @Getter
    private final Map<String, Long> topRequestedClasses;

    /**
     * Performance metrics by category.
     */
    @Getter
    private final Map<String, Double> performanceMetrics;

    /**
     * Number of cross-references discovered.
     */
    @Getter
    private final long crossReferencesDiscovered;

    /**
     * Timestamp when the analytics were compiled.
     */
    @Getter
    private final Instant compiledAt;

    /**
     * Creates a new DocumentationAnalyticsEvent.
     * @param metadata event metadata
     * @param reportingPeriod time period covered by this report
     * @param totalRequests total number of documentation requests processed
     * @param cacheHitRate cache hit rate (0.0 to 1.0)
     * @param averageGenerationTime average documentation generation time
     * @param flowDetectionSuccessRate flow detection success rate (0.0 to 1.0)
     * @param documentationTypeDistribution distribution of documentation types
     * @param strategyDistribution distribution of generation strategies used
     * @param topRequestedClasses top requested classes for documentation
     * @param performanceMetrics performance metrics by category
     * @param crossReferencesDiscovered number of cross-references discovered
     * @param compiledAt timestamp when the analytics were compiled
     */
    public DocumentationAnalyticsEvent(
        final EventMetadata metadata,
        final Duration reportingPeriod,
        final long totalRequests,
        final double cacheHitRate,
        final Duration averageGenerationTime,
        final double flowDetectionSuccessRate,
        final Map<String, Long> documentationTypeDistribution,
        final Map<String, Long> strategyDistribution,
        final Map<String, Long> topRequestedClasses,
        final Map<String, Double> performanceMetrics,
        final long crossReferencesDiscovered,
        final Instant compiledAt
    ) {
        super(metadata);
        this.reportingPeriod = reportingPeriod;
        this.totalRequests = totalRequests;
        this.cacheHitRate = cacheHitRate;
        this.averageGenerationTime = averageGenerationTime;
        this.flowDetectionSuccessRate = flowDetectionSuccessRate;
        this.documentationTypeDistribution = Map.copyOf(documentationTypeDistribution);
        this.strategyDistribution = Map.copyOf(strategyDistribution);
        this.topRequestedClasses = Map.copyOf(topRequestedClasses);
        this.performanceMetrics = Map.copyOf(performanceMetrics);
        this.crossReferencesDiscovered = crossReferencesDiscovered;
        this.compiledAt = compiledAt;
    }

    /**
     * Factory method for creating a periodic analytics event.
     * @param reportingPeriod time period covered by this report
     * @param totalRequests total number of documentation requests processed
     * @param cacheHitRate cache hit rate (0.0 to 1.0)
     * @param averageGenerationTime average documentation generation time
     * @param flowDetectionSuccessRate flow detection success rate
     * @param documentationTypeDistribution distribution of documentation types
     * @param strategyDistribution distribution of generation strategies
     * @param topRequestedClasses top requested classes for documentation
     * @param performanceMetrics performance metrics by category
     * @param crossReferencesDiscovered number of cross-references discovered
     * @return new DocumentationAnalyticsEvent
     */
    public static DocumentationAnalyticsEvent forPeriodicReport(
        final Duration reportingPeriod,
        final long totalRequests,
        final double cacheHitRate,
        final Duration averageGenerationTime,
        final double flowDetectionSuccessRate,
        final Map<String, Long> documentationTypeDistribution,
        final Map<String, Long> strategyDistribution,
        final Map<String, Long> topRequestedClasses,
        final Map<String, Double> performanceMetrics,
        final long crossReferencesDiscovered
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "documentation-analytics", 
            "report-" + System.currentTimeMillis()
        );
        
        return new DocumentationAnalyticsEvent(
            metadata,
            reportingPeriod,
            totalRequests,
            cacheHitRate,
            averageGenerationTime,
            flowDetectionSuccessRate,
            documentationTypeDistribution,
            strategyDistribution,
            topRequestedClasses,
            performanceMetrics,
            crossReferencesDiscovered,
            Instant.now()
        );
    }

    /**
     * Checks if the cache performance is excellent.
     * @return true if cache hit rate is >= 0.9
     */
    public boolean isExcellentCachePerformance() {
        return cacheHitRate >= 0.9;
    }

    /**
     * Checks if the flow detection is performing well.
     * @return true if flow detection success rate is >= 0.8
     */
    public boolean isFlowDetectionPerformingWell() {
        return flowDetectionSuccessRate >= 0.8;
    }

    /**
     * Checks if the generation time is fast on average.
     * @return true if average generation time is less than 50ms
     */
    public boolean isFastGenerationPerformance() {
        return averageGenerationTime.toMillis() < 50;
    }

    /**
     * Gets the overall system health score (0.0 to 1.0).
     * @return health score based on cache performance, flow detection, and generation speed
     */
    public double getSystemHealthScore() {
        double cacheScore = Math.min(cacheHitRate, 1.0);
        double flowScore = Math.min(flowDetectionSuccessRate, 1.0);
        double speedScore = Math.min(1.0, 100.0 / Math.max(averageGenerationTime.toMillis(), 1.0));
        
        return (cacheScore + flowScore + speedScore) / 3.0;
    }

    /**
     * Gets the system health category.
     * @return health category (EXCELLENT, GOOD, FAIR, POOR)
     */
    public String getSystemHealthCategory() {
        final double healthScore = getSystemHealthScore();
        if (healthScore >= 0.9) {
            return "EXCELLENT";
        } else if (healthScore >= 0.7) {
            return "GOOD";
        } else if (healthScore >= 0.5) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }

    /**
     * Checks if there's high documentation activity.
     * @return true if requests per hour exceed 100
     */
    public boolean isHighDocumentationActivity() {
        final double requestsPerHour = totalRequests / Math.max(reportingPeriod.toHours(), 1.0);
        return requestsPerHour > 100;
    }
}