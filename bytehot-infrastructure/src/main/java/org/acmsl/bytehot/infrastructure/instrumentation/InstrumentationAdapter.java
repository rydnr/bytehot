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
 * Filename: InstrumentationAdapter.java
 *
 * Author: Claude Code
 *
 * Class name: InstrumentationAdapter
 *
 * Responsibilities:
 *   - Implement JVM instrumentation using Java Instrumentation API
 *   - Handle class redefinition and bytecode manipulation safely
 *   - Provide infrastructure implementation of InstrumentationPort
 *
 * Collaborators:
 *   - InstrumentationPort: Interface this adapter implements
 *   - Instrumentation: Java JVM instrumentation API
 */
package org.acmsl.bytehot.infrastructure.instrumentation;

import org.acmsl.bytehot.domain.InstrumentationPort;

import org.acmsl.commons.patterns.Adapter;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Infrastructure adapter for JVM instrumentation operations
 * @author Claude Code
 * @since 2025-06-17
 */
public class InstrumentationAdapter
    implements InstrumentationPort, Adapter<InstrumentationPort> {

    /**
     * The JVM instrumentation instance
     */
    private final Instrumentation instrumentation;

    /**
     * Counter for successful redefinitions
     */
    private final AtomicLong redefinitionCount;

    /**
     * Creates a new InstrumentationAdapter instance
     */
    public InstrumentationAdapter(final Instrumentation instrumentation) {
        if (instrumentation == null) {
            throw new IllegalArgumentException("Instrumentation cannot be null");
        }
        
        this.instrumentation = instrumentation;
        this.redefinitionCount = new AtomicLong(0);
    }

    /**
     * Redefines a class with new bytecode
     */
    @Override
    public void redefineClass(final Class<?> clazz, final byte[] newBytecode) throws Exception {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        
        if (newBytecode == null || newBytecode.length == 0) {
            throw new IllegalArgumentException("Bytecode cannot be null or empty");
        }

        if (!instrumentation.isRedefineClassesSupported()) {
            throw new UnsupportedOperationException("Class redefinition is not supported by this JVM");
        }

        if (!instrumentation.isModifiableClass(clazz)) {
            throw new UnmodifiableClassException("Class is not modifiable: " + clazz.getName());
        }

        try {
            final ClassDefinition definition = new ClassDefinition(clazz, newBytecode);
            instrumentation.redefineClasses(definition);
            redefinitionCount.incrementAndGet();
            
        } catch (final Exception e) {
            throw new Exception("Failed to redefine class " + clazz.getName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks if class redefinition is supported
     */
    @Override
    public boolean isRedefineClassesSupported() {
        return instrumentation.isRedefineClassesSupported();
    }

    /**
     * Checks if retransformation is supported
     */
    @Override
    public boolean isRetransformClassesSupported() {
        return instrumentation.isRetransformClassesSupported();
    }

    /**
     * Gets all loaded classes
     */
    @Override
    public Class<?>[] getAllLoadedClasses() {
        return instrumentation.getAllLoadedClasses();
    }

    /**
     * Checks if instrumentation is available
     */
    @Override
    public boolean isInstrumentationAvailable() {
        return instrumentation != null;
    }

    /**
     * Returns the object size for the given object
     */
    @Override
    public long getObjectSize(final Object objectToSize) {
        if (objectToSize == null) {
            return 0;
        }
        
        return instrumentation.getObjectSize(objectToSize);
    }

    /**
     * Returns the port interface this adapter implements
     */
    @Override
    public Class<InstrumentationPort> adapts() {
        return InstrumentationPort.class;
    }

    /**
     * Returns the number of successful redefinitions
     */
    public long getRedefinitionCount() {
        return redefinitionCount.get();
    }

    /**
     * Checks if a specific class is modifiable
     */
    public boolean isModifiableClass(final Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        
        return instrumentation.isModifiableClass(clazz);
    }

    /**
     * Returns the number of loaded classes
     */
    public int getLoadedClassCount() {
        return instrumentation.getAllLoadedClasses().length;
    }

    /**
     * Retransforms the given classes
     */
    public void retransformClasses(final Class<?>... classes) throws Exception {
        if (!instrumentation.isRetransformClassesSupported()) {
            throw new UnsupportedOperationException("Class retransformation is not supported by this JVM");
        }

        try {
            instrumentation.retransformClasses(classes);
        } catch (final Exception e) {
            throw new Exception("Failed to retransform classes: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a transformer to the instrumentation
     */
    public void addTransformer(final java.lang.instrument.ClassFileTransformer transformer, final boolean canRetransform) {
        instrumentation.addTransformer(transformer, canRetransform);
    }

    /**
     * Removes a transformer from the instrumentation
     */
    public boolean removeTransformer(final java.lang.instrument.ClassFileTransformer transformer) {
        return instrumentation.removeTransformer(transformer);
    }
}