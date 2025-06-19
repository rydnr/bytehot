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
 * Filename: DefaultErrorSeverityAssessor.java
 *
 * Author: Claude Code
 *
 * Class name: DefaultErrorSeverityAssessor
 *
 * Responsibilities:
 *   - Default implementation of error severity assessment using double dispatch
 *   - Provides type-specific severity assessment for all supported exception types
 *
 * Collaborators:
 *   - ErrorSeverityAssessor: Interface this class implements
 *   - ErrorSeverity: Enumeration of error severities
 */
package org.acmsl.bytehot.domain;

/**
 * Default implementation of error severity assessment using double dispatch pattern.
 * @author Claude Code
 * @since 2025-06-19
 */
public class DefaultErrorSeverityAssessor implements ErrorSeverityAssessor {

    /**
     * Singleton instance
     */
    private static final DefaultErrorSeverityAssessor INSTANCE = new DefaultErrorSeverityAssessor();

    /**
     * Gets the singleton instance
     * @return the default error severity assessor
     */
    public static DefaultErrorSeverityAssessor getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor for singleton pattern
     */
    protected DefaultErrorSeverityAssessor() {
        // Singleton
    }

    @Override
    public ErrorSeverity assessBytecodeValidationException(final BytecodeValidationException exception) {
        return ErrorSeverity.WARNING; // Validation errors are typically recoverable
    }

    @Override
    public ErrorSeverity assessInstanceUpdateException(final InstanceUpdateException exception) {
        return ErrorSeverity.ERROR; // Instance update failures are more serious
    }

    @Override
    public ErrorSeverity assessHotSwapException(final HotSwapException exception) {
        return ErrorSeverity.ERROR; // Hot-swap failures are significant
    }

    @Override
    public ErrorSeverity assessSecurityException(final SecurityException exception) {
        return ErrorSeverity.ERROR; // Security issues are always serious
    }

    @Override
    public ErrorSeverity assessOutOfMemoryError(final OutOfMemoryError error) {
        return ErrorSeverity.CRITICAL; // Memory errors are critical
    }

    @Override
    public ErrorSeverity assessStackOverflowError(final StackOverflowError error) {
        return ErrorSeverity.CRITICAL; // Stack overflow is critical
    }

    @Override
    public ErrorSeverity assessIllegalArgumentException(final IllegalArgumentException exception) {
        return ErrorSeverity.WARNING; // Usually indicates parameter validation issues
    }

    @Override
    public ErrorSeverity assessIllegalStateException(final IllegalStateException exception) {
        return ErrorSeverity.WARNING; // Usually indicates state validation issues
    }

    @Override
    public ErrorSeverity assessRuntimeException(final RuntimeException exception) {
        return ErrorSeverity.ERROR; // Generic runtime errors are concerning
    }

    @Override
    public ErrorSeverity assessEventSnapshotException(final EventSnapshotException exception) {
        // Delegate to the original exception if available
        Throwable originalException = exception.getOriginalException();
        if (originalException != null) {
            return assessThrowable(originalException);
        }
        return ErrorSeverity.ERROR; // Default for unknown exceptions
    }

    @Override
    public ErrorSeverity assessGenericThrowable(final Throwable throwable) {
        return ErrorSeverity.ERROR; // Safe default for unknown throwables
    }

}