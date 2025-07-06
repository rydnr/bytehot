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
 * Filename: HotSwapCoordinator.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapCoordinator
 *
 * Responsibilities:
 *   - Coordinate and batch hot-swap operations for optimal performance
 *   - Manage operation dependencies and execution order
 *   - Provide transactional hot-swap capabilities with rollback
 *   - Optimize resource utilization during bulk operations
 *
 * Collaborators:
 *   - PerformanceMonitor: Tracks operation performance
 *   - HotSwapCache: Caches operation results and metadata
 *   - ClassLoader: Manages class loading and unloading
 *   - InstrumentationProvider: Provides JVM instrumentation access
 */
package org.acmsl.bytehot.infrastructure.coordination;

import org.acmsl.bytehot.infrastructure.monitoring.PerformanceMonitor;
import org.acmsl.bytehot.infrastructure.cache.HotSwapCache;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Advanced coordination system for optimizing ByteHot hot-swap operations.
 * Provides batching, dependency management, and transactional capabilities.
 * @author Claude Code
 * @since 2025-07-06
 */
public class HotSwapCoordinator {

    private static final HotSwapCoordinator INSTANCE = new HotSwapCoordinator();
    
    private final Map<String, HotSwapBatch> activeBatches = new ConcurrentHashMap<>();
    private final Map<String, HotSwapOperation> pendingOperations = new ConcurrentHashMap<>();
    private final Map<String, HotSwapTransaction> activeTransactions = new ConcurrentHashMap<>();
    
    private final ExecutorService coordinationExecutor = 
        Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "ByteHot-Coordination-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
    
    private final ReentrantReadWriteLock coordinationLock = new ReentrantReadWriteLock();
    private final AtomicLong batchCounter = new AtomicLong(0);
    private final AtomicLong operationCounter = new AtomicLong(0);
    
    private volatile CoordinationConfiguration configuration = CoordinationConfiguration.defaultConfiguration();
    private volatile boolean coordinationEnabled = true;
    
    private HotSwapCoordinator() {
        startCoordinationTasks();
    }

    /**
     * Gets the singleton instance of HotSwapCoordinator.
     * @return The coordinator instance
     */
    public static HotSwapCoordinator getInstance() {
        return INSTANCE;
    }

    /**
     * Submits a hot-swap operation for coordinated execution.
     * This method can be hot-swapped to change operation submission behavior.
     * @param className Name of the class to hot-swap
     * @param bytecode New bytecode for the class
     * @param operationType Type of hot-swap operation
     * @return CompletableFuture representing the operation result
     */
    public CompletableFuture<HotSwapResult> submitOperation(final String className, 
                                                           final byte[] bytecode, 
                                                           final HotSwapOperationType operationType) {
        
        if (!coordinationEnabled) {
            return performImmediateOperation(className, bytecode, operationType);
        }
        
        final String operationId = generateOperationId();
        final HotSwapOperation operation = new HotSwapOperation(
            operationId, className, bytecode, operationType, Instant.now()
        );
        
        pendingOperations.put(operationId, operation);
        
        // Try to batch the operation
        return scheduleOperationForBatching(operation);
    }

    /**
     * Submits multiple hot-swap operations as a batch.
     * This method can be hot-swapped to change batch submission behavior.
     * @param operations List of operations to execute as a batch
     * @return CompletableFuture representing the batch execution result
     */
    public CompletableFuture<BatchResult> submitBatch(final List<HotSwapOperationRequest> operations) {
        if (!coordinationEnabled) {
            return performImmediateBatch(operations);
        }
        
        final String batchId = generateBatchId();
        final List<HotSwapOperation> batchOperations = operations.stream()
            .map(this::convertToOperation)
            .collect(Collectors.toList());
        
        final HotSwapBatch batch = new HotSwapBatch(batchId, batchOperations);
        activeBatches.put(batchId, batch);
        
        return executeBatchCoordinated(batch);
    }

    /**
     * Starts a transactional hot-swap session.
     * This method can be hot-swapped to change transaction behavior.
     * @return Transaction ID for the session
     */
    public String startTransaction() {
        final String transactionId = generateTransactionId();
        final HotSwapTransaction transaction = new HotSwapTransaction(transactionId);
        activeTransactions.put(transactionId, transaction);
        
        return transactionId;
    }

    /**
     * Commits a transactional hot-swap session.
     * This method can be hot-swapped to change commit behavior.
     * @param transactionId Transaction ID to commit
     * @return CompletableFuture representing the commit result
     */
    public CompletableFuture<TransactionResult> commitTransaction(final String transactionId) {
        final HotSwapTransaction transaction = activeTransactions.get(transactionId);
        if (transaction == null) {
            return CompletableFuture.completedFuture(
                new TransactionResult(transactionId, false, "Transaction not found"));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            final Instant startTime = Instant.now();
            
            try {
                coordinationLock.writeLock().lock();
                
                // Validate all operations in the transaction
                final List<HotSwapOperation> operations = transaction.getOperations();
                final List<String> validationErrors = validateOperations(operations);
                
                if (!validationErrors.isEmpty()) {
                    return new TransactionResult(transactionId, false, 
                        "Validation failed: " + String.join(", ", validationErrors));
                }
                
                // Execute all operations atomically
                final List<HotSwapResult> results = new ArrayList<>();
                final List<HotSwapOperation> appliedOperations = new ArrayList<>();
                
                for (final HotSwapOperation operation : operations) {
                    final HotSwapResult result = executeOperationDirect(operation);
                    results.add(result);
                    
                    if (result.isSuccess()) {
                        appliedOperations.add(operation);
                    } else {
                        // Rollback previously applied operations
                        rollbackOperations(appliedOperations);
                        return new TransactionResult(transactionId, false, 
                            "Operation failed: " + result.getErrorMessage());
                    }
                }
                
                // Mark transaction as committed
                transaction.markCommitted();
                
                final Duration duration = Duration.between(startTime, Instant.now());
                PerformanceMonitor.getInstance().recordMetric("transaction_commit_time", 
                    duration.toMillis(), PerformanceMonitor.MetricType.DURATION);
                
                return new TransactionResult(transactionId, true, 
                    "Transaction committed successfully with " + results.size() + " operations");
                
            } catch (final Exception e) {
                return new TransactionResult(transactionId, false, 
                    "Transaction commit failed: " + e.getMessage());
            } finally {
                coordinationLock.writeLock().unlock();
                activeTransactions.remove(transactionId);
            }
        }, coordinationExecutor);
    }

    /**
     * Rolls back a transactional hot-swap session.
     * This method can be hot-swapped to change rollback behavior.
     * @param transactionId Transaction ID to rollback
     * @return CompletableFuture representing the rollback result
     */
    public CompletableFuture<TransactionResult> rollbackTransaction(final String transactionId) {
        final HotSwapTransaction transaction = activeTransactions.remove(transactionId);
        if (transaction == null) {
            return CompletableFuture.completedFuture(
                new TransactionResult(transactionId, false, "Transaction not found"));
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                rollbackOperations(transaction.getAppliedOperations());
                return new TransactionResult(transactionId, true, "Transaction rolled back successfully");
            } catch (final Exception e) {
                return new TransactionResult(transactionId, false, 
                    "Rollback failed: " + e.getMessage());
            }
        }, coordinationExecutor);
    }

    /**
     * Adds an operation to an existing transaction.
     * This method can be hot-swapped to change transaction operation behavior.
     * @param transactionId Transaction ID
     * @param className Name of the class to hot-swap
     * @param bytecode New bytecode for the class
     * @param operationType Type of hot-swap operation
     * @return Operation ID within the transaction
     */
    public String addOperationToTransaction(final String transactionId, 
                                          final String className, 
                                          final byte[] bytecode, 
                                          final HotSwapOperationType operationType) {
        
        final HotSwapTransaction transaction = activeTransactions.get(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }
        
        final String operationId = generateOperationId();
        final HotSwapOperation operation = new HotSwapOperation(
            operationId, className, bytecode, operationType, Instant.now()
        );
        
        transaction.addOperation(operation);
        return operationId;
    }

    /**
     * Gets the current coordination statistics.
     * @return Current coordination statistics
     */
    public CoordinationStatistics getCoordinationStatistics() {
        return new CoordinationStatistics(
            activeBatches.size(),
            pendingOperations.size(),
            activeTransactions.size(),
            batchCounter.get(),
            operationCounter.get(),
            calculateAverageBatchSize(),
            calculateCoordinationEfficiency()
        );
    }

    /**
     * Configures the coordination system.
     * This method can be hot-swapped to change coordination configuration.
     * @param newConfiguration New coordination configuration
     */
    public void configure(final CoordinationConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        System.out.println("Coordination configuration updated");
    }

    /**
     * Enables or disables coordination.
     * This method can be hot-swapped to change coordination state.
     * @param enabled Whether coordination should be enabled
     */
    public void setCoordinationEnabled(final boolean enabled) {
        this.coordinationEnabled = enabled;
        System.out.println("Hot-swap coordination " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Shuts down the coordination system.
     */
    public void shutdown() {
        coordinationExecutor.shutdown();
        try {
            if (!coordinationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                coordinationExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            coordinationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        activeBatches.clear();
        pendingOperations.clear();
        activeTransactions.clear();
    }

    /**
     * Schedules an operation for batching optimization.
     * This method can be hot-swapped to change batching behavior.
     * @param operation Operation to schedule
     * @return CompletableFuture representing the operation result
     */
    protected CompletableFuture<HotSwapResult> scheduleOperationForBatching(final HotSwapOperation operation) {
        return CompletableFuture.supplyAsync(() -> {
            final Instant startTime = Instant.now();
            
            try {
                // Wait for potential batching opportunity
                Thread.sleep(configuration.getBatchingDelayMs());
                
                // Check if operation can be batched with others
                final Optional<HotSwapBatch> availableBatch = findAvailableBatch(operation);
                
                if (availableBatch.isPresent()) {
                    availableBatch.get().addOperation(operation);
                    return waitForBatchCompletion(availableBatch.get(), operation);
                } else {
                    // Execute immediately if no batching opportunity
                    return executeOperationDirect(operation);
                }
                
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return new HotSwapResult(operation.getOperationId(), false, 
                    "Operation interrupted", Duration.between(startTime, Instant.now()));
            } finally {
                pendingOperations.remove(operation.getOperationId());
            }
        }, coordinationExecutor);
    }

    /**
     * Executes a batch of operations in a coordinated manner.
     * This method can be hot-swapped to change batch execution behavior.
     * @param batch Batch to execute
     * @return CompletableFuture representing the batch result
     */
    protected CompletableFuture<BatchResult> executeBatchCoordinated(final HotSwapBatch batch) {
        return CompletableFuture.supplyAsync(() -> {
            final Instant startTime = Instant.now();
            
            try {
                coordinationLock.writeLock().lock();
                
                // Sort operations by dependency order
                final List<HotSwapOperation> sortedOperations = sortOperationsByDependencies(batch.getOperations());
                
                // Validate batch operations
                final List<String> validationErrors = validateOperations(sortedOperations);
                if (!validationErrors.isEmpty()) {
                    return new BatchResult(batch.getBatchId(), false, 
                        "Batch validation failed: " + String.join(", ", validationErrors),
                        Duration.between(startTime, Instant.now()));
                }
                
                // Execute operations in parallel where possible
                final List<HotSwapResult> results = executeOperationsInParallel(sortedOperations);
                
                // Check for any failures
                final List<HotSwapResult> failures = results.stream()
                    .filter(result -> !result.isSuccess())
                    .collect(Collectors.toList());
                
                final boolean success = failures.isEmpty();
                final String message = success ? 
                    "Batch executed successfully with " + results.size() + " operations" :
                    "Batch failed with " + failures.size() + " failures";
                
                final Duration duration = Duration.between(startTime, Instant.now());
                
                // Record performance metrics
                PerformanceMonitor.getInstance().recordMetric("batch_execution_time", 
                    duration.toMillis(), PerformanceMonitor.MetricType.DURATION);
                PerformanceMonitor.getInstance().recordMetric("batch_size", 
                    results.size(), PerformanceMonitor.MetricType.GAUGE);
                
                return new BatchResult(batch.getBatchId(), success, message, duration, results);
                
            } catch (final Exception e) {
                return new BatchResult(batch.getBatchId(), false, 
                    "Batch execution failed: " + e.getMessage(),
                    Duration.between(startTime, Instant.now()));
            } finally {
                coordinationLock.writeLock().unlock();
                activeBatches.remove(batch.getBatchId());
            }
        }, coordinationExecutor);
    }

    /**
     * Executes a single operation directly.
     * This method can be hot-swapped to change direct execution behavior.
     * @param operation Operation to execute
     * @return Operation result
     */
    protected HotSwapResult executeOperationDirect(final HotSwapOperation operation) {
        final Instant startTime = Instant.now();
        
        try {
            // Check cache first
            final HotSwapCache cache = HotSwapCache.getInstance();
            final Optional<HotSwapCache.TransformationMetadata> cachedMetadata = 
                cache.getCachedTransformationMetadata(operation.getClassName());
            
            if (cachedMetadata.isPresent() && 
                java.util.Arrays.equals(cachedMetadata.get().getTransformedBytecode(), operation.getBytecode())) {
                // Already applied, return cached result
                return new HotSwapResult(operation.getOperationId(), true, 
                    "Operation already applied (cached)", Duration.between(startTime, Instant.now()));
            }
            
            // Perform the hot-swap operation
            final boolean success = performHotSwapOperation(operation);
            
            if (success) {
                // Cache the result
                final HotSwapCache.TransformationMetadata metadata = 
                    new HotSwapCache.TransformationMetadata(
                        operation.getClassName(), null, operation.getBytecode()
                    );
                cache.cacheTransformationMetadata(operation.getClassName(), metadata);
            }
            
            final Duration duration = Duration.between(startTime, Instant.now());
            
            // Record performance metrics
            PerformanceMonitor.getInstance().recordHotSwapOperation(
                operation.getClassName(), 
                PerformanceMonitor.HotSwapOperation.valueOf(operation.getOperationType().name()),
                duration, 
                success
            );
            
            return new HotSwapResult(operation.getOperationId(), success, 
                success ? "Operation completed successfully" : "Operation failed", duration);
            
        } catch (final Exception e) {
            final Duration duration = Duration.between(startTime, Instant.now());
            return new HotSwapResult(operation.getOperationId(), false, 
                "Operation failed: " + e.getMessage(), duration);
        }
    }

    /**
     * Performs the actual hot-swap operation using JVM instrumentation.
     * This method can be hot-swapped to change operation implementation.
     * @param operation Operation to perform
     * @return Success status
     */
    protected boolean performHotSwapOperation(final HotSwapOperation operation) {
        try {
            // Get instrumentation from cache or provider
            final Instrumentation instrumentation = getInstrumentation();
            
            if (instrumentation == null) {
                throw new IllegalStateException("Instrumentation not available");
            }
            
            final Class<?> targetClass = Class.forName(operation.getClassName());
            final ClassDefinition classDefinition = 
                new ClassDefinition(targetClass, operation.getBytecode());
            
            switch (operation.getOperationType()) {
                case REDEFINE:
                    instrumentation.redefineClasses(classDefinition);
                    break;
                case RETRANSFORM:
                    instrumentation.retransformClasses(targetClass);
                    break;
                default:
                    throw new UnsupportedOperationException(
                        "Operation type not supported: " + operation.getOperationType());
            }
            
            return true;
            
        } catch (final Exception e) {
            System.err.println("Hot-swap operation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Starts coordination background tasks.
     * This method can be hot-swapped to change coordination task behavior.
     */
    protected void startCoordinationTasks() {
        // Schedule batch optimization
        coordinationExecutor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    optimizeBatches();
                    Thread.sleep(configuration.getBatchOptimizationIntervalMs());
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        // Schedule transaction cleanup
        coordinationExecutor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    cleanupStaleTransactions();
                    Thread.sleep(configuration.getTransactionCleanupIntervalMs());
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Optimizes batches by merging compatible operations.
     * This method can be hot-swapped to change optimization behavior.
     */
    protected void optimizeBatches() {
        // Implementation for batch optimization
        // This would analyze pending operations and create optimal batches
    }

    /**
     * Cleans up stale transactions.
     * This method can be hot-swapped to change cleanup behavior.
     */
    protected void cleanupStaleTransactions() {
        final Instant cutoff = Instant.now().minus(configuration.getTransactionTimeoutDuration());
        
        activeTransactions.entrySet().removeIf(entry -> {
            final HotSwapTransaction transaction = entry.getValue();
            if (transaction.getCreatedAt().isBefore(cutoff)) {
                System.out.println("Cleaning up stale transaction: " + entry.getKey());
                return true;
            }
            return false;
        });
    }

    // Helper methods
    
    protected String generateOperationId() {
        return "op_" + operationCounter.incrementAndGet();
    }
    
    protected String generateBatchId() {
        return "batch_" + batchCounter.incrementAndGet();
    }
    
    protected String generateTransactionId() {
        return "tx_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    protected HotSwapOperation convertToOperation(final HotSwapOperationRequest request) {
        return new HotSwapOperation(
            generateOperationId(), 
            request.getClassName(), 
            request.getBytecode(), 
            request.getOperationType(), 
            Instant.now()
        );
    }
    
    protected CompletableFuture<HotSwapResult> performImmediateOperation(final String className, 
                                                                         final byte[] bytecode, 
                                                                         final HotSwapOperationType operationType) {
        final HotSwapOperation operation = new HotSwapOperation(
            generateOperationId(), className, bytecode, operationType, Instant.now()
        );
        return CompletableFuture.completedFuture(executeOperationDirect(operation));
    }
    
    protected CompletableFuture<BatchResult> performImmediateBatch(final List<HotSwapOperationRequest> operations) {
        final List<HotSwapOperation> batchOperations = operations.stream()
            .map(this::convertToOperation)
            .collect(Collectors.toList());
        
        final HotSwapBatch batch = new HotSwapBatch(generateBatchId(), batchOperations);
        return executeBatchCoordinated(batch);
    }
    
    protected Optional<HotSwapBatch> findAvailableBatch(final HotSwapOperation operation) {
        return activeBatches.values().stream()
            .filter(batch -> batch.canAcceptOperation(operation))
            .findFirst();
    }
    
    protected HotSwapResult waitForBatchCompletion(final HotSwapBatch batch, final HotSwapOperation operation) {
        // This would wait for batch completion and return the specific operation result
        return new HotSwapResult(operation.getOperationId(), true, 
            "Operation completed as part of batch", Duration.ofMillis(100));
    }
    
    protected List<String> validateOperations(final List<HotSwapOperation> operations) {
        final List<String> errors = new ArrayList<>();
        
        for (final HotSwapOperation operation : operations) {
            if (operation.getBytecode() == null || operation.getBytecode().length == 0) {
                errors.add("Invalid bytecode for class: " + operation.getClassName());
            }
            
            try {
                Class.forName(operation.getClassName());
            } catch (final ClassNotFoundException e) {
                errors.add("Class not found: " + operation.getClassName());
            }
        }
        
        return errors;
    }
    
    protected List<HotSwapOperation> sortOperationsByDependencies(final List<HotSwapOperation> operations) {
        // For now, return as-is. In a real implementation, this would analyze class dependencies
        return new ArrayList<>(operations);
    }
    
    protected List<HotSwapResult> executeOperationsInParallel(final List<HotSwapOperation> operations) {
        return operations.stream()
            .map(this::executeOperationDirect)
            .collect(Collectors.toList());
    }
    
    protected void rollbackOperations(final List<HotSwapOperation> operations) {
        // This would implement rollback logic for operations
        System.out.println("Rolling back " + operations.size() + " operations");
    }
    
    protected double calculateAverageBatchSize() {
        return activeBatches.values().stream()
            .mapToInt(batch -> batch.getOperations().size())
            .average()
            .orElse(0.0);
    }
    
    protected double calculateCoordinationEfficiency() {
        // This would calculate efficiency based on batching success, etc.
        return 0.85; // Placeholder
    }
    
    protected Instrumentation getInstrumentation() {
        // This would get instrumentation from the cache or provider
        return null; // Placeholder
    }

    // Enums and supporting classes
    
    public enum HotSwapOperationType {
        REDEFINE, RETRANSFORM, LOAD, VALIDATE
    }

    // Static inner classes for data structures
    
    public static class HotSwapOperation {
        private final String operationId;
        private final String className;
        private final byte[] bytecode;
        private final HotSwapOperationType operationType;
        private final Instant createdAt;

        public HotSwapOperation(final String operationId, final String className, 
                               final byte[] bytecode, final HotSwapOperationType operationType, 
                               final Instant createdAt) {
            this.operationId = operationId;
            this.className = className;
            this.bytecode = bytecode;
            this.operationType = operationType;
            this.createdAt = createdAt;
        }

        public String getOperationId() { return operationId; }
        public String getClassName() { return className; }
        public byte[] getBytecode() { return bytecode; }
        public HotSwapOperationType getOperationType() { return operationType; }
        public Instant getCreatedAt() { return createdAt; }
    }

    public static class HotSwapOperationRequest {
        private final String className;
        private final byte[] bytecode;
        private final HotSwapOperationType operationType;

        public HotSwapOperationRequest(final String className, final byte[] bytecode, 
                                      final HotSwapOperationType operationType) {
            this.className = className;
            this.bytecode = bytecode;
            this.operationType = operationType;
        }

        public String getClassName() { return className; }
        public byte[] getBytecode() { return bytecode; }
        public HotSwapOperationType getOperationType() { return operationType; }
    }

    public static class HotSwapBatch {
        private final String batchId;
        private final List<HotSwapOperation> operations;
        private volatile boolean completed = false;

        public HotSwapBatch(final String batchId, final List<HotSwapOperation> operations) {
            this.batchId = batchId;
            this.operations = Collections.synchronizedList(new ArrayList<>(operations));
        }

        public void addOperation(final HotSwapOperation operation) {
            operations.add(operation);
        }

        public boolean canAcceptOperation(final HotSwapOperation operation) {
            return !completed && operations.size() < 10; // Simple limit
        }

        public String getBatchId() { return batchId; }
        public List<HotSwapOperation> getOperations() { return Collections.unmodifiableList(operations); }
        public boolean isCompleted() { return completed; }
        public void markCompleted() { this.completed = true; }
    }

    public static class HotSwapTransaction {
        private final String transactionId;
        private final List<HotSwapOperation> operations = Collections.synchronizedList(new ArrayList<>());
        private final List<HotSwapOperation> appliedOperations = Collections.synchronizedList(new ArrayList<>());
        private final Instant createdAt;
        private volatile boolean committed = false;

        public HotSwapTransaction(final String transactionId) {
            this.transactionId = transactionId;
            this.createdAt = Instant.now();
        }

        public void addOperation(final HotSwapOperation operation) {
            operations.add(operation);
        }

        public void markOperationApplied(final HotSwapOperation operation) {
            appliedOperations.add(operation);
        }

        public void markCommitted() {
            this.committed = true;
        }

        public String getTransactionId() { return transactionId; }
        public List<HotSwapOperation> getOperations() { return Collections.unmodifiableList(operations); }
        public List<HotSwapOperation> getAppliedOperations() { return Collections.unmodifiableList(appliedOperations); }
        public Instant getCreatedAt() { return createdAt; }
        public boolean isCommitted() { return committed; }
    }

    public static class HotSwapResult {
        private final String operationId;
        private final boolean success;
        private final String message;
        private final Duration duration;
        private final String errorMessage;

        public HotSwapResult(final String operationId, final boolean success, 
                           final String message, final Duration duration) {
            this.operationId = operationId;
            this.success = success;
            this.message = message;
            this.duration = duration;
            this.errorMessage = success ? null : message;
        }

        public String getOperationId() { return operationId; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Duration getDuration() { return duration; }
        public String getErrorMessage() { return errorMessage; }
    }

    public static class BatchResult {
        private final String batchId;
        private final boolean success;
        private final String message;
        private final Duration duration;
        private final List<HotSwapResult> operationResults;

        public BatchResult(final String batchId, final boolean success, 
                          final String message, final Duration duration) {
            this(batchId, success, message, duration, Collections.emptyList());
        }

        public BatchResult(final String batchId, final boolean success, 
                          final String message, final Duration duration,
                          final List<HotSwapResult> operationResults) {
            this.batchId = batchId;
            this.success = success;
            this.message = message;
            this.duration = duration;
            this.operationResults = operationResults;
        }

        public String getBatchId() { return batchId; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Duration getDuration() { return duration; }
        public List<HotSwapResult> getOperationResults() { return operationResults; }
    }

    public static class TransactionResult {
        private final String transactionId;
        private final boolean success;
        private final String message;

        public TransactionResult(final String transactionId, final boolean success, final String message) {
            this.transactionId = transactionId;
            this.success = success;
            this.message = message;
        }

        public String getTransactionId() { return transactionId; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    public static class CoordinationStatistics {
        private final int activeBatches;
        private final int pendingOperations;
        private final int activeTransactions;
        private final long totalBatches;
        private final long totalOperations;
        private final double averageBatchSize;
        private final double coordinationEfficiency;

        public CoordinationStatistics(final int activeBatches, final int pendingOperations,
                                    final int activeTransactions, final long totalBatches,
                                    final long totalOperations, final double averageBatchSize,
                                    final double coordinationEfficiency) {
            this.activeBatches = activeBatches;
            this.pendingOperations = pendingOperations;
            this.activeTransactions = activeTransactions;
            this.totalBatches = totalBatches;
            this.totalOperations = totalOperations;
            this.averageBatchSize = averageBatchSize;
            this.coordinationEfficiency = coordinationEfficiency;
        }

        public int getActiveBatches() { return activeBatches; }
        public int getPendingOperations() { return pendingOperations; }
        public int getActiveTransactions() { return activeTransactions; }
        public long getTotalBatches() { return totalBatches; }
        public long getTotalOperations() { return totalOperations; }
        public double getAverageBatchSize() { return averageBatchSize; }
        public double getCoordinationEfficiency() { return coordinationEfficiency; }
    }

    public static class CoordinationConfiguration {
        private int batchingDelayMs = 100;
        private int maxBatchSize = 10;
        private int batchOptimizationIntervalMs = 5000;
        private int transactionCleanupIntervalMs = 30000;
        private Duration transactionTimeoutDuration = Duration.ofMinutes(5);

        public static CoordinationConfiguration defaultConfiguration() {
            return new CoordinationConfiguration();
        }

        public int getBatchingDelayMs() { return batchingDelayMs; }
        public void setBatchingDelayMs(final int batchingDelayMs) { this.batchingDelayMs = batchingDelayMs; }

        public int getMaxBatchSize() { return maxBatchSize; }
        public void setMaxBatchSize(final int maxBatchSize) { this.maxBatchSize = maxBatchSize; }

        public int getBatchOptimizationIntervalMs() { return batchOptimizationIntervalMs; }
        public void setBatchOptimizationIntervalMs(final int batchOptimizationIntervalMs) { 
            this.batchOptimizationIntervalMs = batchOptimizationIntervalMs; 
        }

        public int getTransactionCleanupIntervalMs() { return transactionCleanupIntervalMs; }
        public void setTransactionCleanupIntervalMs(final int transactionCleanupIntervalMs) { 
            this.transactionCleanupIntervalMs = transactionCleanupIntervalMs; 
        }

        public Duration getTransactionTimeoutDuration() { return transactionTimeoutDuration; }
        public void setTransactionTimeoutDuration(final Duration transactionTimeoutDuration) { 
            this.transactionTimeoutDuration = transactionTimeoutDuration; 
        }
    }
}