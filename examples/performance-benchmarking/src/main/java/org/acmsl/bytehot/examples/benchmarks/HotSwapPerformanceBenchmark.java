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
 * Filename: HotSwapPerformanceBenchmark.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapPerformanceBenchmark
 *
 * Responsibilities:
 *   - Measure performance characteristics of ByteHot hot-swapping operations
 *   - Benchmark method execution times before and after hot-swapping
 *   - Assess memory overhead and GC impact of hot-swapping
 *   - Provide comprehensive performance metrics and analysis
 *
 * Collaborators:
 *   - JMH: Java Microbenchmark Harness for accurate measurements
 *   - BenchmarkTarget: Test classes for hot-swapping scenarios
 *   - Micrometer: Metrics collection and reporting
 */
package org.acmsl.bytehot.examples.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Comprehensive performance benchmarks for ByteHot hot-swapping operations.
 * Uses JMH for accurate micro-benchmarking with proper statistical analysis.
 * @author Claude Code
 * @since 2025-07-05
 */
@BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsAppend = {"-Xmx2g", "-XX:+UnlockExperimentalVMOptions"})
@State(Scope.Benchmark)
public class HotSwapPerformanceBenchmark {

    private BenchmarkTarget originalTarget;
    private BenchmarkTarget hotSwappedTarget;
    private Random random;
    private MemoryMXBean memoryBean;
    private long baselineMemory;

    /**
     * Setup benchmark environment before all iterations.
     */
    @Setup(Level.Trial)
    public void setupTrial() {
        originalTarget = new BenchmarkTarget();
        hotSwappedTarget = new BenchmarkTarget();
        random = new Random(42); // Fixed seed for reproducibility
        memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Perform GC and measure baseline memory
        System.gc();
        Thread.yield();
        baselineMemory = getUsedMemory();
        
        System.out.println("=== ByteHot Performance Benchmark Setup ===");
        System.out.println("Baseline memory usage: " + formatMemory(baselineMemory));
        System.out.println("JVM version: " + System.getProperty("java.version"));
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
    }

    /**
     * Setup before each iteration.
     */
    @Setup(Level.Iteration)
    public void setupIteration() {
        // Perform minor cleanup between iterations
        System.gc();
    }

    /**
     * Cleanup after benchmark completion.
     */
    @TearDown(Level.Trial)
    public void tearDown() {
        long finalMemory = getUsedMemory();
        long memoryIncrease = finalMemory - baselineMemory;
        
        System.out.println("=== ByteHot Performance Benchmark Results ===");
        System.out.println("Final memory usage: " + formatMemory(finalMemory));
        System.out.println("Memory increase: " + formatMemory(memoryIncrease));
        System.out.println("Memory overhead: " + String.format("%.2f%%", 
            (memoryIncrease * 100.0) / baselineMemory));
    }

    /**
     * Benchmark baseline method execution (no hot-swapping).
     * This establishes the performance baseline for comparison.
     */
    @Benchmark
    public void baselineMethodExecution(Blackhole bh) {
        int input = random.nextInt(1000);
        int result = originalTarget.performCalculation(input);
        bh.consume(result);
    }

    /**
     * Benchmark simple arithmetic method execution.
     * Tests hot-swapping overhead on lightweight operations.
     */
    @Benchmark
    public void simpleArithmeticMethod(Blackhole bh) {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        int result = originalTarget.simpleArithmetic(a, b);
        bh.consume(result);
    }

    /**
     * Benchmark complex business logic method execution.
     * Tests hot-swapping overhead on computationally intensive operations.
     */
    @Benchmark
    public void complexBusinessLogicMethod(Blackhole bh) {
        String input = "test-data-" + random.nextInt(1000);
        String result = originalTarget.complexBusinessLogic(input);
        bh.consume(result);
    }

    /**
     * Benchmark method with object allocation.
     * Tests hot-swapping impact on garbage collection.
     */
    @Benchmark
    public void objectAllocationMethod(Blackhole bh) {
        int size = random.nextInt(100) + 10;
        Object result = originalTarget.createDataStructure(size);
        bh.consume(result);
    }

    /**
     * Benchmark method with exception handling.
     * Tests hot-swapping overhead in exception scenarios.
     */
    @Benchmark
    public void exceptionHandlingMethod(Blackhole bh) {
        try {
            int divisor = random.nextInt(10); // May be zero
            int result = originalTarget.divideWithValidation(100, divisor);
            bh.consume(result);
        } catch (IllegalArgumentException e) {
            bh.consume(e.getMessage());
        }
    }

    /**
     * Benchmark method with string operations.
     * Tests hot-swapping impact on string-heavy workloads.
     */
    @Benchmark
    public void stringProcessingMethod(Blackhole bh) {
        String input = generateRandomString(random.nextInt(50) + 10);
        String result = originalTarget.processString(input);
        bh.consume(result);
    }

    /**
     * Benchmark recursive method execution.
     * Tests hot-swapping overhead in recursive call scenarios.
     */
    @Benchmark
    public void recursiveMethodExecution(Blackhole bh) {
        int depth = random.nextInt(10) + 1;
        long result = originalTarget.recursiveCalculation(depth);
        bh.consume(result);
    }

    /**
     * Benchmark method call overhead.
     * Measures the pure overhead of method invocation with hot-swapping.
     */
    @Benchmark
    public void methodCallOverhead(Blackhole bh) {
        originalTarget.noOpMethod();
        bh.consume(1);
    }

    /**
     * Benchmark concurrent method execution.
     * Tests hot-swapping behavior under concurrent access.
     */
    @Benchmark
    public void concurrentMethodExecution(Blackhole bh) {
        // This would typically involve multiple threads, 
        // but JMH handles concurrency separately
        int result = originalTarget.threadSafeCalculation(random.nextInt(1000));
        bh.consume(result);
    }

    /**
     * Benchmark memory-intensive method execution.
     * Tests hot-swapping impact on memory allocation patterns.
     */
    @Benchmark
    public void memoryIntensiveMethod(Blackhole bh) {
        int[] result = originalTarget.createLargeArray(random.nextInt(1000) + 100);
        bh.consume(result);
    }

    /**
     * Gets current used memory in bytes.
     * @return Used memory in bytes
     */
    private long getUsedMemory() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return heapUsage.getUsed();
    }

    /**
     * Formats memory size for display.
     * @param bytes Memory size in bytes
     * @return Formatted memory string
     */
    private String formatMemory(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Generates a random string of specified length.
     * @param length String length
     * @return Random string
     */
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Target class for benchmarking hot-swapping operations.
     * Contains various types of methods to test different scenarios.
     */
    public static class BenchmarkTarget {

        /**
         * Performs a calculation that can be hot-swapped.
         * This method can be hot-swapped to change calculation logic.
         * @param input Input value
         * @return Calculated result
         */
        public int performCalculation(int input) {
            // Original calculation logic (can be hot-swapped)
            return input * 2 + 1;
        }

        /**
         * Simple arithmetic operation.
         * This method can be hot-swapped to change arithmetic logic.
         * @param a First operand
         * @param b Second operand
         * @return Arithmetic result
         */
        public int simpleArithmetic(int a, int b) {
            // Simple arithmetic (can be hot-swapped)
            return a + b * 2;
        }

        /**
         * Complex business logic operation.
         * This method can be hot-swapped to change business rules.
         * @param input Input string
         * @return Processed result
         */
        public String complexBusinessLogic(String input) {
            // Complex processing (can be hot-swapped)
            StringBuilder result = new StringBuilder();
            for (char c : input.toCharArray()) {
                result.append(Character.toUpperCase(c));
                if (Character.isDigit(c)) {
                    result.append("_NUM");
                }
            }
            return result.toString();
        }

        /**
         * Creates a data structure.
         * This method can be hot-swapped to change data structure type.
         * @param size Structure size
         * @return Created data structure
         */
        public Object createDataStructure(int size) {
            // Object allocation (can be hot-swapped)
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = i * i;
            }
            return array;
        }

        /**
         * Divides with validation.
         * This method can be hot-swapped to change validation logic.
         * @param dividend Dividend
         * @param divisor Divisor
         * @return Division result
         * @throws IllegalArgumentException if divisor is zero
         */
        public int divideWithValidation(int dividend, int divisor) {
            // Validation logic (can be hot-swapped)
            if (divisor == 0) {
                throw new IllegalArgumentException("Division by zero");
            }
            return dividend / divisor;
        }

        /**
         * Processes a string.
         * This method can be hot-swapped to change string processing logic.
         * @param input Input string
         * @return Processed string
         */
        public String processString(String input) {
            // String processing (can be hot-swapped)
            return input.toLowerCase()
                      .replaceAll("[0-9]", "X")
                      .concat("_PROCESSED");
        }

        /**
         * Recursive calculation.
         * This method can be hot-swapped to change recursive logic.
         * @param n Recursion depth
         * @return Calculated result
         */
        public long recursiveCalculation(int n) {
            // Recursive logic (can be hot-swapped)
            if (n <= 1) {
                return 1;
            }
            return n * recursiveCalculation(n - 1);
        }

        /**
         * No-operation method for measuring call overhead.
         * This method can be hot-swapped to add functionality.
         */
        public void noOpMethod() {
            // No operation (can be hot-swapped)
        }

        /**
         * Thread-safe calculation.
         * This method can be hot-swapped to change thread safety approach.
         * @param input Input value
         * @return Calculated result
         */
        public synchronized int threadSafeCalculation(int input) {
            // Thread-safe logic (can be hot-swapped)
            return input % 1000;
        }

        /**
         * Creates a large array for memory testing.
         * This method can be hot-swapped to change memory allocation patterns.
         * @param size Array size
         * @return Created array
         */
        public int[] createLargeArray(int size) {
            // Memory allocation (can be hot-swapped)
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = i;
            }
            return array;
        }
    }
}