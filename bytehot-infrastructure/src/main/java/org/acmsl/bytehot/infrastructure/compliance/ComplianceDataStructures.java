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
 * Filename: ComplianceDataStructures.java
 *
 * Author: Claude Code
 *
 * Class name: ComplianceDataStructures
 *
 * Responsibilities:
 *   - Define data structures for compliance and governance operations
 *   - Provide immutable result objects for compliance reporting
 *   - Support comprehensive compliance metadata and policy management
 *   - Enable serialization for compliance audit persistence
 *
 * Collaborators:
 *   - ComplianceManager: Uses these structures for compliance operations
 *   - AuditTrail: Audit event data structures
 *   - SecurityManager: Security compliance validation
 */
package org.acmsl.bytehot.infrastructure.compliance;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.io.Serializable;

/**
 * Compliance framework definition for regulatory requirements.
 */
class ComplianceFramework implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String description;
    private final String version;
    private final Set<String> requiredControls;
    private final Map<String, String> requirements;
    private final ComplianceSeverity severity;
    private final boolean enabled;
    
    public ComplianceFramework(final String name, final String description, final String version,
                              final Set<String> requiredControls, final Map<String, String> requirements,
                              final ComplianceSeverity severity, final boolean enabled) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.version = Objects.requireNonNull(version);
        this.requiredControls = new HashSet<>(Objects.requireNonNull(requiredControls));
        this.requirements = new HashMap<>(Objects.requireNonNull(requirements));
        this.severity = Objects.requireNonNull(severity);
        this.enabled = enabled;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public Set<String> getRequiredControls() { return Collections.unmodifiableSet(requiredControls); }
    public Map<String, String> getRequirements() { return Collections.unmodifiableMap(requirements); }
    public ComplianceSeverity getSeverity() { return severity; }
    public boolean isEnabled() { return enabled; }
    
    @Override
    public String toString() {
        return "ComplianceFramework{" +
               "name='" + name + '\'' +
               ", version='" + version + '\'' +
               ", severity=" + severity +
               ", enabled=" + enabled +
               '}';
    }
}

/**
 * Compliance policy defining specific rules and constraints.
 */
class CompliancePolicy implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String id;
    private final String name;
    private final String description;
    private final String framework;
    private final CompliancePolicyType type;
    private final Map<String, Object> parameters;
    private final Duration retentionPeriod;
    private final ComplianceSeverity severity;
    private final boolean enforced;
    private final Instant createdAt;
    private final String createdBy;
    
    public CompliancePolicy(final String id, final String name, final String description,
                           final String framework, final CompliancePolicyType type,
                           final Map<String, Object> parameters, final Duration retentionPeriod,
                           final ComplianceSeverity severity, final boolean enforced,
                           final Instant createdAt, final String createdBy) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.framework = Objects.requireNonNull(framework);
        this.type = Objects.requireNonNull(type);
        this.parameters = new HashMap<>(Objects.requireNonNull(parameters));
        this.retentionPeriod = Objects.requireNonNull(retentionPeriod);
        this.severity = Objects.requireNonNull(severity);
        this.enforced = enforced;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.createdBy = Objects.requireNonNull(createdBy);
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFramework() { return framework; }
    public CompliancePolicyType getType() { return type; }
    public Map<String, Object> getParameters() { return Collections.unmodifiableMap(parameters); }
    public Duration getRetentionPeriod() { return retentionPeriod; }
    public ComplianceSeverity getSeverity() { return severity; }
    public boolean isEnforced() { return enforced; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    
    @Override
    public String toString() {
        return "CompliancePolicy{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", framework='" + framework + '\'' +
               ", type=" + type +
               ", severity=" + severity +
               ", enforced=" + enforced +
               '}';
    }
}

/**
 * Compliance report containing assessment results and recommendations.
 */
class ComplianceReport implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String reportId;
    private final String title;
    private final String framework;
    private final Instant generatedAt;
    private final Instant periodStart;
    private final Instant periodEnd;
    private final ComplianceStatus overallStatus;
    private final List<ComplianceViolation> violations;
    private final List<ComplianceRecommendation> recommendations;
    private final ComplianceMetrics metrics;
    private final String generatedBy;
    
    public ComplianceReport(final String reportId, final String title, final String framework,
                           final Instant generatedAt, final Instant periodStart, final Instant periodEnd,
                           final ComplianceStatus overallStatus, final List<ComplianceViolation> violations,
                           final List<ComplianceRecommendation> recommendations,
                           final ComplianceMetrics metrics, final String generatedBy) {
        this.reportId = Objects.requireNonNull(reportId);
        this.title = Objects.requireNonNull(title);
        this.framework = Objects.requireNonNull(framework);
        this.generatedAt = Objects.requireNonNull(generatedAt);
        this.periodStart = Objects.requireNonNull(periodStart);
        this.periodEnd = Objects.requireNonNull(periodEnd);
        this.overallStatus = Objects.requireNonNull(overallStatus);
        this.violations = new ArrayList<>(Objects.requireNonNull(violations));
        this.recommendations = new ArrayList<>(Objects.requireNonNull(recommendations));
        this.metrics = Objects.requireNonNull(metrics);
        this.generatedBy = Objects.requireNonNull(generatedBy);
    }
    
    // Getters
    public String getReportId() { return reportId; }
    public String getTitle() { return title; }
    public String getFramework() { return framework; }
    public Instant getGeneratedAt() { return generatedAt; }
    public Instant getPeriodStart() { return periodStart; }
    public Instant getPeriodEnd() { return periodEnd; }
    public ComplianceStatus getOverallStatus() { return overallStatus; }
    public List<ComplianceViolation> getViolations() { return Collections.unmodifiableList(violations); }
    public List<ComplianceRecommendation> getRecommendations() { return Collections.unmodifiableList(recommendations); }
    public ComplianceMetrics getMetrics() { return metrics; }
    public String getGeneratedBy() { return generatedBy; }
    
    @Override
    public String toString() {
        return "ComplianceReport{" +
               "reportId='" + reportId + '\'' +
               ", title='" + title + '\'' +
               ", framework='" + framework + '\'' +
               ", overallStatus=" + overallStatus +
               ", violationCount=" + violations.size() +
               ", recommendationCount=" + recommendations.size() +
               '}';
    }
}

/**
 * Compliance violation representing a breach of policy or regulation.
 */
class ComplianceViolation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String violationId;
    private final String policyId;
    private final String description;
    private final ComplianceSeverity severity;
    private final Instant detectedAt;
    private final String source;
    private final Map<String, Object> context;
    private final ComplianceViolationStatus status;
    private final String assignedTo;
    private final Instant dueDate;
    
    public ComplianceViolation(final String violationId, final String policyId, final String description,
                              final ComplianceSeverity severity, final Instant detectedAt, final String source,
                              final Map<String, Object> context, final ComplianceViolationStatus status,
                              final String assignedTo, final Instant dueDate) {
        this.violationId = Objects.requireNonNull(violationId);
        this.policyId = Objects.requireNonNull(policyId);
        this.description = Objects.requireNonNull(description);
        this.severity = Objects.requireNonNull(severity);
        this.detectedAt = Objects.requireNonNull(detectedAt);
        this.source = Objects.requireNonNull(source);
        this.context = new HashMap<>(Objects.requireNonNull(context));
        this.status = Objects.requireNonNull(status);
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
    }
    
    // Getters
    public String getViolationId() { return violationId; }
    public String getPolicyId() { return policyId; }
    public String getDescription() { return description; }
    public ComplianceSeverity getSeverity() { return severity; }
    public Instant getDetectedAt() { return detectedAt; }
    public String getSource() { return source; }
    public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }
    public ComplianceViolationStatus getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }
    public Instant getDueDate() { return dueDate; }
    
    @Override
    public String toString() {
        return "ComplianceViolation{" +
               "violationId='" + violationId + '\'' +
               ", policyId='" + policyId + '\'' +
               ", severity=" + severity +
               ", detectedAt=" + detectedAt +
               ", status=" + status +
               '}';
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        final ComplianceViolation that = (ComplianceViolation) obj;
        return Objects.equals(violationId, that.violationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(violationId);
    }
}

/**
 * Compliance recommendation for improving compliance posture.
 */
class ComplianceRecommendation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String recommendationId;
    private final String title;
    private final String description;
    private final ComplianceSeverity priority;
    private final String category;
    private final List<String> actionItems;
    private final Duration estimatedEffort;
    private final String framework;
    
    public ComplianceRecommendation(final String recommendationId, final String title, final String description,
                                   final ComplianceSeverity priority, final String category,
                                   final List<String> actionItems, final Duration estimatedEffort,
                                   final String framework) {
        this.recommendationId = Objects.requireNonNull(recommendationId);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.priority = Objects.requireNonNull(priority);
        this.category = Objects.requireNonNull(category);
        this.actionItems = new ArrayList<>(Objects.requireNonNull(actionItems));
        this.estimatedEffort = Objects.requireNonNull(estimatedEffort);
        this.framework = Objects.requireNonNull(framework);
    }
    
    // Getters
    public String getRecommendationId() { return recommendationId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ComplianceSeverity getPriority() { return priority; }
    public String getCategory() { return category; }
    public List<String> getActionItems() { return Collections.unmodifiableList(actionItems); }
    public Duration getEstimatedEffort() { return estimatedEffort; }
    public String getFramework() { return framework; }
    
    @Override
    public String toString() {
        return "ComplianceRecommendation{" +
               "recommendationId='" + recommendationId + '\'' +
               ", title='" + title + '\'' +
               ", priority=" + priority +
               ", category='" + category + '\'' +
               ", framework='" + framework + '\'' +
               '}';
    }
}

/**
 * Compliance metrics for assessment and reporting.
 */
class ComplianceMetrics implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final int totalPolicies;
    private final int activePolicies;
    private final int totalViolations;
    private final int openViolations;
    private final int resolvedViolations;
    private final Map<ComplianceSeverity, Integer> violationsBySeverity;
    private final double complianceScore;
    private final Instant lastAssessment;
    private final Duration assessmentDuration;
    
    public ComplianceMetrics(final int totalPolicies, final int activePolicies, final int totalViolations,
                            final int openViolations, final int resolvedViolations,
                            final Map<ComplianceSeverity, Integer> violationsBySeverity,
                            final double complianceScore, final Instant lastAssessment,
                            final Duration assessmentDuration) {
        this.totalPolicies = totalPolicies;
        this.activePolicies = activePolicies;
        this.totalViolations = totalViolations;
        this.openViolations = openViolations;
        this.resolvedViolations = resolvedViolations;
        this.violationsBySeverity = new HashMap<>(Objects.requireNonNull(violationsBySeverity));
        this.complianceScore = complianceScore;
        this.lastAssessment = Objects.requireNonNull(lastAssessment);
        this.assessmentDuration = Objects.requireNonNull(assessmentDuration);
    }
    
    // Getters
    public int getTotalPolicies() { return totalPolicies; }
    public int getActivePolicies() { return activePolicies; }
    public int getTotalViolations() { return totalViolations; }
    public int getOpenViolations() { return openViolations; }
    public int getResolvedViolations() { return resolvedViolations; }
    public Map<ComplianceSeverity, Integer> getViolationsBySeverity() { 
        return Collections.unmodifiableMap(violationsBySeverity); 
    }
    public double getComplianceScore() { return complianceScore; }
    public Instant getLastAssessment() { return lastAssessment; }
    public Duration getAssessmentDuration() { return assessmentDuration; }
    
    @Override
    public String toString() {
        return "ComplianceMetrics{" +
               "totalPolicies=" + totalPolicies +
               ", activePolicies=" + activePolicies +
               ", totalViolations=" + totalViolations +
               ", openViolations=" + openViolations +
               ", complianceScore=" + complianceScore +
               '}';
    }
}

/**
 * Compliance configuration settings.
 */
class ComplianceConfiguration implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final boolean enabled;
    private final Set<String> enabledFrameworks;
    private final Duration assessmentInterval;
    private final Duration retentionPeriod;
    private final boolean autoRemediation;
    private final ComplianceSeverity minimumSeverity;
    private final Map<String, Object> frameworkSettings;
    
    public ComplianceConfiguration(final boolean enabled, final Set<String> enabledFrameworks,
                                  final Duration assessmentInterval, final Duration retentionPeriod,
                                  final boolean autoRemediation, final ComplianceSeverity minimumSeverity,
                                  final Map<String, Object> frameworkSettings) {
        this.enabled = enabled;
        this.enabledFrameworks = new HashSet<>(Objects.requireNonNull(enabledFrameworks));
        this.assessmentInterval = Objects.requireNonNull(assessmentInterval);
        this.retentionPeriod = Objects.requireNonNull(retentionPeriod);
        this.autoRemediation = autoRemediation;
        this.minimumSeverity = Objects.requireNonNull(minimumSeverity);
        this.frameworkSettings = new HashMap<>(Objects.requireNonNull(frameworkSettings));
    }
    
    public static ComplianceConfiguration defaultConfiguration() {
        return new ComplianceConfiguration(
            true,
            Set.of("SOX", "GDPR", "HIPAA", "SOC2"),
            Duration.ofHours(24),
            Duration.ofDays(365),
            false,
            ComplianceSeverity.LOW,
            Map.of(
                "sox.enabled", true,
                "gdpr.enabled", true,
                "hipaa.enabled", false,
                "soc2.enabled", true
            )
        );
    }
    
    public static ComplianceConfiguration enterpriseConfiguration() {
        return new ComplianceConfiguration(
            true,
            Set.of("SOX", "GDPR", "HIPAA", "SOC2", "PCI-DSS", "ISO27001"),
            Duration.ofHours(6),
            Duration.ofDays(2555), // 7 years
            true,
            ComplianceSeverity.LOW,
            Map.of(
                "sox.enabled", true,
                "gdpr.enabled", true,
                "hipaa.enabled", true,
                "soc2.enabled", true,
                "pci.enabled", true,
                "iso27001.enabled", true,
                "auto.remediation", true,
                "real.time.monitoring", true
            )
        );
    }
    
    // Getters
    public boolean isEnabled() { return enabled; }
    public Set<String> getEnabledFrameworks() { return Collections.unmodifiableSet(enabledFrameworks); }
    public Duration getAssessmentInterval() { return assessmentInterval; }
    public Duration getRetentionPeriod() { return retentionPeriod; }
    public boolean isAutoRemediation() { return autoRemediation; }
    public ComplianceSeverity getMinimumSeverity() { return minimumSeverity; }
    public Map<String, Object> getFrameworkSettings() { return Collections.unmodifiableMap(frameworkSettings); }
    
    @Override
    public String toString() {
        return "ComplianceConfiguration{" +
               "enabled=" + enabled +
               ", enabledFrameworks=" + enabledFrameworks +
               ", assessmentInterval=" + assessmentInterval +
               ", autoRemediation=" + autoRemediation +
               ", minimumSeverity=" + minimumSeverity +
               '}';
    }
}

/**
 * Audit dashboard data structure.
 */
class AuditDashboard implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String dashboardId;
    private final String title;
    private final Instant generatedAt;
    private final ComplianceMetrics overallMetrics;
    private final Map<String, ComplianceMetrics> frameworkMetrics;
    private final List<ComplianceViolation> recentViolations;
    private final List<ComplianceReport> recentReports;
    private final Map<String, Object> charts;
    
    public AuditDashboard(final String dashboardId, final String title, final Instant generatedAt,
                         final ComplianceMetrics overallMetrics, final Map<String, ComplianceMetrics> frameworkMetrics,
                         final List<ComplianceViolation> recentViolations, final List<ComplianceReport> recentReports,
                         final Map<String, Object> charts) {
        this.dashboardId = Objects.requireNonNull(dashboardId);
        this.title = Objects.requireNonNull(title);
        this.generatedAt = Objects.requireNonNull(generatedAt);
        this.overallMetrics = Objects.requireNonNull(overallMetrics);
        this.frameworkMetrics = new HashMap<>(Objects.requireNonNull(frameworkMetrics));
        this.recentViolations = new ArrayList<>(Objects.requireNonNull(recentViolations));
        this.recentReports = new ArrayList<>(Objects.requireNonNull(recentReports));
        this.charts = new HashMap<>(Objects.requireNonNull(charts));
    }
    
    // Getters
    public String getDashboardId() { return dashboardId; }
    public String getTitle() { return title; }
    public Instant getGeneratedAt() { return generatedAt; }
    public ComplianceMetrics getOverallMetrics() { return overallMetrics; }
    public Map<String, ComplianceMetrics> getFrameworkMetrics() { return Collections.unmodifiableMap(frameworkMetrics); }
    public List<ComplianceViolation> getRecentViolations() { return Collections.unmodifiableList(recentViolations); }
    public List<ComplianceReport> getRecentReports() { return Collections.unmodifiableList(recentReports); }
    public Map<String, Object> getCharts() { return Collections.unmodifiableMap(charts); }
    
    @Override
    public String toString() {
        return "AuditDashboard{" +
               "dashboardId='" + dashboardId + '\'' +
               ", title='" + title + '\'' +
               ", generatedAt=" + generatedAt +
               ", violationCount=" + recentViolations.size() +
               ", reportCount=" + recentReports.size() +
               '}';
    }
}

// Enums for compliance operations

/**
 * Compliance severity levels.
 */
enum ComplianceSeverity {
    CRITICAL(4, "Critical - Immediate action required"),
    HIGH(3, "High - Action required within 24 hours"),
    MEDIUM(2, "Medium - Action required within 1 week"),
    LOW(1, "Low - Action required within 1 month"),
    INFO(0, "Informational - No action required");
    
    private final int level;
    private final String description;
    
    ComplianceSeverity(final int level, final String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() { return level; }
    public String getDescription() { return description; }
}

/**
 * Compliance policy types.
 */
enum CompliancePolicyType {
    DATA_RETENTION("Data retention and lifecycle management"),
    ACCESS_CONTROL("User access and permission management"),
    ENCRYPTION("Data encryption and protection"),
    AUDIT_LOGGING("Audit trail and logging requirements"),
    BACKUP_RECOVERY("Backup and disaster recovery"),
    CHANGE_MANAGEMENT("Change management and approval workflows"),
    VULNERABILITY_MANAGEMENT("Security vulnerability assessment and remediation"),
    INCIDENT_RESPONSE("Security incident response procedures"),
    PRIVACY_PROTECTION("Personal data privacy and protection"),
    BUSINESS_CONTINUITY("Business continuity and operational resilience");
    
    private final String description;
    
    CompliancePolicyType(final String description) {
        this.description = description;
    }
    
    public String getDescription() { return description; }
}

/**
 * Compliance status levels.
 */
enum ComplianceStatus {
    COMPLIANT("Fully compliant with all requirements"),
    PARTIALLY_COMPLIANT("Partially compliant - some violations present"),
    NON_COMPLIANT("Non-compliant - significant violations"),
    UNDER_REVIEW("Under review - assessment in progress"),
    EXEMPTED("Exempted from compliance requirements");
    
    private final String description;
    
    ComplianceStatus(final String description) {
        this.description = description;
    }
    
    public String getDescription() { return description; }
}

/**
 * Compliance violation status.
 */
enum ComplianceViolationStatus {
    OPEN("Open - Not yet addressed"),
    IN_PROGRESS("In Progress - Being addressed"),
    RESOLVED("Resolved - Issue has been fixed"),
    ACCEPTED_RISK("Accepted Risk - Risk accepted by management"),
    FALSE_POSITIVE("False Positive - Not a real violation"),
    DEFERRED("Deferred - Resolution postponed");
    
    private final String description;
    
    ComplianceViolationStatus(final String description) {
        this.description = description;
    }
    
    public String getDescription() { return description; }
}