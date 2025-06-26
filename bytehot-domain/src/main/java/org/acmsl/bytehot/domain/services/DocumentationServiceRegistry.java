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
 * Filename: DocumentationServiceRegistry.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationServiceRegistry
 *
 * Responsibilities:
 *   - Registry for DocumentationService implementation
 *   - Support dependency injection while maintaining clean architecture
 *   - Provide static access for default method implementations
 *   - Enable testable service registration and retrieval
 *
 * Collaborators:
 *   - DocumentationService: Manages instances of this interface
 *   - DocLinkAvailable: Uses registry to access service
 */
package org.acmsl.bytehot.domain.services;

import org.acmsl.bytehot.domain.Defaults;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Registry for DocumentationService implementation.
 * Provides static access to service while supporting dependency injection.
 * @author Claude Code
 * @since 2025-06-26
 */
public final class DocumentationServiceRegistry {

    /**
     * The current DocumentationService instance.
     */
    @Nullable
    private static DocumentationService instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private DocumentationServiceRegistry() {
        // Utility class
    }

    /**
     * Registers a DocumentationService implementation.
     * @param service the service to register
     */
    public static void register(@NonNull final DocumentationService service) {
        instance = service;
    }

    /**
     * Gets the current DocumentationService instance.
     * Creates a default implementation if none is registered.
     * @return the current DocumentationService
     */
    @NonNull
    public static DocumentationService getInstance() {
        if (instance == null) {
            instance = createDefaultService();
        }
        return instance;
    }

    /**
     * Clears the registered service (useful for testing).
     */
    public static void clear() {
        instance = null;
    }

    /**
     * Creates a default DocumentationService implementation.
     * @return default service implementation
     */
    @NonNull
    private static DocumentationService createDefaultService() {
        return new DefaultDocumentationService();
    }

    /**
     * Default implementation of DocumentationService.
     * Provides basic URL generation without Flow detection.
     */
    private static class DefaultDocumentationService implements DocumentationService {

        protected static final String DOCUMENTATION_BASE_URL = 
            Defaults.DOCUMENTATION_BASE_URL + "/docs/";
        protected static final String FLOWS_BASE_URL = 
            Defaults.FLOWS_BASE_URL + "/";
        protected static final String CLASSES_BASE_URL = 
            Defaults.CLASSES_BASE_URL + "/";

        @Override
        @NonNull
        public String getDocumentationUrl(@NonNull final Class<?> clazz) {
            final String className = clazz.getSimpleName();
            final String packagePath = clazz.getPackage() != null ? 
                clazz.getPackage().getName().replace('.', '/') + "/" : "";
            
            return DOCUMENTATION_BASE_URL + packagePath + className + ".html";
        }

        @Override
        @NonNull
        public String getMethodDocumentationUrl(@NonNull final Class<?> clazz, 
                                              @Nullable final String methodName) {
            final String baseUrl = getDocumentationUrl(clazz);
            
            if (methodName == null || methodName.trim().isEmpty()) {
                return baseUrl;
            }
            
            final String sanitizedMethodName = sanitizeForUrl(methodName);
            return baseUrl + "#" + sanitizedMethodName;
        }

        @Override
        @NonNull
        public String getRuntimeFlowDocumentationUrl(@NonNull final Object context) {
            // Basic Flow detection based on class name patterns
            final String className = context.getClass().getSimpleName();
            
            if (className.contains("FileWatcher") || className.contains("ClassFileChanged")) {
                return FLOWS_BASE_URL + "file-change-detection.html";
            }
            
            if (className.contains("Configuration")) {
                return FLOWS_BASE_URL + "configuration-management.html";
            }
            
            if (className.contains("HotSwap") || className.contains("Bytecode")) {
                return FLOWS_BASE_URL + "hotswap-operations.html";
            }
            
            if (className.contains("Test") && !className.contains("TestDocumented")) {
                return FLOWS_BASE_URL + "testing-workflow.html";
            }
            
            if (className.contains("Event")) {
                return FLOWS_BASE_URL + "event-processing.html";
            }
            
            // Fallback to general documentation
            return DOCUMENTATION_BASE_URL + "getting-started.html";
        }

        @NonNull
        private String sanitizeForUrl(@NonNull final String input) {
            return input.replaceAll("[^a-zA-Z0-9_-]", "_")
                       .toLowerCase()
                       .replaceAll("_+", "_")
                       .replaceAll("^_|_$", "");
        }
    }
}