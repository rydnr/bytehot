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
 * Result pattern framework providing comprehensive operation result handling with rich metadata.
 * 
 * <p>This package provides a robust foundation for representing operation results in a consistent
 * and type-safe manner. It supports both successful and failed operations with rich error context,
 * timing information, and detailed metadata for debugging and monitoring.</p>
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link org.acmsl.commons.patterns.results.OperationResult} - Interface defining operation result contracts</li>
 *   <li>{@link org.acmsl.commons.patterns.results.SimpleOperationResult} - Comprehensive implementation with full metadata support</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Type Safety:</strong> Generic result types with compile-time safety</li>
 *   <li><strong>Rich Metadata:</strong> Comprehensive context including timing, severity, and error details</li>
 *   <li><strong>Error Handling:</strong> Structured error information with severity levels and categorization</li>
 *   <li><strong>Functional Composition:</strong> Support for mapping and chaining operations</li>
 *   <li><strong>Debugging Support:</strong> Detailed context for troubleshooting and monitoring</li>
 *   <li><strong>Async-Ready:</strong> Compatible with asynchronous and reactive programming models</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * 
 * <h3>Successful Operation</h3>
 * <pre>{@code
 * OperationResult<String> result = SimpleOperationResult.success("operation completed");
 * 
 * if (result.isSuccessful()) {
 *     String value = result.getResult().orElse("default");
 *     System.out.println("Success: " + value);
 * }
 * }</pre>
 * 
 * <h3>Failed Operation</h3>
 * <pre>{@code
 * OperationResult<String> result = SimpleOperationResult.failure(
 *     "Operation failed due to invalid input",
 *     ErrorSeverity.ERROR,
 *     new IllegalArgumentException("Invalid parameter")
 * );
 * 
 * if (result.isFailure()) {
 *     String error = result.getErrorMessageOrEmpty();
 *     System.err.println("Error: " + error);
 * }
 * }</pre>
 * 
 * <h3>Functional Composition</h3>
 * <pre>{@code
 * OperationResult<Integer> numberResult = getNumber();
 * OperationResult<String> stringResult = numberResult.map(Object::toString);
 * }</pre>
 * 
 * <h2>Integration</h2>
 * <p>This package integrates seamlessly with the error handling framework in
 * {@code org.acmsl.commons.patterns} and can be used as a foundation for building
 * robust, monitorable applications with consistent error handling patterns.</p>
 * 
 * @since 2025-06-19
 * @author Claude (Anthropic AI)
 */
package org.acmsl.commons.patterns.results;