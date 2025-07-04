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
 * Filename: ECommerceApplication.java
 *
 * Author: Claude Code
 *
 * Class name: ECommerceApplication
 *
 * Responsibilities:
 *   - Main Spring Boot application class for e-commerce microservice
 *   - Configure ByteHot integration for hot-swapping capabilities
 *   - Bootstrap application with production-ready configuration
 *
 * Collaborators:
 *   - Spring Boot: Main application framework
 *   - ByteHot: Hot-swapping agent integration
 */
package org.acmsl.bytehot.examples.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

/**
 * Main Spring Boot application for the E-Commerce microservice example.
 * Demonstrates ByteHot hot-swapping capabilities in a realistic business application.
 * @author Claude Code
 * @since 2025-07-04
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
public class ECommerceApplication {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ECommerceApplication.class.getName());

    /**
     * Main entry point for the application.
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting ByteHot E-Commerce Microservice Example...");
        SpringApplication.run(ECommerceApplication.class, args);
    }

    /**
     * Initialize application after Spring context is loaded.
     */
    @PostConstruct
    public void initialize() {
        LOGGER.info("E-Commerce application initialized successfully");
        LOGGER.info("ByteHot hot-swapping is enabled for this application");
        LOGGER.info("You can modify business logic and see changes immediately without restart");
    }

    /**
     * Configure application-specific beans.
     * @return Application configuration
     */
    @Bean
    public ApplicationConfiguration applicationConfiguration() {
        return new ApplicationConfiguration();
    }

    /**
     * Application configuration class.
     */
    public static class ApplicationConfiguration {
        
        /**
         * Gets the application name.
         * @return The application name
         */
        public String getApplicationName() {
            return "ByteHot E-Commerce Microservice";
        }

        /**
         * Gets the application version.
         * @return The application version
         */
        public String getVersion() {
            return "1.0.0-SNAPSHOT";
        }

        /**
         * Checks if ByteHot integration is enabled.
         * @return true if ByteHot is enabled, false otherwise
         */
        public boolean isByteHotEnabled() {
            return true;
        }
    }
}