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
/*
 ******************************************************************************
 *
 * Filename: InstanceUpdateMethodTest.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceUpdateMethodTest
 *
 * Responsibilities:
 *   - Test InstanceUpdateMethod enum for instance update strategies
 *
 * Collaborators:
 *   - InstanceUpdateMethod: Enum defining update strategies
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test InstanceUpdateMethod enum for instance update strategies
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstanceUpdateMethodTest {

    /**
     * Tests that all expected update methods are available
     */
    @Test
    public void all_update_methods_available() {
        // When: Getting all enum values
        InstanceUpdateMethod[] methods = InstanceUpdateMethod.values();
        
        // Then: All expected methods should be present
        assertEquals(5, methods.length, "Should have exactly 5 update methods");
        
        // Verify each method exists
        assertNotNull(InstanceUpdateMethod.AUTOMATIC, "AUTOMATIC method should exist");
        assertNotNull(InstanceUpdateMethod.REFLECTION, "REFLECTION method should exist");
        assertNotNull(InstanceUpdateMethod.PROXY_REFRESH, "PROXY_REFRESH method should exist");
        assertNotNull(InstanceUpdateMethod.FACTORY_RESET, "FACTORY_RESET method should exist");
        assertNotNull(InstanceUpdateMethod.NO_UPDATE, "NO_UPDATE method should exist");
    }

    /**
     * Tests that automatic is the default/preferred method
     */
    @Test
    public void automatic_is_default_method() {
        // Given: The AUTOMATIC method
        InstanceUpdateMethod automatic = InstanceUpdateMethod.AUTOMATIC;
        
        // Then: It should be the first enum value (default)
        assertEquals(0, automatic.ordinal(), "AUTOMATIC should be the first enum value");
    }

    /**
     * Tests that enum has meaningful string representation
     */
    @Test
    public void enum_has_meaningful_string_representation() {
        // When: Converting enum values to strings
        // Then: String representation should be meaningful
        assertEquals("AUTOMATIC", InstanceUpdateMethod.AUTOMATIC.toString());
        assertEquals("REFLECTION", InstanceUpdateMethod.REFLECTION.toString());
        assertEquals("PROXY_REFRESH", InstanceUpdateMethod.PROXY_REFRESH.toString());
        assertEquals("FACTORY_RESET", InstanceUpdateMethod.FACTORY_RESET.toString());
        assertEquals("NO_UPDATE", InstanceUpdateMethod.NO_UPDATE.toString());
    }

    /**
     * Tests that enum can be parsed from string
     */
    @Test
    public void enum_can_be_parsed_from_string() {
        // When: Parsing enum from string values
        // Then: Should successfully parse all values
        assertEquals(InstanceUpdateMethod.AUTOMATIC, 
            InstanceUpdateMethod.valueOf("AUTOMATIC"));
        assertEquals(InstanceUpdateMethod.REFLECTION, 
            InstanceUpdateMethod.valueOf("REFLECTION"));
        assertEquals(InstanceUpdateMethod.PROXY_REFRESH, 
            InstanceUpdateMethod.valueOf("PROXY_REFRESH"));
        assertEquals(InstanceUpdateMethod.FACTORY_RESET, 
            InstanceUpdateMethod.valueOf("FACTORY_RESET"));
        assertEquals(InstanceUpdateMethod.NO_UPDATE, 
            InstanceUpdateMethod.valueOf("NO_UPDATE"));
    }

    /**
     * Tests that methods represent different update strategies
     */
    @Test
    public void methods_represent_different_strategies() {
        // Given: All update methods
        InstanceUpdateMethod[] methods = InstanceUpdateMethod.values();
        
        // Then: Each method should be unique
        for (int i = 0; i < methods.length; i++) {
            for (int j = i + 1; j < methods.length; j++) {
                assertTrue(methods[i] != methods[j], 
                    "Each update method should be unique: " + methods[i] + " vs " + methods[j]);
            }
        }
    }
}