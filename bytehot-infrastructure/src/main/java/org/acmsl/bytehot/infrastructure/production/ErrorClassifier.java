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
 *   - Classify errors by type, severity, and recoverability
 *   - Identify transient vs permanent failures
 *   - Determine appropriate recovery strategies
 *   - Assess incident reporting requirements
 *
 * Collaborators:
 *   - ErrorClassification: Represents the classification result
 *   - OperationContext: Provides context about the failed operation
 *   - ErrorPattern: Defines patterns for recognizing error types
 */
package org.acmsl.bytehot.infrastructure.production;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classifies errors to determine their severity, type, and appropriate handling strategy.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ErrorClassifier {
    
    /**
     * Map of error patterns for classification.
     */
    private final Map<Class<? extends Throwable>, ErrorPattern> errorPatterns;
    
    /**
     * List of classification rules.
     */
    private final List<ErrorClassificationRule> classificationRules;
    
    /**
     * Creates a new ErrorClassifier with default patterns and rules.
     */
    public ErrorClassifier() {
        this.errorPatterns = initializeErrorPatterns();
        this.classificationRules = initializeClassificationRules();
    }
    
    /**
     * Classifies an error based on its type, message, and context.
     * @param error The error to classify
     * @param context The operation context
     * @return The error classification
     */
    public ErrorClassification classify(final Throwable error, final OperationContext context) {
        // Start with basic classification from error type
        ErrorClassification baseClassification = classifyByType(error);
        
        // Refine classification using context
        ErrorClassification contextualClassification = refineWithContext(baseClassification, context);
        
        // Apply classification rules
        ErrorClassification finalClassification = applyClassificationRules(contextualClassification, error, context);
        
        return finalClassification;
    }
    
    /**
     * Classifies an error based on its type.
     * @param error The error to classify
     * @return The basic error classification
     */
    protected ErrorClassification classifyByType(final Throwable error) {
        ErrorPattern pattern = errorPatterns.get(error.getClass());
        
        if (pattern != null) {
            return pattern.createClassification(error);
        }
        
        // Check superclasses and interfaces
        for (Map.Entry<Class<? extends Throwable>, ErrorPattern> entry : errorPatterns.entrySet()) {
            if (entry.getKey().isAssignableFrom(error.getClass())) {
                return entry.getValue().createClassification(error);
            }
        }
        
        // Default classification for unknown errors
        return ErrorClassification.builder()
            .errorType(ErrorType.UNKNOWN)
            .severity(ErrorSeverity.MEDIUM)
            .recoverability(Recoverability.UNKNOWN)
            .requiresIncidentReport(true)
            .error(error)
            .build();
    }
    
    /**
     * Refines classification using operation context.
     * @param baseClassification The base classification
     * @param context The operation context
     * @return The refined classification
     */
    protected ErrorClassification refineWithContext(final ErrorClassification baseClassification, 
                                                   final OperationContext context) {
        ErrorClassification.Builder builder = baseClassification.toBuilder();
        
        // Increase severity if operation is critical
        if (context.isCriticalOperation()) {
            ErrorSeverity currentSeverity = baseClassification.getSeverity();
            if (currentSeverity == ErrorSeverity.LOW) {
                builder.severity(ErrorSeverity.MEDIUM);
            } else if (currentSeverity == ErrorSeverity.MEDIUM) {
                builder.severity(ErrorSeverity.HIGH);
            }
        }
        
        // Check for retry context
        if (context.getRetryCount() > 0) {
            builder.previousRetries(context.getRetryCount());
            
            // If this is a repeated failure, it's likely not transient
            if (context.getRetryCount() >= 3) {
                builder.recoverability(Recoverability.PERMANENT);
            }
        }
        
        // Consider user impact
        if (context.hasUserImpact()) {
            builder.requiresIncidentReport(true);
        }
        
        return builder.build();
    }
    
    /**
     * Applies classification rules to determine final classification.
     * @param classification The current classification
     * @param error The original error
     * @param context The operation context
     * @return The final classification
     */
    protected ErrorClassification applyClassificationRules(final ErrorClassification classification,
                                                          final Throwable error,
                                                          final OperationContext context) {
        ErrorClassification result = classification;
        
        for (ErrorClassificationRule rule : classificationRules) {
            if (rule.applies(error, context)) {
                result = rule.apply(result, error, context);
            }
        }
        
        return result;
    }
    
    /**
     * Initializes default error patterns.
     * @return Map of error patterns
     */
    protected Map<Class<? extends Throwable>, ErrorPattern> initializeErrorPatterns() {
        Map<Class<? extends Throwable>, ErrorPattern> patterns = new ConcurrentHashMap<>();
        
        // TODO: Initialize error patterns once enum classes are properly compiled
        // For now, return empty map to allow compilation
        
        return patterns;
    }
    
    /**
     * Initializes classification rules.
     * @return List of classification rules
     */
    protected List<ErrorClassificationRule> initializeClassificationRules() {
        return List.of();
        // TODO: Implement classification rules
        // - ConsecutiveFailureRule: Multiple consecutive failures indicate permanent issue
        // - StartupFailureRule: Errors during system startup are critical
        // - HotSwapCompatibilityRule: Hot-swap compatibility errors are permanent
        // - ResourceExhaustionRule: Resource exhaustion requires immediate attention
    }
}