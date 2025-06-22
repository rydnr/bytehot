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
 * Filename: ErrorSeverityAssessor.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorSeverityAssessor
 *
 * Responsibilities:
 *   - Visitor interface for assessing error severity using double dispatch
 *   - Provides type-specific severity assessment methods for each exception type
 *
 * Collaborators:
 *   - ErrorClassifiable: Interface implemented by classifiable exceptions
 */
package org.acmsl.bytehot.domain;

/**
 * Visitor interface for assessing error severity using double dispatch pattern.
 * @author Claude Code
 * @since 2025-06-19
 */
public interface ErrorSeverityAssessor {
    
    /**
     * Assesses severity of a bytecode validation exception.
     * @param exception the bytecode validation exception
     * @return the error severity
     */
    ErrorSeverity assessBytecodeValidationException(BytecodeValidationException exception);
    
    /**
     * Assesses severity of an instance update exception.
     * @param exception the instance update exception
     * @return the error severity
     */
    ErrorSeverity assessInstanceUpdateException(InstanceUpdateException exception);
    
    /**
     * Assesses severity of a hot-swap exception.
     * @param exception the hot-swap exception
     * @return the error severity
     */
    ErrorSeverity assessHotSwapException(HotSwapException exception);
    
    /**
     * Assesses severity of a security exception.
     * @param exception the security exception
     * @return the error severity
     */
    ErrorSeverity assessSecurityException(SecurityException exception);
    
    /**
     * Assesses severity of an out of memory error.
     * @param error the out of memory error
     * @return the error severity
     */
    ErrorSeverity assessOutOfMemoryError(OutOfMemoryError error);
    
    /**
     * Assesses severity of a stack overflow error.
     * @param error the stack overflow error
     * @return the error severity
     */
    ErrorSeverity assessStackOverflowError(StackOverflowError error);
    
    /**
     * Assesses severity of an illegal argument exception.
     * @param exception the illegal argument exception
     * @return the error severity
     */
    ErrorSeverity assessIllegalArgumentException(IllegalArgumentException exception);
    
    /**
     * Assesses severity of an illegal state exception.
     * @param exception the illegal state exception
     * @return the error severity
     */
    ErrorSeverity assessIllegalStateException(IllegalStateException exception);
    
    /**
     * Assesses severity of a runtime exception.
     * @param exception the runtime exception
     * @return the error severity
     */
    ErrorSeverity assessRuntimeException(RuntimeException exception);
    
    /**
     * Assesses severity of an event snapshot exception by delegating to the original exception.
     * @param exception the event snapshot exception
     * @return the error severity
     */
    ErrorSeverity assessEventSnapshotException(EventSnapshotException exception);
    
    /**
     * Assesses severity of any other throwable that doesn't have a specific assessment method.
     * @param throwable the generic throwable
     * @return the error severity (typically ERROR)
     */
    ErrorSeverity assessGenericThrowable(Throwable throwable);
    
    /**
     * Helper method to assess any throwable by delegating to specific methods.
     * This method should handle the dispatch logic for all throwable types.
     * @param throwable the throwable to assess
     * @return the error severity
     */
    default ErrorSeverity assessThrowable(Throwable throwable) {
        // First check if it implements ErrorClassifiable
        if (throwable instanceof ErrorClassifiable) {
            return ((ErrorClassifiable) throwable).acceptSeverityAssessor(this);
        }
        
        // Handle standard Java exceptions
        if (throwable instanceof OutOfMemoryError) {
            return assessOutOfMemoryError((OutOfMemoryError) throwable);
        }
        if (throwable instanceof StackOverflowError) {
            return assessStackOverflowError((StackOverflowError) throwable);
        }
        if (throwable instanceof SecurityException) {
            return assessSecurityException((SecurityException) throwable);
        }
        if (throwable instanceof IllegalArgumentException) {
            return assessIllegalArgumentException((IllegalArgumentException) throwable);
        }
        if (throwable instanceof IllegalStateException) {
            return assessIllegalStateException((IllegalStateException) throwable);
        }
        if (throwable instanceof RuntimeException) {
            return assessRuntimeException((RuntimeException) throwable);
        }
        
        return assessGenericThrowable(throwable);
    }
}