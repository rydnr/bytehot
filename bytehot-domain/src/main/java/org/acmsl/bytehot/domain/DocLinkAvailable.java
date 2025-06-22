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
 * Filename: DocLinkAvailable.java
 *
 * Author: Claude Code
 *
 * Class name: DocLinkAvailable
 *
 * Responsibilities:
 *   - Provide minimal code pollution documentation access interface
 *   - Enable Flow-aware contextual documentation for all domain components
 *   - Support runtime self-documentation capabilities
 *
 * Collaborators:
 *   - DocProvider: Centralized documentation URL generation and Flow detection
 *   - Flow: Runtime operational context for contextual documentation
 *   - Ports: Infrastructure access for DocProvider resolution
 */
package org.acmsl.bytehot.domain;

import java.util.Optional;

/**
 * Revolutionary documentation access interface that enables any ByteHot component
 * to provide contextual, Flow-aware documentation links with minimal code pollution.
 * 
 * This interface transforms ByteHot into a self-documenting system where every
 * component can provide intelligent documentation access based on runtime context.
 * 
 * Key Features:
 * - Zero implementation burden (all methods have intelligent defaults)
 * - Flow-aware contextual documentation 
 * - Runtime operational context detection
 * - Minimal performance overhead
 * - Clean separation of concerns through delegation
 * 
 * @author Claude Code
 * @since 2025-06-22
 */
public interface DocLinkAvailable {

    /**
     * Gets the basic documentation URL for this component.
     * 
     * This method provides static documentation access without runtime context.
     * For contextual, Flow-aware documentation, use {@link #getRuntimeDocLink()}.
     * 
     * @return URL to the component's documentation, or empty if not available
     */
    default Optional<String> getDocUrl() {
        try {
            final DocProvider docProvider = new DocProvider();
            return docProvider.getDocumentationUrl(this.getClass());
        } catch (final Exception e) {
            // Graceful degradation - documentation access should never break functionality
            return Optional.empty();
        }
    }

    /**
     * Gets the documentation URL for a specific method of this component.
     * 
     * Provides targeted documentation for method-level help and API reference.
     * 
     * @param methodName the name of the method to document
     * @return URL to the method's documentation, or empty if not available
     */
    default Optional<String> getMethodDocUrl(final String methodName) {
        try {
            final DocProvider docProvider = new DocProvider();
            return docProvider.getMethodDocumentationUrl(this.getClass(), methodName);
        } catch (final Exception e) {
            // Graceful degradation - documentation access should never break functionality
            return Optional.empty();
        }
    }

    /**
     * Revolutionary Flow-aware contextual documentation access.
     * 
     * This method analyzes the current runtime context to detect operational Flows
     * and provides documentation that is specifically relevant to what the system
     * is currently doing. Examples:
     * 
     * - During configuration loading: Returns configuration management documentation
     * - During file change detection: Returns file monitoring documentation
     * - During hot-swap operations: Returns class redefinition documentation
     * - During agent startup: Returns initialization and setup documentation
     * 
     * The Flow detection engine uses multiple sources:
     * - Call stack analysis for execution context
     * - Recent domain event patterns
     * - Current configuration state
     * - File system operation activity
     * 
     * @return contextual documentation URL based on detected operational Flow,
     *         or falls back to basic documentation if no Flow is detected
     */
    default Optional<String> getRuntimeDocLink() {
        try {
            final DocProvider docProvider = new DocProvider();
            return docProvider.getContextualDocumentationUrl(this.getClass());
        } catch (final Exception e) {
            // Graceful fallback to basic documentation
            return getDocUrl();
        }
    }

    /**
     * Gets documentation link with explicit Flow context.
     * 
     * This method allows components to provide specific Flow context for
     * documentation generation, useful when the automatic detection needs
     * to be overridden or supplemented.
     * 
     * @param flowContext the specific operational Flow context
     * @return documentation URL tailored for the provided Flow context
     */
    default Optional<String> getDocLinkForFlow(final Flow flowContext) {
        try {
            final DocProvider docProvider = new DocProvider();
            return docProvider.getFlowDocumentationUrl(this.getClass(), flowContext);
        } catch (final Exception e) {
            // Graceful fallback to basic documentation
            return getDocUrl();
        }
    }

    /**
     * Provides enhanced documentation access for manual testing scenarios.
     * 
     * During manual testing, this method returns documentation URLs that include
     * step-by-step testing procedures, expected behaviors, and troubleshooting
     * information specific to the current component and operational context.
     * 
     * @return enhanced documentation URL for manual testing scenarios
     */
    default Optional<String> getTestingDocLink() {
        try {
            final DocProvider docProvider = new DocProvider();
            return docProvider.getTestingDocumentationUrl(this.getClass());
        } catch (final Exception e) {
            // Graceful fallback to basic documentation
            return getDocUrl();
        }
    }

    /**
     * Checks if contextual documentation is available for the current state.
     * 
     * This method allows components to determine if enhanced, Flow-aware
     * documentation is available before attempting to access it, enabling
     * optimized user experience and resource usage.
     * 
     * @return true if contextual documentation is available, false otherwise
     */
    default boolean hasContextualDocumentation() {
        try {
            final DocProvider docProvider = new DocProvider();
            return docProvider.hasContextualDocumentation(this.getClass());
        } catch (final Exception e) {
            // Conservative fallback
            return false;
        }
    }
}