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
 * Filename: FlowAnalysisRequested.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowAnalysisRequested
 *
 * Responsibilities:
 *   - Represent request for flow analysis on event sequences
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events to be analyzed for flow patterns
 *   - UserId: User requesting the analysis
 *   - AnalysisId: Unique identifier for the analysis request
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.AnalysisId;
import org.acmsl.bytehot.domain.TimeWindow;
import org.acmsl.bytehot.domain.UserId;
import org.acmsl.commons.patterns.DomainEvent;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Event requesting analysis of event sequences for flow detection.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class FlowAnalysisRequested implements DomainEvent {

    /**
     * Unique identifier for the analysis request.
     */
    @Getter
    private final AnalysisId analysisId;

    /**
     * Events to be analyzed for flow detection.
     */
    @Getter
    private final List<org.acmsl.bytehot.domain.VersionedDomainEvent> eventsToAnalyze;

    /**
     * Optional time window for analysis.
     */
    @Getter
    private final Optional<TimeWindow> analysisWindow;

    /**
     * Minimum confidence level required for flow detection.
     */
    @Getter
    private final double minimumConfidence;

    /**
     * User who requested the analysis.
     */
    @Getter
    private final UserId requestedBy;

    /**
     * Timestamp when analysis was requested.
     */
    @Getter
    private final Instant requestedAt;

    /**
     * Previous event in the event chain (if any).
     */
    @Getter
    private final Optional<DomainEvent> previousEvent;

}