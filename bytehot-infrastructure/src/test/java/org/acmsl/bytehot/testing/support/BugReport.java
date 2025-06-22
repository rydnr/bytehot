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
 * Filename: BugReport.java
 *
 * Author: Claude Code
 *
 * Class name: BugReport
 *
 * Responsibilities:
 *   - Contains the event sequence that reproduces a specific bug
 *   - Provides metadata about the bug for correlation and tracking
 *   - Enables automatic bug reproduction in tests
 *   - Supports bug report serialization and storage
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events that reproduce the bug
 *   - GivenStage: Uses bug reports to set up reproduction scenarios
 *   - EventTestContext: May record bug reports for failed tests
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Contains the event sequence needed to reproduce a specific bug.
 * This revolutionary feature allows bugs to be captured as event sequences
 * and automatically reproduced in tests, making debugging and regression
 * testing significantly more reliable.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class BugReport {

    /**
     * Unique identifier for this bug report
     */
    private final String bugId;

    /**
     * Human-readable description of the bug
     */
    private final String description;

    /**
     * Events that when replayed will reproduce the bug
     */
    private final List<VersionedDomainEvent> reproductionEvents;

    /**
     * When this bug was first reported
     */
    private final Instant reportedAt;

    /**
     * Expected behavior (what should happen)
     */
    private final String expectedBehavior;

    /**
     * Actual behavior (what goes wrong)
     */
    private final String actualBehavior;

    /**
     * Severity level of the bug
     */
    private final BugSeverity severity;

    /**
     * Environment or context where the bug occurs
     */
    private final String environment;

    /**
     * Bug severity levels
     */
    public enum BugSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    /**
     * Creates a new bug report.
     * 
     * @param bugId unique identifier for the bug
     * @param description human-readable description
     * @param reproductionEvents events that reproduce the bug
     * @param expectedBehavior what should happen
     * @param actualBehavior what actually happens
     * @param severity severity level
     * @param environment environment context
     */
    public BugReport(
            String bugId,
            String description,
            List<VersionedDomainEvent> reproductionEvents,
            String expectedBehavior,
            String actualBehavior,
            BugSeverity severity,
            String environment) {
        this.bugId = bugId;
        this.description = description;
        this.reproductionEvents = new ArrayList<>(reproductionEvents);
        this.reportedAt = Instant.now();
        this.expectedBehavior = expectedBehavior;
        this.actualBehavior = actualBehavior;
        this.severity = severity;
        this.environment = environment;
    }

    /**
     * Gets the unique bug identifier.
     * 
     * @return the bug ID
     */
    public String getBugId() {
        return bugId;
    }

    /**
     * Gets the bug description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the events that reproduce this bug.
     * 
     * @return immutable list of reproduction events
     */
    public List<VersionedDomainEvent> getReproductionEvents() {
        return Collections.unmodifiableList(reproductionEvents);
    }

    /**
     * Gets when this bug was reported.
     * 
     * @return the report timestamp
     */
    public Instant getReportedAt() {
        return reportedAt;
    }

    /**
     * Gets the expected behavior.
     * 
     * @return what should happen
     */
    public String getExpectedBehavior() {
        return expectedBehavior;
    }

    /**
     * Gets the actual behavior.
     * 
     * @return what actually happens
     */
    public String getActualBehavior() {
        return actualBehavior;
    }

    /**
     * Gets the bug severity.
     * 
     * @return the severity level
     */
    public BugSeverity getSeverity() {
        return severity;
    }

    /**
     * Gets the environment context.
     * 
     * @return the environment description
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Creates a builder for constructing bug reports.
     * 
     * @param bugId the unique bug identifier
     * @return a new builder instance
     */
    public static Builder builder(String bugId) {
        return new Builder(bugId);
    }

    /**
     * Builder for creating bug reports with a fluent interface.
     */
    public static class Builder {
        private final String bugId;
        private String description = "";
        private final List<VersionedDomainEvent> reproductionEvents = new ArrayList<>();
        private String expectedBehavior = "";
        private String actualBehavior = "";
        private BugSeverity severity = BugSeverity.MEDIUM;
        private String environment = "unknown";

        Builder(String bugId) {
            this.bugId = bugId;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addReproductionEvent(VersionedDomainEvent event) {
            this.reproductionEvents.add(event);
            return this;
        }

        public Builder reproductionEvents(List<VersionedDomainEvent> events) {
            this.reproductionEvents.clear();
            this.reproductionEvents.addAll(events);
            return this;
        }

        public Builder expectedBehavior(String expectedBehavior) {
            this.expectedBehavior = expectedBehavior;
            return this;
        }

        public Builder actualBehavior(String actualBehavior) {
            this.actualBehavior = actualBehavior;
            return this;
        }

        public Builder severity(BugSeverity severity) {
            this.severity = severity;
            return this;
        }

        public Builder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public BugReport build() {
            return new BugReport(
                bugId,
                description,
                reproductionEvents,
                expectedBehavior,
                actualBehavior,
                severity,
                environment
            );
        }
    }

    /**
     * Converts this bug report to a summary string.
     * 
     * @return formatted summary of the bug report
     */
    public String getSummary() {
        return String.format(
            "Bug %s [%s]: %s\nExpected: %s\nActual: %s\nEvents: %d\nEnvironment: %s",
            bugId,
            severity,
            description,
            expectedBehavior,
            actualBehavior,
            reproductionEvents.size(),
            environment
        );
    }

    @Override
    public String toString() {
        return String.format("BugReport[%s: %s]", bugId, description);
    }
}