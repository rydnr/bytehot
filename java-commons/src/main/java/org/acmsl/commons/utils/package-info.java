/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com
 */

/**
 * Comprehensive utility classes providing common functionality for string manipulation, reflection, 
 * I/O operations, temporal analysis, and system interactions.
 * 
 * <p>This package contains a rich collection of utility classes that support various aspects of application
 * development, from basic string operations to complex temporal analysis and system introspection.
 * All utilities are designed to be stateless and thread-safe where applicable.</p>
 * 
 * <h2>Core Utility Categories</h2>
 * 
 * <h3>String and Text Processing</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.StringUtils} - Comprehensive string manipulation utilities</li>
 *   <li>{@link org.acmsl.commons.utils.StringValidator} - String validation and pattern matching</li>
 *   <li>{@link org.acmsl.commons.utils.CharUtils} - Character manipulation and analysis</li>
 *   <li>{@link org.acmsl.commons.utils.EnglishGrammarUtils} - English language grammar utilities</li>
 *   <li>{@link org.acmsl.commons.utils.GrammarUtils} - General grammar and linguistic utilities</li>
 * </ul>
 * 
 * <h3>Reflection and Class Manipulation</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.ReflectionUtils} - Java reflection utilities and helpers</li>
 *   <li>{@link org.acmsl.commons.utils.ClassLoaderUtils} - Class loading and management utilities</li>
 *   <li>{@link org.acmsl.commons.utils.EnumUtils} - Enumeration manipulation and analysis</li>
 * </ul>
 * 
 * <h3>Data Processing and Conversion</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.ConversionUtils} - Type conversion and data transformation</li>
 *   <li>{@link org.acmsl.commons.utils.NumericUtils} - Numeric operations and calculations</li>
 *   <li>{@link org.acmsl.commons.utils.EqualityComparator} - Object equality and comparison utilities</li>
 *   <li>{@link org.acmsl.commons.utils.ToStringUtils} - Object string representation utilities</li>
 * </ul>
 * 
 * <h3>Time and Performance</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.TimeWindow} - Temporal analysis and time range operations</li>
 *   <li>{@link org.acmsl.commons.utils.Chronometer} - Performance measurement and timing utilities</li>
 * </ul>
 * 
 * <h3>Exception and Error Handling</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.ThrowableUtils} - Exception analysis and error handling utilities</li>
 * </ul>
 * 
 * <h3>XML and Data Processing</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.SaxUtils} - SAX XML processing utilities</li>
 * </ul>
 * 
 * <h3>Specialized Sub-packages</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.utils.http} - HTTP servlet and web utilities</li>
 *   <li>{@link org.acmsl.commons.utils.io} - File I/O and stream processing utilities</li>
 *   <li>{@link org.acmsl.commons.utils.jmx} - JMX management and monitoring utilities</li>
 *   <li>{@link org.acmsl.commons.utils.net} - Network and URL manipulation utilities</li>
 *   <li>{@link org.acmsl.commons.utils.regexp} - Regular expression utilities and patterns</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * 
 * <h3>String Processing</h3>
 * <pre>{@code
 * // String manipulation
 * String processed = StringUtils.capitalize("hello world");
 * boolean isEmpty = StringUtils.isEmpty(value);
 * 
 * // String validation
 * boolean isEmail = StringValidator.getInstance().isValidEmail(email);
 * }</pre>
 * 
 * <h3>Time Window Operations</h3>
 * <pre>{@code
 * Instant start = Instant.now();
 * Instant end = start.plus(Duration.ofHours(2));
 * 
 * TimeWindow window = TimeWindow.builder()
 *     .startTime(start)
 *     .endTime(end)
 *     .build();
 * 
 * Duration duration = window.getDuration();
 * boolean overlaps = window.overlapsWith(otherWindow);
 * }</pre>
 * 
 * <h3>Reflection Operations</h3>
 * <pre>{@code
 * // Reflection utilities
 * Method method = ReflectionUtils.findMethod(clazz, methodName);
 * Object result = ReflectionUtils.invokeMethod(method, instance, args);
 * }</pre>
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><strong>Thread Safety:</strong> All utilities designed for concurrent use</li>
 *   <li><strong>Null Safety:</strong> Comprehensive null checking and validation</li>
 *   <li><strong>Performance:</strong> Optimized implementations for common operations</li>
 *   <li><strong>Consistency:</strong> Uniform API patterns across all utility classes</li>
 *   <li><strong>Immutability:</strong> Value objects and stateless utilities where appropriate</li>
 *   <li><strong>Robustness:</strong> Comprehensive error handling and validation</li>
 * </ul>
 * 
 * @since 2002 (Enhanced with TimeWindow and comprehensive documentation in 2025-06-19)
 * @author Jose San Leandro Armendariz
 * @author Claude (Anthropic AI) - TimeWindow framework and documentation enhancements
 */
package org.acmsl.commons.utils;
