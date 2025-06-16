/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*
 ******************************************************************************
 *
 * Filename: HotSwapCapabilityEnabled.java
 *
 * Author: Claude Code
 *
 * Class name: HotSwapCapabilityEnabled
 *
 * Responsibilities:
 *   - Represent the event when hot-swap capabilities are verified and enabled
 *
 * Collaborators:
 *   - Instrumentation: The JVM instrumentation instance
 *   - ByteHotAttachRequested: The preceding event that requested capability check
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.instrument.Instrumentation;

/**
 * Event representing successful verification and enabling of hot-swap capabilities.
 * @author Claude Code
 * @since 2025-06-15
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class HotSwapCapabilityEnabled implements DomainResponseEvent<ByteHotAttachRequested> {

    /**
     * The instrumentation instance that supports hot-swapping.
     */
    @Getter
    private final Instrumentation instrumentation;

    /**
     * The preceding event that requested this capability check.
     */
    @Getter
    private final ByteHotAttachRequested precedingEvent;

    /**
     * Returns whether class redefinition is supported.
     * @return true if class redefinition is supported.
     */
    public boolean canRedefineClasses() {
        return instrumentation.isRedefineClassesSupported();
    }

    /**
     * Returns whether class retransformation is supported.
     * @return true if class retransformation is supported.
     */
    public boolean canRetransformClasses() {
        return instrumentation.isRetransformClassesSupported();
    }

    @Override
    public ByteHotAttachRequested getPreceding() {
        return precedingEvent;
    }
}