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
 * Data Access Object (DAO) patterns and value object framework with enhanced identifier support.
 * 
 * <p>This package provides foundational components for building robust domain models with
 * type-safe identifiers and value objects. It includes a comprehensive framework for creating
 * and managing entity identifiers with validation, factory methods, and consistent semantics.</p>
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link ValueObject} - Marker interface for value objects</li>
 *   <li>{@link AbstractId} - Generic base class for type-safe identifiers</li>
 *   <li>{@link DAO} - Interface for data access objects</li>
 *   <li>{@link ValueObjectDAO} - Specialized DAO for value objects</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Type-Safe Identifiers:</strong> Generic ID framework preventing ID confusion between different entity types</li>
 *   <li><strong>Factory Methods:</strong> Convenient creation methods for random IDs, validation, and prefix support</li>
 *   <li><strong>Validation:</strong> Built-in validation with customizable rules for identifier formats</li>
 *   <li><strong>Immutability:</strong> All identifiers are immutable value objects</li>
 *   <li><strong>Consistency:</strong> Standardized ID patterns across all domain entities</li>
 *   <li><strong>DAO Pattern Support:</strong> Traditional data access object patterns with type safety</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * 
 * <h3>Creating Custom ID Types</h3>
 * <pre>{@code
 * public final class UserId extends AbstractId<UserId> {
 *     private UserId(String value) {
 *         super(value);
 *     }
 *     
 *     public static UserId random() {
 *         return AbstractId.random(UserId::new);
 *     }
 *     
 *     public static UserId of(String value) {
 *         return AbstractId.of(value, UserId::new);
 *     }
 * }
 * }</pre>
 * 
 * <h3>Using ID Types</h3>
 * <pre>{@code
 * // Create random ID
 * UserId userId = UserId.random();
 * 
 * // Create from existing value
 * UserId specificId = UserId.of("user-12345");
 * 
 * // Create with prefix
 * UserId prefixedId = AbstractId.withPrefix("admin", UserId::new);
 * }</pre>
 * 
 * @since 2002 (Enhanced with AbstractId framework in 2025-06-19)
 * @author Jose San Leandro Armendariz
 * @author Claude (Anthropic AI) - AbstractId framework
 */
package org.acmsl.commons.patterns.dao;
