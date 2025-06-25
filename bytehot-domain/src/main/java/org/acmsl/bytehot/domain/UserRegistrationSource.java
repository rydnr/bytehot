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
 * Filename: UserRegistrationSource.java
 *
 * Author: Claude Code
 *
 * Enum name: UserRegistrationSource
 *
 * Responsibilities:
 *   - Define the sources from which users can be registered in the system
 *   - Track the origin of user registration for audit and analytics purposes
 */
package org.acmsl.bytehot.domain;

/**
 * User registration source enumeration defining how users are discovered and registered
 * in the ByteHot system. Used for tracking user-aware operations and analytics.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum UserRegistrationSource {
    /**
     * User automatically discovered and registered.
     * The system automatically detects and registers users based on context clues
     * such as system properties, current user session, or development environment.
     * This is the most common registration method for seamless user experience.
     */
    AUTOMATIC,

    /**
     * User explicitly registered.
     * The user or administrator explicitly registered the user through
     * configuration, command line parameters, or direct API calls.
     * Provides explicit control over user registration.
     */
    EXPLICIT,

    /**
     * User registered from Git configuration.
     * User information is extracted from Git configuration files
     * (user.name and user.email) to identify the developer.
     * Common in development environments where Git is used.
     */
    GIT_CONFIG,

    /**
     * User registered from environment variables.
     * User information is obtained from environment variables
     * such as USER, USERNAME, or custom ByteHot-specific variables.
     * Useful for CI/CD environments and automated systems.
     */
    ENVIRONMENT
}