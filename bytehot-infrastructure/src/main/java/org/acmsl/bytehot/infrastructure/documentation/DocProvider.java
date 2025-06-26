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
 * Filename: DocProvider.java
 *
 * Author: Claude Code
 *
 * Class name: DocProvider
 *
 * Responsibilities:
 *   - Centralized documentation URL generation and Flow detection engine
 *   - Analyze runtime context to determine current operational Flow
 *   - Generate contextual documentation links based on detected Flow
 *   - Provide static methods for integration with DocLinkAvailable interface
 *
 * Collaborators:
 *   - DocLinkAvailable: Uses this provider for URL generation
 *   - FlowDetector: Analyzes runtime context for Flow identification
 */
package org.acmsl.bytehot.infrastructure.documentation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Centralized documentation URL generation and Flow detection engine.
 * Provides static methods for generating documentation URLs based on runtime context.
 * @author Claude Code
 * @since 2025-06-26
 */
public final class DocProvider {

    /**
     * Base URL for ByteHot documentation (GitHub Pages).
     */
    protected static final String DOCUMENTATION_BASE_URL = 
        "https://rydnr.github.io/bytehot/docs/";

    /**
     * Base URL for Flow-specific documentation.
     */
    protected static final String FLOWS_BASE_URL = 
        DOCUMENTATION_BASE_URL + "flows/";

    /**
     * Base URL for class-specific documentation.
     */
    protected static final String CLASSES_BASE_URL = 
        DOCUMENTATION_BASE_URL + "classes/";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DocProvider() {
        // Utility class
    }

    /**
     * Gets the documentation URL for a specific class.
     * @param clazz the class to get documentation for
     * @return documentation URL for the class
     */
    @NonNull
    public static String getDocumentationUrl(@NonNull final Class<?> clazz) {
        final String className = clazz.getSimpleName();
        final String packagePath = clazz.getPackage() != null ? 
            clazz.getPackage().getName().replace('.', '/') + "/" : "";
        
        return CLASSES_BASE_URL + packagePath + className + ".html";
    }

    /**
     * Gets the documentation URL for a specific method of a class.
     * @param clazz the class containing the method
     * @param methodName the name of the method
     * @return documentation URL for the method
     */
    @NonNull
    public static String getMethodDocumentationUrl(@NonNull final Class<?> clazz, 
                                                  @Nullable final String methodName) {
        final String baseUrl = getDocumentationUrl(clazz);
        
        if (methodName == null || methodName.trim().isEmpty()) {
            return baseUrl;
        }
        
        // Sanitize method name for URL
        final String sanitizedMethodName = sanitizeForUrl(methodName);
        return baseUrl + "#" + sanitizedMethodName;
    }

    /**
     * Gets the runtime Flow documentation URL based on current context.
     * Analyzes runtime context to provide contextual documentation.
     * @param context the object providing context for Flow detection
     * @return Flow-aware documentation URL
     */
    @NonNull
    public static String getRuntimeFlowDocumentationUrl(@NonNull final Object context) {
        // Detect current Flow context
        final FlowContext detectedFlow = detectCurrentFlow(context);
        
        if (detectedFlow != null) {
            return FLOWS_BASE_URL + detectedFlow.getFlowName() + ".html";
        }
        
        // Fallback to general documentation if no specific Flow detected
        return DOCUMENTATION_BASE_URL + "getting-started.html";
    }

    /**
     * Detects the current Flow context based on runtime analysis.
     * @param context the object providing context for analysis
     * @return detected Flow context or null if no specific Flow identified
     */
    @Nullable
    protected static FlowContext detectCurrentFlow(@NonNull final Object context) {
        // Analyze call stack for Flow patterns
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // Look for File monitoring patterns
        for (final StackTraceElement element : stackTrace) {
            final String className = element.getClassName();
            final String methodName = element.getMethodName();
            
            if (className.contains("FileWatcher") || className.contains("ClassFileChanged")) {
                return new FlowContext("file-change-detection", 0.8);
            }
            
            if (className.contains("Configuration") || methodName.contains("config")) {
                return new FlowContext("configuration-management", 0.7);
            }
            
            if (className.contains("HotSwap") || className.contains("BytecodeReload")) {
                return new FlowContext("hotswap-operations", 0.9);
            }
            
            if (className.contains("Test") && !className.contains("TestDocumented")) {
                return new FlowContext("testing-workflow", 0.6);
            }
        }
        
        // Check class type for additional context
        final String contextClassName = context.getClass().getSimpleName();
        if (contextClassName.contains("Event")) {
            return new FlowContext("event-processing", 0.5);
        }
        
        return null; // No specific Flow detected
    }

    /**
     * Sanitizes a string for use in URLs.
     * @param input the input string to sanitize
     * @return sanitized string safe for URL usage
     */
    @NonNull
    protected static String sanitizeForUrl(@NonNull final String input) {
        return input.replaceAll("[^a-zA-Z0-9_-]", "_")
                   .toLowerCase()
                   .replaceAll("_+", "_")
                   .replaceAll("^_|_$", "");
    }

    /**
     * Represents a detected Flow context with confidence scoring.
     */
    protected static class FlowContext {
        @NonNull
        private final String flowName;
        private final double confidence;

        protected FlowContext(@NonNull final String flowName, final double confidence) {
            this.flowName = flowName;
            this.confidence = Math.max(0.0, Math.min(1.0, confidence)); // Clamp to [0,1]
        }

        @NonNull
        protected String getFlowName() {
            return flowName;
        }

        protected double getConfidence() {
            return confidence;
        }
    }
}