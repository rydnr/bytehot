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
 * Filename: BugReproductionStage.java
 *
 * Author: Claude Code
 *
 * Class name: BugReproductionStage
 *
 * Responsibilities:
 *   - Orchestrate the reproduction of specific bugs using event sequences
 *   - Combine Given/When/Then stages for bug-specific scenarios
 *   - Provide specialized assertions for bug verification
 *   - Enable systematic regression testing of fixed bugs
 *
 * Collaborators:
 *   - BugReport: Contains the reproduction sequence and metadata
 *   - GivenStage: Sets up the initial state for bug reproduction
 *   - WhenStage: Triggers the problematic event that caused the bug
 *   - ThenStage: Verifies that the bug is reproduced or fixed
 */
package org.acmsl.bytehot.testing.support;

import org.acmsl.bytehot.application.ByteHotApplication;
import org.acmsl.bytehot.domain.EventStorePort;
import org.acmsl.bytehot.testing.stages.GivenStage;
import org.acmsl.bytehot.testing.stages.ThenStage;
import org.acmsl.bytehot.testing.stages.WhenStage;

import org.acmsl.bytehot.domain.VersionedDomainEvent;
import org.acmsl.commons.patterns.DomainEvent;

import java.util.List;

/**
 * Specialized stage for reproducing bugs using their captured event sequences.
 * This revolutionary approach allows bugs to be automatically reproduced
 * and verified, making debugging and regression testing significantly more reliable.
 * 
 * @author Claude Code
 * @since 2025-06-17
 */
public class BugReproductionStage {

    /**
     * The bug report containing reproduction instructions
     */
    private final BugReport bugReport;

    /**
     * Test context for tracking the reproduction
     */
    private final EventTestContext context;

    /**
     * Event store for persistence during reproduction
     */
    private final EventStorePort eventStore;

    /**
     * Application for processing events
     */
    private final ByteHotApplication application;

    /**
     * Creates a new bug reproduction stage.
     * 
     * @param bugReport the bug report to reproduce
     * @param context the test context for tracking
     * @param eventStore the event store for persistence
     * @param application the application for event processing
     */
    public BugReproductionStage(
            BugReport bugReport,
            EventTestContext context,
            EventStorePort eventStore,
            ByteHotApplication application) {
        this.bugReport = bugReport;
        this.context = context;
        this.eventStore = eventStore;
        this.application = application;
    }

    /**
     * Reproduces the bug by replaying its event sequence.
     * This automatically sets up the Given state, triggers the When event,
     * and prepares for Then assertions.
     * 
     * @return the Then stage for verification
     */
    public ThenStage reproduce() {
        // Set up the bug context in the Given stage
        GivenStage given = new GivenStage(context, eventStore);
        given.bugContext(bugReport);

        // The last event in the sequence is typically the trigger
        if (!bugReport.getReproductionEvents().isEmpty()) {
            final List<VersionedDomainEvent> events = bugReport.getReproductionEvents();
            final VersionedDomainEvent triggerEvent = events.get(events.size() - 1);
            
            // Execute the trigger event
            WhenStage when = new WhenStage(context, application);
            when.event(triggerEvent);
        }

        // Return Then stage for verification
        return new ThenStage(context);
    }

    /**
     * Reproduces the bug and verifies it still occurs (for regression testing).
     * This is useful when you want to verify that a bug is reproducible
     * before implementing a fix.
     * 
     * @return the Then stage for verification
     */
    public ThenStage reproduceBugBehavior() {
        ThenStage then = reproduce();

        // Add bug-specific verification that the problematic behavior occurs
        // This is where we would check that the bug actually manifests
        return then.verify(
            ctx -> {
                // Check if the bug behavior occurred
                // For now, this is a placeholder - in practice this would
                // check for the specific error condition described in the bug report
                return ctx.getTestException().isPresent() || 
                       ctx.getAllEvents().stream()
                           .anyMatch(event -> event.toString().contains("Error"));
            },
            "Bug behavior is reproduced: " + bugReport.getActualBehavior()
        );
    }

    /**
     * Reproduces the bug and verifies it has been fixed.
     * This is useful for regression testing after implementing a fix.
     * 
     * @return the Then stage for verification
     */
    public ThenStage verifyBugFixed() {
        ThenStage then = reproduce();

        // Add verification that the bug no longer occurs
        return then.verify(
            ctx -> {
                // Check that the expected behavior occurs instead of the bug
                // For now, this is a placeholder - in practice this would
                // verify the specific fix described in the expected behavior
                return ctx.getTestException().isEmpty() && 
                       ctx.hasResults();
            },
            "Bug is fixed - expected behavior occurs: " + bugReport.getExpectedBehavior()
        );
    }

    /**
     * Gets the bug report being reproduced.
     * 
     * @return the bug report
     */
    public BugReport getBugReport() {
        return bugReport;
    }

    /**
     * Gets a summary of the bug reproduction setup.
     * 
     * @return formatted summary of the reproduction
     */
    public String getReproductionSummary() {
        return String.format(
            "Reproducing Bug %s: %s\n" +
            "Events to replay: %d\n" +
            "Expected: %s\n" +
            "Actual: %s",
            bugReport.getBugId(),
            bugReport.getDescription(),
            bugReport.getReproductionEvents().size(),
            bugReport.getExpectedBehavior(),
            bugReport.getActualBehavior()
        );
    }
}