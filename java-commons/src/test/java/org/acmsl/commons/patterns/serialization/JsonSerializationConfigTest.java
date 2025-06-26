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
 * Filename: JsonSerializationConfigTest.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Description: TDD tests for JsonSerializationConfig
 */
package org.acmsl.commons.patterns.serialization;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.ZoneId;
import java.util.Set;
import java.util.HashSet;

/**
 * TDD tests for JsonSerializationConfig.
 * Tests define the expected behavior before implementation.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
@DisplayName("JsonSerializationConfig TDD Tests")
class JsonSerializationConfigTest {

    @Nested
    @DisplayName("Default Configuration Tests")
    class DefaultConfigurationTests {

        @Test
        @DisplayName("Should create default configuration with expected values")
        void shouldCreateDefaultConfigurationWithExpectedValues() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.defaultConfig();
            
            // Then
            assertNotNull(config);
            assertFalse(config.isPrettyPrint());
            assertFalse(config.isIncludeNulls());
            assertTrue(config.isIncludeEmpty());
            assertFalse(config.isIncludeTypeInfo());
            assertFalse(config.isFailOnUnknownProperties());
            assertFalse(config.isFailOnEmptyBeans());
            assertTrue(config.isUseJavaTimeModule());
            assertEquals(50, config.getMaxDepth());
            assertEquals(ZoneId.systemDefault(), config.getTimeZone());
        }

        @Test
        @DisplayName("Should create configuration with builder")
        void shouldCreateConfigurationWithBuilder() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .includeNulls(true)
                .build();
            
            // Then
            assertTrue(config.isPrettyPrint());
            assertTrue(config.isIncludeNulls());
        }
    }

    @Nested
    @DisplayName("Preset Configuration Tests")
    class PresetConfigurationTests {

        @Test
        @DisplayName("Should create EventSourcing configuration")
        void shouldCreateEventSourcingConfiguration() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.eventSourcingConfig();
            
            // Then
            assertTrue(config.isIncludeTypeInfo());
            assertTrue(config.isPreserveFieldOrder());
            assertFalse(config.isIncludeNulls());
            assertFalse(config.isFailOnUnknownProperties());
            assertTrue(config.isUseJavaTimeModule());
        }

        @Test
        @DisplayName("Should create pretty print configuration")
        void shouldCreatePrettyPrintConfiguration() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.prettyPrintConfig();
            
            // Then
            assertTrue(config.isPrettyPrint());
            assertFalse(config.isIncludeNulls());
        }

        @Test
        @DisplayName("Should create compact configuration")
        void shouldCreateCompactConfiguration() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.compactConfig();
            
            // Then
            assertFalse(config.isPrettyPrint());
            assertFalse(config.isIncludeNulls());
            assertFalse(config.isIncludeEmpty());
        }

        @Test
        @DisplayName("Should create strict configuration")
        void shouldCreateStrictConfiguration() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.strictConfig();
            
            // Then
            assertTrue(config.isFailOnUnknownProperties());
            assertTrue(config.isFailOnEmptyBeans());
            assertTrue(config.isIncludeTypeInfo());
        }

        @Test
        @DisplayName("Should create bug report configuration")
        void shouldCreateBugReportConfiguration() {
            // When
            JsonSerializationConfig config = JsonSerializationConfig.bugReportConfig();
            
            // Then
            assertTrue(config.isPrettyPrint());
            assertTrue(config.isIncludeTypeInfo());
            assertTrue(config.isIncludeNulls());
            assertTrue(config.isIncludeEmpty());
            assertTrue(config.isPreserveFieldOrder());
            assertFalse(config.isFailOnUnknownProperties());
            assertTrue(config.isUseJavaTimeModule());
        }
    }

    @Nested
    @DisplayName("Configuration Modification Tests")
    class ConfigurationModificationTests {

        @Test
        @DisplayName("Should create configuration with pretty print enabled")
        void shouldCreateConfigurationWithPrettyPrintEnabled() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.defaultConfig();
            
            // When
            JsonSerializationConfig modified = original.withPrettyPrint();
            
            // Then
            assertFalse(original.isPrettyPrint());
            assertTrue(modified.isPrettyPrint());
            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("Should create configuration with type info enabled")
        void shouldCreateConfigurationWithTypeInfoEnabled() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.defaultConfig();
            
            // When
            JsonSerializationConfig modified = original.withTypeInfo();
            
            // Then
            assertFalse(original.isIncludeTypeInfo());
            assertTrue(modified.isIncludeTypeInfo());
            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("Should create configuration with custom date time pattern")
        void shouldCreateConfigurationWithCustomDateTimePattern() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.defaultConfig();
            String pattern = "yyyy-MM-dd HH:mm:ss";
            
            // When
            JsonSerializationConfig modified = original.withDateTimePattern(pattern);
            
            // Then
            assertNull(original.getDateTimePattern());
            assertEquals(pattern, modified.getDateTimePattern());
            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("Should create configuration with custom timezone")
        void shouldCreateConfigurationWithCustomTimezone() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.defaultConfig();
            ZoneId utc = ZoneId.of("UTC");
            
            // When
            JsonSerializationConfig modified = original.withTimeZone(utc);
            
            // Then
            assertEquals(ZoneId.systemDefault(), original.getTimeZone());
            assertEquals(utc, modified.getTimeZone());
            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("Should create configuration with excluded properties")
        void shouldCreateConfigurationWithExcludedProperties() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.defaultConfig();
            Set<String> excludedProps = new HashSet<>();
            excludedProps.add("password");
            excludedProps.add("secret");
            
            // When
            JsonSerializationConfig modified = original.withExcludedProperties(excludedProps);
            
            // Then
            assertNull(original.getExcludedProperties());
            assertEquals(excludedProps, modified.getExcludedProperties());
            assertNotSame(original, modified);
        }

        @Test
        @DisplayName("Should create configuration with included properties")
        void shouldCreateConfigurationWithIncludedProperties() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.defaultConfig();
            Set<String> includedProps = new HashSet<>();
            includedProps.add("name");
            includedProps.add("email");
            
            // When
            JsonSerializationConfig modified = original.withIncludedProperties(includedProps);
            
            // Then
            assertNull(original.getIncludedProperties());
            assertEquals(includedProps, modified.getIncludedProperties());
            assertNotSame(original, modified);
        }
    }

    @Nested
    @DisplayName("Configuration Equality Tests")
    class ConfigurationEqualityTests {

        @Test
        @DisplayName("Should be equal for same configuration values")
        void shouldBeEqualForSameConfigurationValues() {
            // Given
            JsonSerializationConfig config1 = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .includeNulls(false)
                .build();
            
            JsonSerializationConfig config2 = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .includeNulls(false)
                .build();
            
            // When & Then
            assertEquals(config1, config2);
            assertEquals(config1.hashCode(), config2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different configuration values")
        void shouldNotBeEqualForDifferentConfigurationValues() {
            // Given
            JsonSerializationConfig config1 = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .build();
            
            JsonSerializationConfig config2 = JsonSerializationConfig.builder()
                .prettyPrint(false)
                .build();
            
            // When & Then
            assertNotEquals(config1, config2);
        }

        @Test
        @DisplayName("Should have proper toString representation")
        void shouldHaveProperToStringRepresentation() {
            // Given
            JsonSerializationConfig config = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .includeNulls(false)
                .build();
            
            // When
            String toString = config.toString();
            
            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("prettyPrint"));
            assertTrue(toString.contains("includeNulls"));
        }
    }

    @Nested
    @DisplayName("Configuration Builder Tests")
    class ConfigurationBuilderTests {

        @Test
        @DisplayName("Should create toBuilder from existing configuration")
        void shouldCreateToBuilderFromExistingConfiguration() {
            // Given
            JsonSerializationConfig original = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .includeNulls(false)
                .build();
            
            // When
            JsonSerializationConfig modified = original.toBuilder()
                .includeNulls(true)
                .build();
            
            // Then
            assertTrue(original.isPrettyPrint());
            assertFalse(original.isIncludeNulls());
            assertTrue(modified.isPrettyPrint());
            assertTrue(modified.isIncludeNulls());
        }

        @Test
        @DisplayName("Should handle all builder properties")
        void shouldHandleAllBuilderProperties() {
            // Given
            Set<String> excludedProps = new HashSet<>();
            excludedProps.add("secret");
            
            // When
            JsonSerializationConfig config = JsonSerializationConfig.builder()
                .prettyPrint(true)
                .includeNulls(true)
                .includeEmpty(false)
                .includeTypeInfo(true)
                .failOnUnknownProperties(true)
                .failOnEmptyBeans(true)
                .dateTimePattern("yyyy-MM-dd")
                .timeZone(ZoneId.of("UTC"))
                .excludedProperties(excludedProps)
                .preserveFieldOrder(true)
                .useJavaTimeModule(false)
                .maxDepth(100)
                .wrapRootValue(true)
                .build();
            
            // Then
            assertTrue(config.isPrettyPrint());
            assertTrue(config.isIncludeNulls());
            assertFalse(config.isIncludeEmpty());
            assertTrue(config.isIncludeTypeInfo());
            assertTrue(config.isFailOnUnknownProperties());
            assertTrue(config.isFailOnEmptyBeans());
            assertEquals("yyyy-MM-dd", config.getDateTimePattern());
            assertEquals(ZoneId.of("UTC"), config.getTimeZone());
            assertEquals(excludedProps, config.getExcludedProperties());
            assertTrue(config.isPreserveFieldOrder());
            assertFalse(config.isUseJavaTimeModule());
            assertEquals(100, config.getMaxDepth());
            assertTrue(config.isWrapRootValue());
        }
    }
}