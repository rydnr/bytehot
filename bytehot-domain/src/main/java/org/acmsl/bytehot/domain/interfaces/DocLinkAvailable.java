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
 *   - Provide documentation access methods with minimal code pollution
 *   - Default method implementations delegate to centralized DocProvider
 *   - Enable runtime self-documentation capabilities for any implementing class
 *   - Support Flow-aware contextual documentation generation
 *
 * Collaborators:
 *   - DocProvider: Centralized documentation URL generation engine
 */
package org.acmsl.bytehot.domain.interfaces;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Interface providing documentation access methods with minimal code pollution.
 * Uses default methods to avoid requiring implementation changes in existing classes.
 * Enables runtime self-documentation capabilities for ByteHot components.
 * @author Claude Code
 * @since 2025-06-26
 */
public interface DocLinkAvailable {

    /**
     * Gets the documentation URL for this class.
     * Uses default implementation that delegates to DocProvider.
     * @return documentation URL for this class
     */
    @NonNull
    default String getDocUrl() {
        return org.acmsl.bytehot.domain.services.DocumentationServiceRegistry.getInstance()
               .getDocumentationUrl(this.getClass());
    }
    
    /**
     * Gets the documentation URL for a specific method of this class.
     * @param methodName the name of the method to get documentation for
     * @return documentation URL for the specified method
     */
    @NonNull
    default String getMethodDocUrl(@Nullable final String methodName) {
        return org.acmsl.bytehot.domain.services.DocumentationServiceRegistry.getInstance()
               .getMethodDocumentationUrl(this.getClass(), methodName);
    }
    
    /**
     * Gets the runtime Flow documentation link based on current context.
     * Analyzes runtime context to provide contextual documentation.
     * @return Flow-aware documentation URL based on current runtime context
     */
    @NonNull
    default String getRuntimeDocLink() {
        return org.acmsl.bytehot.domain.services.DocumentationServiceRegistry.getInstance()
               .getRuntimeFlowDocumentationUrl(this);
    }
}