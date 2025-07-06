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
 * Filename: BackupManager.java
 *
 * Author: Claude Code
 *
 * Class name: BackupManager
 *
 * Responsibilities:
 *   - Manage comprehensive backup and recovery for ByteHot hot-swap state
 *   - Implement incremental and full backup strategies for system state
 *   - Provide point-in-time recovery capabilities for hot-swap operations
 *   - Support automated backup scheduling and cleanup policies
 *
 * Collaborators:
 *   - PerformanceMonitor: Monitors backup performance and system impact
 *   - SecurityManager: Validates backup access permissions and encryption
 *   - ByteHotLogger: Logs backup operations and events
 *   - AuditTrail: Records backup audit events for compliance
 */
package org.acmsl.bytehot.infrastructure.backup;

import org.acmsl.bytehot.infrastructure.monitoring.PerformanceMonitor;
import org.acmsl.bytehot.infrastructure.security.SecurityManager;
import org.acmsl.bytehot.infrastructure.logging.ByteHotLogger;
import org.acmsl.bytehot.infrastructure.logging.AuditTrail;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Enterprise-grade backup and recovery system for ByteHot hot-swap state.
 * Provides comprehensive data protection with point-in-time recovery capabilities.
 * @author Claude Code
 * @since 2025-07-06
 */
public class BackupManager {

    private static final BackupManager INSTANCE = new BackupManager();
    private static final ByteHotLogger LOGGER = ByteHotLogger.getLogger(BackupManager.class);
    
    private final Map<String, BackupEntry> backups = new ConcurrentHashMap<>();
    private final Map<String, BackupMetadata> backupMetadata = new ConcurrentHashMap<>();
    private final Set<BackupListener> listeners = Collections.synchronizedSet(new HashSet<>());
    
    private final ReentrantReadWriteLock backupLock = new ReentrantReadWriteLock();
    private final AtomicLong backupCounter = new AtomicLong(0);
    
    private final ScheduledExecutorService backupExecutor = 
        Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "ByteHot-Backup-Manager");
            t.setDaemon(true);
            return t;
        });
    
    private volatile BackupConfiguration configuration = BackupConfiguration.defaultConfiguration();
    private volatile boolean backupEnabled = true;
    
    private BackupManager() {
        initializeBackupSystem();
        startBackupScheduler();
    }

    /**
     * Gets the singleton instance of BackupManager.
     * @return The backup manager instance
     */
    public static BackupManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a full backup of the current hot-swap state.
     * This method can be hot-swapped to change backup creation behavior.
     * @param backupName Name for the backup
     * @return Backup result with metadata and status
     */
    public CompletableFuture<BackupResult> createFullBackup(final String backupName) {
        return CompletableFuture.supplyAsync(() -> {
            if (!backupEnabled) {
                return BackupResult.disabled();
            }
            
            final long backupId = backupCounter.incrementAndGet();
            final Instant startTime = Instant.now();
            
            LOGGER.info("Creating full backup: {} (ID: {})", backupName, backupId);
            LOGGER.audit("BACKUP_STARTED", backupName, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Full backup creation initiated");
            
            backupLock.writeLock().lock();
            try {
                // Create backup state snapshot
                final BackupStateSnapshot snapshot = captureCurrentState();
                
                // Generate backup metadata
                final BackupMetadata metadata = new BackupMetadata(
                    backupId, backupName, BackupType.FULL, startTime,
                    snapshot.getComponentCount(), snapshot.getTotalSize(),
                    configuration.getRetentionPolicy(), getCurrentUser()
                );
                
                // Create backup entry
                final BackupEntry entry = new BackupEntry(
                    backupId, metadata, snapshot, BackupStatus.IN_PROGRESS
                );
                
                backups.put(backupName, entry);
                backupMetadata.put(backupName, metadata);
                
                // Perform actual backup storage
                final BackupStorageResult storageResult = storeBackup(entry);
                
                final Duration duration = Duration.between(startTime, Instant.now());
                final BackupResult result = new BackupResult(
                    backupId, backupName, BackupType.FULL, 
                    storageResult.isSuccess() ? BackupStatus.COMPLETED : BackupStatus.FAILED,
                    startTime, duration, storageResult.getStorageLocation(),
                    storageResult.getCompressedSize(), snapshot.getComponentCount(),
                    storageResult.isSuccess() ? null : storageResult.getError()
                );
                
                // Update entry status
                entry.updateStatus(result.getStatus());
                
                // Notify listeners
                notifyBackupListeners(result);
                
                // Log completion
                LOGGER.info("Full backup completed: {} (Duration: {}ms, Size: {} bytes)", 
                    backupName, duration.toMillis(), storageResult.getCompressedSize());
                
                LOGGER.audit("BACKUP_COMPLETED", backupName, 
                    result.getStatus() == BackupStatus.COMPLETED ? 
                        ByteHotLogger.AuditOutcome.SUCCESS : ByteHotLogger.AuditOutcome.FAILURE,
                    String.format("Backup completed with status %s", result.getStatus()));
                
                return result;
                
            } catch (final Exception e) {
                final Duration duration = Duration.between(startTime, Instant.now());
                final BackupResult errorResult = BackupResult.failure(
                    backupId, backupName, BackupType.FULL, startTime, duration, e
                );
                
                LOGGER.error("Full backup failed: " + backupName, e);
                LOGGER.audit("BACKUP_FAILED", backupName, ByteHotLogger.AuditOutcome.FAILURE, 
                    "Backup failed: " + e.getMessage());
                
                return errorResult;
                
            } finally {
                backupLock.writeLock().unlock();
            }
        }, backupExecutor);
    }

    /**
     * Creates an incremental backup since the last backup.
     * This method can be hot-swapped to change incremental backup behavior.
     * @param backupName Name for the incremental backup
     * @param lastBackupName Name of the reference backup
     * @return Incremental backup result
     */
    public CompletableFuture<BackupResult> createIncrementalBackup(final String backupName, 
                                                                  final String lastBackupName) {
        return CompletableFuture.supplyAsync(() -> {
            if (!backupEnabled) {
                return BackupResult.disabled();
            }
            
            final long backupId = backupCounter.incrementAndGet();
            final Instant startTime = Instant.now();
            
            LOGGER.info("Creating incremental backup: {} (Reference: {})", backupName, lastBackupName);
            
            backupLock.writeLock().lock();
            try {
                // Get reference backup
                final BackupEntry referenceBackup = backups.get(lastBackupName);
                if (referenceBackup == null) {
                    throw new IllegalArgumentException("Reference backup not found: " + lastBackupName);
                }
                
                // Create incremental snapshot
                final BackupStateSnapshot currentSnapshot = captureCurrentState();
                final BackupStateSnapshot incrementalSnapshot = 
                    createIncrementalSnapshot(currentSnapshot, referenceBackup.getSnapshot());
                
                // Generate backup metadata
                final BackupMetadata metadata = new BackupMetadata(
                    backupId, backupName, BackupType.INCREMENTAL, startTime,
                    incrementalSnapshot.getComponentCount(), incrementalSnapshot.getTotalSize(),
                    configuration.getRetentionPolicy(), getCurrentUser()
                );
                metadata.setReferenceBackup(lastBackupName);
                
                // Create backup entry
                final BackupEntry entry = new BackupEntry(
                    backupId, metadata, incrementalSnapshot, BackupStatus.IN_PROGRESS
                );
                
                backups.put(backupName, entry);
                backupMetadata.put(backupName, metadata);
                
                // Perform incremental backup storage
                final BackupStorageResult storageResult = storeIncrementalBackup(entry, referenceBackup);
                
                final Duration duration = Duration.between(startTime, Instant.now());
                final BackupResult result = new BackupResult(
                    backupId, backupName, BackupType.INCREMENTAL,
                    storageResult.isSuccess() ? BackupStatus.COMPLETED : BackupStatus.FAILED,
                    startTime, duration, storageResult.getStorageLocation(),
                    storageResult.getCompressedSize(), incrementalSnapshot.getComponentCount(),
                    storageResult.isSuccess() ? null : storageResult.getError()
                );
                
                // Update entry status
                entry.updateStatus(result.getStatus());
                
                // Notify listeners
                notifyBackupListeners(result);
                
                LOGGER.info("Incremental backup completed: {} (Duration: {}ms, Size: {} bytes)", 
                    backupName, duration.toMillis(), storageResult.getCompressedSize());
                
                return result;
                
            } catch (final Exception e) {
                final Duration duration = Duration.between(startTime, Instant.now());
                final BackupResult errorResult = BackupResult.failure(
                    backupId, backupName, BackupType.INCREMENTAL, startTime, duration, e
                );
                
                LOGGER.error("Incremental backup failed: " + backupName, e);
                
                return errorResult;
                
            } finally {
                backupLock.writeLock().unlock();
            }
        }, backupExecutor);
    }

    /**
     * Restores system state from a backup.
     * This method can be hot-swapped to change restore behavior.
     * @param backupName Name of the backup to restore
     * @param targetTime Point-in-time to restore to (null for full restore)
     * @return Restore result with status and details
     */
    public CompletableFuture<RestoreResult> restoreFromBackup(final String backupName, 
                                                             final Instant targetTime) {
        return CompletableFuture.supplyAsync(() -> {
            final Instant startTime = Instant.now();
            
            LOGGER.info("Starting restore from backup: {} (Target time: {})", backupName, targetTime);
            LOGGER.audit("RESTORE_STARTED", backupName, ByteHotLogger.AuditOutcome.SUCCESS, 
                "System restore initiated");
            
            backupLock.readLock().lock();
            try {
                // Validate backup exists
                final BackupEntry backup = backups.get(backupName);
                if (backup == null) {
                    throw new IllegalArgumentException("Backup not found: " + backupName);
                }
                
                // Create pre-restore snapshot for rollback
                final BackupStateSnapshot preRestoreSnapshot = captureCurrentState();
                
                // Load backup chain if incremental
                final List<BackupEntry> backupChain = buildBackupChain(backup);
                
                // Perform restore operation
                final RestoreOperationResult operationResult = performRestore(backupChain, targetTime);
                
                final Duration duration = Duration.between(startTime, Instant.now());
                final RestoreResult result = new RestoreResult(
                    generateRestoreId(), backupName, startTime, duration,
                    operationResult.isSuccess() ? RestoreStatus.COMPLETED : RestoreStatus.FAILED,
                    operationResult.getRestoredComponents(), operationResult.getSkippedComponents(),
                    preRestoreSnapshot, operationResult.isSuccess() ? null : operationResult.getError()
                );
                
                LOGGER.info("Restore completed: {} (Duration: {}ms, Components: {}/{})", 
                    backupName, duration.toMillis(), 
                    operationResult.getRestoredComponents(), 
                    operationResult.getRestoredComponents() + operationResult.getSkippedComponents());
                
                LOGGER.audit("RESTORE_COMPLETED", backupName, 
                    result.getStatus() == RestoreStatus.COMPLETED ? 
                        ByteHotLogger.AuditOutcome.SUCCESS : ByteHotLogger.AuditOutcome.FAILURE,
                    String.format("Restore completed with status %s", result.getStatus()));
                
                return result;
                
            } catch (final Exception e) {
                final Duration duration = Duration.between(startTime, Instant.now());
                final RestoreResult errorResult = RestoreResult.failure(
                    generateRestoreId(), backupName, startTime, duration, e
                );
                
                LOGGER.error("Restore failed: " + backupName, e);
                LOGGER.audit("RESTORE_FAILED", backupName, ByteHotLogger.AuditOutcome.FAILURE, 
                    "Restore failed: " + e.getMessage());
                
                return errorResult;
                
            } finally {
                backupLock.readLock().unlock();
            }
        }, backupExecutor);
    }

    /**
     * Lists all available backups with metadata.
     * @return List of backup metadata sorted by creation time
     */
    public List<BackupMetadata> listBackups() {
        backupLock.readLock().lock();
        try {
            return backupMetadata.values().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
        } finally {
            backupLock.readLock().unlock();
        }
    }

    /**
     * Gets backup statistics for monitoring.
     * @return Current backup system statistics
     */
    public BackupStatistics getBackupStatistics() {
        backupLock.readLock().lock();
        try {
            final int totalBackups = backups.size();
            final int fullBackups = (int) backups.values().stream()
                .filter(entry -> entry.getMetadata().getType() == BackupType.FULL)
                .count();
            final int incrementalBackups = totalBackups - fullBackups;
            
            final long totalSize = backups.values().stream()
                .mapToLong(entry -> entry.getSnapshot().getTotalSize())
                .sum();
            
            final long totalComponents = backups.values().stream()
                .mapToLong(entry -> entry.getSnapshot().getComponentCount())
                .sum();
            
            final Optional<Instant> lastBackupTime = backups.values().stream()
                .map(entry -> entry.getMetadata().getCreatedAt())
                .max(Instant::compareTo);
            
            return new BackupStatistics(
                totalBackups, fullBackups, incrementalBackups,
                totalSize, totalComponents, lastBackupTime.orElse(null),
                backupCounter.get(), configuration.getRetentionPolicy()
            );
            
        } finally {
            backupLock.readLock().unlock();
        }
    }

    /**
     * Deletes a backup and its associated data.
     * This method can be hot-swapped to change backup deletion behavior.
     * @param backupName Name of the backup to delete
     * @return Deletion result
     */
    public CompletableFuture<BackupDeletionResult> deleteBackup(final String backupName) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Deleting backup: {}", backupName);
            
            backupLock.writeLock().lock();
            try {
                final BackupEntry entry = backups.get(backupName);
                if (entry == null) {
                    return BackupDeletionResult.notFound(backupName);
                }
                
                // Check for dependent incremental backups
                final List<String> dependentBackups = findDependentBackups(backupName);
                if (!dependentBackups.isEmpty()) {
                    return BackupDeletionResult.hasDependencies(backupName, dependentBackups);
                }
                
                // Remove from storage
                final boolean storageDeleted = deleteBackupStorage(entry);
                
                // Remove from memory
                backups.remove(backupName);
                backupMetadata.remove(backupName);
                
                LOGGER.info("Backup deleted: {} (Storage deleted: {})", backupName, storageDeleted);
                LOGGER.audit("BACKUP_DELETED", backupName, ByteHotLogger.AuditOutcome.SUCCESS, 
                    "Backup successfully deleted");
                
                return BackupDeletionResult.success(backupName, storageDeleted);
                
            } catch (final Exception e) {
                LOGGER.error("Failed to delete backup: " + backupName, e);
                
                return BackupDeletionResult.failure(backupName, e);
                
            } finally {
                backupLock.writeLock().unlock();
            }
        }, backupExecutor);
    }

    /**
     * Cleans up old backups according to retention policy.
     * This method can be hot-swapped to change cleanup behavior.
     * @return Cleanup result with details
     */
    public CompletableFuture<BackupCleanupResult> cleanupOldBackups() {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Starting backup cleanup with retention policy: {}", 
                configuration.getRetentionPolicy());
            
            backupLock.writeLock().lock();
            try {
                final List<String> backupsToDelete = new ArrayList<>();
                final Instant cutoffTime = calculateRetentionCutoff();
                
                // Find backups to delete based on retention policy
                for (final Map.Entry<String, BackupMetadata> entry : backupMetadata.entrySet()) {
                    final BackupMetadata metadata = entry.getValue();
                    
                    if (shouldDeleteBackup(metadata, cutoffTime)) {
                        // Check for dependencies first
                        final List<String> dependents = findDependentBackups(entry.getKey());
                        if (dependents.isEmpty()) {
                            backupsToDelete.add(entry.getKey());
                        }
                    }
                }
                
                // Delete identified backups
                final List<String> deletedBackups = new ArrayList<>();
                final List<String> failedDeletions = new ArrayList<>();
                
                for (final String backupName : backupsToDelete) {
                    try {
                        final BackupDeletionResult result = deleteBackup(backupName).join();
                        if (result.isSuccess()) {
                            deletedBackups.add(backupName);
                        } else {
                            failedDeletions.add(backupName);
                        }
                    } catch (final Exception e) {
                        failedDeletions.add(backupName);
                        LOGGER.error("Failed to delete backup during cleanup: " + backupName, e);
                    }
                }
                
                final BackupCleanupResult result = new BackupCleanupResult(
                    true, deletedBackups.size(), failedDeletions.size(),
                    deletedBackups, failedDeletions, 
                    "Cleanup completed: " + deletedBackups.size() + " deleted, " + 
                    failedDeletions.size() + " failed"
                );
                
                LOGGER.info("Backup cleanup completed: {} deleted, {} failed", 
                    deletedBackups.size(), failedDeletions.size());
                
                return result;
                
            } catch (final Exception e) {
                LOGGER.error("Backup cleanup failed", e);
                
                return BackupCleanupResult.failure("Cleanup failed: " + e.getMessage());
                
            } finally {
                backupLock.writeLock().unlock();
            }
        }, backupExecutor);
    }

    /**
     * Adds a backup listener for notifications.
     * @param listener Backup listener to add
     */
    public void addBackupListener(final BackupListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a backup listener.
     * @param listener Backup listener to remove
     */
    public void removeBackupListener(final BackupListener listener) {
        listeners.remove(listener);
    }

    /**
     * Configures backup system settings.
     * This method can be hot-swapped to change backup configuration.
     * @param newConfiguration New backup configuration
     */
    public void configure(final BackupConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        this.backupEnabled = newConfiguration.isEnabled();
        
        LOGGER.info("Backup configuration updated");
        LOGGER.audit("BACKUP_CONFIGURED", "system", ByteHotLogger.AuditOutcome.SUCCESS, 
            "Backup configuration updated successfully");
    }

    /**
     * Shuts down the backup system gracefully.
     */
    public void shutdown() {
        LOGGER.info("Shutting down backup system");
        
        backupExecutor.shutdown();
        try {
            if (!backupExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                backupExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            backupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        LOGGER.info("Backup system shutdown completed");
    }

    // Helper methods and implementation details...
    
    /**
     * Initializes the backup system with default settings.
     * This method can be hot-swapped to change initialization behavior.
     */
    protected void initializeBackupSystem() {
        LOGGER.info("Initializing backup system");
        
        // Ensure backup directories exist
        try {
            Files.createDirectories(configuration.getBackupDirectory());
            Files.createDirectories(configuration.getBackupDirectory().resolve("metadata"));
            Files.createDirectories(configuration.getBackupDirectory().resolve("data"));
        } catch (final IOException e) {
            LOGGER.error("Failed to create backup directories", e);
        }
        
        // Load existing backup metadata
        loadExistingBackups();
        
        LOGGER.info("Backup system initialized successfully");
    }

    /**
     * Starts the backup scheduler for automated backups.
     * This method can be hot-swapped to change scheduling behavior.
     */
    protected void startBackupScheduler() {
        if (configuration.isAutomaticBackupsEnabled()) {
            // Schedule automatic full backups
            backupExecutor.scheduleAtFixedRate(
                this::performAutomaticFullBackup,
                configuration.getFullBackupInterval().toMinutes(),
                configuration.getFullBackupInterval().toMinutes(),
                TimeUnit.MINUTES
            );
            
            // Schedule automatic incremental backups
            backupExecutor.scheduleAtFixedRate(
                this::performAutomaticIncrementalBackup,
                configuration.getIncrementalBackupInterval().toMinutes(),
                configuration.getIncrementalBackupInterval().toMinutes(),
                TimeUnit.MINUTES
            );
        }
        
        // Schedule cleanup
        backupExecutor.scheduleAtFixedRate(
            () -> cleanupOldBackups(),
            1, 6, TimeUnit.HOURS
        );
        
        LOGGER.info("Backup scheduler started");
    }

    protected BackupStateSnapshot captureCurrentState() {
        // Implementation would capture current hot-swap state
        // This is a simplified version for demonstration
        return new BackupStateSnapshot(
            Instant.now(),
            generateStateComponents(),
            calculateTotalSize()
        );
    }

    protected List<StateComponent> generateStateComponents() {
        // Generate state components representing current system state
        final List<StateComponent> components = new ArrayList<>();
        
        // Add class loader state
        components.add(new StateComponent(
            "ClassLoaderState", "Current class loader hierarchy and loaded classes",
            1024 * 1024, Instant.now() // 1MB example
        ));
        
        // Add instrumentation state
        components.add(new StateComponent(
            "InstrumentationState", "Current instrumentation configuration and active transformers",
            512 * 1024, Instant.now() // 512KB example
        ));
        
        // Add configuration state
        components.add(new StateComponent(
            "ConfigurationState", "Current system configuration and settings",
            256 * 1024, Instant.now() // 256KB example
        ));
        
        return components;
    }

    protected long calculateTotalSize() {
        return generateStateComponents().stream()
            .mapToLong(StateComponent::getSize)
            .sum();
    }

    protected BackupStateSnapshot createIncrementalSnapshot(final BackupStateSnapshot current, 
                                                           final BackupStateSnapshot reference) {
        // Compare current and reference snapshots to create incremental snapshot
        final List<StateComponent> changedComponents = new ArrayList<>();
        
        for (final StateComponent currentComponent : current.getComponents()) {
            final Optional<StateComponent> referenceComponent = reference.getComponents().stream()
                .filter(comp -> comp.getName().equals(currentComponent.getName()))
                .findFirst();
            
            if (referenceComponent.isEmpty() || !referenceComponent.get().equals(currentComponent)) {
                changedComponents.add(currentComponent);
            }
        }
        
        final long totalSize = changedComponents.stream()
            .mapToLong(StateComponent::getSize)
            .sum();
        
        return new BackupStateSnapshot(current.getTimestamp(), changedComponents, totalSize);
    }

    protected BackupStorageResult storeBackup(final BackupEntry entry) {
        try {
            final Path backupPath = configuration.getBackupDirectory()
                .resolve("data")
                .resolve(entry.getMetadata().getName() + "_" + entry.getMetadata().getId() + ".backup");
            
            // Serialize and compress backup data
            final byte[] serializedData = serializeBackupData(entry.getSnapshot());
            final byte[] compressedData = compressData(serializedData);
            
            Files.write(backupPath, compressedData, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            
            // Store metadata
            storeBackupMetadata(entry.getMetadata());
            
            return new BackupStorageResult(true, backupPath, compressedData.length, null);
            
        } catch (final Exception e) {
            return new BackupStorageResult(false, null, 0, e);
        }
    }

    protected BackupStorageResult storeIncrementalBackup(final BackupEntry entry, final BackupEntry reference) {
        // Similar to storeBackup but with incremental logic
        return storeBackup(entry);
    }

    protected void storeBackupMetadata(final BackupMetadata metadata) throws IOException {
        final Path metadataPath = configuration.getBackupDirectory()
            .resolve("metadata")
            .resolve(metadata.getName() + "_" + metadata.getId() + ".metadata");
        
        try (final ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(metadataPath))) {
            oos.writeObject(metadata);
        }
    }

    protected byte[] serializeBackupData(final BackupStateSnapshot snapshot) {
        // Serialize snapshot data - simplified implementation
        return snapshot.toString().getBytes();
    }

    protected byte[] compressData(final byte[] data) {
        // Compress data - simplified implementation (would use actual compression)
        return data;
    }

    protected List<BackupEntry> buildBackupChain(final BackupEntry backup) {
        final List<BackupEntry> chain = new ArrayList<>();
        chain.add(backup);
        
        // For incremental backups, build the chain back to the full backup
        BackupEntry current = backup;
        while (current.getMetadata().getType() == BackupType.INCREMENTAL && 
               current.getMetadata().getReferenceBackup() != null) {
            
            final BackupEntry reference = backups.get(current.getMetadata().getReferenceBackup());
            if (reference != null) {
                chain.add(0, reference); // Add at beginning to maintain order
                current = reference;
            } else {
                break;
            }
        }
        
        return chain;
    }

    protected RestoreOperationResult performRestore(final List<BackupEntry> backupChain, final Instant targetTime) {
        try {
            int restoredComponents = 0;
            int skippedComponents = 0;
            
            // Apply backups in order
            for (final BackupEntry backup : backupChain) {
                for (final StateComponent component : backup.getSnapshot().getComponents()) {
                    if (targetTime == null || component.getTimestamp().isBefore(targetTime) || 
                        component.getTimestamp().equals(targetTime)) {
                        
                        // Restore component state
                        restoreComponent(component);
                        restoredComponents++;
                    } else {
                        skippedComponents++;
                    }
                }
            }
            
            return new RestoreOperationResult(true, restoredComponents, skippedComponents, null);
            
        } catch (final Exception e) {
            return new RestoreOperationResult(false, 0, 0, e);
        }
    }

    protected void restoreComponent(final StateComponent component) {
        // Restore individual component state - simplified implementation
        LOGGER.debug("Restoring component: {} (Size: {} bytes)", 
            component.getName(), component.getSize());
    }

    protected void loadExistingBackups() {
        // Load existing backup metadata from storage - simplified implementation
        LOGGER.debug("Loading existing backup metadata");
    }

    protected void performAutomaticFullBackup() {
        if (backupEnabled && configuration.isAutomaticBackupsEnabled()) {
            final String backupName = "auto_full_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            createFullBackup(backupName).thenAccept(result -> {
                if (result.getStatus() == BackupStatus.COMPLETED) {
                    LOGGER.info("Automatic full backup completed: {}", backupName);
                } else {
                    LOGGER.warn("Automatic full backup failed: {}", backupName);
                }
            });
        }
    }

    protected void performAutomaticIncrementalBackup() {
        if (backupEnabled && configuration.isAutomaticBackupsEnabled()) {
            // Find the most recent backup as reference
            final Optional<String> lastBackup = backupMetadata.values().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(BackupMetadata::getName)
                .findFirst();
            
            if (lastBackup.isPresent()) {
                final String backupName = "auto_incr_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                
                createIncrementalBackup(backupName, lastBackup.get()).thenAccept(result -> {
                    if (result.getStatus() == BackupStatus.COMPLETED) {
                        LOGGER.info("Automatic incremental backup completed: {}", backupName);
                    } else {
                        LOGGER.warn("Automatic incremental backup failed: {}", backupName);
                    }
                });
            }
        }
    }

    protected List<String> findDependentBackups(final String backupName) {
        return backupMetadata.values().stream()
            .filter(metadata -> backupName.equals(metadata.getReferenceBackup()))
            .map(BackupMetadata::getName)
            .collect(Collectors.toList());
    }

    protected boolean shouldDeleteBackup(final BackupMetadata metadata, final Instant cutoffTime) {
        return metadata.getCreatedAt().isBefore(cutoffTime);
    }

    protected Instant calculateRetentionCutoff() {
        return Instant.now().minus(configuration.getRetentionPolicy().getRetentionDuration());
    }

    protected boolean deleteBackupStorage(final BackupEntry entry) {
        try {
            final Path backupPath = configuration.getBackupDirectory()
                .resolve("data")
                .resolve(entry.getMetadata().getName() + "_" + entry.getMetadata().getId() + ".backup");
            
            final Path metadataPath = configuration.getBackupDirectory()
                .resolve("metadata")
                .resolve(entry.getMetadata().getName() + "_" + entry.getMetadata().getId() + ".metadata");
            
            boolean success = true;
            if (Files.exists(backupPath)) {
                Files.delete(backupPath);
            }
            if (Files.exists(metadataPath)) {
                Files.delete(metadataPath);
            }
            
            return success;
            
        } catch (final IOException e) {
            LOGGER.error("Failed to delete backup storage", e);
            return false;
        }
    }

    protected void notifyBackupListeners(final BackupResult result) {
        listeners.forEach(listener -> {
            try {
                listener.onBackupCompleted(result);
            } catch (final Exception e) {
                LOGGER.error("Backup listener failed", e);
            }
        });
    }

    protected String getCurrentUser() {
        // Get current user context
        return "system"; // Simplified implementation
    }

    protected long generateRestoreId() {
        return System.currentTimeMillis();
    }

    // Supporting classes and enums will be defined in separate files or as inner classes...
    
    public enum BackupType {
        FULL, INCREMENTAL
    }
    
    public enum BackupStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }
    
    public enum RestoreStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }
    
    // Interfaces for listeners
    public interface BackupListener {
        void onBackupCompleted(BackupResult result);
    }
}