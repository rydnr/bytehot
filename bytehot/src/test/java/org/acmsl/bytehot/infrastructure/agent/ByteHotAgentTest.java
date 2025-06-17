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
 * Filename: ByteHotAgentTest.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotAgentTest
 *
 * Responsibilities:
 *   - Test ByteHotAgent for JVM agent entry point functionality
 *
 * Collaborators:
 *   - ByteHotAgent: JVM agent entry point for instrumentation setup
 *   - InstrumentationProvider: Singleton for instrumentation access
 */
package org.acmsl.bytehot.infrastructure.agent;

import org.acmsl.bytehot.domain.InstrumentationProvider;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test ByteHotAgent for JVM agent entry point functionality
 * @author Claude Code
 * @since 2025-06-17
 */
public class ByteHotAgentTest {

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
     * Tests that premain sets up instrumentation provider
     */
    @Test
    public void premain_sets_up_instrumentation_provider() {
        // Given: A fake instrumentation that supports redefinition
        Instrumentation fakeInstrumentation = new FakeInstrumentationWithRedefinition();
        
        // When: premain is called with instrumentation
        ByteHotAgent.premain("", fakeInstrumentation);
        
        // Then: InstrumentationProvider should be available
        assertTrue(InstrumentationProvider.isAvailable(), 
            "InstrumentationProvider should be available after premain");
    }

    /**
     * Tests that agentmain sets up instrumentation provider
     */
    @Test
    public void agentmain_sets_up_instrumentation_provider() {
        // Given: A fake instrumentation that supports redefinition
        Instrumentation fakeInstrumentation = new FakeInstrumentationWithRedefinition();
        
        // When: agentmain is called with instrumentation
        ByteHotAgent.agentmain("", fakeInstrumentation);
        
        // Then: InstrumentationProvider should be available
        assertTrue(InstrumentationProvider.isAvailable(), 
            "InstrumentationProvider should be available after agentmain");
    }

    /**
     * Tests that premain handles null instrumentation gracefully
     */
    @Test
    public void premain_handles_null_instrumentation() {
        // When: premain is called with null instrumentation
        ByteHotAgent.premain("", null);
        
        // Then: InstrumentationProvider should not be available
        assertFalse(InstrumentationProvider.isAvailable(), 
            "InstrumentationProvider should not be available with null instrumentation");
    }

    /**
     * Tests that premain handles unsupported instrumentation
     */
    @Test
    public void premain_handles_unsupported_instrumentation() {
        // Given: A fake instrumentation that does NOT support redefinition
        Instrumentation fakeInstrumentation = new FakeInstrumentationWithoutRedefinition();
        
        // When: premain is called with unsupported instrumentation
        ByteHotAgent.premain("", fakeInstrumentation);
        
        // Then: InstrumentationProvider should not be available
        assertFalse(InstrumentationProvider.isAvailable(), 
            "InstrumentationProvider should not be available without redefinition support");
    }

    /**
     * Fake instrumentation implementation that supports redefinition
     */
    private static class FakeInstrumentationWithRedefinition implements Instrumentation {
        public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {}
        public void addTransformer(ClassFileTransformer transformer) {}
        public boolean removeTransformer(ClassFileTransformer transformer) { return false; }
        public boolean isRetransformClassesSupported() { return false; }
        public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {}
        public boolean isRedefineClassesSupported() { return true; } // This one matters
        public void redefineClasses(ClassDefinition... definitions) {}
        public boolean isModifiableClass(Class<?> theClass) { return false; }
        public Class<?>[] getAllLoadedClasses() { return new Class[0]; }
        public Class<?>[] getInitiatedClasses(ClassLoader loader) { return new Class[0]; }
        public long getObjectSize(Object objectToSize) { return 0; }
        public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {}
        public void appendToSystemClassLoaderSearch(JarFile jarfile) {}
        public boolean isNativeMethodPrefixSupported() { return false; }
        public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {}
        public boolean isModifiableModule(java.lang.Module module) { return false; }
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
        public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {}
        public void addTransformer(ClassFileTransformer transformer) {}
        public boolean removeTransformer(ClassFileTransformer transformer) { return false; }
        public boolean isRetransformClassesSupported() { return false; }
        public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {}
        public boolean isRedefineClassesSupported() { return false; } // This one matters
        public void redefineClasses(ClassDefinition... definitions) {}
        public boolean isModifiableClass(Class<?> theClass) { return false; }
        public Class<?>[] getAllLoadedClasses() { return new Class[0]; }
        public Class<?>[] getInitiatedClasses(ClassLoader loader) { return new Class[0]; }
        public long getObjectSize(Object objectToSize) { return 0; }
        public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {}
        public void appendToSystemClassLoaderSearch(JarFile jarfile) {}
        public boolean isNativeMethodPrefixSupported() { return false; }
        public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {}
        public boolean isModifiableModule(java.lang.Module module) { return false; }
        public void redefineModule(java.lang.Module module, Set<java.lang.Module> extraReads, 
                                   Map<String, Set<java.lang.Module>> extraExports, 
                                   Map<String, Set<java.lang.Module>> extraOpens, 
                                   Set<Class<?>> extraUses, 
                                   Map<Class<?>, List<Class<?>>> extraProvides) {}
    }
}