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
 * Filename: ByteHotAttachRequested.java
 *
 * Author: rydnr
 *
 * Class name: ByteHotAttachRequested
 *
 * Responsibilities: Represent a request to attach ByteHot agent with a given configuration.
 *
 * Collaborators:
 *   - None
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.bytehot.domain.WatchConfiguration;
import org.acmsl.commons.patterns.DomainEvent;

import java.lang.instrument.Instrumentation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a request to attach ByteHot agent with a given configuration.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ByteHotAttachRequested
    extends AbstractVersionedDomainEvent {

    /**
     * The configuration for ByteHot.
     * @return the configuration object.
     */
    @Getter
    private final WatchConfiguration configuration;

    /**
     * The instrumentation instance for the JVM.
     * @return the instrumentation instance.
     */
    @Getter
    private final Instrumentation instrumentation;

    /**
     * Creates a new ByteHotAttachRequested event.
     * 
     * @param metadata the event metadata including user context
     * @param configuration the configuration for ByteHot
     * @param instrumentation the instrumentation instance for the JVM
     */
    public ByteHotAttachRequested(
            EventMetadata metadata,
            WatchConfiguration configuration,
            Instrumentation instrumentation) {
        super(metadata);
        this.configuration = configuration;
        this.instrumentation = instrumentation;
    }

    /**
     * Factory method to create a ByteHotAttachRequested event with user context.
     * 
     * @param configuration the configuration for ByteHot
     * @param instrumentation the instrumentation instance for the JVM
     * @return a new ByteHotAttachRequested event
     */
    public static ByteHotAttachRequested withUserContext(
            WatchConfiguration configuration,
            Instrumentation instrumentation) {
        
        final EventMetadata metadata = createMetadataForNewAggregate(
            "bytehot-agent",
            "attach-" + System.currentTimeMillis()
        );
        
        return new ByteHotAttachRequested(metadata, configuration, instrumentation);
    }
}
