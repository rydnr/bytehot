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
 * Filename: DocumentationService.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationService
 *
 * Responsibilities:
 *   - Domain service interface for documentation URL generation
 *   - Define contract for Flow-aware documentation access
 *   - Support dependency inversion for infrastructure implementations
 *   - Enable testable documentation service with clean architecture
 *
 * Collaborators:
 *   - DocLinkAvailable: Uses this service for URL generation
 *   - FlowDetectionService: For runtime Flow context analysis
 */
package org.acmsl.bytehot.domain.services;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Domain service interface for documentation URL generation and Flow detection.
 * Defines the contract for providing contextual documentation access.
 * @author Claude Code
 * @since 2025-06-26
 */
public interface DocumentationService {

    /**
     * Gets the documentation URL for a specific class.
     * @param clazz the class to get documentation for
     * @return documentation URL for the class
     */
    @NonNull
    String getDocumentationUrl(@NonNull final Class<?> clazz);

    /**
     * Gets the documentation URL for a specific method of a class.
     * @param clazz the class containing the method
     * @param methodName the name of the method (can be null or empty)
     * @return documentation URL for the method
     */
    @NonNull
    String getMethodDocumentationUrl(@NonNull final Class<?> clazz, @Nullable final String methodName);

    /**
     * Gets the runtime Flow documentation URL based on current context.
     * Analyzes runtime context to provide contextual documentation.
     * @param context the object providing context for Flow detection
     * @return Flow-aware documentation URL
     */
    @NonNull
    String getRuntimeFlowDocumentationUrl(@NonNull final Object context);
}