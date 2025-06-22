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
 * Filename: JvmInstrumentationService.java
 *
 * Author: Claude Code
 *
 * Class name: JvmInstrumentationService
 *
 * Responsibilities:
 *   - Concrete implementation of InstrumentationService
 *   - Wraps JVM Instrumentation API for domain use
 *
 * Collaborators:
 *   - Instrumentation: JVM instrumentation API
 *   - HotSwapManager: Primary consumer of instrumentation services
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.ClassRedefinitionFailed;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Concrete implementation of InstrumentationService using JVM instrumentation.
 * @author Claude Code
 * @since 2025-06-22
 */
@EqualsAndHashCode
@ToString
public final class JvmInstrumentationService implements InstrumentationService {

    /**
     * The underlying JVM instrumentation instance
     */
    @Getter
    private final Instrumentation instrumentation;

    /**
     * Creates a new JvmInstrumentationService.
     * @param instrumentation the JVM instrumentation instance
     */
    public JvmInstrumentationService(final Instrumentation instrumentation) {
        if (instrumentation == null) {
            throw new IllegalArgumentException("Instrumentation cannot be null");
        }
        this.instrumentation = instrumentation;
    }

    @Override
    public boolean isRedefineClassesSupported() {
        return instrumentation.isRedefineClassesSupported();
    }

    @Override
    public boolean isRetransformClassesSupported() {
        return instrumentation.isRetransformClassesSupported();
    }

    @Override
    public void redefineClasses(final ClassDefinition... definitions) throws HotSwapException {
        try {
            instrumentation.redefineClasses(definitions);
        } catch (final ClassNotFoundException e) {
            throw createHotSwapException("Class not found during redefinition", e);
        } catch (final UnmodifiableClassException e) {
            throw createHotSwapException("Class is not modifiable", e);
        } catch (final UnsupportedOperationException e) {
            throw createHotSwapException("Redefinition not supported", e);
        } catch (final Exception e) {
            throw createHotSwapException("Unexpected error during class redefinition", e);
        }
    }

    @Override
    public Class<?>[] getAllLoadedClasses() {
        return instrumentation.getAllLoadedClasses();
    }

    @Override
    public boolean isModifiableClass(final Class<?> theClass) {
        return instrumentation.isModifiableClass(theClass);
    }

    @Override
    public Class<?> findLoadedClass(final String className) {
        final Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
        for (final Class<?> clazz : loadedClasses) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }
        return null;
    }

    @Override
    public void redefineClass(final Class<?> targetClass, final byte[] newBytecode) throws HotSwapException {
        final ClassDefinition definition = new ClassDefinition(targetClass, newBytecode);
        redefineClasses(definition);
    }

    /**
     * Creates a HotSwapException with proper event structure.
     * @param reason the failure reason
     * @param cause the original exception
     * @return the HotSwapException
     */
    protected HotSwapException createHotSwapException(final String reason, final Throwable cause) {
        final org.acmsl.bytehot.domain.events.ClassRedefinitionFailed failureEvent = 
            new org.acmsl.bytehot.domain.events.ClassRedefinitionFailed(
                "Unknown", // className - would need to be passed in
                null,      // classFile - would need to be passed in
                reason,
                cause.getMessage(),
                "Check bytecode compatibility and retry",
                java.time.Instant.now()
            );
        return new HotSwapException(failureEvent, cause);
    }
}