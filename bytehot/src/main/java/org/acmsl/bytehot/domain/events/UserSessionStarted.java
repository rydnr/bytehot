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
 * Filename: UserSessionStarted.java
 *
 * Author: Claude Code
 *
 * Class name: UserSessionStarted
 *
 * Responsibilities:
 *   - Represent user session start domain event
 *   - Capture session and environment information
 *   - Support session tracking and analytics
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base event implementation
 *   - UserId: User identifier
 *   - UserSession: Session information
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.UserSession;

import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Domain event representing user session start
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class UserSessionStarted implements DomainResponseEvent<UserSessionStartRequested> {

    /**
     * The original session start request event
     */
    private final UserSessionStartRequested preceding;

    /**
     * The user session information
     */
    private final UserSession session;

    /**
     * Factory method for session start
     * @param precedingEvent the request event
     * @param session the user session
     * @return new UserSessionStarted event
     */
    public static UserSessionStarted of(final UserSessionStartRequested precedingEvent, 
                                       final UserSession session) {
        return new UserSessionStarted(precedingEvent, session);
    }
}