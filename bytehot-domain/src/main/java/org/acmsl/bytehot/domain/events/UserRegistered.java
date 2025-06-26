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
 * Filename: UserRegistered.java
 *
 * Author: Claude Code
 *
 * Class name: UserRegistered
 *
 * Responsibilities:
 *   - Represent user registration domain event
 *   - Capture registration source and user profile information
 *   - Support EventSourcing reconstruction
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base event implementation
 *   - UserId: User identifier
 *   - UserProfile: User profile information
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.bytehot.domain.UserId;
import org.acmsl.bytehot.domain.UserProfile;
import org.acmsl.bytehot.domain.UserRegistrationSource;

import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Domain event representing user registration
 * @author Claude Code
 * @since 2025-06-18
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class UserRegistered implements DomainResponseEvent<UserRegistrationRequested> {

    /**
     * The original registration request event
     */
    protected final UserRegistrationRequested preceding;

    /**
     * The registered user's profile
     */
    protected final UserProfile userProfile;

    /**
     * Source of the user registration
     */
    protected final UserRegistrationSource source;

    /**
     * Factory method for new user registration
     * @param precedingEvent the request event
     * @param userProfile the user profile
     * @param source the registration source
     * @return new UserRegistered event
     */
    public static UserRegistered of(final UserRegistrationRequested precedingEvent, 
                                   final UserProfile userProfile, 
                                   final UserRegistrationSource source) {
        return new UserRegistered(precedingEvent, userProfile, source);
    }

    /**
     * Factory method for automatic user registration
     * @param precedingEvent the request event
     * @param userProfile the user profile
     * @return new UserRegistered event with automatic source
     */
    public static UserRegistered automatic(final UserRegistrationRequested precedingEvent, 
                                          final UserProfile userProfile) {
        return of(precedingEvent, userProfile, UserRegistrationSource.AUTOMATIC);
    }

}