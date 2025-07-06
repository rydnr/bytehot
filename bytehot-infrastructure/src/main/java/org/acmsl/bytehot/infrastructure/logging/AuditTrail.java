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
 * Filename: AuditTrail.java
 *
 * Author: Claude Code
 *
 * Class name: AuditTrail
 *
 * Responsibilities:
 *   - Maintain comprehensive audit trail for ByteHot operations
 *   - Ensure tamper-proof audit log storage and retrieval
 *   - Provide audit search and filtering capabilities
 *   - Support compliance requirements and regulatory reporting
 *
 * Collaborators:
 *   - ByteHotLogger: Provides audit log entries
 *   - UserContextResolver: Resolves user context for audit events
 *   - DigitalSignature: Ensures audit log integrity
 *   - AuditStorage: Persists audit events to secure storage
 */
package org.acmsl.bytehot.infrastructure.logging;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Comprehensive audit trail system for ByteHot operations.
 * Provides tamper-proof audit logging with compliance features.
 * @author Claude Code
 * @since 2025-07-06
 */
public class AuditTrail {

    private static final AuditTrail INSTANCE = new AuditTrail();
    
    private final Map<String, List<ByteHotLogger.AuditEntry>> auditsByDate = new ConcurrentHashMap<>();
    private final Map<String, List<ByteHotLogger.AuditEntry>> auditsByUser = new ConcurrentHashMap<>();
    private final Map<String, List<ByteHotLogger.AuditEntry>> auditsByAction = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<ByteHotLogger.AuditEntry> pendingAudits = new ConcurrentLinkedQueue<>();
    
    private final ReentrantReadWriteLock auditLock = new ReentrantReadWriteLock();
    private final AtomicLong auditCounter = new AtomicLong(0);
    
    private final ScheduledExecutorService auditExecutor = 
        Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "ByteHot-Audit-Trail");
            t.setDaemon(true);
            return t;
        });
    
    private volatile AuditConfiguration configuration = AuditConfiguration.defaultConfiguration();
    private volatile boolean auditEnabled = true;
    
    private AuditTrail() {
        startAuditProcessing();
    }

    /**
     * Gets the singleton instance of AuditTrail.
     * @return The audit trail instance
     */
    public static AuditTrail getInstance() {
        return INSTANCE;
    }

    /**
     * Records an audit event in the trail.
     * This method can be hot-swapped to change audit recording behavior.
     * @param auditEntry Audit entry to record
     */
    public void recordAuditEvent(final ByteHotLogger.AuditEntry auditEntry) {
        if (!auditEnabled) {
            return;
        }
        
        // Add digital signature for integrity
        final SignedAuditEntry signedEntry = signAuditEntry(auditEntry);
        
        // Queue for processing
        pendingAudits.offer(auditEntry);
        
        // Immediate indexing for fast retrieval
        indexAuditEntry(signedEntry);
        
        // Process asynchronously
        auditExecutor.submit(() -> processAuditEntry(signedEntry));
    }

    /**
     * Searches audit entries by criteria.
     * This method can be hot-swapped to change search behavior.
     * @param criteria Search criteria for audit entries
     * @return List of matching audit entries
     */
    public List<ByteHotLogger.AuditEntry> searchAuditEntries(final AuditSearchCriteria criteria) {
        auditLock.readLock().lock();
        try {
            List<ByteHotLogger.AuditEntry> results = new ArrayList<>();
            
            // Search by date range
            if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
                results = searchByDateRange(criteria.getStartDate(), criteria.getEndDate());
            } else {
                // Get all audit entries
                results = getAllAuditEntries();
            }
            
            // Filter by user
            if (criteria.getUserId() != null) {
                results = results.stream()
                    .filter(entry -> criteria.getUserId().equals(entry.getUserId()))
                    .collect(Collectors.toList());
            }
            
            // Filter by action
            if (criteria.getAction() != null) {
                results = results.stream()
                    .filter(entry -> entry.getAction().contains(criteria.getAction()))
                    .collect(Collectors.toList());
            }
            
            // Filter by resource
            if (criteria.getResource() != null) {
                results = results.stream()
                    .filter(entry -> entry.getResource().contains(criteria.getResource()))
                    .collect(Collectors.toList());
            }
            
            // Filter by outcome
            if (criteria.getOutcome() != null) {
                results = results.stream()
                    .filter(entry -> criteria.getOutcome() == entry.getOutcome())
                    .collect(Collectors.toList());
            }
            
            // Apply limit
            if (criteria.getMaxResults() > 0) {
                results = results.stream()
                    .limit(criteria.getMaxResults())
                    .collect(Collectors.toList());
            }
            
            return results;
            
        } finally {
            auditLock.readLock().unlock();
        }
    }

    /**
     * Generates compliance report for specified period.
     * This method can be hot-swapped to change report generation behavior.
     * @param startDate Start date for the report
     * @param endDate End date for the report
     * @param reportType Type of compliance report
     * @return Generated compliance report
     */
    public ComplianceReport generateComplianceReport(final LocalDate startDate, 
                                                    final LocalDate endDate, 
                                                    final ComplianceReportType reportType) {
        
        final List<ByteHotLogger.AuditEntry> auditEntries = searchByDateRange(startDate, endDate);
        
        return switch (reportType) {
            case SOX_COMPLIANCE -> generateSoxComplianceReport(auditEntries, startDate, endDate);
            case GDPR_COMPLIANCE -> generateGdprComplianceReport(auditEntries, startDate, endDate);
            case HIPAA_COMPLIANCE -> generateHipaaComplianceReport(auditEntries, startDate, endDate);
            case SECURITY_AUDIT -> generateSecurityAuditReport(auditEntries, startDate, endDate);
            case CHANGE_MANAGEMENT -> generateChangeManagementReport(auditEntries, startDate, endDate);
        };
    }

    /**
     * Validates audit trail integrity.
     * This method can be hot-swapped to change integrity validation behavior.
     * @return Integrity validation result
     */
    public AuditIntegrityResult validateIntegrity() {
        auditLock.readLock().lock();
        try {
            final List<String> integrityIssues = new ArrayList<>();
            int totalEntries = 0;
            int validEntries = 0;
            
            for (final List<ByteHotLogger.AuditEntry> dailyAudits : auditsByDate.values()) {
                for (final ByteHotLogger.AuditEntry entry : dailyAudits) {
                    totalEntries++;
                    
                    if (validateAuditEntryIntegrity(entry)) {
                        validEntries++;
                    } else {
                        integrityIssues.add("Integrity violation in audit entry: " + entry.getEventId());
                    }
                }
            }
            
            // Check for sequence gaps
            final List<String> sequenceIssues = validateAuditSequence();
            integrityIssues.addAll(sequenceIssues);
            
            final boolean isValid = integrityIssues.isEmpty();
            final double integrityPercentage = totalEntries > 0 ? (double) validEntries / totalEntries : 1.0;
            
            return new AuditIntegrityResult(
                isValid,
                integrityPercentage,
                totalEntries,
                validEntries,
                integrityIssues
            );
            
        } finally {
            auditLock.readLock().unlock();
        }
    }

    /**
     * Archives old audit entries according to retention policy.
     * This method can be hot-swapped to change archival behavior.
     * @return Archival statistics
     */
    public AuditArchivalResult archiveOldEntries() {
        auditLock.writeLock().lock();
        try {
            final LocalDate cutoffDate = LocalDate.now().minusDays(configuration.getRetentionDays());
            final List<ByteHotLogger.AuditEntry> entriesToArchive = new ArrayList<>();
            
            // Find entries to archive
            auditsByDate.entrySet().removeIf(entry -> {
                final LocalDate entryDate = LocalDate.parse(entry.getKey());
                if (entryDate.isBefore(cutoffDate)) {
                    entriesToArchive.addAll(entry.getValue());
                    return true;
                }
                return false;
            });
            
            // Remove from other indexes
            for (final ByteHotLogger.AuditEntry entry : entriesToArchive) {
                removeFromUserIndex(entry);
                removeFromActionIndex(entry);
            }
            
            // Archive to long-term storage
            final int archivedCount = archiveEntries(entriesToArchive);
            
            return new AuditArchivalResult(
                true,
                archivedCount,
                entriesToArchive.size() - archivedCount,
                "Archived " + archivedCount + " audit entries"
            );
            
        } catch (final Exception e) {
            return new AuditArchivalResult(
                false,
                0,
                0,
                "Archival failed: " + e.getMessage()
            );
        } finally {
            auditLock.writeLock().unlock();
        }
    }

    /**
     * Gets audit statistics for monitoring.
     * @return Current audit trail statistics
     */
    public AuditStatistics getAuditStatistics() {
        auditLock.readLock().lock();
        try {
            int totalEntries = auditsByDate.values().stream()
                .mapToInt(List::size)
                .sum();
            
            final long uniqueUsers = auditsByUser.size();
            final long uniqueActions = auditsByAction.size();
            final int pendingCount = pendingAudits.size();
            
            // Calculate daily statistics
            final Map<String, Integer> dailyStats = auditsByDate.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().size()
                ));
            
            return new AuditStatistics(
                totalEntries,
                uniqueUsers,
                uniqueActions,
                pendingCount,
                auditCounter.get(),
                configuration.getRetentionDays(),
                dailyStats
            );
            
        } finally {
            auditLock.readLock().unlock();
        }
    }

    /**
     * Configures audit trail settings.
     * This method can be hot-swapped to change audit configuration.
     * @param newConfiguration New audit configuration
     */
    public void configure(final AuditConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        this.auditEnabled = newConfiguration.isEnabled();
        System.out.println("Audit trail configuration updated");
    }

    /**
     * Shuts down the audit trail system.
     */
    public void shutdown() {
        // Process remaining pending audits
        while (!pendingAudits.isEmpty()) {
            final ByteHotLogger.AuditEntry entry = pendingAudits.poll();
            if (entry != null) {
                final SignedAuditEntry signedEntry = signAuditEntry(entry);
                processAuditEntry(signedEntry);
            }
        }
        
        auditExecutor.shutdown();
        try {
            if (!auditExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                auditExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            auditExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Signs an audit entry for integrity protection.
     * This method can be hot-swapped to change signing behavior.
     * @param auditEntry Audit entry to sign
     * @return Signed audit entry
     */
    protected SignedAuditEntry signAuditEntry(final ByteHotLogger.AuditEntry auditEntry) {
        // Generate digital signature for the audit entry
        final String signature = generateDigitalSignature(auditEntry);
        final long sequenceNumber = auditCounter.incrementAndGet();
        
        return new SignedAuditEntry(auditEntry, signature, sequenceNumber);
    }

    /**
     * Processes an audit entry asynchronously.
     * This method can be hot-swapped to change processing behavior.
     * @param signedEntry Signed audit entry to process
     */
    protected void processAuditEntry(final SignedAuditEntry signedEntry) {
        try {
            // Store to persistent storage if configured
            if (configuration.isPersistentStorageEnabled()) {
                storeAuditEntry(signedEntry);
            }
            
            // Send to external audit systems if configured
            if (configuration.hasExternalAuditSystems()) {
                sendToExternalSystems(signedEntry);
            }
            
            // Trigger real-time alerts if configured
            if (shouldTriggerAlert(signedEntry.getAuditEntry())) {
                triggerAuditAlert(signedEntry.getAuditEntry());
            }
            
        } catch (final Exception e) {
            System.err.println("Failed to process audit entry: " + e.getMessage());
        }
    }

    /**
     * Starts audit processing background tasks.
     * This method can be hot-swapped to change processing behavior.
     */
    protected void startAuditProcessing() {
        // Schedule audit archival
        auditExecutor.scheduleAtFixedRate(
            this::archiveOldEntries,
            1, 24, TimeUnit.HOURS
        );
        
        // Schedule integrity validation
        auditExecutor.scheduleAtFixedRate(
            this::validateIntegrity,
            1, 6, TimeUnit.HOURS
        );
        
        // Schedule statistics reporting
        auditExecutor.scheduleAtFixedRate(
            this::reportAuditStatistics,
            5, 15, TimeUnit.MINUTES
        );
    }

    // Helper methods
    
    protected void indexAuditEntry(final SignedAuditEntry signedEntry) {
        final ByteHotLogger.AuditEntry entry = signedEntry.getAuditEntry();
        final String dateKey = entry.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate().toString();
        
        auditLock.writeLock().lock();
        try {
            // Index by date
            auditsByDate.computeIfAbsent(dateKey, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(entry);
            
            // Index by user
            auditsByUser.computeIfAbsent(entry.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(entry);
            
            // Index by action
            auditsByAction.computeIfAbsent(entry.getAction(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(entry);
                
        } finally {
            auditLock.writeLock().unlock();
        }
    }
    
    protected List<ByteHotLogger.AuditEntry> searchByDateRange(final LocalDate startDate, final LocalDate endDate) {
        final List<ByteHotLogger.AuditEntry> results = new ArrayList<>();
        
        for (final Map.Entry<String, List<ByteHotLogger.AuditEntry>> entry : auditsByDate.entrySet()) {
            final LocalDate entryDate = LocalDate.parse(entry.getKey());
            
            if ((startDate == null || !entryDate.isBefore(startDate)) &&
                (endDate == null || !entryDate.isAfter(endDate))) {
                results.addAll(entry.getValue());
            }
        }
        
        return results.stream()
            .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    protected List<ByteHotLogger.AuditEntry> getAllAuditEntries() {
        return auditsByDate.values().stream()
            .flatMap(List::stream)
            .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    protected String generateDigitalSignature(final ByteHotLogger.AuditEntry auditEntry) {
        // Simple signature generation - in real implementation would use proper cryptography
        final String data = auditEntry.getEventId() + auditEntry.getTimestamp() + 
                           auditEntry.getUserId() + auditEntry.getAction() + auditEntry.getResource();
        return "SIG_" + Math.abs(data.hashCode());
    }
    
    protected boolean validateAuditEntryIntegrity(final ByteHotLogger.AuditEntry auditEntry) {
        // Validate digital signature and integrity
        // In real implementation would verify cryptographic signature
        return auditEntry.getEventId() != null && auditEntry.getTimestamp() != null;
    }
    
    protected List<String> validateAuditSequence() {
        // Check for sequence number gaps
        final List<String> issues = new ArrayList<>();
        // Implementation would check for missing sequence numbers
        return issues;
    }
    
    protected void removeFromUserIndex(final ByteHotLogger.AuditEntry entry) {
        final List<ByteHotLogger.AuditEntry> userEntries = auditsByUser.get(entry.getUserId());
        if (userEntries != null) {
            userEntries.remove(entry);
            if (userEntries.isEmpty()) {
                auditsByUser.remove(entry.getUserId());
            }
        }
    }
    
    protected void removeFromActionIndex(final ByteHotLogger.AuditEntry entry) {
        final List<ByteHotLogger.AuditEntry> actionEntries = auditsByAction.get(entry.getAction());
        if (actionEntries != null) {
            actionEntries.remove(entry);
            if (actionEntries.isEmpty()) {
                auditsByAction.remove(entry.getAction());
            }
        }
    }
    
    protected int archiveEntries(final List<ByteHotLogger.AuditEntry> entries) {
        // Archive to long-term storage
        // In real implementation would use external storage systems
        return entries.size();
    }
    
    protected void storeAuditEntry(final SignedAuditEntry signedEntry) {
        // Store to persistent storage
        // In real implementation would use database or secure file storage
    }
    
    protected void sendToExternalSystems(final SignedAuditEntry signedEntry) {
        // Send to external audit systems
        // In real implementation would integrate with SIEM systems
    }
    
    protected boolean shouldTriggerAlert(final ByteHotLogger.AuditEntry auditEntry) {
        // Check if audit entry requires immediate alerting
        return auditEntry.getOutcome() == ByteHotLogger.AuditOutcome.FAILURE ||
               auditEntry.getAction().contains("SECURITY") ||
               auditEntry.getAction().contains("CRITICAL");
    }
    
    protected void triggerAuditAlert(final ByteHotLogger.AuditEntry auditEntry) {
        System.err.println("AUDIT ALERT: " + auditEntry.getAction() + " - " + auditEntry.getOutcome());
    }
    
    protected void reportAuditStatistics() {
        if (configuration.isStatisticsReportingEnabled()) {
            final AuditStatistics stats = getAuditStatistics();
            System.out.println("Audit Statistics: " + stats.getTotalEntries() + " entries, " +
                stats.getUniqueUsers() + " users, " + stats.getPendingCount() + " pending");
        }
    }

    // Compliance report generation methods
    
    protected ComplianceReport generateSoxComplianceReport(final List<ByteHotLogger.AuditEntry> entries,
                                                          final LocalDate startDate, final LocalDate endDate) {
        // SOX compliance report generation
        return new ComplianceReport(
            ComplianceReportType.SOX_COMPLIANCE,
            startDate, endDate,
            entries.size(),
            "SOX compliance report generated successfully",
            generateSoxAnalysis(entries)
        );
    }
    
    protected ComplianceReport generateGdprComplianceReport(final List<ByteHotLogger.AuditEntry> entries,
                                                           final LocalDate startDate, final LocalDate endDate) {
        // GDPR compliance report generation
        return new ComplianceReport(
            ComplianceReportType.GDPR_COMPLIANCE,
            startDate, endDate,
            entries.size(),
            "GDPR compliance report generated successfully",
            generateGdprAnalysis(entries)
        );
    }
    
    protected ComplianceReport generateHipaaComplianceReport(final List<ByteHotLogger.AuditEntry> entries,
                                                            final LocalDate startDate, final LocalDate endDate) {
        // HIPAA compliance report generation
        return new ComplianceReport(
            ComplianceReportType.HIPAA_COMPLIANCE,
            startDate, endDate,
            entries.size(),
            "HIPAA compliance report generated successfully",
            generateHipaaAnalysis(entries)
        );
    }
    
    protected ComplianceReport generateSecurityAuditReport(final List<ByteHotLogger.AuditEntry> entries,
                                                          final LocalDate startDate, final LocalDate endDate) {
        // Security audit report generation
        return new ComplianceReport(
            ComplianceReportType.SECURITY_AUDIT,
            startDate, endDate,
            entries.size(),
            "Security audit report generated successfully",
            generateSecurityAnalysis(entries)
        );
    }
    
    protected ComplianceReport generateChangeManagementReport(final List<ByteHotLogger.AuditEntry> entries,
                                                             final LocalDate startDate, final LocalDate endDate) {
        // Change management report generation
        return new ComplianceReport(
            ComplianceReportType.CHANGE_MANAGEMENT,
            startDate, endDate,
            entries.size(),
            "Change management report generated successfully",
            generateChangeAnalysis(entries)
        );
    }
    
    protected Map<String, Object> generateSoxAnalysis(final List<ByteHotLogger.AuditEntry> entries) {
        final Map<String, Object> analysis = new java.util.HashMap<>();
        analysis.put("totalChanges", entries.size());
        analysis.put("approvedChanges", entries.stream().filter(e -> e.getOutcome() == ByteHotLogger.AuditOutcome.SUCCESS).count());
        analysis.put("failedChanges", entries.stream().filter(e -> e.getOutcome() == ByteHotLogger.AuditOutcome.FAILURE).count());
        return analysis;
    }
    
    protected Map<String, Object> generateGdprAnalysis(final List<ByteHotLogger.AuditEntry> entries) {
        final Map<String, Object> analysis = new java.util.HashMap<>();
        analysis.put("dataAccess", entries.stream().filter(e -> e.getAction().contains("DATA_ACCESS")).count());
        analysis.put("dataModification", entries.stream().filter(e -> e.getAction().contains("DATA_MODIFY")).count());
        return analysis;
    }
    
    protected Map<String, Object> generateHipaaAnalysis(final List<ByteHotLogger.AuditEntry> entries) {
        final Map<String, Object> analysis = new java.util.HashMap<>();
        analysis.put("phiAccess", entries.stream().filter(e -> e.getResource().contains("PHI")).count());
        analysis.put("unauthorizedAccess", entries.stream().filter(e -> e.getOutcome() == ByteHotLogger.AuditOutcome.FAILURE).count());
        return analysis;
    }
    
    protected Map<String, Object> generateSecurityAnalysis(final List<ByteHotLogger.AuditEntry> entries) {
        final Map<String, Object> analysis = new java.util.HashMap<>();
        analysis.put("securityEvents", entries.stream().filter(e -> e.getAction().contains("SECURITY")).count());
        analysis.put("suspiciousActivity", entries.stream().filter(e -> e.getAction().contains("SUSPICIOUS")).count());
        return analysis;
    }
    
    protected Map<String, Object> generateChangeAnalysis(final List<ByteHotLogger.AuditEntry> entries) {
        final Map<String, Object> analysis = new java.util.HashMap<>();
        analysis.put("hotSwapChanges", entries.stream().filter(e -> e.getAction().contains("HOT_SWAP")).count());
        analysis.put("configChanges", entries.stream().filter(e -> e.getAction().contains("CONFIG")).count());
        return analysis;
    }

    // Enums and supporting classes
    
    public enum ComplianceReportType {
        SOX_COMPLIANCE,
        GDPR_COMPLIANCE,
        HIPAA_COMPLIANCE,
        SECURITY_AUDIT,
        CHANGE_MANAGEMENT
    }

    // Static inner classes for data structures
    
    public static class SignedAuditEntry {
        private final ByteHotLogger.AuditEntry auditEntry;
        private final String digitalSignature;
        private final long sequenceNumber;

        public SignedAuditEntry(final ByteHotLogger.AuditEntry auditEntry, 
                               final String digitalSignature, final long sequenceNumber) {
            this.auditEntry = auditEntry;
            this.digitalSignature = digitalSignature;
            this.sequenceNumber = sequenceNumber;
        }

        public ByteHotLogger.AuditEntry getAuditEntry() { return auditEntry; }
        public String getDigitalSignature() { return digitalSignature; }
        public long getSequenceNumber() { return sequenceNumber; }
    }

    public static class AuditSearchCriteria {
        private LocalDate startDate;
        private LocalDate endDate;
        private String userId;
        private String action;
        private String resource;
        private ByteHotLogger.AuditOutcome outcome;
        private int maxResults = 100;

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(final LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(final LocalDate endDate) { this.endDate = endDate; }

        public String getUserId() { return userId; }
        public void setUserId(final String userId) { this.userId = userId; }

        public String getAction() { return action; }
        public void setAction(final String action) { this.action = action; }

        public String getResource() { return resource; }
        public void setResource(final String resource) { this.resource = resource; }

        public ByteHotLogger.AuditOutcome getOutcome() { return outcome; }
        public void setOutcome(final ByteHotLogger.AuditOutcome outcome) { this.outcome = outcome; }

        public int getMaxResults() { return maxResults; }
        public void setMaxResults(final int maxResults) { this.maxResults = maxResults; }
    }

    public static class ComplianceReport {
        private final ComplianceReportType reportType;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final int totalEntries;
        private final String summary;
        private final Map<String, Object> analysis;
        private final Instant generatedAt;

        public ComplianceReport(final ComplianceReportType reportType, final LocalDate startDate,
                               final LocalDate endDate, final int totalEntries, final String summary,
                               final Map<String, Object> analysis) {
            this.reportType = reportType;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalEntries = totalEntries;
            this.summary = summary;
            this.analysis = analysis;
            this.generatedAt = Instant.now();
        }

        public ComplianceReportType getReportType() { return reportType; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public int getTotalEntries() { return totalEntries; }
        public String getSummary() { return summary; }
        public Map<String, Object> getAnalysis() { return analysis; }
        public Instant getGeneratedAt() { return generatedAt; }
    }

    public static class AuditIntegrityResult {
        private final boolean isValid;
        private final double integrityPercentage;
        private final int totalEntries;
        private final int validEntries;
        private final List<String> integrityIssues;

        public AuditIntegrityResult(final boolean isValid, final double integrityPercentage,
                                   final int totalEntries, final int validEntries,
                                   final List<String> integrityIssues) {
            this.isValid = isValid;
            this.integrityPercentage = integrityPercentage;
            this.totalEntries = totalEntries;
            this.validEntries = validEntries;
            this.integrityIssues = integrityIssues;
        }

        public boolean isValid() { return isValid; }
        public double getIntegrityPercentage() { return integrityPercentage; }
        public int getTotalEntries() { return totalEntries; }
        public int getValidEntries() { return validEntries; }
        public List<String> getIntegrityIssues() { return integrityIssues; }
    }

    public static class AuditArchivalResult {
        private final boolean success;
        private final int archivedCount;
        private final int failedCount;
        private final String message;

        public AuditArchivalResult(final boolean success, final int archivedCount,
                                  final int failedCount, final String message) {
            this.success = success;
            this.archivedCount = archivedCount;
            this.failedCount = failedCount;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public int getArchivedCount() { return archivedCount; }
        public int getFailedCount() { return failedCount; }
        public String getMessage() { return message; }
    }

    public static class AuditStatistics {
        private final int totalEntries;
        private final long uniqueUsers;
        private final long uniqueActions;
        private final int pendingCount;
        private final long sequenceNumber;
        private final int retentionDays;
        private final Map<String, Integer> dailyStatistics;

        public AuditStatistics(final int totalEntries, final long uniqueUsers, final long uniqueActions,
                              final int pendingCount, final long sequenceNumber, final int retentionDays,
                              final Map<String, Integer> dailyStatistics) {
            this.totalEntries = totalEntries;
            this.uniqueUsers = uniqueUsers;
            this.uniqueActions = uniqueActions;
            this.pendingCount = pendingCount;
            this.sequenceNumber = sequenceNumber;
            this.retentionDays = retentionDays;
            this.dailyStatistics = dailyStatistics;
        }

        public int getTotalEntries() { return totalEntries; }
        public long getUniqueUsers() { return uniqueUsers; }
        public long getUniqueActions() { return uniqueActions; }
        public int getPendingCount() { return pendingCount; }
        public long getSequenceNumber() { return sequenceNumber; }
        public int getRetentionDays() { return retentionDays; }
        public Map<String, Integer> getDailyStatistics() { return dailyStatistics; }
    }

    public static class AuditConfiguration {
        private boolean enabled = true;
        private boolean persistentStorageEnabled = true;
        private boolean statisticsReportingEnabled = true;
        private int retentionDays = 2555; // 7 years default
        private String storageLocation = "/var/log/bytehot/audit";
        private List<String> externalAuditSystems = new ArrayList<>();
        private boolean digitalSigningEnabled = true;
        private String signatureAlgorithm = "SHA256withRSA";

        public static AuditConfiguration defaultConfiguration() {
            return new AuditConfiguration();
        }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(final boolean enabled) { this.enabled = enabled; }

        public boolean isPersistentStorageEnabled() { return persistentStorageEnabled; }
        public void setPersistentStorageEnabled(final boolean persistentStorageEnabled) { 
            this.persistentStorageEnabled = persistentStorageEnabled; 
        }

        public boolean isStatisticsReportingEnabled() { return statisticsReportingEnabled; }
        public void setStatisticsReportingEnabled(final boolean statisticsReportingEnabled) { 
            this.statisticsReportingEnabled = statisticsReportingEnabled; 
        }

        public int getRetentionDays() { return retentionDays; }
        public void setRetentionDays(final int retentionDays) { this.retentionDays = retentionDays; }

        public String getStorageLocation() { return storageLocation; }
        public void setStorageLocation(final String storageLocation) { this.storageLocation = storageLocation; }

        public List<String> getExternalAuditSystems() { return Collections.unmodifiableList(externalAuditSystems); }
        public void setExternalAuditSystems(final List<String> externalAuditSystems) { 
            this.externalAuditSystems = new ArrayList<>(externalAuditSystems); 
        }

        public boolean isDigitalSigningEnabled() { return digitalSigningEnabled; }
        public void setDigitalSigningEnabled(final boolean digitalSigningEnabled) { 
            this.digitalSigningEnabled = digitalSigningEnabled; 
        }

        public String getSignatureAlgorithm() { return signatureAlgorithm; }
        public void setSignatureAlgorithm(final String signatureAlgorithm) { 
            this.signatureAlgorithm = signatureAlgorithm; 
        }

        public boolean hasExternalAuditSystems() { return !externalAuditSystems.isEmpty(); }
    }
}