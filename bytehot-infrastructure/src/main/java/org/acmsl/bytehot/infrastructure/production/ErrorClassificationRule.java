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
 * Filename: ErrorClassificationRule.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorClassificationRule
 *
 * Responsibilities:
 *   - Define interface for error classification rules
 *   - Support dynamic error classification refinement
 *   - Enable context-based classification adjustments
 *
 * Collaborators:
 *   - ErrorClassification: The classification to be refined
 *   - OperationContext: Context information for rule evaluation
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Interface for error classification rules that can refine error classifications.
 * @author Claude Code
 * @since 2025-07-04
 */
public interface ErrorClassificationRule {
    
    /**
     * Checks if this rule applies to the given error and context.
     * @param error The error to evaluate
     * @param context The operation context
     * @return true if the rule applies, false otherwise
     */
    boolean applies(Throwable error, OperationContext context);
    
    /**
     * Applies this rule to refine the error classification.
     * @param classification The current classification
     * @param error The original error
     * @param context The operation context
     * @return The refined classification
     */
    ErrorClassification apply(ErrorClassification classification, Throwable error, OperationContext context);
}