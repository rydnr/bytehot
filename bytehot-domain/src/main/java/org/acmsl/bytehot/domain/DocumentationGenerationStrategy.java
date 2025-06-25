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
 * Filename: DocumentationGenerationStrategy.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationGenerationStrategy
 *
 * Responsibilities:
 *   - Define strategies for generating documentation URLs
 *   - Support documentation introspection and analytics
 *   - Enable tracking of documentation generation approaches
 *
 * Collaborators:
 *   - DocumentationLinkGenerated: Uses this enum to track generation strategy
 *   - DocProvider: Uses strategies to determine URL generation approach
 */
package org.acmsl.bytehot.domain;

/**
 * Enumeration of strategies for generating documentation URLs in ByteHot.
 * Used for tracking how documentation links are generated and optimizing
 * the documentation system based on usage patterns.
 * @author Claude Code
 * @since 2025-06-24
 */
public enum DocumentationGenerationStrategy {
    
    /**
     * Basic documentation URL generation without contextual information.
     */
    BASIC,
    
    /**
     * Contextual documentation based on detected runtime flow.
     */
    CONTEXTUAL,
    
    /**
     * Flow-specific documentation tailored to a particular flow.
     */
    FLOW_SPECIFIC,
    
    /**
     * Cached documentation URL retrieved from cache.
     */
    CACHED,
    
    /**
     * Fallback documentation when preferred strategy fails.
     */
    FALLBACK,
    
    /**
     * User-customized documentation based on preferences.
     */
    PERSONALIZED,
    
    /**
     * AI-generated contextual documentation.
     */
    AI_ENHANCED,
    
    /**
     * Template-based documentation generation.
     */
    TEMPLATE_BASED
}