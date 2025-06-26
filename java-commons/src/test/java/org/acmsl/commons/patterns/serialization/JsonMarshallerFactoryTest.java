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

 ******************************************************************************
 *
 * Filename: JsonMarshallerFactoryTest.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Description: TDD tests for JsonMarshallerFactory
 */
package org.acmsl.commons.patterns.serialization;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * TDD tests for JsonMarshallerFactory.
 * Tests define the expected behavior before implementation.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
@DisplayName("JsonMarshallerFactory TDD Tests")
class JsonMarshallerFactoryTest {

    @Nested
    @DisplayName("Factory Creation Tests")
    class FactoryCreationTests {

        @Test
        @DisplayName("Should create default marshaller")
        void shouldCreateDefaultMarshaller() {
            // When
            JsonMarshaller marshaller = JsonMarshallerFactory.createDefault();
            
            // Then
            assertNotNull(marshaller);
            assertNotNull(marshaller.getConfiguration());
        }

        @Test
        @DisplayName("Should create marshaller with custom configuration")
        void shouldCreateMarshallerWithCustomConfiguration() {
            // Given
            JsonSerializationConfig config = JsonSerializationConfig.prettyPrintConfig();
            
            // When
            JsonMarshaller marshaller = JsonMarshallerFactory.create(config);
            
            // Then
            assertNotNull(marshaller);
            assertEquals(config, marshaller.getConfiguration());
        }

        @Test
        @DisplayName("Should create EventSourcing optimized marshaller")
        void shouldCreateEventSourcingOptimizedMarshaller() {
            // When
            JsonMarshaller marshaller = JsonMarshallerFactory.createForEventSourcing();
            
            // Then
            assertNotNull(marshaller);
            JsonSerializationConfig config = marshaller.getConfiguration();
            assertTrue(config.isIncludeTypeInfo());
            assertTrue(config.isPreserveFieldOrder());
        }

        @Test
        @DisplayName("Should create bug report optimized marshaller")
        void shouldCreateBugReportOptimizedMarshaller() {
            // When
            JsonMarshaller marshaller = JsonMarshallerFactory.createForBugReports();
            
            // Then
            assertNotNull(marshaller);
            JsonSerializationConfig config = marshaller.getConfiguration();
            assertTrue(config.isPrettyPrint());
            assertTrue(config.isIncludeTypeInfo());
            assertTrue(config.isIncludeNulls());
        }

        @Test
        @DisplayName("Should create compact marshaller")
        void shouldCreateCompactMarshaller() {
            // When
            JsonMarshaller marshaller = JsonMarshallerFactory.createCompact();
            
            // Then
            assertNotNull(marshaller);
            JsonSerializationConfig config = marshaller.getConfiguration();
            assertFalse(config.isPrettyPrint());
            assertFalse(config.isIncludeNulls());
            assertFalse(config.isIncludeEmpty());
        }

        @Test
        @DisplayName("Should create different instances for each call")
        void shouldCreateDifferentInstancesForEachCall() {
            // When
            JsonMarshaller marshaller1 = JsonMarshallerFactory.createDefault();
            JsonMarshaller marshaller2 = JsonMarshallerFactory.createDefault();
            
            // Then
            assertNotSame(marshaller1, marshaller2);
        }
    }

    @Nested
    @DisplayName("Factory Configuration Tests")
    class FactoryConfigurationTests {

        @Test
        @DisplayName("Should handle null configuration gracefully")
        void shouldHandleNullConfigurationGracefully() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                JsonMarshallerFactory.create(null);
            });
        }

        @Test
        @DisplayName("Should preserve configuration immutability")
        void shouldPreserveConfigurationImmutability() {
            // Given
            JsonSerializationConfig originalConfig = JsonSerializationConfig.defaultConfig();
            
            // When
            JsonMarshaller marshaller = JsonMarshallerFactory.create(originalConfig);
            JsonSerializationConfig marshallerConfig = marshaller.getConfiguration();
            
            // Then
            assertEquals(originalConfig, marshallerConfig);
            // Configurations should be equal but potentially different instances
            // to ensure immutability
        }
    }
}