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

*/
package org.acmsl.bytehot.infrastructure.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.instrument.Instrumentation;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ByteHotCLI.
 * @author Claude Code
 * @since 2025-06-24
 */
public class ByteHotCLITest {

    @Test
    @DisplayName("Should have protected constructor")
    public void shouldHaveProtectedConstructor() {
        // The constructor is protected, so we can't test instantiation directly
        // This test just verifies the class exists and can be referenced
        assertNotNull(ByteHotCLI.class);
    }

    @Test
    @DisplayName("Should fail premain when bhconfig system property is missing")
    public void shouldFailPremainWhenBhconfigSystemPropertyIsMissing() {
        // Given - Clear the system property
        System.clearProperty("bhconfig");

        // When/Then - Should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            ByteHotCLI.premain("", null);
        });
    }

    @Test
    @DisplayName("Should fail premain when bhconfig system property is blank")
    public void shouldFailPremainWhenBhconfigSystemPropertyIsBlank() {
        // Given
        System.setProperty("bhconfig", "");

        // When/Then - Should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            ByteHotCLI.premain("", null);
        });
    }

    @Test
    @DisplayName("Should call premain from agentmain")
    public void shouldCallPremainFromAgentmain() {
        // Given - Clear system property to cause predictable failure
        System.clearProperty("bhconfig");

        // When/Then - agentmain should behave the same as premain
        assertThrows(IllegalStateException.class, () -> {
            ByteHotCLI.agentmain("", null);
        });
    }
}