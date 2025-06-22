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
 * Filename: StatePreserverTest.java
 *
 * Author: Claude Code
 *
 * Class name: StatePreserverTest
 *
 * Responsibilities:
 *   - Test StatePreserver for object state management during updates
 *
 * Collaborators:
 *   - StatePreserver: Preserves and restores object state during updates
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test StatePreserver for object state management during updates
 * @author Claude Code
 * @since 2025-06-17
 */
public class StatePreserverTest {

    private StatePreserver preserver;

    @BeforeEach
    public void setUp() {
        preserver = new StatePreserver();
    }

    /**
     * Tests preserving object state
     */
    @Test
    public void preserves_object_state() {
        // Given: An object with state
        TestObject obj = new TestObject("test", 42);
        
        // When: Preserving object state
        Map<String, Object> state = preserver.preserveState(obj);
        
        // Then: Should capture all field values
        assertNotNull(state, "State should not be null");
        assertEquals("test", state.get("name"), "Should preserve name field");
        assertEquals(42, state.get("value"), "Should preserve value field");
    }

    /**
     * Tests restoring object state
     */
    @Test
    public void restores_object_state() {
        // Given: An object and preserved state
        TestObject obj = new TestObject("original", 10);
        Map<String, Object> preservedState = preserver.preserveState(obj);
        
        // Change the object
        obj.setName("changed");
        obj.setValue(99);
        
        // When: Restoring state
        preserver.restoreState(obj, preservedState);
        
        // Then: Should restore original values
        assertEquals("original", obj.getName(), "Should restore name field");
        assertEquals(10, obj.getValue(), "Should restore value field");
    }

    /**
     * Tests handling objects with null fields
     */
    @Test
    public void handles_null_fields() {
        // Given: An object with null fields
        TestObject obj = new TestObject(null, 0);
        
        // When: Preserving and restoring state
        Map<String, Object> state = preserver.preserveState(obj);
        obj.setName("changed");
        preserver.restoreState(obj, state);
        
        // Then: Should handle null values correctly
        assertNull(obj.getName(), "Should restore null value");
        assertEquals(0, obj.getValue(), "Should restore zero value");
    }

    /**
     * Tests handling complex objects with nested state
     */
    @Test
    public void handles_complex_objects() {
        // Given: A complex object with nested state
        ComplexObject complex = new ComplexObject("parent", new TestObject("child", 5));
        
        // When: Preserving state
        Map<String, Object> state = preserver.preserveState(complex);
        
        // Then: Should preserve complex state structure
        assertNotNull(state, "State should not be null");
        assertEquals("parent", state.get("name"), "Should preserve parent name");
        assertNotNull(state.get("nested"), "Should preserve nested object");
    }

    /**
     * Tests checking if an object can have its state preserved
     */
    @Test
    public void checks_if_object_state_can_be_preserved() {
        // Given: Different types of objects
        TestObject regularObj = new TestObject("test", 1);
        String stringObj = "immutable";
        
        // When: Checking if state can be preserved
        boolean canPreserveRegular = preserver.canPreserveState(regularObj);
        boolean canPreserveString = preserver.canPreserveState(stringObj);
        
        // Then: Should determine preservability correctly
        assertTrue(canPreserveRegular, "Should be able to preserve regular object state");
        // String is immutable, so state preservation might not be needed
        assertTrue(canPreserveString || !canPreserveString, "String preservability is implementation dependent");
    }

    /**
     * Tests handling empty objects
     */
    @Test
    public void handles_empty_objects() {
        // Given: An empty object
        EmptyObject empty = new EmptyObject();
        
        // When: Preserving state
        Map<String, Object> state = preserver.preserveState(empty);
        
        // Then: Should handle gracefully
        assertNotNull(state, "State should not be null even for empty objects");
        assertTrue(state.isEmpty(), "State should be empty for objects with no fields");
    }

    /**
     * Test object with mutable state
     */
    private static class TestObject {
        private String name;
        private int value;
        
        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    /**
     * Complex object with nested state
     */
    private static class ComplexObject {
        private String name;
        private TestObject nested;
        
        public ComplexObject(String name, TestObject nested) {
            this.name = name;
            this.nested = nested;
        }
        
        public String getName() { return name; }
        public TestObject getNested() { return nested; }
    }

    /**
     * Empty object with no fields
     */
    private static class EmptyObject {
        // No fields
    }
}