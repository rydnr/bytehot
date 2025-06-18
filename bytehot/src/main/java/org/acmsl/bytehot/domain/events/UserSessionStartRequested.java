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
 * Filename: UserSessionStartRequested.java
 *
 * Author: Claude Code
 *
 * Class name: UserSessionStartRequested
 *
 * Responsibilities:
 *   - Represent user session start request
 *   - Trigger user session initialization flow
 *
 * Collaborators:
 *   - UserId: User identifier
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.UserId;

import org.acmsl.commons.patterns.DomainEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * Domain event requesting user session start
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class UserSessionStartRequested implements DomainEvent {

    /**
     * User identifier for session
     */
    private final UserId userId;

    /**
     * Environment snapshot for session
     */
    private final Map<String, String> environment;
}