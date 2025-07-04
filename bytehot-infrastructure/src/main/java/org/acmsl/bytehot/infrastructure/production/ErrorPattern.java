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
 * Filename: ErrorPattern.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorPattern
 *
 * Responsibilities:
 *   - Define patterns for recognizing specific error types
 *   - Create appropriate error classifications based on patterns
 *   - Provide factory methods for common error patterns
 *   - Support error type, severity, and recoverability determination
 *
 * Collaborators:
 *   - ErrorClassification: The classification result created by this pattern
 *   - ErrorType: Enumeration of error types
 *   - ErrorSeverity: Enumeration of error severities
 *   - Recoverability: Enumeration of recoverability levels
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Represents a pattern for recognizing and classifying specific types of errors.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ErrorPattern {
    
    /**
     * The type of error this pattern recognizes.
     */
    private final ErrorType errorType;
    
    /**
     * The severity level for errors matching this pattern.
     */
    private final ErrorSeverity severity;
    
    /**
     * The recoverability level for errors matching this pattern.
     */
    private final Recoverability recoverability;
    
    /**
     * Whether errors matching this pattern require incident reporting.
     */
    private final boolean requiresIncidentReport;
    
    /**
     * Creates a new ErrorPattern.
     * @param errorType The error type
     * @param severity The error severity
     * @param recoverability The recoverability level
     * @param requiresIncidentReport Whether incident reporting is required
     */
    public ErrorPattern(final ErrorType errorType,
                       final ErrorSeverity severity,
                       final Recoverability recoverability,
                       final boolean requiresIncidentReport) {
        this.errorType = errorType;
        this.severity = severity;
        this.recoverability = recoverability;
        this.requiresIncidentReport = requiresIncidentReport;
    }
    
    /**
     * Creates an error classification for the given error based on this pattern.
     * @param error The error to classify
     * @return The error classification
     */
    public ErrorClassification createClassification(final Throwable error) {
        return ErrorClassification.builder()
            .errorType(errorType)
            .severity(severity)
            .recoverability(recoverability)
            .requiresIncidentReport(requiresIncidentReport)
            .error(error)
            .build();
    }
    
    /**
     * Creates a pattern for transient errors.
     * @param errorType The error type
     * @param severity The error severity
     * @return A new ErrorPattern for transient errors
     */
    public static ErrorPattern forTransientError(final ErrorType errorType, final ErrorSeverity severity) {
        return new ErrorPattern(errorType, severity, Recoverability.TRANSIENT, 
            severity == ErrorSeverity.HIGH || severity == ErrorSeverity.CRITICAL);
    }
    
    /**
     * Creates a pattern for permanent errors.
     * @param errorType The error type
     * @param severity The error severity
     * @return A new ErrorPattern for permanent errors
     */
    public static ErrorPattern forPermanentError(final ErrorType errorType, final ErrorSeverity severity) {
        return new ErrorPattern(errorType, severity, Recoverability.PERMANENT, true);
    }
    
    /**
     * Creates a pattern for resource-related errors.
     * @param errorType The error type
     * @param severity The error severity
     * @return A new ErrorPattern for resource errors
     */
    public static ErrorPattern forResourceError(final ErrorType errorType, final ErrorSeverity severity) {
        return new ErrorPattern(errorType, severity, Recoverability.TRANSIENT, true);
    }
    
    /**
     * Gets the error type for this pattern.
     * @return The error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets the severity level for this pattern.
     * @return The error severity
     */
    public ErrorSeverity getSeverity() {
        return severity;
    }
    
    /**
     * Gets the recoverability level for this pattern.
     * @return The recoverability level
     */
    public Recoverability getRecoverability() {
        return recoverability;
    }
    
    /**
     * Checks if errors matching this pattern require incident reporting.
     * @return true if incident reporting is required, false otherwise
     */
    public boolean requiresIncidentReport() {
        return requiresIncidentReport;
    }
}