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
 * Filename: IncidentReporter.java
 *
 * Author: Claude Code
 *
 * Class name: IncidentReporter
 *
 * Responsibilities:
 *   - Report critical incidents to monitoring and alerting systems
 *   - Generate incident reports with comprehensive context
 *   - Manage incident escalation and notification
 *   - Track incident metrics and resolution status
 *
 * Collaborators:
 *   - OperationContext: Provides context about the failed operation
 *   - RecoveryResult: Information about recovery attempts
 *   - IncidentReport: Structured incident information
 *   - AlertingSystem: External alerting and notification system
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Reports critical incidents to monitoring and alerting systems.
 * @author Claude Code
 * @since 2025-07-04
 */
public class IncidentReporter {
    
    /**
     * Map of active incidents by incident ID.
     */
    private final Map<String, IncidentReport> activeIncidents;
    
    /**
     * Incident counter for generating unique IDs.
     */
    private final AtomicLong incidentCounter;
    
    /**
     * List of alerting channels for incident notifications.
     */
    private final List<AlertingChannel> alertingChannels;
    
    /**
     * Configuration for incident reporting.
     */
    private final IncidentReportingConfiguration configuration;
    
    /**
     * Creates a new IncidentReporter with default configuration.
     */
    public IncidentReporter() {
        this(IncidentReportingConfiguration.defaultConfiguration());
    }
    
    /**
     * Creates a new IncidentReporter with the specified configuration.
     * @param configuration The incident reporting configuration
     */
    public IncidentReporter(final IncidentReportingConfiguration configuration) {
        this.configuration = configuration;
        this.activeIncidents = new ConcurrentHashMap<>();
        this.incidentCounter = new AtomicLong(0);
        this.alertingChannels = initializeAlertingChannels();
    }
    
    /**
     * Reports an incident for the given error and context.
     * @param error The error that occurred
     * @param context The operation context
     * @param recoveryResult The result of recovery attempts
     */
    public void reportIncident(final Throwable error, 
                              final OperationContext context, 
                              final RecoveryResult recoveryResult) {
        try {
            // Create incident report
            IncidentReport incident = createIncidentReport(error, context, recoveryResult);
            
            // Store incident
            activeIncidents.put(incident.getIncidentId(), incident);
            
            // Send alerts
            sendAlerts(incident);
            
            // Log incident
            logIncident(incident);
            
        } catch (Exception e) {
            // If incident reporting fails, fall back to basic logging
            handleIncidentReportingFailure(error, e);
        }
    }
    
    /**
     * Updates an existing incident with new information.
     * @param incidentId The incident ID
     * @param update The incident update
     */
    public void updateIncident(final String incidentId, final IncidentUpdate update) {
        IncidentReport incident = activeIncidents.get(incidentId);
        
        if (incident != null) {
            IncidentReport updatedIncident = incident.withUpdate(update);
            activeIncidents.put(incidentId, updatedIncident);
            
            // Send update alerts if needed
            if (update.requiresNotification()) {
                sendAlerts(updatedIncident);
            }
        }
    }
    
    /**
     * Resolves an incident.
     * @param incidentId The incident ID
     * @param resolution The incident resolution
     */
    public void resolveIncident(final String incidentId, final IncidentResolution resolution) {
        IncidentReport incident = activeIncidents.get(incidentId);
        
        if (incident != null) {
            IncidentReport resolvedIncident = incident.withResolution(resolution);
            activeIncidents.put(incidentId, resolvedIncident);
            
            // Send resolution notification
            sendResolutionAlert(resolvedIncident);
            
            // Move to resolved incidents after delay
            scheduleIncidentCleanup(incidentId);
        }
    }
    
    /**
     * Gets all active incidents.
     * @return List of active incidents
     */
    public List<IncidentReport> getActiveIncidents() {
        return List.copyOf(activeIncidents.values());
    }
    
    /**
     * Gets incident statistics for monitoring.
     * @return The incident statistics
     */
    public IncidentStatistics getIncidentStatistics() {
        return IncidentStatistics.builder()
            .activeIncidentCount(activeIncidents.size())
            .totalIncidentsReported(getTotalIncidentsReported())
            .resolvedIncidentsCount(getResolvedIncidentsCount())
            .averageResolutionTime(getAverageResolutionTime())
            .build();
    }
    
    /**
     * Creates an incident report from error information.
     * @param error The error that occurred
     * @param context The operation context
     * @param recoveryResult The recovery result
     * @return The incident report
     */
    protected IncidentReport createIncidentReport(final Throwable error,
                                                 final OperationContext context,
                                                 final RecoveryResult recoveryResult) {
        String incidentId = generateIncidentId();
        
        return IncidentReport.builder()
            .incidentId(incidentId)
            .timestamp(Instant.now())
            .error(error)
            .operationContext(context)
            .recoveryResult(recoveryResult)
            .severity(determineSeverity(error, context))
            .status(IncidentStatus.OPEN)
            .build();
    }
    
    /**
     * Sends alerts for the incident to all configured channels.
     * @param incident The incident to alert on
     */
    protected void sendAlerts(final IncidentReport incident) {
        for (AlertingChannel channel : alertingChannels) {
            try {
                if (channel.shouldAlert(incident)) {
                    channel.sendAlert(incident);
                }
            } catch (Exception e) {
                // Log alerting failure but don't fail the whole incident reporting
                System.err.println("Failed to send alert via " + channel.getName() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Generates a unique incident ID.
     * @return The incident ID
     */
    protected String generateIncidentId() {
        return "INC-" + System.currentTimeMillis() + "-" + incidentCounter.incrementAndGet();
    }
    
    /**
     * Determines the incident severity based on error and context.
     * @param error The error
     * @param context The operation context
     * @return The incident severity
     */
    protected IncidentSeverity determineSeverity(final Throwable error, final OperationContext context) {
        // Critical operations always get high severity
        if (context.isCriticalOperation()) {
            return IncidentSeverity.HIGH;
        }
        
        // Security errors are always high severity
        if (error instanceof SecurityException) {
            return IncidentSeverity.HIGH;
        }
        
        // Memory errors are critical
        if (error instanceof OutOfMemoryError) {
            return IncidentSeverity.CRITICAL;
        }
        
        // Default to medium severity
        return IncidentSeverity.MEDIUM;
    }
    
    /**
     * Logs the incident for record keeping.
     * @param incident The incident to log
     */
    protected void logIncident(final IncidentReport incident) {
        System.err.println("INCIDENT REPORTED: " + incident.getIncidentId() + 
                          " - " + incident.getError().getMessage());
    }
    
    /**
     * Handles incident reporting failures.
     * @param originalError The original error
     * @param reportingError The reporting error
     */
    protected void handleIncidentReportingFailure(final Throwable originalError,
                                                 final Exception reportingError) {
        System.err.println("INCIDENT REPORTING FAILED for error: " + originalError.getMessage());
        System.err.println("Reporting error: " + reportingError.getMessage());
    }
    
    /**
     * Initializes alerting channels.
     * @return List of alerting channels
     */
    protected List<AlertingChannel> initializeAlertingChannels() {
        return List.of(
            new LoggingAlertingChannel(),
            new EmailAlertingChannel(configuration.getEmailConfiguration()),
            new SlackAlertingChannel(configuration.getSlackConfiguration())
        );
    }
    
    /**
     * Sends resolution alert for resolved incident.
     * @param incident The resolved incident
     */
    protected void sendResolutionAlert(final IncidentReport incident) {
        // Implementation would send resolution notifications
    }
    
    /**
     * Schedules cleanup of resolved incidents.
     * @param incidentId The incident ID to clean up
     */
    protected void scheduleIncidentCleanup(final String incidentId) {
        // Implementation would schedule cleanup after retention period
    }
    
    /**
     * Gets the total number of incidents reported.
     * @return The total incidents count
     */
    protected long getTotalIncidentsReported() {
        return incidentCounter.get();
    }
    
    /**
     * Gets the number of resolved incidents.
     * @return The resolved incidents count
     */
    protected long getResolvedIncidentsCount() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the average incident resolution time.
     * @return The average resolution time in milliseconds
     */
    protected long getAverageResolutionTime() {
        // Implementation would calculate this metric
        return 0;
    }
}