/*
                        ByteHot

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
 * Filename: ECommerceConfiguration.java
 *
 * Author: Claude Code
 *
 * Class name: ECommerceConfiguration
 *
 * Responsibilities:
 *   - Configure hexagonal architecture beans for Spring Boot
 *   - Wire domain, application, and infrastructure layers
 *   - Enable hot-swappable configuration changes
 *
 * Collaborators:
 *   - ECommerceApplication: Application layer bean
 *   - ECommerceEventRouter: Event routing bean
 *   - Spring Boot: Configuration framework
 */
package org.acmsl.bytehot.examples.ecommerce.infrastructure.config;

import org.acmsl.bytehot.examples.ecommerce.application.ECommerceApplication;
import org.acmsl.bytehot.examples.ecommerce.application.ECommerceEventRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring configuration for the e-commerce hexagonal architecture.
 * @author Claude Code
 * @since 2025-07-04
 */
@Configuration
public class ECommerceConfiguration {

    /**
     * Creates the event router bean.
     * This method can be hot-swapped to change event routing configuration.
     * @return ECommerceEventRouter instance
     */
    @Bean
    @Primary
    public ECommerceEventRouter ecommerceEventRouter() {
        return new ECommerceEventRouter();
    }

    /**
     * Creates the main application bean.
     * This method can be hot-swapped to change application configuration.
     * @param eventRouter The event router dependency
     * @return ECommerceApplication instance
     */
    @Bean
    @Primary
    public ECommerceApplication ecommerceApplication(final ECommerceEventRouter eventRouter) {
        return new ECommerceApplication(eventRouter);
    }
}