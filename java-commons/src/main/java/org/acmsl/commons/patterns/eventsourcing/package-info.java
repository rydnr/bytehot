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
 * Event Sourcing infrastructure providing comprehensive support for event-driven architectures.
 * 
 * <p>This package contains the foundational components for implementing Event Sourcing patterns
 * across any domain. The framework provides rich metadata support, causality tracking, and
 * versioning capabilities essential for robust event-sourced systems.</p>
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent} - Interface for domain events with comprehensive metadata</li>
 *   <li>{@link org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent} - Base implementation for versioned domain events</li>
 *   <li>{@link org.acmsl.commons.patterns.eventsourcing.EventMetadata} - Rich metadata container for event sourcing</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Versioning:</strong> Complete support for aggregate versioning and optimistic concurrency control</li>
 *   <li><strong>Causality Tracking:</strong> Event causality chains with previous event references</li>
 *   <li><strong>Correlation Support:</strong> Cross-aggregate event correlation for distributed tracing</li>
 *   <li><strong>Schema Evolution:</strong> Built-in versioning for event schema migration</li>
 *   <li><strong>Audit Trail:</strong> Comprehensive audit capabilities with user and timestamp tracking</li>
 *   <li><strong>Stream Positioning:</strong> Event store positioning for efficient retrieval and ordering</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Creating event metadata for a new aggregate
 * EventMetadata metadata = EventMetadata.forNewAggregate("user", "user-123");
 * 
 * // Creating a domain event
 * public class UserRegistered extends AbstractVersionedDomainEvent {
 *     public UserRegistered(EventMetadata metadata, String email) {
 *         super(metadata);
 *         this.email = email;
 *     }
 * }
 * 
 * // Using the event
 * UserRegistered event = new UserRegistered(metadata, "user@example.com");
 * }</pre>
 * 
 * <h2>Integration</h2>
 * <p>This package is designed to integrate with any event store implementation and provides
 * the foundation for building sophisticated event-sourced systems. The interfaces and base
 * classes are technology-agnostic and can be used with various persistence mechanisms.</p>
 * 
 * @since 2025-06-19
 * @author Claude (Anthropic AI)
 */
package org.acmsl.commons.patterns.eventsourcing;