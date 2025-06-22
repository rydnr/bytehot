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
 * Filename: InstrumentationProviderTest.java
 *
 * Author: Claude Code
 *
 * Class name: InstrumentationProviderTest
 *
 * Responsibilities:
 *   - Test InstrumentationProvider for JVM instrumentation access
 *
 * Collaborators:
 *   - InstrumentationProvider: Singleton for instrumentation access
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test InstrumentationProvider for JVM instrumentation access
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstrumentationProviderTest {

    /**
     * Clean up instrumentation state before each test
     */
    @BeforeEach
    public void setUp() {
        InstrumentationProvider.reset(); // Clear any previous state
    }

    /**
     * Clean up instrumentation state after each test
     */
    @AfterEach
    public void tearDown() {
        InstrumentationProvider.reset(); // Clean up for next test
    }

    /**
     * Tests that instrumentation is not available initially
     */
    @Test
    public void instrumentation_not_available_initially() {
        // When: No instrumentation has been set
        // Then: Provider should report it's not available
        assertFalse(InstrumentationProvider.isAvailable(), 
            "Instrumentation should not be available initially");
    }

    /**
     * Tests that instrumentation becomes available after setting
     */
    @Test
    public void instrumentation_becomes_available_after_setting() {
        // Given: A fake instrumentation that supports redefinition
        Instrumentation fakeInstrumentation = new FakeInstrumentationWithRedefinition();
        
        // When: Instrumentation is set
        InstrumentationProvider.setInstrumentation(fakeInstrumentation);
        
        // Then: Provider should report it's available
        assertTrue(InstrumentationProvider.isAvailable(), 
            "Instrumentation should be available after setting");
    }

    /**
     * Tests that get() throws exception when instrumentation not available
     */
    @Test
    public void get_throws_exception_when_not_available() {
        // Given: No instrumentation set
        // When: Attempting to get instrumentation
        // Then: Should throw IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> InstrumentationProvider.get());
        
        assertTrue(exception.getMessage().contains("not available"),
            "Exception should mention instrumentation not available");
    }

    /**
     * Tests that get() returns instrumentation when available
     */
    @Test
    public void get_returns_instrumentation_when_available() {
        // Given: A fake instrumentation that supports redefinition
        Instrumentation fakeInstrumentation = new FakeInstrumentationWithRedefinition();
        InstrumentationProvider.setInstrumentation(fakeInstrumentation);
        
        // When: Getting instrumentation
        Instrumentation result = InstrumentationProvider.get();
        
        // Then: Should return the same instance
        assertNotNull(result, "Should return non-null instrumentation");
        assertEquals(fakeInstrumentation, result, "Should return the same instrumentation instance");
    }

    /**
     * Tests that instrumentation without redefinition support is not available
     */
    @Test
    public void instrumentation_without_redefinition_not_available() {
        // Given: A fake instrumentation that does NOT support redefinition
        Instrumentation fakeInstrumentation = new FakeInstrumentationWithoutRedefinition();
        
        // When: Instrumentation is set
        InstrumentationProvider.setInstrumentation(fakeInstrumentation);
        
        // Then: Provider should report it's not available
        assertFalse(InstrumentationProvider.isAvailable(), 
            "Instrumentation should not be available without redefinition support");
    }

    /**
     * Fake instrumentation implementation that supports redefinition
     */
    private static class FakeInstrumentationWithRedefinition implements Instrumentation {
        @Override
        public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {}
        
        @Override
        public void addTransformer(ClassFileTransformer transformer) {}
        
        @Override
        public boolean removeTransformer(ClassFileTransformer transformer) { return false; }
        
        @Override
        public boolean isRetransformClassesSupported() { return false; }
        
        @Override
        public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {}
        
        @Override
        public boolean isRedefineClassesSupported() { return true; } // This one matters
        
        @Override
        public void redefineClasses(ClassDefinition... definitions) {}
        
        @Override
        public boolean isModifiableClass(Class<?> theClass) { return false; }
        
        @Override
        public Class<?>[] getAllLoadedClasses() { return new Class<?>[0]; }
        
        @Override
        public Class<?>[] getInitiatedClasses(ClassLoader loader) { return new Class<?>[0]; }
        
        @Override
        public long getObjectSize(Object objectToSize) { return 0; }
        
        @Override
        public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {}
        
        @Override
        public void appendToSystemClassLoaderSearch(JarFile jarfile) {}
        
        @Override
        public boolean isNativeMethodPrefixSupported() { return false; }
        
        @Override
        public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {}
        
        @Override
        public boolean isModifiableModule(java.lang.Module module) { return false; }
        
        @Override
        public void redefineModule(java.lang.Module module, Set<java.lang.Module> extraReads, 
                                   Map<String, Set<java.lang.Module>> extraExports, 
                                   Map<String, Set<java.lang.Module>> extraOpens, 
                                   Set<Class<?>> extraUses, 
                                   Map<Class<?>, List<Class<?>>> extraProvides) {}
    }

    /**
     * Fake instrumentation implementation that does NOT support redefinition
     */
    private static class FakeInstrumentationWithoutRedefinition implements Instrumentation {
        @Override
        public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {}
        
        @Override
        public void addTransformer(ClassFileTransformer transformer) {}
        
        @Override
        public boolean removeTransformer(ClassFileTransformer transformer) { return false; }
        
        @Override
        public boolean isRetransformClassesSupported() { return false; }
        
        @Override
        public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {}
        
        @Override
        public boolean isRedefineClassesSupported() { return false; } // This one matters
        
        @Override
        public void redefineClasses(ClassDefinition... definitions) {}
        
        @Override
        public boolean isModifiableClass(Class<?> theClass) { return false; }
        
        @Override
        public Class<?>[] getAllLoadedClasses() { return new Class<?>[0]; }
        
        @Override
        public Class<?>[] getInitiatedClasses(ClassLoader loader) { return new Class<?>[0]; }
        
        @Override
        public long getObjectSize(Object objectToSize) { return 0; }
        
        @Override
        public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {}
        
        @Override
        public void appendToSystemClassLoaderSearch(JarFile jarfile) {}
        
        @Override
        public boolean isNativeMethodPrefixSupported() { return false; }
        
        @Override
        public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {}
        
        @Override
        public boolean isModifiableModule(java.lang.Module module) { return false; }
        
        @Override
        public void redefineModule(java.lang.Module module, Set<java.lang.Module> extraReads, 
                                   Map<String, Set<java.lang.Module>> extraExports, 
                                   Map<String, Set<java.lang.Module>> extraOpens, 
                                   Set<Class<?>> extraUses, 
                                   Map<Class<?>, List<Class<?>>> extraProvides) {}
    }
}