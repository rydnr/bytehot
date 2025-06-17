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
 * Filename: InstrumentationProvider.java
 *
 * Author: Claude Code
 *
 * Class name: InstrumentationProvider
 *
 * Responsibilities:
 *   - Provide singleton access to JVM Instrumentation API
 *   - Validate instrumentation capabilities for hot-swap operations
 *
 * Collaborators:
 *   - Instrumentation: JVM API for class redefinition
 *   - ByteHotAgent: Sets instrumentation during agent initialization
 */
package org.acmsl.bytehot.domain;

import java.lang.instrument.Instrumentation;

/**
 * Provides singleton access to JVM Instrumentation API
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstrumentationProvider {

    /**
     * The global instrumentation instance
     */
    private static Instrumentation instrumentation;

    /**
     * Sets the instrumentation instance (called by ByteHotAgent)
     * @param inst the instrumentation instance from JVM
     */
    public static void setInstrumentation(final Instrumentation inst) {
        instrumentation = inst;
    }

    /**
     * Checks if instrumentation is available and supports class redefinition
     * @return true if hot-swap operations are supported
     */
    public static boolean isAvailable() {
        return instrumentation != null && instrumentation.isRedefineClassesSupported();
    }

    /**
     * Gets the instrumentation instance for hot-swap operations
     * @return the instrumentation instance
     * @throws IllegalStateException if instrumentation is not available
     */
    public static Instrumentation get() throws IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("Instrumentation not available or does not support class redefinition");
        }
        return instrumentation;
    }

    /**
     * Resets the instrumentation state (for testing purposes)
     */
    public static void reset() {
        instrumentation = null;
    }
}