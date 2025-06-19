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
 * Filename: ErrorContext.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorContext
 *
 * Responsibilities:
 *   - Capture comprehensive environmental context when errors occur
 *   - Preserve system state and configuration for error reproduction
 *   - Provide contextual information for debugging and analysis
 *   - Support serialization for bug reports and test generation
 *
 * Collaborators:
 *   - UserId: User context at time of error
 *   - UserContextResolver: Current user context information
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.UserId;
import org.acmsl.bytehot.domain.UserContextResolver;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;

/**
 * Comprehensive error context capturing all relevant environmental information
 * at the time an error occurred, enabling precise reproduction and debugging.
 * @author Claude Code
 * @since 2025-06-19
 */
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Getter
public class ErrorContext {

    /**
     * When this error context was captured
     */
    @NonNull
    private final Instant capturedAt;

    /**
     * User associated with the error (if any)
     */
    @Nullable
    private final UserId userId;

    /**
     * Thread where the error occurred
     */
    @NonNull
    private final String threadName;

    /**
     * Thread ID for precise identification
     */
    private final long threadId;

    /**
     * Thread state when error occurred
     */
    private final Thread.@NonNull State threadState;

    /**
     * JVM system properties at error time
     */
    @NonNull
    private final Map<String, String> systemProperties;

    /**
     * Environment variables relevant to the error
     */
    @NonNull
    private final Map<String, String> environmentVariables;

    /**
     * Memory information at error time
     */
    @NonNull
    private final MemoryInfo memoryInfo;

    /**
     * Class loading context
     */
    @NonNull
    private final String classLoaderInfo;

    /**
     * Stack trace where error was captured
     */
    @NonNull
    private final StackTraceElement[] stackTrace;

    /**
     * ByteHot-specific context information
     */
    @NonNull
    private final Map<String, Object> byteHotContext;

    /**
     * Additional custom context data
     */
    @NonNull
    private final Map<String, Object> customContext;

    /**
     * Memory information snapshot
     */
    @RequiredArgsConstructor
    @Builder
    @EqualsAndHashCode
    @ToString
    @Getter
    public static class MemoryInfo {
        /**
         * Total heap memory in bytes
         */
        private final long totalHeapMemory;

        /**
         * Used heap memory in bytes
         */
        private final long usedHeapMemory;

        /**
         * Maximum heap memory in bytes
         */
        private final long maxHeapMemory;

        /**
         * Free heap memory in bytes
         */
        private final long freeHeapMemory;

        /**
         * Number of garbage collection cycles
         */
        private final long gcCount;

        /**
         * Time spent in garbage collection (milliseconds)
         */
        private final long gcTime;
    }

    /**
     * Captures current error context automatically
     * @return complete error context for the current state
     */
    @NonNull
    public static ErrorContext capture() {
        Thread currentThread = Thread.currentThread();
        Runtime runtime = Runtime.getRuntime();
        
        // Capture memory information
        MemoryInfo memoryInfo = MemoryInfo.builder()
            .totalHeapMemory(runtime.totalMemory())
            .usedHeapMemory(runtime.totalMemory() - runtime.freeMemory())
            .maxHeapMemory(runtime.maxMemory())
            .freeHeapMemory(runtime.freeMemory())
            .gcCount(getGarbageCollectionCount())
            .gcTime(getGarbageCollectionTime())
            .build();

        // Get current user context
        UserId currentUser = UserContextResolver.getCurrentUserOrNull();

        // Capture system properties (filtered for relevance)
        Map<String, String> relevantSystemProps = filterRelevantSystemProperties();
        
        // Capture environment variables (filtered for security)
        Map<String, String> relevantEnvVars = filterRelevantEnvironmentVariables();

        return ErrorContext.builder()
            .capturedAt(Instant.now())
            .userId(currentUser)
            .threadName(currentThread.getName())
            .threadId(currentThread.getId())
            .threadState(currentThread.getState())
            .systemProperties(relevantSystemProps)
            .environmentVariables(relevantEnvVars)
            .memoryInfo(memoryInfo)
            .classLoaderInfo(currentThread.getContextClassLoader().toString())
            .stackTrace(currentThread.getStackTrace())
            .byteHotContext(captureByteHotContext())
            .customContext(Map.of())
            .build();
    }

    /**
     * Gets the memory usage percentage
     * @return used memory as percentage of total (0.0 to 1.0)
     */
    public double getMemoryUsagePercentage() {
        if (memoryInfo.getTotalHeapMemory() == 0) {
            return 0.0;
        }
        return (double) memoryInfo.getUsedHeapMemory() / memoryInfo.getTotalHeapMemory();
    }

    /**
     * Checks if memory usage is high (> 80%)
     * @return true if memory usage is concerning
     */
    public boolean isHighMemoryUsage() {
        return getMemoryUsagePercentage() > 0.8;
    }

    /**
     * Gets a formatted description of the error context
     * @return human-readable context summary
     */
    @NonNull
    public String getContextSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("ErrorContext[")
            .append("thread=").append(threadName)
            .append(", user=").append(userId != null ? userId.getDisplayName() : "anonymous")
            .append(", memory=").append(String.format("%.1f%%", getMemoryUsagePercentage() * 100))
            .append(", time=").append(capturedAt)
            .append("]");
        return summary.toString();
    }

    /**
     * Adds custom context information
     * @param key context key
     * @param value context value
     * @return updated error context
     */
    @NonNull
    public ErrorContext withCustomContext(@NonNull final String key, @NonNull final Object value) {
        Map<String, Object> updatedContext = new java.util.HashMap<>(customContext);
        updatedContext.put(key, value);
        return this.toBuilder()
            .customContext(updatedContext)
            .build();
    }

    /**
     * Gets the stack trace depth where error was captured
     * @return number of stack frames
     */
    public int getStackDepth() {
        return stackTrace.length;
    }

    /**
     * Gets the method where error context was captured
     * @return method name and class, or "unknown" if not available
     */
    @NonNull
    public String getCaptureLocation() {
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            return element.getClassName() + "." + element.getMethodName() + 
                   "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
        }
        return "unknown";
    }

    /**
     * Helper method to filter system properties for relevance and security
     */
    @NonNull
    private static Map<String, String> filterRelevantSystemProperties() {
        Properties props = System.getProperties();
        return props.stringPropertyNames().stream()
            .filter(key -> isRelevantSystemProperty(key))
            .collect(java.util.stream.Collectors.toMap(
                key -> key,
                props::getProperty
            ));
    }

    /**
     * Helper method to filter environment variables for relevance and security
     */
    @NonNull
    private static Map<String, String> filterRelevantEnvironmentVariables() {
        return System.getenv().entrySet().stream()
            .filter(entry -> isRelevantEnvironmentVariable(entry.getKey()))
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    /**
     * Checks if a system property is relevant for error context
     */
    private static boolean isRelevantSystemProperty(@NonNull final String key) {
        return key.startsWith("java.") || 
               key.startsWith("os.") || 
               key.startsWith("user.") ||
               key.contains("bytehot") ||
               key.contains("memory") ||
               key.contains("gc");
    }

    /**
     * Checks if an environment variable is relevant and safe to capture
     */
    private static boolean isRelevantEnvironmentVariable(@NonNull final String key) {
        String lowerKey = key.toLowerCase();
        return (lowerKey.contains("java") || 
                lowerKey.contains("path") || 
                lowerKey.contains("bytehot")) &&
               !lowerKey.contains("password") &&
               !lowerKey.contains("secret") &&
               !lowerKey.contains("key");
    }

    /**
     * Captures ByteHot-specific context information
     */
    @NonNull
    private static Map<String, Object> captureByteHotContext() {
        Map<String, Object> context = new java.util.HashMap<>();
        context.put("userContext", UserContextResolver.hasUserContext());
        context.put("contextDescription", UserContextResolver.getContextDescription());
        // Add more ByteHot-specific context as needed
        return context;
    }

    /**
     * Gets garbage collection count (simplified implementation)
     */
    private static long getGarbageCollectionCount() {
        try {
            return java.lang.management.ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(java.lang.management.GarbageCollectorMXBean::getCollectionCount)
                .sum();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Gets garbage collection time (simplified implementation)
     */
    private static long getGarbageCollectionTime() {
        try {
            return java.lang.management.ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(java.lang.management.GarbageCollectorMXBean::getCollectionTime)
                .sum();
        } catch (Exception e) {
            return -1;
        }
    }
}