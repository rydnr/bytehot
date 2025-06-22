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
 * Filename: MockInstrumentationService.java
 *
 * Author: Claude Code
 *
 * Class name: MockInstrumentationService
 *
 * Responsibilities:
 *   - Mock implementation of InstrumentationService for testing
 *   - Provides configurable behavior for different test scenarios
 *
 * Collaborators:
 *   - InstrumentationService: Interface being mocked
 *   - Test classes: Uses this mock for testing
 */
package org.acmsl.bytehot.domain.testing;

import org.acmsl.bytehot.domain.HotSwapException;
import org.acmsl.bytehot.domain.InstrumentationService;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Mock implementation of InstrumentationService for testing purposes.
 * @author Claude Code
 * @since 2025-06-22
 */
public class MockInstrumentationService implements InstrumentationService {

    /**
     * Whether redefinition is supported
     */
    @Setter
    private boolean redefineClassesSupported = true;

    /**
     * Whether retransformation is supported
     */
    @Setter
    private boolean retransformClassesSupported = true;

    /**
     * Mock loaded classes
     */
    private final Map<String, Class<?>> loadedClasses = new HashMap<>();

    /**
     * Classes that are marked as modifiable
     */
    private final Map<String, Boolean> modifiableClasses = new HashMap<>();

    /**
     * Record of redefinition calls
     */
    @Getter
    private final List<ClassDefinition> redefinitionCalls = new ArrayList<>();

    /**
     * Whether to throw exception on redefinition
     */
    @Getter
    @Setter
    private boolean shouldFailRedefinition = false;

    /**
     * Exception to throw on redefinition failure
     */
    @Getter
    @Setter
    private HotSwapException redefinitionException;

    @Override
    public boolean isRedefineClassesSupported() {
        return redefineClassesSupported;
    }

    @Override
    public boolean isRetransformClassesSupported() {
        return retransformClassesSupported;
    }

    @Override
    public void redefineClasses(final ClassDefinition... definitions) throws HotSwapException {
        if (shouldFailRedefinition && redefinitionException != null) {
            throw redefinitionException;
        }
        
        for (final ClassDefinition definition : definitions) {
            redefinitionCalls.add(definition);
        }
    }

    @Override
    public Class<?>[] getAllLoadedClasses() {
        return loadedClasses.values().toArray(new Class<?>[0]);
    }

    @Override
    public boolean isModifiableClass(final Class<?> theClass) {
        return modifiableClasses.getOrDefault(theClass.getName(), true);
    }

    @Override
    public Instrumentation getInstrumentation() {
        return null; // Mock implementation - tests should not need actual instrumentation
    }

    @Override
    public Class<?> findLoadedClass(final String className) {
        return loadedClasses.get(className);
    }

    @Override
    public void redefineClass(final Class<?> targetClass, final byte[] newBytecode) throws HotSwapException {
        final ClassDefinition definition = new ClassDefinition(targetClass, newBytecode);
        redefineClasses(definition);
    }

    /**
     * Adds a mock loaded class for testing.
     * @param className the class name
     * @param clazz the class object
     */
    public void addLoadedClass(final String className, final Class<?> clazz) {
        loadedClasses.put(className, clazz);
    }

    /**
     * Sets whether a class is modifiable.
     * @param className the class name
     * @param modifiable whether the class is modifiable
     */
    public void setClassModifiable(final String className, final boolean modifiable) {
        modifiableClasses.put(className, modifiable);
    }

    /**
     * Clears all recorded calls and state.
     */
    public void reset() {
        redefinitionCalls.clear();
        loadedClasses.clear();
        modifiableClasses.clear();
        shouldFailRedefinition = false;
        redefinitionException = null;
        redefineClassesSupported = true;
        retransformClassesSupported = true;
    }
}