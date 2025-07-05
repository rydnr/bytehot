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
 * Filename: SpringBootECommerceApplication.java
 *
 * Author: Claude Code
 *
 * Class name: SpringBootECommerceApplication
 *
 * Responsibilities:
 *   - Bootstrap Spring Boot application with ByteHot integration
 *   - Configure hexagonal architecture beans
 *   - Enable hot-swapping capabilities for e-commerce system
 *
 * Collaborators:
 *   - ECommerceApplication: Core application layer
 *   - ECommerceEventRouter: Event routing logic
 *   - Spring Boot: Infrastructure framework
 */
package org.acmsl.bytehot.examples.ecommerce.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot main application class for the e-commerce system.
 * Demonstrates ByteHot integration with Spring Boot and hexagonal architecture.
 * @author Claude Code
 * @since 2025-07-04
 */
@SpringBootApplication(scanBasePackages = {
    "org.acmsl.bytehot.examples.ecommerce.infrastructure",
    "org.acmsl.bytehot.examples.ecommerce.application"
})
@EnableJpaRepositories(basePackages = "org.acmsl.bytehot.examples.ecommerce.infrastructure.jpa")
@EntityScan(basePackages = "org.acmsl.bytehot.examples.ecommerce.infrastructure.jpa.entity")
@EnableCaching
public class SpringBootECommerceApplication {

    /**
     * Main entry point for the Spring Boot application.
     * @param args Command-line arguments
     */
    public static void main(final String[] args) {
        System.out.println("Starting ByteHot E-Commerce System with Spring Boot");
        SpringApplication.run(SpringBootECommerceApplication.class, args);
    }
}