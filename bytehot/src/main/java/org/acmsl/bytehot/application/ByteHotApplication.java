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
 * Filename: ByteHotApplication.java
 *
 * Author: rydnr
 *
 * Class name: ByteHotApplication
 *
 * Responsibilities: Application layer for ByteHot.
 *
 * Collaborators:
 *   - org.acmsl.bytehot.domain.events.ByteHotStartRequested
 */
package org.acmsl.bytehot.application;

import org.acmsl.bytehot.domain.ByteHot;
import org.acmsl.bytehot.domain.events.ByteHotStarted;
import org.acmsl.bytehot.domain.events.ByteHotStartRequested;

import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.util.Arrays;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Application layer for ByteHot.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@EqualsAndHashCode
@ToString
public class ByteHotApplication
    implements HandlesByteHotStarted {

    /**
     * Default constructor to point to the singleton.
     */
    protected ByteHotApplication() {}

    /**
     * Returns the singleton instance of ByteHotApplication.
     * @return the singleton instance.
     */
    public static ByteHotApplication getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Holder class for the singleton instance of ByteHotApplication.
     */
    protected static class Holder {
        private static final ByteHotApplication INSTANCE = new ByteHotApplication();
    }

    /**
     * Accepts a ByteHotStartRequested event.
     * @param event such event.
     * @return A list of events in response.
     */
    @Override
    public List<DomainResponseEvent<ByteHotStartRequested>> accept(final ByteHotStartRequested event) {
        // TODO: Discover aggregate / primary port dynamically
        return Arrays.asList(ByteHot.accept(event));
    }
}
