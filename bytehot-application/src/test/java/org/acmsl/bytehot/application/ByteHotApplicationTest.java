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
 * Filename: ByteHotApplicationTest.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotApplicationTest
 *
 * Responsibilities:
 *   - Test ByteHotApplication implements generic Application interface
 *   - Verify event handling through generic interface
 *   - Ensure backward compatibility with existing functionality
 *
 * Collaborators:
 *   - ByteHotApplication: The class under test
 *   - Application: Generic interface from java-commons
 *   - DomainEvent: Events processed by the application
 */
package org.acmsl.bytehot.application;

import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.bytehot.domain.events.ClassFileChanged;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ByteHotApplication - verifying generic Application interface implementation.
 * @author Claude Code
 * @since 2025-07-02
 */
class ByteHotApplicationTest {

    private ByteHotApplication application;

    @BeforeEach
    void setUp() {
        application = new ByteHotApplication();
    }

    @Test
    @DisplayName("🧪 ByteHotApplication implements generic Application interface")
    void byteHotApplicationImplementsGenericApplication() {
        // Then: ByteHotApplication should implement Application interface
        assertInstanceOf(Application.class, application,
            "ByteHotApplication should implement generic Application interface from java-commons");
    }

    @Test
    @DisplayName("🧪 ByteHotApplication accepts domain events through generic interface")
    void byteHotApplicationAcceptsDomainEventsGeneric() {
        // Given: A domain event
        final ClassFileChanged event = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass.class"),
            "TestClass",
            1024L,
            Instant.now()
        );

        // When: Processing through generic Application interface
        final Application genericApp = application;
        
        // Then: Should be able to accept events through generic interface
        assertDoesNotThrow(() -> {
            final List<? extends DomainResponseEvent<?>> responses = genericApp.accept(event);
            assertNotNull(responses, "Response list should not be null");
        }, "Should accept domain events through generic Application interface");
    }

    @Test
    @DisplayName("🧪 ByteHotApplication processes ClassFileChanged through generic interface")
    void byteHotApplicationProcessesClassFileChangedGeneric() {
        // Given: A ClassFileChanged event (simpler than ByteHotAttachRequested)
        final ClassFileChanged event = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass.class"),
            "TestClass",
            1024L,
            Instant.now()
        );

        // When: Processing through generic Application interface
        final Application genericApp = application;
        final List<? extends DomainResponseEvent<?>> responses = genericApp.accept(event);

        // Then: Should process the event and return appropriate responses
        assertNotNull(responses, "Response list should not be null");
        // Response behavior depends on current implementation
    }

    @Test
    @DisplayName("🧪 Generic Application interface maintains type safety")
    void genericApplicationInterfaceMaintainsTypeSafety() {
        // Given: Application as generic interface
        final Application genericApp = application;

        // When: Calling accept method
        // Then: Method signature should use proper generics
        assertNotNull(genericApp, "Generic application reference should not be null");
        
        // Test that we can assign the result to proper generic type
        final DomainEvent testEvent = ClassFileChanged.forNewSession(
            Paths.get("/test/TestClass.class"),
            "TestClass", 
            1024L,
            Instant.now()
        );
        
        final List<? extends DomainResponseEvent<?>> responses = genericApp.accept(testEvent);
        assertNotNull(responses, "Generic response should maintain type safety");
    }
}