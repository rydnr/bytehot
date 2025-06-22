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
 * Filename: ByteHotAgentAttached.java
 *
 * Author: rydnr
 *
 * Class name: ByteHotAgentAttached
 *
 * Responsibilities: Represent the moment ByteHot agent has been attached and started.
 *
 * Collaborators:
 *   - org.acmsl.bytehot.domain.events.ByteHotAttachRequested
 *   - org.acmsl.bytehot.domain.ByteHot
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents the moment ByteHot agent has been attached and started.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class ByteHotAgentAttached
    implements DomainResponseEvent<ByteHotAttachRequested> {

    /**
     * The original event.
     * @return such event.
     */
    @Getter
    private final ByteHotAttachRequested preceding;

    /**
     * The configuration for ByteHot.
     * @return the configuration object.
     */
    @Getter
    private final WatchConfiguration configuration;
}
