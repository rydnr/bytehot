/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ErrorSeverity.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: ErrorSeverity
 *
 * Responsibilities:
 *   - Define severity levels for errors in any application
 *   - Support error prioritization and alerting decisions
 *   - Provide standard severity hierarchy for error handling
 *
 * Collaborators:
 *   - ErrorHandler: Uses severity for error processing
 *   - OperationResult: Contains severity information
 */
package org.acmsl.commons.patterns;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Enumeration of error severity levels for application error handling.
 * Provides a standard hierarchy for categorizing errors by their impact and urgency.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
public enum ErrorSeverity {
    
    /**
     * Informational messages (not really errors).
     * Used for debugging or informational purposes.
     */
    INFO(0, "Informational"),
    
    /**
     * Warning level - potential issues that don't prevent operation.
     * The operation can continue but attention may be needed.
     */
    WARNING(1, "Warning"),
    
    /**
     * Error level - operation failed but system can continue.
     * Individual operations fail but overall system stability is maintained.
     */
    ERROR(2, "Error"),
    
    /**
     * Critical level - system stability is at risk.
     * Significant functionality is impaired and immediate attention is required.
     */
    CRITICAL(3, "Critical"),
    
    /**
     * Fatal level - immediate shutdown or intervention required.
     * System cannot continue operating safely.
     */
    FATAL(4, "Fatal");

    /**
     * Numeric severity level for comparison purposes.
     */
    private final int level;

    /**
     * Human-readable description of the severity.
     */
    @NonNull
    private final String description;

    /**
     * Constructor for severity levels.
     * @param level The numeric severity level
     * @param description The human-readable description
     */
    ErrorSeverity(final int level, @NonNull final String description) {
        this.level = level;
        this.description = description;
    }

    /**
     * Gets the numeric severity level.
     * @return The severity level (higher numbers indicate greater severity)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the human-readable description.
     * @return The severity description
     */
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this severity is more severe than another.
     * @param other The other severity to compare against
     * @return true if this severity is more severe
     */
    public boolean isMoreSevereThan(@NonNull final ErrorSeverity other) {
        return this.level > other.level;
    }

    /**
     * Checks if this severity is at least as severe as another.
     * @param other The other severity to compare against
     * @return true if this severity is at least as severe
     */
    public boolean isAtLeastAsSevereAs(@NonNull final ErrorSeverity other) {
        return this.level >= other.level;
    }
}