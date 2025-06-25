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
 * Filename: DefaultErrorClassifier.java
 *
 * Author: Claude Code
 *
 * Class name: DefaultErrorClassifier
 *
 * Responsibilities:
 *   - Default implementation of error classification using double dispatch
 *   - Provides type-specific classification for all supported exception types
 *
 * Collaborators:
 *   - ErrorClassifier: Interface this class implements
 *   - ErrorType: Enumeration of error types
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.exceptions.EventSnapshotException;

/**
 * Default implementation of error classification using double dispatch pattern.
 * @author Claude Code
 * @since 2025-06-19
 */
public class DefaultErrorClassifier implements ErrorClassifier {

    /**
     * Singleton instance
     */
    private static final DefaultErrorClassifier INSTANCE = new DefaultErrorClassifier();

    /**
     * Gets the singleton instance
     * @return the default error classifier
     */
    public static DefaultErrorClassifier getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor for singleton pattern
     */
    protected DefaultErrorClassifier() {
        // Singleton
    }

    /**
     * Classifies bytecode validation exceptions.
     * @param exception the bytecode validation exception to classify
     * @return the error type for validation errors
     */
    @Override
    public ErrorType classifyBytecodeValidationException(final BytecodeValidationException exception) {
        return ErrorType.VALIDATION_ERROR;
    }

    /**
     * Classifies instance update exceptions.
     * @param exception the instance update exception to classify
     * @return the error type for instance update errors
     */
    @Override
    public ErrorType classifyInstanceUpdateException(final InstanceUpdateException exception) {
        return ErrorType.INSTANCE_UPDATE_ERROR;
    }

    /**
     * Classifies hot-swap exceptions.
     * @param exception the hot-swap exception to classify
     * @return the error type for redefinition failures
     */
    @Override
    public ErrorType classifyHotSwapException(final HotSwapException exception) {
        return ErrorType.REDEFINITION_FAILURE;
    }

    /**
     * Classifies security exceptions.
     * @param exception the security exception to classify
     * @return the error type for security errors
     */
    @Override
    public ErrorType classifySecurityException(final SecurityException exception) {
        return ErrorType.SECURITY_ERROR;
    }

    /**
     * Classifies out of memory errors.
     * @param error the out of memory error to classify
     * @return the error type for critical system errors
     */
    @Override
    public ErrorType classifyOutOfMemoryError(final OutOfMemoryError error) {
        return ErrorType.CRITICAL_SYSTEM_ERROR;
    }

    /**
     * Classifies stack overflow errors.
     * @param error the stack overflow error to classify
     * @return the error type for critical system errors
     */
    @Override
    public ErrorType classifyStackOverflowError(final StackOverflowError error) {
        return ErrorType.CRITICAL_SYSTEM_ERROR;
    }

    /**
     * Classifies no such file exceptions.
     * @param exception the no such file exception to classify
     * @return the error type for file system errors
     */
    @Override
    public ErrorType classifyNoSuchFileException(final java.nio.file.NoSuchFileException exception) {
        return ErrorType.FILE_SYSTEM_ERROR;
    }

    /**
     * Classifies access denied exceptions.
     * @param exception the access denied exception to classify
     * @return the error type for file system errors
     */
    @Override
    public ErrorType classifyAccessDeniedException(final java.nio.file.AccessDeniedException exception) {
        return ErrorType.FILE_SYSTEM_ERROR;
    }

    /**
     * Classifies event snapshot exceptions.
     * @param exception the event snapshot exception to classify
     * @return the error type based on the original cause, or unknown error if no cause
     */
    @Override
    public ErrorType classifyEventSnapshotException(final EventSnapshotException exception) {
        // Delegate to the original exception if available
        Throwable originalException = exception.getOriginalCause();
        if (originalException != null) {
            return classifyThrowable(originalException);
        }
        return ErrorType.UNKNOWN_ERROR;
    }

    /**
     * Classifies generic throwables.
     * @param throwable the throwable to classify
     * @return the error type for unknown errors
     */
    @Override
    public ErrorType classifyGenericThrowable(final Throwable throwable) {
        return ErrorType.UNKNOWN_ERROR;
    }

}