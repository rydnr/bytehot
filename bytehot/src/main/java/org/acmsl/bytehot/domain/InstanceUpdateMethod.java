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
 * Filename: InstanceUpdateMethod.java
 *
 * Author: Claude Code
 *
 * Class name: InstanceUpdateMethod
 *
 * Responsibilities:
 *   - Define strategies for updating existing instances after class redefinition
 *   - Provide method types for different update scenarios
 *
 * Collaborators:
 *   - InstancesUpdated: Uses this enum to indicate update method used
 *   - InstanceUpdater: Uses this enum to determine update strategy
 */
package org.acmsl.bytehot.domain;

/**
 * Defines strategies for updating existing instances after class redefinition
 * @author Claude Code
 * @since 2025-06-17
 */
public enum InstanceUpdateMethod {
    
    /**
     * JVM automatically updates instances - default for method body changes
     */
    AUTOMATIC,
    
    /**
     * Manual update via reflection - for stateful objects and complex scenarios
     */
    REFLECTION,
    
    /**
     * Refresh dynamic proxies - for AOP and framework-managed objects
     */
    PROXY_REFRESH,
    
    /**
     * Re-create instances through factories - for dependency injection scenarios
     */
    FACTORY_RESET,
    
    /**
     * No instances or update not needed - fallback for problematic cases
     */
    NO_UPDATE
}