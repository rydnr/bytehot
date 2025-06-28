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
 * Filename: JavaedaInfrastructureTest.java
 *
 * Author: JavaEDA Framework Generator
 *
 * Class name: JavaedaInfrastructureTest
 *
 * Responsibilities:
 *   - Test infrastructure layer functionality
 *   - Validate adapter management
 *   - Test external system integration status
 */
package org.acmsl.javaeda.infrastructure;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for JavaedaInfrastructure infrastructure layer.
 * Follows TDD principles with comprehensive coverage.
 * @author JavaEDA Framework Generator
 * @since 1.0.0
 */
class JavaedaInfrastructureTest {

    @Test
    void shouldGetLayerName() {
        // Given & When
        final String layerName = JavaedaInfrastructure.getLayerName();

        // Then
        assertThat(layerName).isEqualTo("Infrastructure");
    }

    @Test
    void shouldGetLayerIdentifier() {
        // Given & When
        final String identifier = JavaedaInfrastructure.getLayerIdentifier();

        // Then
        assertThat(identifier).isEqualTo("JavaEDA Framework Infrastructure Layer v1.0.0");
    }

    @Test
    void shouldReportInfrastructureReady() {
        // Given & When
        final boolean ready = JavaedaInfrastructure.isInfrastructureReady();

        // Then
        assertThat(ready).isTrue();
    }

    @Test
    void shouldGetInfrastructureStatus() {
        // Given & When
        final String status = JavaedaInfrastructure.getStatus();

        // Then
        assertThat(status)
            .contains("JavaEDA Framework Infrastructure Layer v1.0.0")
            .contains("Ready");
    }

    @Test
    void shouldValidateAdapterConfigWithValidInputs() {
        // Given
        final String adapterName = "TestAdapter";
        final Object config = new Object();

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            JavaedaInfrastructure.validateAdapterConfig(adapterName, config));
    }

    @Test
    void shouldRejectEmptyAdapterName() {
        // Given
        final String emptyName = "";
        final Object config = new Object();

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaInfrastructure.validateAdapterConfig(emptyName, config))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("adapterName cannot be null or empty");
    }

    @Test
    void shouldRejectNullAdapterName() {
        // Given
        final String nullName = null;
        final Object config = new Object();

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaInfrastructure.validateAdapterConfig(nullName, config))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("adapterName cannot be null or empty");
    }

    @Test
    void shouldRejectNullAdapterConfig() {
        // Given
        final String adapterName = "TestAdapter";
        final Object nullConfig = null;

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaInfrastructure.validateAdapterConfig(adapterName, nullConfig))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("adapterConfig cannot be null");
    }

    @Test
    void shouldValidateAdapterConfigWithWhitespaceOnlyName() {
        // Given
        final String whitespaceName = "   ";
        final Object config = new Object();

        // When & Then
        assertThatThrownBy(() -> 
            JavaedaInfrastructure.validateAdapterConfig(whitespaceName, config))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("adapterName cannot be null or empty");
    }

    @Test
    void shouldHandleComplexAdapterConfig() {
        // Given
        final String adapterName = "DatabaseAdapter";
        final AdapterConfig complexConfig = new AdapterConfig();

        // When & Then
        assertThatNoException().isThrownBy(() -> 
            JavaedaInfrastructure.validateAdapterConfig(adapterName, complexConfig));
    }

    @Test
    void shouldProvideConsistentLayerInformation() {
        // Given & When
        final String layerName = JavaedaInfrastructure.getLayerName();
        final String layerIdentifier = JavaedaInfrastructure.getLayerIdentifier();

        // Then
        assertThat(layerIdentifier).contains(layerName);
        assertThat(layerIdentifier).contains("JavaEDA Framework");
        assertThat(layerIdentifier).contains("v1.0.0");
    }

    @Test
    void shouldDependOnDomainLayer() {
        // Given & When
        final boolean infrastructureReady = JavaedaInfrastructure.isInfrastructureReady();
        final String status = JavaedaInfrastructure.getStatus();

        // Then - Infrastructure layer should be ready when domain is ready
        assertThat(infrastructureReady).isTrue();
        assertThat(status).contains("Ready");
    }

    @Test
    void shouldProvideInfrastructureServices() {
        // Given & When - Infrastructure should provide basic services
        final String layerName = JavaedaInfrastructure.getLayerName();
        final boolean ready = JavaedaInfrastructure.isInfrastructureReady();

        // Then
        assertThat(layerName).isNotEmpty();
        assertThat(ready).isTrue();
    }

    // Test helper class
    private static class AdapterConfig {
        private String connectionString = "test://localhost";
        private int timeout = 5000;
        private boolean enabled = true;

        public String getConnectionString() { return connectionString; }
        public int getTimeout() { return timeout; }
        public boolean isEnabled() { return enabled; }
    }
}