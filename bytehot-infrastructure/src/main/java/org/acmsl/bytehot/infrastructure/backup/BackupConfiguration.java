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
 * Filename: BackupConfiguration.java
 *
 * Author: Claude Code
 *
 * Class name: BackupConfiguration
 *
 * Responsibilities:
 *   - Define configuration settings for backup and recovery operations
 *   - Specify backup schedules, retention policies, and storage options
 *   - Support both automatic and manual backup configurations
 *   - Enable enterprise-grade backup policy management
 *
 * Collaborators:
 *   - BackupManager: Uses configuration for backup operations
 *   - RetentionPolicy: Defines backup retention rules
 *   - Path: Specifies backup storage locations
 */
package org.acmsl.bytehot.infrastructure.backup;

import java.time.Duration;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Configuration settings for the ByteHot backup and recovery system.
 * Provides comprehensive control over backup behavior and policies.
 * @author Claude Code
 * @since 2025-07-06
 */
public class BackupConfiguration {
    
    private boolean enabled = true;
    private boolean automaticBackupsEnabled = true;
    private boolean compressionEnabled = true;
    private boolean encryptionEnabled = false;
    
    private Path backupDirectory = Paths.get(System.getProperty("user.home"), ".bytehot", "backups");
    private Duration fullBackupInterval = Duration.ofHours(24);
    private Duration incrementalBackupInterval = Duration.ofHours(4);
    
    private RetentionPolicy retentionPolicy = RetentionPolicy.defaultPolicy();
    
    private int maxConcurrentBackups = 2;
    private long maxBackupSize = 1024 * 1024 * 1024; // 1GB default
    private boolean verifyBackupIntegrity = true;
    
    private String compressionAlgorithm = "GZIP";
    private String encryptionAlgorithm = "AES-256";
    
    private BackupConfiguration() {
        // Use factory methods
    }
    
    /**
     * Creates a default backup configuration.
     * @return Default backup configuration
     */
    public static BackupConfiguration defaultConfiguration() {
        return new BackupConfiguration();
    }
    
    /**
     * Creates a configuration for enterprise environments.
     * @return Enterprise backup configuration with enhanced security
     */
    public static BackupConfiguration enterpriseConfiguration() {
        final BackupConfiguration config = new BackupConfiguration();
        config.encryptionEnabled = true;
        config.fullBackupInterval = Duration.ofHours(12);
        config.incrementalBackupInterval = Duration.ofHours(2);
        config.retentionPolicy = RetentionPolicy.enterprisePolicy();
        config.maxConcurrentBackups = 4;
        config.verifyBackupIntegrity = true;
        return config;
    }
    
    /**
     * Creates a configuration for development environments.
     * @return Development backup configuration with minimal overhead
     */
    public static BackupConfiguration developmentConfiguration() {
        final BackupConfiguration config = new BackupConfiguration();
        config.automaticBackupsEnabled = false;
        config.fullBackupInterval = Duration.ofDays(7);
        config.incrementalBackupInterval = Duration.ofDays(1);
        config.retentionPolicy = RetentionPolicy.shortTermPolicy();
        config.compressionEnabled = false;
        config.verifyBackupIntegrity = false;
        return config;
    }
    
    // Getters and setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public BackupConfiguration setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public boolean isAutomaticBackupsEnabled() {
        return automaticBackupsEnabled;
    }
    
    public BackupConfiguration setAutomaticBackupsEnabled(final boolean automaticBackupsEnabled) {
        this.automaticBackupsEnabled = automaticBackupsEnabled;
        return this;
    }
    
    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }
    
    public BackupConfiguration setCompressionEnabled(final boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
        return this;
    }
    
    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }
    
    public BackupConfiguration setEncryptionEnabled(final boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
        return this;
    }
    
    public Path getBackupDirectory() {
        return backupDirectory;
    }
    
    public BackupConfiguration setBackupDirectory(final Path backupDirectory) {
        this.backupDirectory = Objects.requireNonNull(backupDirectory);
        return this;
    }
    
    public Duration getFullBackupInterval() {
        return fullBackupInterval;
    }
    
    public BackupConfiguration setFullBackupInterval(final Duration fullBackupInterval) {
        this.fullBackupInterval = Objects.requireNonNull(fullBackupInterval);
        return this;
    }
    
    public Duration getIncrementalBackupInterval() {
        return incrementalBackupInterval;
    }
    
    public BackupConfiguration setIncrementalBackupInterval(final Duration incrementalBackupInterval) {
        this.incrementalBackupInterval = Objects.requireNonNull(incrementalBackupInterval);
        return this;
    }
    
    public RetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }
    
    public BackupConfiguration setRetentionPolicy(final RetentionPolicy retentionPolicy) {
        this.retentionPolicy = Objects.requireNonNull(retentionPolicy);
        return this;
    }
    
    public int getMaxConcurrentBackups() {
        return maxConcurrentBackups;
    }
    
    public BackupConfiguration setMaxConcurrentBackups(final int maxConcurrentBackups) {
        this.maxConcurrentBackups = maxConcurrentBackups;
        return this;
    }
    
    public long getMaxBackupSize() {
        return maxBackupSize;
    }
    
    public BackupConfiguration setMaxBackupSize(final long maxBackupSize) {
        this.maxBackupSize = maxBackupSize;
        return this;
    }
    
    public boolean isVerifyBackupIntegrity() {
        return verifyBackupIntegrity;
    }
    
    public BackupConfiguration setVerifyBackupIntegrity(final boolean verifyBackupIntegrity) {
        this.verifyBackupIntegrity = verifyBackupIntegrity;
        return this;
    }
    
    public String getCompressionAlgorithm() {
        return compressionAlgorithm;
    }
    
    public BackupConfiguration setCompressionAlgorithm(final String compressionAlgorithm) {
        this.compressionAlgorithm = Objects.requireNonNull(compressionAlgorithm);
        return this;
    }
    
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
    
    public BackupConfiguration setEncryptionAlgorithm(final String encryptionAlgorithm) {
        this.encryptionAlgorithm = Objects.requireNonNull(encryptionAlgorithm);
        return this;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        final BackupConfiguration that = (BackupConfiguration) obj;
        return enabled == that.enabled &&
               automaticBackupsEnabled == that.automaticBackupsEnabled &&
               compressionEnabled == that.compressionEnabled &&
               encryptionEnabled == that.encryptionEnabled &&
               maxConcurrentBackups == that.maxConcurrentBackups &&
               maxBackupSize == that.maxBackupSize &&
               verifyBackupIntegrity == that.verifyBackupIntegrity &&
               Objects.equals(backupDirectory, that.backupDirectory) &&
               Objects.equals(fullBackupInterval, that.fullBackupInterval) &&
               Objects.equals(incrementalBackupInterval, that.incrementalBackupInterval) &&
               Objects.equals(retentionPolicy, that.retentionPolicy) &&
               Objects.equals(compressionAlgorithm, that.compressionAlgorithm) &&
               Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(enabled, automaticBackupsEnabled, compressionEnabled, 
                           encryptionEnabled, backupDirectory, fullBackupInterval, 
                           incrementalBackupInterval, retentionPolicy, maxConcurrentBackups, 
                           maxBackupSize, verifyBackupIntegrity, compressionAlgorithm, 
                           encryptionAlgorithm);
    }
    
    @Override
    public String toString() {
        return "BackupConfiguration{" +
               "enabled=" + enabled +
               ", automaticBackupsEnabled=" + automaticBackupsEnabled +
               ", compressionEnabled=" + compressionEnabled +
               ", encryptionEnabled=" + encryptionEnabled +
               ", backupDirectory=" + backupDirectory +
               ", fullBackupInterval=" + fullBackupInterval +
               ", incrementalBackupInterval=" + incrementalBackupInterval +
               ", retentionPolicy=" + retentionPolicy +
               ", maxConcurrentBackups=" + maxConcurrentBackups +
               ", maxBackupSize=" + maxBackupSize +
               ", verifyBackupIntegrity=" + verifyBackupIntegrity +
               ", compressionAlgorithm='" + compressionAlgorithm + '\'' +
               ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
               '}';
    }
}

/**
 * Backup retention policy configuration.
 */
class RetentionPolicy implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Duration retentionDuration;
    private final int maxBackupCount;
    private final boolean keepLastFullBackup;
    private final Duration minimumFullBackupInterval;
    
    public RetentionPolicy(final Duration retentionDuration, final int maxBackupCount, 
                          final boolean keepLastFullBackup, final Duration minimumFullBackupInterval) {
        this.retentionDuration = Objects.requireNonNull(retentionDuration);
        this.maxBackupCount = maxBackupCount;
        this.keepLastFullBackup = keepLastFullBackup;
        this.minimumFullBackupInterval = Objects.requireNonNull(minimumFullBackupInterval);
    }
    
    public static RetentionPolicy defaultPolicy() {
        return new RetentionPolicy(
            Duration.ofDays(30), // 30 days retention
            50,                  // Max 50 backups
            true,               // Always keep last full backup
            Duration.ofDays(1)  // At least one full backup per day
        );
    }
    
    public static RetentionPolicy enterprisePolicy() {
        return new RetentionPolicy(
            Duration.ofDays(90), // 90 days retention
            200,                 // Max 200 backups
            true,               // Always keep last full backup
            Duration.ofHours(12) // At least one full backup every 12 hours
        );
    }
    
    public static RetentionPolicy shortTermPolicy() {
        return new RetentionPolicy(
            Duration.ofDays(7),  // 7 days retention
            20,                  // Max 20 backups
            true,               // Always keep last full backup
            Duration.ofDays(7)  // At least one full backup per week
        );
    }
    
    public Duration getRetentionDuration() {
        return retentionDuration;
    }
    
    public int getMaxBackupCount() {
        return maxBackupCount;
    }
    
    public boolean isKeepLastFullBackup() {
        return keepLastFullBackup;
    }
    
    public Duration getMinimumFullBackupInterval() {
        return minimumFullBackupInterval;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        final RetentionPolicy that = (RetentionPolicy) obj;
        return maxBackupCount == that.maxBackupCount &&
               keepLastFullBackup == that.keepLastFullBackup &&
               Objects.equals(retentionDuration, that.retentionDuration) &&
               Objects.equals(minimumFullBackupInterval, that.minimumFullBackupInterval);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(retentionDuration, maxBackupCount, keepLastFullBackup, minimumFullBackupInterval);
    }
    
    @Override
    public String toString() {
        return "RetentionPolicy{" +
               "retentionDuration=" + retentionDuration +
               ", maxBackupCount=" + maxBackupCount +
               ", keepLastFullBackup=" + keepLastFullBackup +
               ", minimumFullBackupInterval=" + minimumFullBackupInterval +
               '}';
    }
}