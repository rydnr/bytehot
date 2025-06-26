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
 * Marker interfaces and generic implementations of common design patterns, including enterprise-grade
 * error handling, recovery strategies, and architectural patterns.
 * 
 * <p>This package provides foundational patterns for building robust, maintainable applications with
 * comprehensive error handling, result patterns, and architectural abstractions. Enhanced with modern
 * patterns for event sourcing, error recovery, and result handling.</p>
 * 
 * <h2>Core Pattern Categories</h2>
 * 
 * <h3>Architectural Patterns</h3>
 * <ul>
 *   <li>{@link Adapter} - Adapter pattern for infrastructure integration</li>
 *   <li>{@link Port} - Port pattern for hexagonal architecture</li>
 *   <li>{@link Singleton} - Singleton pattern marker interface</li>
 *   <li>{@link Utils} - Utility class marker interface</li>
 * </ul>
 * 
 * <h3>Domain-Driven Design</h3>
 * <ul>
 *   <li>{@link DomainEvent} - Base interface for domain events</li>
 *   <li>{@link DomainResponseEvent} - Response events in event-driven architectures</li>
 *   <li>{@link Repository} - Repository pattern for aggregate persistence</li>
 * </ul>
 * 
 * <h3>Error Handling &amp; Recovery</h3>
 * <ul>
 *   <li>{@link ErrorSeverity} - Comprehensive error severity classification</li>
 *   <li>{@link ErrorCategory} - Interface for error categorization systems</li>
 *   <li>{@link RecoveryStrategy} - Systematic error recovery approaches</li>
 * </ul>
 * 
 * <h3>Specialized Sub-packages</h3>
 * <ul>
 *   <li>{@link org.acmsl.commons.patterns.dao} - Data access object patterns and value objects</li>
 *   <li>{@link org.acmsl.commons.patterns.eventsourcing} - Event sourcing infrastructure</li>
 *   <li>{@link org.acmsl.commons.patterns.results} - Result pattern framework</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * 
 * <h3>Error Handling</h3>
 * <pre>{@code
 * if (error.getDefaultSeverity().isMoreSevereThan(ErrorSeverity.WARNING)) {
 *     RecoveryStrategy strategy = error.getRecommendedStrategy();
 *     applyRecovery(strategy);
 * }
 * }</pre>
 * 
 * <h3>Domain Events</h3>
 * <pre>{@code
 * public class UserRegistered implements DomainEvent {
 *     // Event implementation
 * }
 * }</pre>
 * 
 * <h3>Hexagonal Architecture</h3>
 * <pre>{@code
 * public interface NotificationPort extends Port {
 *     void sendNotification(String message);
 * }
 * 
 * public class EmailAdapter implements NotificationPort, Adapter {
 *     // Email implementation
 * }
 * }</pre>
 * 
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><strong>SOLID Principles:</strong> All patterns follow SOLID design principles</li>
 *   <li><strong>Immutability:</strong> Value objects and events are immutable by design</li>
 *   <li><strong>Type Safety:</strong> Generic types provide compile-time safety</li>
 *   <li><strong>Separation of Concerns:</strong> Clear boundaries between different architectural layers</li>
 *   <li><strong>Enterprise-Ready:</strong> Patterns suitable for production enterprise applications</li>
 * </ul>
 * 
 * @since 2002 (Enhanced with error handling and event sourcing patterns in 2025-06-19)
 * @author Jose San Leandro Armendariz
 * @author Claude (Anthropic AI) - Error handling and event sourcing enhancements
 */
package org.acmsl.commons.patterns;
