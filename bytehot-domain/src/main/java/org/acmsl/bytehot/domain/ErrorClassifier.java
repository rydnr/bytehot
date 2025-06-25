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
 * Filename: ErrorClassifier.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorClassifier
 *
 * Responsibilities:
 *   - Visitor interface for classifying errors using double dispatch
 *   - Provides type-specific classification methods for each exception type
 *
 * Collaborators:
 *   - ErrorClassifiable: Interface implemented by classifiable exceptions
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.exceptions.EventSnapshotException;

/**
 * Visitor interface for classifying errors using double dispatch pattern.
 * @author Claude Code
 * @since 2025-06-19
 */
public interface ErrorClassifier {
    
    /**
     * Classifies a bytecode validation exception.
     * @param exception the bytecode validation exception
     * @return the error type
     */
    ErrorType classifyBytecodeValidationException(BytecodeValidationException exception);
    
    /**
     * Classifies an instance update exception.
     * @param exception the instance update exception
     * @return the error type
     */
    ErrorType classifyInstanceUpdateException(InstanceUpdateException exception);
    
    /**
     * Classifies a hot-swap exception.
     * @param exception the hot-swap exception
     * @return the error type
     */
    ErrorType classifyHotSwapException(HotSwapException exception);
    
    /**
     * Classifies a security exception.
     * @param exception the security exception
     * @return the error type
     */
    ErrorType classifySecurityException(SecurityException exception);
    
    /**
     * Classifies an out of memory error.
     * @param error the out of memory error
     * @return the error type
     */
    ErrorType classifyOutOfMemoryError(OutOfMemoryError error);
    
    /**
     * Classifies a stack overflow error.
     * @param error the stack overflow error
     * @return the error type
     */
    ErrorType classifyStackOverflowError(StackOverflowError error);
    
    /**
     * Classifies a no such file exception.
     * @param exception the no such file exception
     * @return the error type
     */
    ErrorType classifyNoSuchFileException(java.nio.file.NoSuchFileException exception);
    
    /**
     * Classifies an access denied exception.
     * @param exception the access denied exception
     * @return the error type
     */
    ErrorType classifyAccessDeniedException(java.nio.file.AccessDeniedException exception);
    
    /**
     * Classifies an event snapshot exception by delegating to the original exception.
     * @param exception the event snapshot exception
     * @return the error type
     */
    ErrorType classifyEventSnapshotException(EventSnapshotException exception);
    
    /**
     * Classifies any other throwable that doesn't have a specific classification method.
     * @param throwable the generic throwable
     * @return the error type (typically UNKNOWN_ERROR)
     */
    ErrorType classifyGenericThrowable(Throwable throwable);
    
    /**
     * Helper method to classify any throwable by delegating to specific methods.
     * This method should handle the dispatch logic for all throwable types.
     * @param throwable the throwable to classify
     * @return the error type
     */
    default ErrorType classifyThrowable(Throwable throwable) {
        // First check if it implements ErrorClassifiable
        if (throwable instanceof ErrorClassifiable) {
            return ((ErrorClassifiable) throwable).acceptClassifier(this);
        }
        
        // Handle EventSnapshotException specially
        if (throwable instanceof EventSnapshotException) {
            return classifyEventSnapshotException((EventSnapshotException) throwable);
        }
        
        // Handle standard Java exceptions
        if (throwable instanceof SecurityException) {
            return classifySecurityException((SecurityException) throwable);
        }
        if (throwable instanceof OutOfMemoryError) {
            return classifyOutOfMemoryError((OutOfMemoryError) throwable);
        }
        if (throwable instanceof StackOverflowError) {
            return classifyStackOverflowError((StackOverflowError) throwable);
        }
        if (throwable instanceof java.nio.file.NoSuchFileException) {
            return classifyNoSuchFileException((java.nio.file.NoSuchFileException) throwable);
        }
        if (throwable instanceof java.nio.file.AccessDeniedException) {
            return classifyAccessDeniedException((java.nio.file.AccessDeniedException) throwable);
        }
        
        return classifyGenericThrowable(throwable);
    }
}