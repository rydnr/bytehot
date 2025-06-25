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
 * Filename: DocumentationType.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationType
 *
 * Responsibilities:
 *   - Define types of documentation that can be requested
 *   - Support documentation introspection events
 *   - Enable categorization of documentation requests
 *
 * Collaborators:
 *   - DocumentationRequested: Uses this enum to specify request type
 *   - DocProvider: Uses this enum to generate appropriate documentation
 */
package org.acmsl.bytehot.domain;

/**
 * Enumeration of documentation types that can be requested in ByteHot.
 * Used for documentation introspection and contextual documentation generation.
 * @author Claude Code
 * @since 2025-06-24
 */
public enum DocumentationType {
    
    /**
     * Basic class-level documentation without specific context.
     */
    BASIC,
    
    /**
     * Method-specific documentation for a particular method.
     */
    METHOD,
    
    /**
     * Contextual documentation based on detected flow context.
     */
    CONTEXTUAL,
    
    /**
     * Testing-related documentation for test scenarios.
     */
    TESTING,
    
    /**
     * Architecture and design documentation.
     */
    ARCHITECTURE,
    
    /**
     * API documentation for public interfaces.
     */
    API,
    
    /**
     * Tutorial or getting started documentation.
     */
    TUTORIAL,
    
    /**
     * Troubleshooting and error resolution documentation.
     */
    TROUBLESHOOTING
}