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
 * Filename: InstancesUpdated.java
 *
 * Author: Claude Code
 *
 * Class name: InstancesUpdated
 *
 * Responsibilities:
 *   - Domain event for instance update completion after class redefinition
 *   - Provides details about updated instances and update method used
 *
 * Collaborators:
 *   - InstanceUpdateMethod: Enum defining the update strategy used
 *   - DomainEvent: Base interface for domain events
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.InstanceUpdateMethod;
import org.acmsl.commons.patterns.DomainEvent;

import java.time.Duration;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Domain event for instance update completion after class redefinition
 * @author Claude Code
 * @since 2025-06-17
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class InstancesUpdated implements DomainEvent {

    /**
     * Fully qualified name of the updated class
     */
    @Getter
    private final String className;

    /**
     * Number of instances that were updated successfully
     */
    @Getter
    private final int updatedInstances;

    /**
     * Total number of instances found for the class
     */
    @Getter
    private final int totalInstances;

    /**
     * Method used to update the instances
     */
    @Getter
    private final InstanceUpdateMethod updateMethod;

    /**
     * Number of instances that failed to update
     */
    @Getter
    private final int failedUpdates;

    /**
     * Technical details about the update process
     */
    @Getter
    private final String updateDetails;

    /**
     * Time taken for instance updates
     */
    @Getter
    private final Duration duration;

    /**
     * When instance update completed
     */
    @Getter
    private final Instant timestamp;
}