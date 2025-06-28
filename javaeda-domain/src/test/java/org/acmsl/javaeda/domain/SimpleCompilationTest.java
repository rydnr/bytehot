/*
                        JavaEDA Framework

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
 * Filename: SimpleCompilationTest.java
 *
 * Author: JavaEDA Framework Generator
 *
 * Class name: SimpleCompilationTest
 *
 * Responsibilities:
 *   - Test basic compilation of JavaEDA framework
 *   - Validate core classes can be instantiated
 */
package org.acmsl.javaeda.domain;


import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Simple compilation test for JavaEDA framework.
 * @author JavaEDA Framework Generator
 * @since 1.0.0
 */
class SimpleCompilationTest {

    @Test
    void shouldGetFrameworkVersion() {
        // Given & When
        final String version = JavaedaFoundation.getVersion();

        // Then
        assertThat(version).isEqualTo("1.0.0");
    }

    @Test
    void shouldGetFrameworkName() {
        // Given & When
        final String name = JavaedaFoundation.getName();

        // Then
        assertThat(name).isEqualTo("JavaEDA Framework");
    }

    @Test
    void shouldGetFrameworkIdentifier() {
        // Given & When
        final String identifier = JavaedaFoundation.getIdentifier();

        // Then
        assertThat(identifier).isEqualTo("JavaEDA Framework v1.0.0");
    }

    @Test
    void shouldValidateNonEmptyStrings() {
        // Given
        final String validString = "test";
        final String emptyString = "";

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            JavaedaFoundation.requireNonEmpty(validString, "testField"));
        
        assertThatThrownBy(() -> 
            JavaedaFoundation.requireNonEmpty(emptyString, "testField"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("testField cannot be null or empty");
    }

    @Test
    void shouldValidateNonNullValues() {
        // Given
        final String validValue = "test";
        final String nullValue = null;

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            JavaedaFoundation.requireNonNull(validValue, "testField"));
        
        assertThatThrownBy(() -> 
            JavaedaFoundation.requireNonNull(nullValue, "testField"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("testField cannot be null");
    }

    @Test
    void shouldReportInitialized() {
        // Given & When
        final boolean initialized = JavaedaFoundation.isInitialized();

        // Then
        assertThat(initialized).isTrue();
    }
}