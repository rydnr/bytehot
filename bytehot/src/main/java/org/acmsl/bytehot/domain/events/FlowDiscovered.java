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
 * Filename: FlowDiscovered.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: FlowDiscovered
 *
 * Responsibilities:
 *   - Represent successful discovery of a business flow from event analysis
 *
 * Collaborators:
 *   - FlowAnalysisRequested: The original analysis request
 *   - Flow: The discovered business flow
 *   - VersionedDomainEvent: Events that triggered the flow detection
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.Flow;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Event indicating a new business flow has been discovered.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class FlowDiscovered implements DomainResponseEvent<FlowAnalysisRequested> {

    /**
     * The original analysis request.
     */
    @Getter
    private final FlowAnalysisRequested originalEvent;

    /**
     * The discovered flow.
     */
    @Getter
    private final Flow discoveredFlow;

    /**
     * Events that triggered the flow detection.
     */
    @Getter
    private final List<org.acmsl.bytehot.domain.VersionedDomainEvent> triggeringEvents;

    /**
     * Confidence level of the discovery.
     */
    @Getter
    private final double confidence;

    /**
     * Timestamp when flow was discovered.
     */
    @Getter
    private final Instant discoveredAt;

    @Override
    public FlowAnalysisRequested getPreceding() {
        return originalEvent;
    }
}