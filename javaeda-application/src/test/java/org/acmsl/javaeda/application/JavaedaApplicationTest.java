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
 * Filename: JavaedaApplicationTest.java
 *
 * Author: JavaEDA Framework Generator
 *
 * Class name: JavaedaApplicationTest
 *
 * Responsibilities:
 *   - Test application layer functionality
 *   - Validate use case orchestration
 *   - Test framework status reporting
 */
package org.acmsl.javaeda.application;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for JavaedaApplication application layer.
 * Follows TDD principles with comprehensive coverage.
 * @author JavaEDA Framework Generator
 * @since 1.0.0
 */
class JavaedaApplicationTest {

    @Test
    void shouldGetLayerName() {
        // Given & When
        final String layerName = JavaedaApplication.getLayerName();

        // Then
        assertThat(layerName).isEqualTo("Application");
    }

    @Test
    void shouldGetLayerIdentifier() {
        // Given & When
        final String identifier = JavaedaApplication.getLayerIdentifier();

        // Then
        assertThat(identifier).isEqualTo("JavaEDA Framework Application Layer v1.0.0");
    }

    @Test
    void shouldReportApplicationReady() {
        // Given & When
        final boolean ready = JavaedaApplication.isApplicationReady();

        // Then
        assertThat(ready).isTrue();
    }

    @Test
    void shouldGetApplicationStatus() {
        // Given & When
        final String status = JavaedaApplication.getStatus();

        // Then
        assertThat(status)
            .contains("JavaEDA Framework Application Layer v1.0.0")
            .contains("Ready");
    }

    @Test
    void shouldGetFrameworkStatus() {
        // Given & When
        final String frameworkStatus = JavaedaApplication.getFrameworkStatus();

        // Then
        assertThat(frameworkStatus)
            .contains("JavaEDA Framework Status:")
            .contains("Domain: Ready")
            .contains("Infrastructure: Ready")
            .contains("Application: Ready");
    }

    @Test
    void shouldValidateUseCaseConfigWithValidInputs() {
        // Given
        final String useCaseName = "TestUseCase";
        final Object config = new Object();

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            JavaedaApplication.validateUseCaseConfig(useCaseName, config));
    }

    @Test
    void shouldRejectEmptyUseCaseName() {
        // Given
        final String emptyName = "";
        final Object config = new Object();

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaApplication.validateUseCaseConfig(emptyName, config))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("useCaseName cannot be null or empty");
    }

    @Test
    void shouldRejectNullUseCaseName() {
        // Given
        final String nullName = null;
        final Object config = new Object();

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaApplication.validateUseCaseConfig(nullName, config))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("useCaseName cannot be null or empty");
    }

    @Test
    void shouldRejectNullUseCaseConfig() {
        // Given
        final String useCaseName = "TestUseCase";
        final Object nullConfig = null;

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaApplication.validateUseCaseConfig(useCaseName, nullConfig))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("useCaseConfig cannot be null");
    }

    @Test
    void shouldValidateUseCaseConfigWithWhitespaceOnlyName() {
        // Given
        final String whitespaceName = "   ";
        final Object config = new Object();

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaApplication.validateUseCaseConfig(whitespaceName, config))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("useCaseName cannot be null or empty");
    }

    @Test
    void shouldHandleComplexUseCaseConfig() {
        // Given
        final String useCaseName = "ComplexUseCase";
        final UseCaseConfig complexConfig = new UseCaseConfig();

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            JavaedaApplication.validateUseCaseConfig(useCaseName, complexConfig));
    }

    @Test
    void shouldProvideConsistentLayerInformation() {
        // Given & When
        final String layerName = JavaedaApplication.getLayerName();
        final String layerIdentifier = JavaedaApplication.getLayerIdentifier();

        // Then
        assertThat(layerIdentifier).contains(layerName);
        assertThat(layerIdentifier).contains("JavaEDA Framework");
        assertThat(layerIdentifier).contains("v1.0.0");
    }

    // Test helper class
    private static class UseCaseConfig {
        private String name = "test";
        private boolean enabled = true;

        public String getName() { return name; }
        public boolean isEnabled() { return enabled; }
    }
}