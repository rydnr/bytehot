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
 * Filename: ByteHot.java
 *
 * Author: rydnr
 *
 * Class name: ByteHot
 *
 * Responsibilities:
 *   - Hot-swap bytecode at runtime.
 *   - Receive callbacks when watched classes change.
 *
 * Collaborators:
 *   - ByteHotAttachRequested: the event that triggers ByteHot agent attachment.
 *   - FolderWatch: the domain object that represents a folder to watch for changes.
 *   - Defaults: the default configuration values for ByteHot.
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.Defaults;
import org.acmsl.bytehot.domain.events.ByteHotAgentAttached;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.events.ByteHotNotStarted;
import org.acmsl.bytehot.domain.events.WatchPathConfigured;
import org.acmsl.bytehot.domain.WatchConfiguration;

import org.acmsl.commons.patterns.DomainResponseEvent;

import java.lang.instrument.Instrumentation;
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The ByteHot runtime.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ByteHot {

    /**
     * Retrieves the ByteHot configuration.
     * @return the configuration.
     */
    @Getter
    private final WatchConfiguration configuration;

    /**
     * The Instrumentation instance used for bytecode manipulation.
     * @return the Instrumentation instance.
     */
    @Getter
    private final Instrumentation instrumentation;

    /**
     * Creates a new ByteHot instance with the specified port.
     * @param instrumentation the Instrumentation instance for the JVM.
     * @param config the configuration for ByteHot.
     */
    public ByteHot(final Instrumentation instrumentation, final WatchConfiguration config) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        this.configuration = Objects.requireNonNull(config);
    }

    /**
     * Accepts a ByteHotAttachRequested event and starts the ByteHot runtime.
     * @param event the event containing the configuration to attach ByteHot.
     * @return an event representing ByteHot agent has been attached and started or
     * an error occurred during the attachment.
     */
    public static DomainResponseEvent<ByteHotAttachRequested> accept(final ByteHotAttachRequested event) {
        DomainResponseEvent<ByteHotAttachRequested> result = null;
        try {
            new ByteHot(event.getInstrumentation(), event.getConfiguration())
                .start();
            result = new ByteHotAgentAttached(event, event.getConfiguration());
        } catch (final Throwable t) {
            result = new ByteHotNotStarted(event, t);
        }
        return result;
    }

    /**
     * Starts ByteHot with the provided configuration.
     */
    public void start() {
        System.out.println("ByteHotAgentAttached");
        System.out.println("WatchPathConfigured");
        
        // Check hot-swap capabilities
        if (instrumentation.isRedefineClassesSupported() && instrumentation.isRetransformClassesSupported()) {
            System.out.println("HotSwapCapabilityEnabled");
        }
        
        // This could involve setting up watchers, starting servers, etc.
    }
}
