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
 * Filename: InstrumentationPort.java
 *
 * Author: Claude Code
 *
 * Class name: InstrumentationPort
 *
 * Responsibilities:
 *   - Define interface for JVM instrumentation operations
 *   - Abstract JVM instrumentation implementation from domain logic
 *   - Enable class redefinition and bytecode manipulation
 *
 * Collaborators:
 *   - HotSwapManager: Uses this port for class redefinition
 *   - InstrumentationAdapter: Infrastructure implementation
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.Port;

/**
 * Port interface for JVM instrumentation operations
 * @author Claude Code
 * @since 2025-06-17
 */
public interface InstrumentationPort
    extends Port {

    /**
     * Redefines a class with new bytecode
     * @param clazz the class to redefine
     * @param newBytecode the new bytecode for the class
     * @throws Exception if redefinition fails
     */
    void redefineClass(final Class<?> clazz, final byte[] newBytecode) throws Exception;

    /**
     * Checks if class redefinition is supported
     * @return true if redefinition is available
     */
    boolean isRedefineClassesSupported();

    /**
     * Checks if retransformation is supported
     * @return true if retransformation is available
     */
    boolean isRetransformClassesSupported();

    /**
     * Gets all loaded classes
     * @return array of all loaded classes
     */
    Class<?>[] getAllLoadedClasses();

    /**
     * Checks if instrumentation is available
     * @return true if JVM instrumentation is operational
     */
    boolean isInstrumentationAvailable();

    /**
     * Returns the object size for the given object
     * @param objectToSize the object to measure
     * @return size in bytes
     */
    long getObjectSize(final Object objectToSize);
}