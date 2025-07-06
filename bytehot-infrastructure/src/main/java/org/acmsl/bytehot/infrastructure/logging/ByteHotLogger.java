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
 * Filename: ByteHotLogger.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotLogger
 *
 * Responsibilities:
 *   - Provide comprehensive logging capabilities for ByteHot operations
 *   - Implement structured logging with contextual information
 *   - Support multiple log levels and output formats
 *   - Integrate with enterprise logging systems and audit trails
 *
 * Collaborators:
 *   - AuditTrail: Records audit events for compliance
 *   - UserContextResolver: Provides user context for log entries
 *   - PerformanceMonitor: Logs performance metrics and alerts
 *   - LogAppender: Handles log output to various destinations
 */
package org.acmsl.bytehot.infrastructure.logging;

import org.acmsl.bytehot.domain.UserContextResolver;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Enterprise-grade logging system for ByteHot operations.
 * Provides structured logging, audit trails, and compliance features.
 * @author Claude Code
 * @since 2025-07-06
 */
public class ByteHotLogger {

    private static final Map<String, ByteHotLogger> LOGGERS = new ConcurrentHashMap<>();
    private static final LogConfiguration GLOBAL_CONFIG = LogConfiguration.defaultConfiguration();
    
    private final String loggerName;
    private final ConcurrentLinkedQueue<LogEntry> logBuffer = new ConcurrentLinkedQueue<>();
    private final AtomicLong sequenceNumber = new AtomicLong(0);
    
    private static final ExecutorService logWriterExecutor = 
        Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "ByteHot-Logger-Writer");
            t.setDaemon(true);
            return t;
        });
    
    private static final ScheduledExecutorService logMaintenanceExecutor = 
        Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "ByteHot-Logger-Maintenance");
            t.setDaemon(true);
            return t;
        });
    
    static {
        // Start log maintenance tasks
        startLogMaintenance();
    }
    
    private ByteHotLogger(final String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Gets a logger instance for the specified name.
     * This method can be hot-swapped to change logger creation behavior.
     * @param name Logger name (typically class name)
     * @return Logger instance
     */
    public static ByteHotLogger getLogger(final String name) {
        return LOGGERS.computeIfAbsent(name, ByteHotLogger::new);
    }

    /**
     * Gets a logger instance for the specified class.
     * @param clazz Class to create logger for
     * @return Logger instance
     */
    public static ByteHotLogger getLogger(final Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Logs an audit event for compliance and governance.
     * This method can be hot-swapped to change audit logging behavior.
     * @param action Action being performed
     * @param resource Resource being accessed
     * @param outcome Success or failure outcome
     * @param details Additional details about the action
     */
    public void audit(final String action, final String resource, final AuditOutcome outcome, final String details) {
        if (!GLOBAL_CONFIG.isAuditEnabled()) {
            return;
        }
        
        final AuditEntry auditEntry = new AuditEntry(
            generateEventId(),
            Instant.now(),
            UserContextResolver.getCurrentUser().getValue(),
            action,
            resource,
            outcome,
            details,
            getContextualInformation()
        );
        
        logEntry(LogLevel.AUDIT, "AUDIT: " + action + " on " + resource + " - " + outcome, auditEntry);
        
        // Send to audit trail system
        AuditTrail.getInstance().recordAuditEvent(auditEntry);
    }

    /**
     * Logs a security event for monitoring and alerting.
     * This method can be hot-swapped to change security logging behavior.
     * @param eventType Type of security event
     * @param severity Severity level of the security event
     * @param message Description of the security event
     * @param context Additional context information
     */
    public void security(final SecurityEventType eventType, final SecuritySeverity severity, 
                        final String message, final Map<String, Object> context) {
        
        if (!GLOBAL_CONFIG.isSecurityLoggingEnabled()) {
            return;
        }
        
        final SecurityLogEntry securityEntry = new SecurityLogEntry(
            generateEventId(),
            Instant.now(),
            UserContextResolver.getCurrentUser().getValue(),
            eventType,
            severity,
            message,
            context,
            getSecurityContext()
        );
        
        final LogLevel logLevel = mapSecuritySeverityToLogLevel(severity);
        logEntry(logLevel, "SECURITY [" + eventType + "]: " + message, securityEntry);
        
        // Alert on critical security events
        if (severity == SecuritySeverity.CRITICAL) {
            triggerSecurityAlert(securityEntry);
        }
    }

    /**
     * Logs a hot-swap operation for operational monitoring.
     * This method can be hot-swapped to change hot-swap logging behavior.
     * @param operation Type of hot-swap operation
     * @param className Class being hot-swapped
     * @param success Whether the operation succeeded
     * @param durationMs Duration of the operation
     * @param details Additional operation details
     */
    public void hotSwap(final HotSwapOperationType operation, final String className, 
                       final boolean success, final long durationMs, final String details) {
        
        final HotSwapLogEntry hotSwapEntry = new HotSwapLogEntry(
            generateEventId(),
            Instant.now(),
            UserContextResolver.getCurrentUser().getValue(),
            operation,
            className,
            success,
            durationMs,
            details,
            getHotSwapContext()
        );
        
        final LogLevel logLevel = success ? LogLevel.INFO : LogLevel.ERROR;
        final String message = String.format("HOT-SWAP [%s]: %s %s in %dms - %s", 
            operation, className, success ? "SUCCESS" : "FAILED", durationMs, details);
        
        logEntry(logLevel, message, hotSwapEntry);
    }

    /**
     * Logs a performance metric or alert.
     * This method can be hot-swapped to change performance logging behavior.
     * @param metricName Name of the performance metric
     * @param value Metric value
     * @param threshold Alert threshold if applicable
     * @param alertLevel Alert level if threshold exceeded
     */
    public void performance(final String metricName, final double value, 
                           final Double threshold, final AlertLevel alertLevel) {
        
        if (!GLOBAL_CONFIG.isPerformanceLoggingEnabled()) {
            return;
        }
        
        final PerformanceLogEntry performanceEntry = new PerformanceLogEntry(
            generateEventId(),
            Instant.now(),
            metricName,
            value,
            threshold,
            alertLevel,
            getPerformanceContext()
        );
        
        LogLevel logLevel = LogLevel.DEBUG;
        String message = String.format("PERFORMANCE [%s]: %.2f", metricName, value);
        
        if (threshold != null && alertLevel != null && value > threshold) {
            logLevel = mapAlertLevelToLogLevel(alertLevel);
            message += String.format(" (ALERT: %.2f > %.2f)", value, threshold);
        }
        
        logEntry(logLevel, message, performanceEntry);
    }

    /**
     * Logs an informational message.
     * This method can be hot-swapped to change info logging behavior.
     * @param message Log message
     * @param args Message arguments for formatting
     */
    public void info(final String message, final Object... args) {
        if (isLevelEnabled(LogLevel.INFO)) {
            logEntry(LogLevel.INFO, formatMessage(message, args), null);
        }
    }

    /**
     * Logs a warning message.
     * This method can be hot-swapped to change warning logging behavior.
     * @param message Log message
     * @param args Message arguments for formatting
     */
    public void warn(final String message, final Object... args) {
        if (isLevelEnabled(LogLevel.WARN)) {
            logEntry(LogLevel.WARN, formatMessage(message, args), null);
        }
    }

    /**
     * Logs an error message.
     * This method can be hot-swapped to change error logging behavior.
     * @param message Log message
     * @param throwable Exception associated with the error
     * @param args Message arguments for formatting
     */
    public void error(final String message, final Throwable throwable, final Object... args) {
        if (isLevelEnabled(LogLevel.ERROR)) {
            final String formattedMessage = formatMessage(message, args);
            final ErrorLogEntry errorEntry = new ErrorLogEntry(
                generateEventId(),
                Instant.now(),
                formattedMessage,
                throwable,
                getErrorContext()
            );
            logEntry(LogLevel.ERROR, formattedMessage, errorEntry);
        }
    }

    /**
     * Logs a debug message.
     * This method can be hot-swapped to change debug logging behavior.
     * @param message Log message
     * @param args Message arguments for formatting
     */
    public void debug(final String message, final Object... args) {
        if (isLevelEnabled(LogLevel.DEBUG)) {
            logEntry(LogLevel.DEBUG, formatMessage(message, args), null);
        }
    }

    /**
     * Logs a trace message.
     * This method can be hot-swapped to change trace logging behavior.
     * @param message Log message
     * @param args Message arguments for formatting
     */
    public void trace(final String message, final Object... args) {
        if (isLevelEnabled(LogLevel.TRACE)) {
            logEntry(LogLevel.TRACE, formatMessage(message, args), null);
        }
    }

    /**
     * Gets recent log entries for analysis.
     * @param maxEntries Maximum number of entries to return
     * @return List of recent log entries
     */
    public List<LogEntry> getRecentEntries(final int maxEntries) {
        return logBuffer.stream()
            .limit(maxEntries)
            .collect(Collectors.toList());
    }

    /**
     * Configures global logging settings.
     * This method can be hot-swapped to change logging configuration.
     * @param configuration New logging configuration
     */
    public static void configure(final LogConfiguration configuration) {
        GLOBAL_CONFIG.updateFrom(configuration);
        info("Global logging configuration updated");
    }

    /**
     * Flushes all pending log entries to configured appenders.
     */
    public static void flush() {
        // Force flush all buffered log entries
        logWriterExecutor.submit(() -> {
            LOGGERS.values().forEach(logger -> {
                while (!logger.logBuffer.isEmpty()) {
                    final LogEntry entry = logger.logBuffer.poll();
                    if (entry != null) {
                        writeLogEntry(entry);
                    }
                }
            });
        });
    }

    /**
     * Shuts down the logging system gracefully.
     */
    public static void shutdown() {
        flush();
        
        logWriterExecutor.shutdown();
        logMaintenanceExecutor.shutdown();
        
        try {
            if (!logWriterExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logWriterExecutor.shutdownNow();
            }
            if (!logMaintenanceExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                logMaintenanceExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            logWriterExecutor.shutdownNow();
            logMaintenanceExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Core log entry method.
     * This method can be hot-swapped to change core logging behavior.
     * @param level Log level
     * @param message Log message
     * @param structuredData Additional structured data
     */
    protected void logEntry(final LogLevel level, final String message, final Object structuredData) {
        if (!isLevelEnabled(level)) {
            return;
        }
        
        final LogEntry entry = new LogEntry(
            generateLogId(),
            Instant.now(),
            level,
            loggerName,
            Thread.currentThread().getName(),
            UserContextResolver.getCurrentUserOrNull() != null ? UserContextResolver.getCurrentUserOrNull().getValue() : null,
            message,
            structuredData,
            getLogContext()
        );
        
        // Add to buffer for recent entries
        logBuffer.offer(entry);
        
        // Maintain buffer size
        while (logBuffer.size() > GLOBAL_CONFIG.getMaxBufferSize()) {
            logBuffer.poll();
        }
        
        // Asynchronously write to appenders
        logWriterExecutor.submit(() -> writeLogEntry(entry));
    }

    /**
     * Writes a log entry to configured appenders.
     * This method can be hot-swapped to change log writing behavior.
     * @param entry Log entry to write
     */
    protected static void writeLogEntry(final LogEntry entry) {
        try {
            // Write to console if enabled
            if (GLOBAL_CONFIG.isConsoleLoggingEnabled()) {
                writeToConsole(entry);
            }
            
            // Write to file if enabled
            if (GLOBAL_CONFIG.isFileLoggingEnabled()) {
                writeToFile(entry);
            }
            
            // Write to structured log if enabled
            if (GLOBAL_CONFIG.isStructuredLoggingEnabled()) {
                writeToStructuredLog(entry);
            }
            
            // Send to external systems if configured
            if (GLOBAL_CONFIG.hasExternalAppenders()) {
                writeToExternalSystems(entry);
            }
            
        } catch (final Exception e) {
            // Fallback logging to stderr
            System.err.println("Failed to write log entry: " + e.getMessage());
            System.err.println("Original log: " + entry.getMessage());
        }
    }

    /**
     * Starts log maintenance tasks.
     * This method can be hot-swapped to change maintenance behavior.
     */
    protected static void startLogMaintenance() {
        // Schedule log file rotation
        logMaintenanceExecutor.scheduleAtFixedRate(
            ByteHotLogger::rotateLogFiles,
            1, 1, TimeUnit.HOURS
        );
        
        // Schedule log cleanup
        logMaintenanceExecutor.scheduleAtFixedRate(
            ByteHotLogger::cleanupOldLogs,
            6, 6, TimeUnit.HOURS
        );
        
        // Schedule metric reporting
        logMaintenanceExecutor.scheduleAtFixedRate(
            ByteHotLogger::reportLoggingMetrics,
            5, 5, TimeUnit.MINUTES
        );
    }

    // Helper methods
    
    protected boolean isLevelEnabled(final LogLevel level) {
        return level.ordinal() >= GLOBAL_CONFIG.getMinLevel().ordinal();
    }
    
    protected String formatMessage(final String message, final Object... args) {
        if (args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        } catch (final Exception e) {
            return message + " [FORMAT_ERROR: " + e.getMessage() + "]";
        }
    }
    
    protected String generateLogId() {
        return loggerName + "_" + sequenceNumber.incrementAndGet();
    }
    
    protected String generateEventId() {
        return "evt_" + System.currentTimeMillis() + "_" + System.nanoTime();
    }
    
    protected Map<String, Object> getContextualInformation() {
        final Map<String, Object> context = new java.util.HashMap<>();
        context.put("timestamp", Instant.now().toString());
        context.put("thread", Thread.currentThread().getName());
        context.put("logger", loggerName);
        return context;
    }
    
    protected Map<String, Object> getSecurityContext() {
        final Map<String, Object> context = getContextualInformation();
        context.put("sessionId", UserContextResolver.ensureUserSession());
        context.put("userAgent", System.getProperty("user.agent", "unknown"));
        context.put("remoteAddr", System.getProperty("remote.addr", "unknown"));
        return context;
    }
    
    protected Map<String, Object> getHotSwapContext() {
        final Map<String, Object> context = getContextualInformation();
        context.put("jvmVersion", System.getProperty("java.version"));
        context.put("bytehotVersion", "latest-SNAPSHOT");
        return context;
    }
    
    protected Map<String, Object> getPerformanceContext() {
        final Map<String, Object> context = getContextualInformation();
        final Runtime runtime = Runtime.getRuntime();
        context.put("memoryUsed", runtime.totalMemory() - runtime.freeMemory());
        context.put("memoryTotal", runtime.totalMemory());
        context.put("memoryMax", runtime.maxMemory());
        return context;
    }
    
    protected Map<String, Object> getErrorContext() {
        final Map<String, Object> context = getContextualInformation();
        context.put("stackTrace", Thread.currentThread().getStackTrace());
        return context;
    }
    
    protected Map<String, Object> getLogContext() {
        final Map<String, Object> context = new java.util.HashMap<>();
        context.put("hostname", System.getProperty("hostname", "unknown"));
        context.put("pid", ProcessHandle.current().pid());
        return context;
    }
    
    protected LogLevel mapSecuritySeverityToLogLevel(final SecuritySeverity severity) {
        return switch (severity) {
            case LOW -> LogLevel.INFO;
            case MEDIUM -> LogLevel.WARN;
            case HIGH -> LogLevel.ERROR;
            case CRITICAL -> LogLevel.ERROR;
        };
    }
    
    protected LogLevel mapAlertLevelToLogLevel(final AlertLevel alertLevel) {
        return switch (alertLevel) {
            case INFO -> LogLevel.INFO;
            case WARNING -> LogLevel.WARN;
            case CRITICAL -> LogLevel.ERROR;
        };
    }
    
    protected void triggerSecurityAlert(final SecurityLogEntry securityEntry) {
        // Send immediate alert for critical security events
        System.err.println("CRITICAL SECURITY ALERT: " + securityEntry.getMessage());
        // In a real implementation, this would integrate with alerting systems
    }
    
    protected static void writeToConsole(final LogEntry entry) {
        final String formatted = formatLogEntry(entry, GLOBAL_CONFIG.getConsoleFormat());
        if (entry.getLevel().ordinal() >= LogLevel.ERROR.ordinal()) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }
    }
    
    protected static void writeToFile(final LogEntry entry) {
        // File logging implementation
        // In a real implementation, this would write to rotating log files
    }
    
    protected static void writeToStructuredLog(final LogEntry entry) {
        // Structured logging (JSON, etc.) implementation
        // In a real implementation, this would write structured logs
    }
    
    protected static void writeToExternalSystems(final LogEntry entry) {
        // External system integration (ELK, Splunk, etc.)
        // In a real implementation, this would send to external logging systems
    }
    
    protected static String formatLogEntry(final LogEntry entry, final LogFormat format) {
        return switch (format) {
            case SIMPLE -> String.format("[%s] %s - %s", 
                entry.getLevel(), entry.getLoggerName(), entry.getMessage());
            case DETAILED -> String.format("%s [%s] %s [%s] %s - %s",
                entry.getTimestamp(), entry.getLevel(), entry.getThreadName(),
                entry.getLoggerName(), entry.getUserId(), entry.getMessage());
            case JSON -> formatAsJson(entry);
        };
    }
    
    protected static String formatAsJson(final LogEntry entry) {
        // JSON formatting implementation
        return String.format(
            "{\"timestamp\":\"%s\",\"level\":\"%s\",\"logger\":\"%s\",\"thread\":\"%s\",\"user\":\"%s\",\"message\":\"%s\"}",
            entry.getTimestamp(), entry.getLevel(), entry.getLoggerName(),
            entry.getThreadName(), entry.getUserId(), entry.getMessage()
        );
    }
    
    protected static void rotateLogFiles() {
        // Log file rotation implementation
    }
    
    protected static void cleanupOldLogs() {
        // Old log cleanup implementation
    }
    
    protected static void reportLoggingMetrics() {
        // Logging metrics reporting implementation
    }
    
    protected static void info(final String message) {
        System.out.println("[ByteHotLogger] " + message);
    }

    // Enums and supporting classes
    
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, AUDIT
    }

    public enum LogFormat {
        SIMPLE, DETAILED, JSON
    }

    public enum SecurityEventType {
        AUTHENTICATION_SUCCESS,
        AUTHENTICATION_FAILURE,
        AUTHORIZATION_FAILURE,
        PRIVILEGE_ESCALATION,
        SUSPICIOUS_ACTIVITY,
        DATA_ACCESS,
        CONFIGURATION_CHANGE,
        SECURITY_VIOLATION
    }

    public enum SecuritySeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum HotSwapOperationType {
        CLASS_REDEFINITION,
        CLASS_RETRANSFORMATION,
        CLASS_LOADING,
        CLASS_UNLOADING,
        BYTECODE_VALIDATION,
        INSTRUMENTATION_SETUP
    }

    public enum AuditOutcome {
        SUCCESS, FAILURE, PARTIAL_SUCCESS
    }

    public enum AlertLevel {
        INFO, WARNING, CRITICAL
    }

    // Static inner classes for data structures
    
    public static class LogEntry {
        private final String logId;
        private final Instant timestamp;
        private final LogLevel level;
        private final String loggerName;
        private final String threadName;
        private final String userId;
        private final String message;
        private final Object structuredData;
        private final Map<String, Object> context;

        public LogEntry(final String logId, final Instant timestamp, final LogLevel level,
                       final String loggerName, final String threadName, final String userId,
                       final String message, final Object structuredData, final Map<String, Object> context) {
            this.logId = logId;
            this.timestamp = timestamp;
            this.level = level;
            this.loggerName = loggerName;
            this.threadName = threadName;
            this.userId = userId;
            this.message = message;
            this.structuredData = structuredData;
            this.context = context;
        }

        public String getLogId() { return logId; }
        public Instant getTimestamp() { return timestamp; }
        public LogLevel getLevel() { return level; }
        public String getLoggerName() { return loggerName; }
        public String getThreadName() { return threadName; }
        public String getUserId() { return userId; }
        public String getMessage() { return message; }
        public Object getStructuredData() { return structuredData; }
        public Map<String, Object> getContext() { return context; }
    }

    public static class AuditEntry {
        private final String eventId;
        private final Instant timestamp;
        private final String userId;
        private final String action;
        private final String resource;
        private final AuditOutcome outcome;
        private final String details;
        private final Map<String, Object> context;

        public AuditEntry(final String eventId, final Instant timestamp, final String userId,
                         final String action, final String resource, final AuditOutcome outcome,
                         final String details, final Map<String, Object> context) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.userId = userId;
            this.action = action;
            this.resource = resource;
            this.outcome = outcome;
            this.details = details;
            this.context = context;
        }

        public String getEventId() { return eventId; }
        public Instant getTimestamp() { return timestamp; }
        public String getUserId() { return userId; }
        public String getAction() { return action; }
        public String getResource() { return resource; }
        public AuditOutcome getOutcome() { return outcome; }
        public String getDetails() { return details; }
        public Map<String, Object> getContext() { return context; }
    }

    public static class SecurityLogEntry {
        private final String eventId;
        private final Instant timestamp;
        private final String userId;
        private final SecurityEventType eventType;
        private final SecuritySeverity severity;
        private final String message;
        private final Map<String, Object> context;
        private final Map<String, Object> securityContext;

        public SecurityLogEntry(final String eventId, final Instant timestamp, final String userId,
                               final SecurityEventType eventType, final SecuritySeverity severity,
                               final String message, final Map<String, Object> context,
                               final Map<String, Object> securityContext) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.userId = userId;
            this.eventType = eventType;
            this.severity = severity;
            this.message = message;
            this.context = context;
            this.securityContext = securityContext;
        }

        public String getEventId() { return eventId; }
        public Instant getTimestamp() { return timestamp; }
        public String getUserId() { return userId; }
        public SecurityEventType getEventType() { return eventType; }
        public SecuritySeverity getSeverity() { return severity; }
        public String getMessage() { return message; }
        public Map<String, Object> getContext() { return context; }
        public Map<String, Object> getSecurityContext() { return securityContext; }
    }

    public static class HotSwapLogEntry {
        private final String eventId;
        private final Instant timestamp;
        private final String userId;
        private final HotSwapOperationType operation;
        private final String className;
        private final boolean success;
        private final long durationMs;
        private final String details;
        private final Map<String, Object> context;

        public HotSwapLogEntry(final String eventId, final Instant timestamp, final String userId,
                              final HotSwapOperationType operation, final String className,
                              final boolean success, final long durationMs, final String details,
                              final Map<String, Object> context) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.userId = userId;
            this.operation = operation;
            this.className = className;
            this.success = success;
            this.durationMs = durationMs;
            this.details = details;
            this.context = context;
        }

        public String getEventId() { return eventId; }
        public Instant getTimestamp() { return timestamp; }
        public String getUserId() { return userId; }
        public HotSwapOperationType getOperation() { return operation; }
        public String getClassName() { return className; }
        public boolean isSuccess() { return success; }
        public long getDurationMs() { return durationMs; }
        public String getDetails() { return details; }
        public Map<String, Object> getContext() { return context; }
    }

    public static class PerformanceLogEntry {
        private final String eventId;
        private final Instant timestamp;
        private final String metricName;
        private final double value;
        private final Double threshold;
        private final AlertLevel alertLevel;
        private final Map<String, Object> context;

        public PerformanceLogEntry(final String eventId, final Instant timestamp, final String metricName,
                                  final double value, final Double threshold, final AlertLevel alertLevel,
                                  final Map<String, Object> context) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.metricName = metricName;
            this.value = value;
            this.threshold = threshold;
            this.alertLevel = alertLevel;
            this.context = context;
        }

        public String getEventId() { return eventId; }
        public Instant getTimestamp() { return timestamp; }
        public String getMetricName() { return metricName; }
        public double getValue() { return value; }
        public Double getThreshold() { return threshold; }
        public AlertLevel getAlertLevel() { return alertLevel; }
        public Map<String, Object> getContext() { return context; }
    }

    public static class ErrorLogEntry {
        private final String eventId;
        private final Instant timestamp;
        private final String message;
        private final Throwable throwable;
        private final Map<String, Object> context;

        public ErrorLogEntry(final String eventId, final Instant timestamp, final String message,
                            final Throwable throwable, final Map<String, Object> context) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.message = message;
            this.throwable = throwable;
            this.context = context;
        }

        public String getEventId() { return eventId; }
        public Instant getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
        public Throwable getThrowable() { return throwable; }
        public Map<String, Object> getContext() { return context; }
    }

    public static class LogConfiguration {
        private LogLevel minLevel = LogLevel.INFO;
        private LogFormat consoleFormat = LogFormat.DETAILED;
        private boolean consoleLoggingEnabled = true;
        private boolean fileLoggingEnabled = true;
        private boolean structuredLoggingEnabled = false;
        private boolean auditEnabled = true;
        private boolean securityLoggingEnabled = true;
        private boolean performanceLoggingEnabled = true;
        private int maxBufferSize = 1000;
        private String logFilePattern = "bytehot-%d{yyyy-MM-dd}.log";
        private List<String> externalAppenders = new ArrayList<>();

        public static LogConfiguration defaultConfiguration() {
            return new LogConfiguration();
        }

        public void updateFrom(final LogConfiguration other) {
            this.minLevel = other.minLevel;
            this.consoleFormat = other.consoleFormat;
            this.consoleLoggingEnabled = other.consoleLoggingEnabled;
            this.fileLoggingEnabled = other.fileLoggingEnabled;
            this.structuredLoggingEnabled = other.structuredLoggingEnabled;
            this.auditEnabled = other.auditEnabled;
            this.securityLoggingEnabled = other.securityLoggingEnabled;
            this.performanceLoggingEnabled = other.performanceLoggingEnabled;
            this.maxBufferSize = other.maxBufferSize;
            this.logFilePattern = other.logFilePattern;
            this.externalAppenders = new ArrayList<>(other.externalAppenders);
        }

        // Getters and setters
        public LogLevel getMinLevel() { return minLevel; }
        public void setMinLevel(final LogLevel minLevel) { this.minLevel = minLevel; }

        public LogFormat getConsoleFormat() { return consoleFormat; }
        public void setConsoleFormat(final LogFormat consoleFormat) { this.consoleFormat = consoleFormat; }

        public boolean isConsoleLoggingEnabled() { return consoleLoggingEnabled; }
        public void setConsoleLoggingEnabled(final boolean consoleLoggingEnabled) { 
            this.consoleLoggingEnabled = consoleLoggingEnabled; 
        }

        public boolean isFileLoggingEnabled() { return fileLoggingEnabled; }
        public void setFileLoggingEnabled(final boolean fileLoggingEnabled) { 
            this.fileLoggingEnabled = fileLoggingEnabled; 
        }

        public boolean isStructuredLoggingEnabled() { return structuredLoggingEnabled; }
        public void setStructuredLoggingEnabled(final boolean structuredLoggingEnabled) { 
            this.structuredLoggingEnabled = structuredLoggingEnabled; 
        }

        public boolean isAuditEnabled() { return auditEnabled; }
        public void setAuditEnabled(final boolean auditEnabled) { this.auditEnabled = auditEnabled; }

        public boolean isSecurityLoggingEnabled() { return securityLoggingEnabled; }
        public void setSecurityLoggingEnabled(final boolean securityLoggingEnabled) { 
            this.securityLoggingEnabled = securityLoggingEnabled; 
        }

        public boolean isPerformanceLoggingEnabled() { return performanceLoggingEnabled; }
        public void setPerformanceLoggingEnabled(final boolean performanceLoggingEnabled) { 
            this.performanceLoggingEnabled = performanceLoggingEnabled; 
        }

        public int getMaxBufferSize() { return maxBufferSize; }
        public void setMaxBufferSize(final int maxBufferSize) { this.maxBufferSize = maxBufferSize; }

        public String getLogFilePattern() { return logFilePattern; }
        public void setLogFilePattern(final String logFilePattern) { this.logFilePattern = logFilePattern; }

        public List<String> getExternalAppenders() { return Collections.unmodifiableList(externalAppenders); }
        public void setExternalAppenders(final List<String> externalAppenders) { 
            this.externalAppenders = new ArrayList<>(externalAppenders); 
        }

        public boolean hasExternalAppenders() { return !externalAppenders.isEmpty(); }
    }
}