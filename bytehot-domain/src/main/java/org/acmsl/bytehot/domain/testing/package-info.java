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
 * ByteHot Domain Testing - Test utilities and mocks for domain testing.
 * 
 * <p>This package provides testing utilities, mocks, and test doubles
 * specifically designed for testing the ByteHot domain layer in isolation.
 * These utilities enable comprehensive unit testing without external dependencies.</p>
 * 
 * <h2>Testing Components</h2>
 * 
 * <h3>Mock Services</h3>
 * <ul>
 *   <li>{@code MockInstrumentationService} - JVM instrumentation mock</li>
 *   <li>{@code MockFileWatcher} - File system monitoring mock</li>
 *   <li>{@code MockEventEmitter} - Event emission testing mock</li>
 * </ul>
 * 
 * <h3>Test Builders</h3>
 * <ul>
 *   <li>{@code EventTestDataBuilder} - Domain event test data</li>
 *   <li>{@code BytecodeTestBuilder} - Test bytecode generation</li>
 *   <li>{@code ClassFileTestBuilder} - Test class file utilities</li>
 * </ul>
 * 
 * <h3>Test Utilities</h3>
 * <ul>
 *   <li>{@code DomainTestSupport} - Common domain testing patterns</li>
 *   <li>{@code EventAssertions} - Domain event testing assertions</li>
 *   <li>{@code TestFixtures} - Reusable test data fixtures</li>
 * </ul>
 * 
 * <h2>Testing Patterns</h2>
 * 
 * <h3>Domain Unit Testing</h3>
 * <pre>{@code
 * @Test
 * void hotSwapManager_should_emit_success_event_on_successful_redefinition() {
 *     // Given
 *     MockInstrumentationService mockService = new MockInstrumentationService();
 *     mockService.addLoadedClass("TestClass", TestClass.class);
 *     HotSwapManager manager = new HotSwapManager(mockService);
 *     
 *     // When
 *     ClassRedefinitionSucceeded result = manager.performRedefinition(request);
 *     
 *     // Then
 *     assertThat(result.getClassName()).isEqualTo("TestClass");
 *     assertThat(result.getAffectedInstances()).isGreaterThanOrEqualTo(0);
 * }
 * }</pre>
 * 
 * <h3>Event-Driven Testing</h3>
 * <pre>{@code
 * @Test
 * void bytecode_validation_should_emit_rejection_event_for_incompatible_changes() {
 *     // Given
 *     BytecodeValidator validator = new BytecodeValidator();
 *     Path classFile = createIncompatibleClassFile();
 *     
 *     // When & Then
 *     assertThatThrownBy(() -> validator.validate(classFile))
 *         .isInstanceOf(BytecodeValidationException.class)
 *         .extracting(e -> ((BytecodeValidationException) e).getRejectionEvent())
 *         .satisfies(event -> {
 *             assertThat(event.getRejectionReason()).contains("incompatible");
 *             assertThat(event.getClassName()).isEqualTo("TestClass");
 *         });
 * }
 * }</pre>
 * 
 * <h2>Isolation Principles</h2>
 * <p>This testing package ensures domain tests are:</p>
 * <ul>
 *   <li><strong>Fast</strong> - No I/O or external dependencies</li>
 *   <li><strong>Isolated</strong> - Each test is independent</li>
 *   <li><strong>Repeatable</strong> - Deterministic results</li>
 *   <li><strong>Focused</strong> - Test one domain concept at a time</li>
 * </ul>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.domain.testing;