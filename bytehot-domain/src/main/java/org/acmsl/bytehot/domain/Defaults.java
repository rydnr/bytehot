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
 * Filename: Defaults.java
 *
 * Author: rydnr
 *
 * Class name: Defaults
 *
 * Responsibilities: Define default configuration values for ByteHot.
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain;

/**
 * Defines default configuration values for ByteHot.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
public interface Defaults {

    /**
     * The default port on which ByteHot will listen for requests.
     */
    int PORT = 62345;

    /**
     * Base URL for ByteHot documentation system.
     * Used by DocProvider for generating documentation URLs.
     */
    String DOCUMENTATION_BASE_URL = "https://rydnr.github.io/bytehot";

    /**
     * Base URL for ByteHot flow documentation.
     * Used by DocProvider for generating flow-specific documentation URLs.
     */
    String FLOWS_BASE_URL = "https://rydnr.github.io/bytehot/flows";

    /**
     * Base URL for ByteHot class documentation.
     * Used by DocProvider for generating class-specific documentation URLs.
     */
    String CLASSES_BASE_URL = "https://rydnr.github.io/bytehot/classes";
}
