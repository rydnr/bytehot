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
 * Filename: InstrumentationService.java
 *
 * Author: Claude Code
 *
 * Class name: InstrumentationService
 *
 * Responsibilities:
 *   - Domain service for JVM instrumentation operations
 *   - Abstracts instrumentation capabilities for domain layer
 *
 * Collaborators:
 *   - HotSwapManager: Uses instrumentation for class redefinition
 *   - BytecodeValidator: Validates instrumentation compatibility
 */
package org.acmsl.bytehot.domain;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * Domain service for JVM instrumentation operations.
 * @author Claude Code
 * @since 2025-06-22
 */
public interface InstrumentationService {

    /**
     * Checks if class redefinition is supported by the JVM.
     * @return true if class redefinition is supported
     */
    boolean isRedefineClassesSupported();

    /**
     * Checks if class retransformation is supported by the JVM.
     * @return true if class retransformation is supported
     */
    boolean isRetransformClassesSupported();

    /**
     * Redefines classes in the JVM.
     * @param definitions the class definitions to apply
     * @throws HotSwapException if redefinition fails
     */
    void redefineClasses(final ClassDefinition... definitions) throws HotSwapException;

    /**
     * Gets all currently loaded classes in the JVM.
     * @return array of all loaded classes
     */
    Class<?>[] getAllLoadedClasses();

    /**
     * Checks if a specific class is modifiable.
     * @param theClass the class to check
     * @return true if the class can be modified
     */
    boolean isModifiableClass(final Class<?> theClass);

    /**
     * Gets the underlying JVM instrumentation instance.
     * @return the instrumentation instance
     */
    Instrumentation getInstrumentation();

    /**
     * Finds a loaded class by name.
     * @param className the name of the class to find
     * @return the loaded class, or null if not found
     */
    Class<?> findLoadedClass(final String className);

    /**
     * Redefines a single class with new bytecode.
     * @param targetClass the class to redefine
     * @param newBytecode the new bytecode for the class
     * @throws HotSwapException if redefinition fails
     */
    void redefineClass(final Class<?> targetClass, final byte[] newBytecode) throws HotSwapException;
}