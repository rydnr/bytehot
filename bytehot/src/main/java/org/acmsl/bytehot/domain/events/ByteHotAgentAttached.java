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
 * Author: Claude
 *
 * Class name: ByteHotAgentAttached
 *
 * Responsibilities:
 *   - Represent the moment when the JVM agent is successfully attached
 *
 * Collaborators:
 *   - org.acmsl.commons.patterns.DomainEvent: Base event interface
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.DomainEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents the moment when the JVM agent is successfully attached.
 * @author Claude
 * @since 2025-06-15
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class ByteHotAgentAttached
    implements DomainEvent {

    /**
     * The JVM process ID where the agent was attached.
     */
    @Getter
    private final long jvmProcessId;

    /**
     * The agent arguments passed during attachment.
     */
    @Getter
    private final String agentArguments;

    /**
     * The instrumentation class name used by the agent.
     */
    @Getter
    private final String instrumentationClassName;
}