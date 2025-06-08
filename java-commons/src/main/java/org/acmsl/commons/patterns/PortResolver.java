//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: PortResolver.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides actual implementations of Port interfaces.
 *
 */
package org.acmsl.commons.patterns;

import org.acmsl.commons.patterns.Port;
import java.util.List;
import java.util.Optional;

/**
 * Provides actual implementations of Port interfaces.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-08
 */
@SuppressWarnings("unused")
public interface PortResolver<P extends Port> {

    /**
     * Resolves all implementations of a given port.
     * @param port such port.
     * @return A list of implementations.
     */
    List<? extends P> resolveAll(final P port);

    /**
     * Resolves the main implementation of a given port.
     * @param port such port.
     * @return The port implementation, if any.
     */
    Optional<? extends P> resolv(final P port);
}
