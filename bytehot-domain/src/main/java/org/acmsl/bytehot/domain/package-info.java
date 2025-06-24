/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/**
 * ByteHot Domain Layer - Core business logic and domain models.
 * 
 * <p>This package contains the pure domain logic for ByteHot, a JVM agent
 * that enables bytecode hot-swapping at runtime. The domain layer follows
 * Domain-Driven Design (DDD) principles and is technology-agnostic.</p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li><strong>Aggregates</strong> - Core business entities like {@code ByteHot}, {@code HotSwapManager}</li>
 *   <li><strong>Value Objects</strong> - Immutable objects like {@code UserId}, {@code EventMetadata}</li>
 *   <li><strong>Domain Services</strong> - Business logic that doesn't belong to a specific entity</li>
 *   <li><strong>Ports</strong> - Interfaces for external dependencies (hexagonal architecture)</li>
 *   <li><strong>Domain Events</strong> - Events that occur within the domain</li>
 * </ul>
 * 
 * <h2>Architecture</h2>
 * <p>This domain layer is designed to be:</p>
 * <ul>
 *   <li><strong>Pure</strong> - No external dependencies beyond java-commons</li>
 *   <li><strong>Testable</strong> - All business logic can be unit tested</li>
 *   <li><strong>Event-driven</strong> - Uses domain events for loose coupling</li>
 *   <li><strong>Hexagonal</strong> - Depends on abstractions, not implementations</li>
 * </ul>
 * 
 * <h2>Hot-Swap Process</h2>
 * <p>The domain orchestrates the following hot-swap workflow:</p>
 * <ol>
 *   <li>File change detection triggers {@code ClassFileChanged} event</li>
 *   <li>Bytecode validation ensures compatibility</li>
 *   <li>Hot-swap request is created and validated</li>
 *   <li>JVM class redefinition is performed</li>
 *   <li>Success/failure events are emitted</li>
 * </ol>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.domain;