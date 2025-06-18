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
 * Filename: UserAuthenticated.java
 *
 * Author: Claude Code
 *
 * Class name: UserAuthenticated
 *
 * Responsibilities:
 *   - Represent user authentication domain event
 *   - Capture authentication method and session information
 *   - Support user session tracking
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base event implementation
 *   - UserId: User identifier
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.UserId;
import org.acmsl.bytehot.domain.UserProfile;

import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Domain event representing user authentication
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class UserAuthenticated implements DomainResponseEvent<UserAuthenticationRequested> {

    /**
     * The original authentication request event
     */
    private final UserAuthenticationRequested preceding;

    /**
     * The authenticated user's profile
     */
    private final UserProfile userProfile;

    /**
     * Authentication success status
     */
    private final boolean authenticated;

    /**
     * Factory method for successful authentication
     * @param precedingEvent the request event
     * @param userProfile the user profile
     * @return new UserAuthenticated event
     */
    public static UserAuthenticated success(final UserAuthenticationRequested precedingEvent, 
                                           final UserProfile userProfile) {
        return new UserAuthenticated(precedingEvent, userProfile, true);
    }

    /**
     * Factory method for failed authentication
     * @param precedingEvent the request event
     * @param userProfile the user profile (may be partial)
     * @return new UserAuthenticated event
     */
    public static UserAuthenticated failure(final UserAuthenticationRequested precedingEvent, 
                                           final UserProfile userProfile) {
        return new UserAuthenticated(precedingEvent, userProfile, false);
    }

}