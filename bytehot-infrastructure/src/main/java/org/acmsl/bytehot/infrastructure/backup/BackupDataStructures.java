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
 * Filename: BackupDataStructures.java
 *
 * Author: Claude Code
 *
 * Class name: BackupDataStructures
 *
 * Responsibilities:
 *   - Define data structures for backup and recovery operations
 *   - Provide immutable result objects for backup operations
 *   - Support comprehensive backup metadata and state management
 *   - Enable serialization for backup persistence
 *
 * Collaborators:
 *   - BackupManager: Uses these structures for backup operations
 *   - BackupConfiguration: Configuration settings reference
 *   - Serializable: For backup persistence
 */
package org.acmsl.bytehot.infrastructure.backup;

import java.time.Duration;
import java.time.Instant;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

/**
 * Backup result containing comprehensive operation information.
 */
class BackupResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final long backupId;
    private final String backupName;
    private final BackupManager.BackupType type;
    private final BackupManager.BackupStatus status;
    private final Instant startTime;
    private final Duration duration;
    private final Path storageLocation;
    private final long compressedSize;
    private final int componentCount;
    private final Throwable error;
    
    public BackupResult(final long backupId, final String backupName, final BackupManager.BackupType type,
                       final BackupManager.BackupStatus status, final Instant startTime, 
                       final Duration duration, final Path storageLocation, final long compressedSize,
                       final int componentCount, final Throwable error) {
        this.backupId = backupId;
        this.backupName = Objects.requireNonNull(backupName);
        this.type = Objects.requireNonNull(type);
        this.status = Objects.requireNonNull(status);
        this.startTime = Objects.requireNonNull(startTime);
        this.duration = Objects.requireNonNull(duration);
        this.storageLocation = storageLocation;
        this.compressedSize = compressedSize;
        this.componentCount = componentCount;
        this.error = error;
    }
    
    public static BackupResult disabled() {
        return new BackupResult(0, "disabled", BackupManager.BackupType.FULL, 
                               BackupManager.BackupStatus.CANCELLED, Instant.now(), 
                               Duration.ZERO, null, 0, 0, null);
    }
    
    public static BackupResult failure(final long backupId, final String backupName, 
                                      final BackupManager.BackupType type, final Instant startTime, 
                                      final Duration duration, final Throwable error) {
        return new BackupResult(backupId, backupName, type, BackupManager.BackupStatus.FAILED,
                               startTime, duration, null, 0, 0, error);
    }
    
    // Getters
    public long getBackupId() { return backupId; }
    public String getBackupName() { return backupName; }
    public BackupManager.BackupType getType() { return type; }
    public BackupManager.BackupStatus getStatus() { return status; }
    public Instant getStartTime() { return startTime; }
    public Duration getDuration() { return duration; }
    public Path getStorageLocation() { return storageLocation; }
    public long getCompressedSize() { return compressedSize; }
    public int getComponentCount() { return componentCount; }
    public Throwable getError() { return error; }
    
    public boolean isSuccess() {
        return status == BackupManager.BackupStatus.COMPLETED;
    }
    
    @Override
    public String toString() {
        return "BackupResult{" +
               "backupId=" + backupId +
               ", backupName='" + backupName + '\'' +
               ", type=" + type +
               ", status=" + status +
               ", duration=" + duration +
               ", compressedSize=" + compressedSize +
               ", componentCount=" + componentCount +
               '}';
    }
}

/**
 * Backup metadata containing detailed information about a backup.
 */
class BackupMetadata implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final long id;
    private final String name;
    private final BackupManager.BackupType type;
    private final Instant createdAt;
    private final int componentCount;
    private final long totalSize;
    private final RetentionPolicy retentionPolicy;
    private final String createdBy;
    
    private String referenceBackup;
    private String description;
    private List<String> tags = new ArrayList<>();
    
    public BackupMetadata(final long id, final String name, final BackupManager.BackupType type,
                         final Instant createdAt, final int componentCount, final long totalSize,
                         final RetentionPolicy retentionPolicy, final String createdBy) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.componentCount = componentCount;
        this.totalSize = totalSize;
        this.retentionPolicy = Objects.requireNonNull(retentionPolicy);
        this.createdBy = Objects.requireNonNull(createdBy);
    }
    
    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public BackupManager.BackupType getType() { return type; }
    public Instant getCreatedAt() { return createdAt; }
    public int getComponentCount() { return componentCount; }
    public long getTotalSize() { return totalSize; }
    public RetentionPolicy getRetentionPolicy() { return retentionPolicy; }
    public String getCreatedBy() { return createdBy; }
    public String getReferenceBackup() { return referenceBackup; }
    public String getDescription() { return description; }
    public List<String> getTags() { return Collections.unmodifiableList(tags); }
    
    // Setters for mutable fields
    public void setReferenceBackup(final String referenceBackup) {
        this.referenceBackup = referenceBackup;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public void addTag(final String tag) {
        this.tags.add(tag);
    }
    
    @Override
    public String toString() {
        return "BackupMetadata{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", type=" + type +
               ", createdAt=" + createdAt +
               ", componentCount=" + componentCount +
               ", totalSize=" + totalSize +
               ", createdBy='" + createdBy + '\'' +
               '}';
    }
}

/**
 * Backup entry combining metadata and state snapshot.
 */
class BackupEntry implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final long id;
    private final BackupMetadata metadata;
    private final BackupStateSnapshot snapshot;
    
    private volatile BackupManager.BackupStatus status;
    
    public BackupEntry(final long id, final BackupMetadata metadata, 
                      final BackupStateSnapshot snapshot, final BackupManager.BackupStatus status) {
        this.id = id;
        this.metadata = Objects.requireNonNull(metadata);
        this.snapshot = Objects.requireNonNull(snapshot);
        this.status = Objects.requireNonNull(status);
    }
    
    public long getId() { return id; }
    public BackupMetadata getMetadata() { return metadata; }
    public BackupStateSnapshot getSnapshot() { return snapshot; }
    public BackupManager.BackupStatus getStatus() { return status; }
    
    public void updateStatus(final BackupManager.BackupStatus newStatus) {
        this.status = newStatus;
    }
}

/**
 * State snapshot representing system state at a point in time.
 */
class BackupStateSnapshot implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Instant timestamp;
    private final List<StateComponent> components;
    private final long totalSize;
    
    public BackupStateSnapshot(final Instant timestamp, final List<StateComponent> components, 
                              final long totalSize) {
        this.timestamp = Objects.requireNonNull(timestamp);
        this.components = new ArrayList<>(Objects.requireNonNull(components));
        this.totalSize = totalSize;
    }
    
    public Instant getTimestamp() { return timestamp; }
    public List<StateComponent> getComponents() { return Collections.unmodifiableList(components); }
    public long getTotalSize() { return totalSize; }
    public int getComponentCount() { return components.size(); }
    
    @Override
    public String toString() {
        return "BackupStateSnapshot{" +
               "timestamp=" + timestamp +
               ", componentCount=" + components.size() +
               ", totalSize=" + totalSize +
               '}';
    }
}

/**
 * Individual state component within a backup snapshot.
 */
class StateComponent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String description;
    private final long size;
    private final Instant timestamp;
    private final String checksum;
    
    public StateComponent(final String name, final String description, 
                         final long size, final Instant timestamp) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.size = size;
        this.timestamp = Objects.requireNonNull(timestamp);
        this.checksum = calculateChecksum();
    }
    
    private String calculateChecksum() {
        // Simplified checksum calculation
        return "checksum_" + Math.abs((name + description + size + timestamp.toString()).hashCode());
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getSize() { return size; }
    public Instant getTimestamp() { return timestamp; }
    public String getChecksum() { return checksum; }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        final StateComponent that = (StateComponent) obj;
        return size == that.size &&
               Objects.equals(name, that.name) &&
               Objects.equals(description, that.description) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(checksum, that.checksum);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, description, size, timestamp, checksum);
    }
    
    @Override
    public String toString() {
        return "StateComponent{" +
               "name='" + name + '\'' +
               ", size=" + size +
               ", timestamp=" + timestamp +
               '}';
    }
}

/**
 * Storage result for backup operations.
 */
class BackupStorageResult {
    
    private final boolean success;
    private final Path storageLocation;
    private final long compressedSize;
    private final Throwable error;
    
    public BackupStorageResult(final boolean success, final Path storageLocation, 
                              final long compressedSize, final Throwable error) {
        this.success = success;
        this.storageLocation = storageLocation;
        this.compressedSize = compressedSize;
        this.error = error;
    }
    
    public boolean isSuccess() { return success; }
    public Path getStorageLocation() { return storageLocation; }
    public long getCompressedSize() { return compressedSize; }
    public Throwable getError() { return error; }
}

/**
 * Restore operation result.
 */
class RestoreResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final long restoreId;
    private final String backupName;
    private final Instant startTime;
    private final Duration duration;
    private final BackupManager.RestoreStatus status;
    private final int restoredComponents;
    private final int skippedComponents;
    private final BackupStateSnapshot preRestoreSnapshot;
    private final Throwable error;
    
    public RestoreResult(final long restoreId, final String backupName, final Instant startTime,
                        final Duration duration, final BackupManager.RestoreStatus status,
                        final int restoredComponents, final int skippedComponents,
                        final BackupStateSnapshot preRestoreSnapshot, final Throwable error) {
        this.restoreId = restoreId;
        this.backupName = Objects.requireNonNull(backupName);
        this.startTime = Objects.requireNonNull(startTime);
        this.duration = Objects.requireNonNull(duration);
        this.status = Objects.requireNonNull(status);
        this.restoredComponents = restoredComponents;
        this.skippedComponents = skippedComponents;
        this.preRestoreSnapshot = preRestoreSnapshot;
        this.error = error;
    }
    
    public static RestoreResult failure(final long restoreId, final String backupName,
                                       final Instant startTime, final Duration duration,
                                       final Throwable error) {
        return new RestoreResult(restoreId, backupName, startTime, duration,
                                BackupManager.RestoreStatus.FAILED, 0, 0, null, error);
    }
    
    // Getters
    public long getRestoreId() { return restoreId; }
    public String getBackupName() { return backupName; }
    public Instant getStartTime() { return startTime; }
    public Duration getDuration() { return duration; }
    public BackupManager.RestoreStatus getStatus() { return status; }
    public int getRestoredComponents() { return restoredComponents; }
    public int getSkippedComponents() { return skippedComponents; }
    public BackupStateSnapshot getPreRestoreSnapshot() { return preRestoreSnapshot; }
    public Throwable getError() { return error; }
    
    public boolean isSuccess() {
        return status == BackupManager.RestoreStatus.COMPLETED;
    }
}

/**
 * Restore operation internal result.
 */
class RestoreOperationResult {
    
    private final boolean success;
    private final int restoredComponents;
    private final int skippedComponents;
    private final Throwable error;
    
    public RestoreOperationResult(final boolean success, final int restoredComponents,
                                 final int skippedComponents, final Throwable error) {
        this.success = success;
        this.restoredComponents = restoredComponents;
        this.skippedComponents = skippedComponents;
        this.error = error;
    }
    
    public boolean isSuccess() { return success; }
    public int getRestoredComponents() { return restoredComponents; }
    public int getSkippedComponents() { return skippedComponents; }
    public Throwable getError() { return error; }
}

/**
 * Backup deletion result.
 */
class BackupDeletionResult {
    
    private final boolean success;
    private final String backupName;
    private final boolean storageDeleted;
    private final List<String> dependentBackups;
    private final Throwable error;
    private final String message;
    
    private BackupDeletionResult(final boolean success, final String backupName,
                                final boolean storageDeleted, final List<String> dependentBackups,
                                final Throwable error, final String message) {
        this.success = success;
        this.backupName = backupName;
        this.storageDeleted = storageDeleted;
        this.dependentBackups = dependentBackups != null ? new ArrayList<>(dependentBackups) : null;
        this.error = error;
        this.message = message;
    }
    
    public static BackupDeletionResult success(final String backupName, final boolean storageDeleted) {
        return new BackupDeletionResult(true, backupName, storageDeleted, null, null,
                                       "Backup deleted successfully");
    }
    
    public static BackupDeletionResult notFound(final String backupName) {
        return new BackupDeletionResult(false, backupName, false, null, null,
                                       "Backup not found");
    }
    
    public static BackupDeletionResult hasDependencies(final String backupName, 
                                                      final List<String> dependentBackups) {
        return new BackupDeletionResult(false, backupName, false, dependentBackups, null,
                                       "Cannot delete backup with dependent incremental backups");
    }
    
    public static BackupDeletionResult failure(final String backupName, final Throwable error) {
        return new BackupDeletionResult(false, backupName, false, null, error,
                                       "Failed to delete backup: " + error.getMessage());
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getBackupName() { return backupName; }
    public boolean isStorageDeleted() { return storageDeleted; }
    public List<String> getDependentBackups() { 
        return dependentBackups != null ? Collections.unmodifiableList(dependentBackups) : null; 
    }
    public Throwable getError() { return error; }
    public String getMessage() { return message; }
}

/**
 * Backup cleanup result.
 */
class BackupCleanupResult {
    
    private final boolean success;
    private final int deletedCount;
    private final int failedCount;
    private final List<String> deletedBackups;
    private final List<String> failedBackups;
    private final String message;
    
    public BackupCleanupResult(final boolean success, final int deletedCount, final int failedCount,
                              final List<String> deletedBackups, final List<String> failedBackups,
                              final String message) {
        this.success = success;
        this.deletedCount = deletedCount;
        this.failedCount = failedCount;
        this.deletedBackups = new ArrayList<>(Objects.requireNonNullElse(deletedBackups, Collections.emptyList()));
        this.failedBackups = new ArrayList<>(Objects.requireNonNullElse(failedBackups, Collections.emptyList()));
        this.message = message;
    }
    
    public static BackupCleanupResult failure(final String message) {
        return new BackupCleanupResult(false, 0, 0, null, null, message);
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public int getDeletedCount() { return deletedCount; }
    public int getFailedCount() { return failedCount; }
    public List<String> getDeletedBackups() { return Collections.unmodifiableList(deletedBackups); }
    public List<String> getFailedBackups() { return Collections.unmodifiableList(failedBackups); }
    public String getMessage() { return message; }
}

/**
 * Backup system statistics.
 */
class BackupStatistics {
    
    private final int totalBackups;
    private final int fullBackups;
    private final int incrementalBackups;
    private final long totalSize;
    private final long totalComponents;
    private final Instant lastBackupTime;
    private final long totalOperations;
    private final RetentionPolicy retentionPolicy;
    
    public BackupStatistics(final int totalBackups, final int fullBackups, final int incrementalBackups,
                           final long totalSize, final long totalComponents, final Instant lastBackupTime,
                           final long totalOperations, final RetentionPolicy retentionPolicy) {
        this.totalBackups = totalBackups;
        this.fullBackups = fullBackups;
        this.incrementalBackups = incrementalBackups;
        this.totalSize = totalSize;
        this.totalComponents = totalComponents;
        this.lastBackupTime = lastBackupTime;
        this.totalOperations = totalOperations;
        this.retentionPolicy = retentionPolicy;
    }
    
    // Getters
    public int getTotalBackups() { return totalBackups; }
    public int getFullBackups() { return fullBackups; }
    public int getIncrementalBackups() { return incrementalBackups; }
    public long getTotalSize() { return totalSize; }
    public long getTotalComponents() { return totalComponents; }
    public Instant getLastBackupTime() { return lastBackupTime; }
    public long getTotalOperations() { return totalOperations; }
    public RetentionPolicy getRetentionPolicy() { return retentionPolicy; }
    
    @Override
    public String toString() {
        return "BackupStatistics{" +
               "totalBackups=" + totalBackups +
               ", fullBackups=" + fullBackups +
               ", incrementalBackups=" + incrementalBackups +
               ", totalSize=" + totalSize +
               ", lastBackupTime=" + lastBackupTime +
               '}';
    }
}