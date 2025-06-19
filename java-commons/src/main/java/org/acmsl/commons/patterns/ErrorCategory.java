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
 * Filename: ErrorCategory.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Class name: ErrorCategory
 *
 * Responsibilities:
 *   - Define interface for error categorization
 *   - Provide contract for error classification systems
 *   - Support pluggable error taxonomy
 *
 * Collaborators:
 *   - ErrorSeverity: Associated default severity for categories
 *   - RecoveryStrategy: Recommended recovery approach for categories
 */
package org.acmsl.commons.patterns;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Interface for error categorization in application error handling.
 * Allows applications to define their own error taxonomies while maintaining
 * consistent error handling patterns.
 * @author Claude (Anthropic AI)
 * @since 2025-06-19
 */
public interface ErrorCategory {

    /**
     * Gets the name of this error category.
     * @return The category name
     */
    @NonNull
    String name();

    /**
     * Gets the default severity for errors in this category.
     * @return The default error severity
     */
    @NonNull
    ErrorSeverity getDefaultSeverity();

    /**
     * Gets the recommended recovery strategy for this error category.
     * @return The recommended recovery strategy
     */
    @NonNull
    RecoveryStrategy getRecommendedStrategy();

    /**
     * Gets a human-readable description of this error category.
     * @return The category description
     */
    @NonNull
    String getDescription();

    /**
     * Checks if errors in this category are retryable.
     * @return true if operations can be retried for this category
     */
    default boolean isRetryable() {
        return getRecommendedStrategy() == RecoveryStrategy.RETRY_OPERATION;
    }

    /**
     * Checks if errors in this category are considered critical.
     * @return true if this category represents critical errors
     */
    default boolean isCritical() {
        return getDefaultSeverity().isAtLeastAsSevereAs(ErrorSeverity.CRITICAL);
    }
}