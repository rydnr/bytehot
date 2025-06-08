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
 * Filename: Ports.java
 *
 * Author: rydnr
 *
 * Class name: Ports
 *
 * Responsibilities:
 *   - Provide Port implementations to domain classes.
 *
 * Collaborators:
 *   - application layer: discovering the Port implementations in the infrastructure layer.
 */
package org.acmsl.bytehot.domain;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.CachingPortResolver;
import org.acmsl.commons.patterns.Port;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provide Port implementations to domain classes.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-08
 */
public class Ports
    extends CachingPortResolver {

    /**
     * Do not call! It's used by the application layer.
     */
    @SuppressWarnings("unchecked")
    public <P extends Port> void inject(final Class<P> port, final Adapter<P> adapter) {
        final Map<Class<? extends Port>, List<Adapter<? extends Port>>> implementations = getImplementations();

        List<Adapter<P>> adapters = (List<Adapter<P>>) (List<?>) implementations.get(port);

        if (adapters == null) {
            adapters = new ArrayList<>();
            implementations.put(port, (List<Adapter<? extends Port>>) (List<?>) adapters);
        }
        if (!adapters.contains(adapter)) {
            adapters.add(adapter);
        }
    }
}
