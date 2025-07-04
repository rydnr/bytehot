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
 * Filename: BackoffStrategy.java
 *
 * Author: Claude Code
 *
 * Class name: BackoffStrategy
 *
 * Responsibilities:
 *   - Calculate wait times between retry attempts
 *   - Support different backoff algorithms
 *   - Enable customizable retry timing patterns
 *
 * Collaborators:
 *   - RetryPolicy: Uses strategies for wait time calculation
 *   - RecoveryAttempt: Applies backoff for retry timing
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.util.function.BiFunction;

/**
 * Strategy for calculating backoff wait times between retry attempts.
 * @author Claude Code
 * @since 2025-07-04
 */
public class BackoffStrategy {
    
    /**
     * Function to calculate wait time based on attempt number and base duration.
     */
    private final BiFunction<Integer, Duration, Duration> calculator;
    
    /**
     * Name of this backoff strategy.
     */
    private final String name;
    
    /**
     * Configuration parameter for the strategy.
     */
    private final double parameter;
    
    /**
     * Creates a new BackoffStrategy.
     * @param calculator The wait time calculator function
     * @param name The strategy name
     * @param parameter The strategy parameter
     */
    protected BackoffStrategy(final BiFunction<Integer, Duration, Duration> calculator,
                            final String name,
                            final double parameter) {
        this.calculator = calculator;
        this.name = name;
        this.parameter = parameter;
    }
    
    /**
     * Creates a fixed backoff strategy (constant wait time).
     * @return A fixed backoff strategy
     */
    public static BackoffStrategy fixed() {
        return new BackoffStrategy(
            (attemptNumber, initialWait) -> initialWait,
            "Fixed",
            1.0
        );
    }
    
    /**
     * Creates a linear backoff strategy (wait time increases linearly).
     * @return A linear backoff strategy
     */
    public static BackoffStrategy linear() {
        return new BackoffStrategy(
            (attemptNumber, initialWait) -> initialWait.multipliedBy(attemptNumber + 1),
            "Linear",
            1.0
        );
    }
    
    /**
     * Creates an exponential backoff strategy.
     * @param multiplier The exponential multiplier
     * @return An exponential backoff strategy
     */
    public static BackoffStrategy exponential(final double multiplier) {
        return new BackoffStrategy(
            (attemptNumber, initialWait) -> {
                double factor = Math.pow(multiplier, attemptNumber);
                return Duration.ofMillis((long) (initialWait.toMillis() * factor));
            },
            "Exponential",
            multiplier
        );
    }
    
    /**
     * Creates a Fibonacci backoff strategy.
     * @return A Fibonacci backoff strategy
     */
    public static BackoffStrategy fibonacci() {
        return new BackoffStrategy(
            (attemptNumber, initialWait) -> {
                long fibValue = calculateFibonacci(attemptNumber + 1);
                return initialWait.multipliedBy(fibValue);
            },
            "Fibonacci",
            1.0
        );
    }
    
    /**
     * Creates a polynomial backoff strategy.
     * @param exponent The polynomial exponent
     * @return A polynomial backoff strategy
     */
    public static BackoffStrategy polynomial(final double exponent) {
        return new BackoffStrategy(
            (attemptNumber, initialWait) -> {
                double factor = Math.pow(attemptNumber + 1, exponent);
                return Duration.ofMillis((long) (initialWait.toMillis() * factor));
            },
            "Polynomial",
            exponent
        );
    }
    
    /**
     * Calculates the wait time for a given attempt.
     * @param attemptNumber The attempt number (0-based)
     * @param initialWait The initial wait time
     * @param maxWait The maximum wait time
     * @return The calculated wait time, capped at maxWait
     */
    public Duration calculateWaitTime(final int attemptNumber,
                                    final Duration initialWait,
                                    final Duration maxWait) {
        Duration calculatedWait = calculator.apply(attemptNumber, initialWait);
        
        // Ensure wait time doesn't exceed maximum
        if (calculatedWait.compareTo(maxWait) > 0) {
            return maxWait;
        }
        
        // Ensure wait time is not negative
        if (calculatedWait.isNegative()) {
            return Duration.ZERO;
        }
        
        return calculatedWait;
    }
    
    /**
     * Gets the strategy name.
     * @return The strategy name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the strategy parameter.
     * @return The strategy parameter
     */
    public double getParameter() {
        return parameter;
    }
    
    /**
     * Calculates the nth Fibonacci number.
     * @param n The position in the Fibonacci sequence
     * @return The Fibonacci number at position n
     */
    protected static long calculateFibonacci(final int n) {
        if (n <= 1) {
            return n;
        }
        
        long a = 0;
        long b = 1;
        
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        
        return b;
    }
    
    /**
     * Gets a description of this backoff strategy.
     * @return A human-readable description
     */
    public String getDescription() {
        switch (name) {
            case "Fixed":
                return "Fixed wait time for all attempts";
            case "Linear":
                return "Wait time increases linearly with attempt number";
            case "Exponential":
                return String.format("Exponential backoff with multiplier %.2f", parameter);
            case "Fibonacci":
                return "Wait time follows Fibonacci sequence";
            case "Polynomial":
                return String.format("Polynomial backoff with exponent %.2f", parameter);
            default:
                return String.format("Custom backoff strategy: %s", name);
        }
    }
    
    @Override
    public String toString() {
        return String.format("BackoffStrategy{name='%s', parameter=%.2f}", name, parameter);
    }
}