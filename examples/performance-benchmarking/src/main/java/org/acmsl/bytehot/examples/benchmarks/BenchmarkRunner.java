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
 * Filename: BenchmarkRunner.java
 *
 * Author: Claude Code
 *
 * Class name: BenchmarkRunner
 *
 * Responsibilities:
 *   - Execute comprehensive ByteHot performance benchmarks
 *   - Generate detailed performance reports and analysis
 *   - Configure benchmark environments and parameters
 *   - Export results in multiple formats (JSON, CSV, HTML)
 *
 * Collaborators:
 *   - JMH: Java Microbenchmark Harness for benchmark execution
 *   - HotSwapPerformanceBenchmark: Core benchmark implementation
 *   - Micrometer: Metrics collection and reporting
 *   - FileWriter: Report generation and export
 */
package org.acmsl.bytehot.examples.benchmarks;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive benchmark runner for ByteHot performance analysis.
 * Executes all benchmark suites and generates detailed reports.
 * @author Claude Code
 * @since 2025-07-05
 */
public class BenchmarkRunner {

    private final MeterRegistry meterRegistry;
    private final Path outputDirectory;
    private final String timestamp;

    /**
     * Creates a new benchmark runner.
     */
    public BenchmarkRunner() {
        this.meterRegistry = new SimpleMeterRegistry();
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        this.outputDirectory = Paths.get("benchmark-results", timestamp);
        
        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create output directory: " + outputDirectory, e);
        }
    }

    /**
     * Main entry point for benchmark execution.
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
        BenchmarkRunner runner = new BenchmarkRunner();
        
        try {
            System.out.println("=== ByteHot Performance Benchmark Suite ===");
            System.out.println("Starting comprehensive performance analysis...");
            System.out.println("Results will be saved to: " + runner.outputDirectory);
            System.out.println();
            
            runner.runAllBenchmarks();
            
            System.out.println();
            System.out.println("=== Benchmark Suite Completed Successfully ===");
            System.out.println("Check results in: " + runner.outputDirectory);
            
        } catch (Exception e) {
            System.err.println("Benchmark execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Executes all benchmark suites.
     * This method can be hot-swapped to change benchmark execution strategy.
     * @throws RunnerException if benchmark execution fails
     * @throws IOException if report generation fails
     */
    protected void runAllBenchmarks() throws RunnerException, IOException {
        // Run core performance benchmarks
        System.out.println("Running core performance benchmarks...");
        Collection<RunResult> coreResults = runCoreBenchmarks();
        generateReports(coreResults, "core-performance");
        
        // Run memory benchmarks
        System.out.println("Running memory impact benchmarks...");
        Collection<RunResult> memoryResults = runMemoryBenchmarks();
        generateReports(memoryResults, "memory-impact");
        
        // Run concurrency benchmarks
        System.out.println("Running concurrency benchmarks...");
        Collection<RunResult> concurrencyResults = runConcurrencyBenchmarks();
        generateReports(concurrencyResults, "concurrency");
        
        // Run hot-swap specific benchmarks
        System.out.println("Running hot-swap specific benchmarks...");
        Collection<RunResult> hotSwapResults = runHotSwapBenchmarks();
        generateReports(hotSwapResults, "hot-swap-operations");
        
        // Generate consolidated report
        System.out.println("Generating consolidated analysis...");
        generateConsolidatedReport(coreResults, memoryResults, concurrencyResults, hotSwapResults);
    }

    /**
     * Runs core performance benchmarks.
     * This method can be hot-swapped to change core benchmark configuration.
     * @return Benchmark results
     * @throws RunnerException if benchmark execution fails
     */
    protected Collection<RunResult> runCoreBenchmarks() throws RunnerException {
        Options opts = new OptionsBuilder()
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".baseline.*")
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".simpleArithmetic.*")
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".complexBusinessLogic.*")
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".methodCallOverhead.*")
            .warmupIterations(3)
            .measurementIterations(5)
            .warmupTime(TimeValue.seconds(2))
            .measurementTime(TimeValue.seconds(3))
            .timeUnit(TimeUnit.NANOSECONDS)
            .forks(1)
            .jvmArgsAppend("-Xmx1g", "-XX:+UnlockExperimentalVMOptions")
            .build();

        Runner runner = new Runner(opts);
        return runner.run();
    }

    /**
     * Runs memory impact benchmarks.
     * This method can be hot-swapped to change memory benchmark configuration.
     * @return Benchmark results
     * @throws RunnerException if benchmark execution fails
     */
    protected Collection<RunResult> runMemoryBenchmarks() throws RunnerException {
        Options opts = new OptionsBuilder()
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".objectAllocation.*")
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".memoryIntensive.*")
            .warmupIterations(2)
            .measurementIterations(5)
            .warmupTime(TimeValue.seconds(1))
            .measurementTime(TimeValue.seconds(2))
            .timeUnit(TimeUnit.NANOSECONDS)
            .forks(1)
            .jvmArgsAppend("-Xmx2g", "-XX:+UseG1GC", "-XX:+UnlockExperimentalVMOptions")
            .build();

        Runner runner = new Runner(opts);
        return runner.run();
    }

    /**
     * Runs concurrency benchmarks.
     * This method can be hot-swapped to change concurrency benchmark configuration.
     * @return Benchmark results
     * @throws RunnerException if benchmark execution fails
     */
    protected Collection<RunResult> runConcurrencyBenchmarks() throws RunnerException {
        Options opts = new OptionsBuilder()
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".concurrent.*")
            .warmupIterations(3)
            .measurementIterations(5)
            .warmupTime(TimeValue.seconds(2))
            .measurementTime(TimeValue.seconds(3))
            .timeUnit(TimeUnit.NANOSECONDS)
            .forks(1)
            .threads(4) // Multi-threaded execution
            .jvmArgsAppend("-Xmx2g", "-XX:+UnlockExperimentalVMOptions")
            .build();

        Runner runner = new Runner(opts);
        return runner.run();
    }

    /**
     * Runs hot-swap specific benchmarks.
     * This method can be hot-swapped to change hot-swap benchmark configuration.
     * @return Benchmark results
     * @throws RunnerException if benchmark execution fails
     */
    protected Collection<RunResult> runHotSwapBenchmarks() throws RunnerException {
        Options opts = new OptionsBuilder()
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".recursive.*")
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".stringProcessing.*")
            .include(HotSwapPerformanceBenchmark.class.getSimpleName() + ".exceptionHandling.*")
            .warmupIterations(3)
            .measurementIterations(7)
            .warmupTime(TimeValue.seconds(2))
            .measurementTime(TimeValue.seconds(4))
            .timeUnit(TimeUnit.NANOSECONDS)
            .forks(1)
            .jvmArgsAppend("-Xmx1g", "-XX:+UnlockExperimentalVMOptions")
            .build();

        Runner runner = new Runner(opts);
        return runner.run();
    }

    /**
     * Generates reports for benchmark results.
     * This method can be hot-swapped to change report generation.
     * @param results Benchmark results
     * @param benchmarkType Type of benchmark for report naming
     * @throws IOException if report generation fails
     */
    protected void generateReports(final Collection<RunResult> results, final String benchmarkType) 
            throws IOException {
        
        // Generate JSON report
        generateJsonReport(results, benchmarkType);
        
        // Generate CSV report
        generateCsvReport(results, benchmarkType);
        
        // Generate HTML report
        generateHtmlReport(results, benchmarkType);
        
        // Update metrics registry
        updateMetrics(results, benchmarkType);
    }

    /**
     * Generates JSON format report.
     * This method can be hot-swapped to change JSON report format.
     * @param results Benchmark results
     * @param benchmarkType Benchmark type
     * @throws IOException if file writing fails
     */
    protected void generateJsonReport(final Collection<RunResult> results, final String benchmarkType) 
            throws IOException {
        
        Path jsonFile = outputDirectory.resolve(benchmarkType + "-results.json");
        StringBuilder json = new StringBuilder();
        
        json.append("{\n");
        json.append("  \"benchmarkType\": \"").append(benchmarkType).append("\",\n");
        json.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        json.append("  \"results\": [\n");
        
        boolean first = true;
        for (RunResult result : results) {
            if (!first) {
                json.append(",\n");
            }
            first = false;
            
            json.append("    {\n");
            json.append("      \"benchmark\": \"").append(result.getParams().getBenchmark()).append("\",\n");
            json.append("      \"mode\": \"").append(result.getParams().getMode()).append("\",\n");
            json.append("      \"score\": ").append(result.getPrimaryResult().getScore()).append(",\n");
            json.append("      \"scoreUnit\": \"").append(result.getPrimaryResult().getScoreUnit()).append("\",\n");
            json.append("      \"error\": ").append(result.getPrimaryResult().getScoreError()).append("\n");
            json.append("    }");
        }
        
        json.append("\n  ]\n");
        json.append("}\n");
        
        Files.write(jsonFile, json.toString().getBytes());
        System.out.println("JSON report saved: " + jsonFile);
    }

    /**
     * Generates CSV format report.
     * This method can be hot-swapped to change CSV report format.
     * @param results Benchmark results
     * @param benchmarkType Benchmark type
     * @throws IOException if file writing fails
     */
    protected void generateCsvReport(final Collection<RunResult> results, final String benchmarkType) 
            throws IOException {
        
        Path csvFile = outputDirectory.resolve(benchmarkType + "-results.csv");
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append("Benchmark,Mode,Score,ScoreUnit,Error,Samples\n");
        
        // Data rows
        for (RunResult result : results) {
            csv.append(result.getParams().getBenchmark()).append(",");
            csv.append(result.getParams().getMode()).append(",");
            csv.append(result.getPrimaryResult().getScore()).append(",");
            csv.append(result.getPrimaryResult().getScoreUnit()).append(",");
            csv.append(result.getPrimaryResult().getScoreError()).append(",");
            csv.append(result.getPrimaryResult().getSampleCount()).append("\n");
        }
        
        Files.write(csvFile, csv.toString().getBytes());
        System.out.println("CSV report saved: " + csvFile);
    }

    /**
     * Generates HTML format report with charts.
     * This method can be hot-swapped to change HTML report format.
     * @param results Benchmark results
     * @param benchmarkType Benchmark type
     * @throws IOException if file writing fails
     */
    protected void generateHtmlReport(final Collection<RunResult> results, final String benchmarkType) 
            throws IOException {
        
        Path htmlFile = outputDirectory.resolve(benchmarkType + "-results.html");
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>ByteHot Performance Benchmark: ").append(benchmarkType).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("table { border-collapse: collapse; width: 100%; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("th { background-color: #f2f2f2; }\n");
        html.append("tr:nth-child(even) { background-color: #f9f9f9; }\n");
        html.append(".header { background-color: #4CAF50; color: white; padding: 10px; }\n");
        html.append(".summary { background-color: #e7f3ff; padding: 15px; margin: 10px 0; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        html.append("<div class='header'>\n");
        html.append("<h1>ByteHot Performance Benchmark Results</h1>\n");
        html.append("<h2>").append(benchmarkType.toUpperCase()).append(" Benchmarks</h2>\n");
        html.append("</div>\n");
        
        html.append("<div class='summary'>\n");
        html.append("<p><strong>Generated:</strong> ").append(timestamp).append("</p>\n");
        html.append("<p><strong>Total Benchmarks:</strong> ").append(results.size()).append("</p>\n");
        html.append("<p><strong>JVM Version:</strong> ").append(System.getProperty("java.version")).append("</p>\n");
        html.append("</div>\n");
        
        html.append("<table>\n");
        html.append("<tr><th>Benchmark</th><th>Mode</th><th>Score</th><th>Unit</th><th>Error</th><th>Samples</th></tr>\n");
        
        for (RunResult result : results) {
            html.append("<tr>");
            html.append("<td>").append(result.getParams().getBenchmark()).append("</td>");
            html.append("<td>").append(result.getParams().getMode()).append("</td>");
            html.append("<td>").append(String.format("%.2f", result.getPrimaryResult().getScore())).append("</td>");
            html.append("<td>").append(result.getPrimaryResult().getScoreUnit()).append("</td>");
            html.append("<td>").append(String.format("%.2f", result.getPrimaryResult().getScoreError())).append("</td>");
            html.append("<td>").append(result.getPrimaryResult().getSampleCount()).append("</td>");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        html.append("</body>\n</html>\n");
        
        Files.write(htmlFile, html.toString().getBytes());
        System.out.println("HTML report saved: " + htmlFile);
    }

    /**
     * Updates metrics registry with benchmark results.
     * This method can be hot-swapped to change metrics collection.
     * @param results Benchmark results
     * @param benchmarkType Benchmark type
     */
    protected void updateMetrics(final Collection<RunResult> results, final String benchmarkType) {
        for (RunResult result : results) {
            String metricName = "bytehot.benchmark." + benchmarkType + "." + 
                               extractMethodName(result.getParams().getBenchmark());
            
            meterRegistry.gauge(metricName + ".score", result.getPrimaryResult().getScore());
            meterRegistry.gauge(metricName + ".error", result.getPrimaryResult().getScoreError());
        }
    }

    /**
     * Extracts method name from full benchmark name.
     * This method can be hot-swapped to change name extraction logic.
     * @param fullBenchmarkName Full benchmark name
     * @return Extracted method name
     */
    protected String extractMethodName(final String fullBenchmarkName) {
        String[] parts = fullBenchmarkName.split("\\.");
        return parts[parts.length - 1];
    }

    /**
     * Generates consolidated report combining all benchmark types.
     * This method can be hot-swapped to change consolidated report format.
     * @param coreResults Core benchmark results
     * @param memoryResults Memory benchmark results
     * @param concurrencyResults Concurrency benchmark results
     * @param hotSwapResults Hot-swap benchmark results
     * @throws IOException if report generation fails
     */
    protected void generateConsolidatedReport(final Collection<RunResult> coreResults,
                                            final Collection<RunResult> memoryResults,
                                            final Collection<RunResult> concurrencyResults,
                                            final Collection<RunResult> hotSwapResults) throws IOException {
        
        Path consolidatedFile = outputDirectory.resolve("consolidated-analysis.html");
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<title>ByteHot Comprehensive Performance Analysis</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append(".section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; }\n");
        html.append(".header { background-color: #2196F3; color: white; padding: 15px; text-align: center; }\n");
        html.append(".summary { background-color: #f0f8ff; padding: 10px; }\n");
        html.append(".metric { display: inline-block; margin: 10px; padding: 10px; border: 1px solid #ccc; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        html.append("<div class='header'>\n");
        html.append("<h1>ByteHot Comprehensive Performance Analysis</h1>\n");
        html.append("<p>Generated: ").append(timestamp).append("</p>\n");
        html.append("</div>\n");
        
        html.append("<div class='section'>\n");
        html.append("<h2>Executive Summary</h2>\n");
        html.append("<div class='summary'>\n");
        html.append("<div class='metric'><strong>Core Benchmarks:</strong> ").append(coreResults.size()).append("</div>\n");
        html.append("<div class='metric'><strong>Memory Benchmarks:</strong> ").append(memoryResults.size()).append("</div>\n");
        html.append("<div class='metric'><strong>Concurrency Benchmarks:</strong> ").append(concurrencyResults.size()).append("</div>\n");
        html.append("<div class='metric'><strong>Hot-Swap Benchmarks:</strong> ").append(hotSwapResults.size()).append("</div>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        
        html.append("<div class='section'>\n");
        html.append("<h2>Performance Insights</h2>\n");
        html.append("<p>This comprehensive analysis demonstrates ByteHot's performance characteristics across various scenarios:</p>\n");
        html.append("<ul>\n");
        html.append("<li><strong>Core Performance:</strong> Baseline execution overhead analysis</li>\n");
        html.append("<li><strong>Memory Impact:</strong> GC and allocation overhead assessment</li>\n");
        html.append("<li><strong>Concurrency:</strong> Multi-threaded hot-swapping behavior</li>\n");
        html.append("<li><strong>Hot-Swap Operations:</strong> Runtime code modification performance</li>\n");
        html.append("</ul>\n");
        html.append("</div>\n");
        
        html.append("<div class='section'>\n");
        html.append("<h2>Report Files</h2>\n");
        html.append("<ul>\n");
        html.append("<li><a href='core-performance-results.html'>Core Performance Results</a></li>\n");
        html.append("<li><a href='memory-impact-results.html'>Memory Impact Results</a></li>\n");
        html.append("<li><a href='concurrency-results.html'>Concurrency Results</a></li>\n");
        html.append("<li><a href='hot-swap-operations-results.html'>Hot-Swap Operations Results</a></li>\n");
        html.append("</ul>\n");
        html.append("</div>\n");
        
        html.append("</body>\n</html>\n");
        
        Files.write(consolidatedFile, html.toString().getBytes());
        System.out.println("Consolidated analysis saved: " + consolidatedFile);
    }
}