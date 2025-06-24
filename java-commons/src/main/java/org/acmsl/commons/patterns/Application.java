//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: Application.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Marks all classes in charge of the Application layer.
 *
 */
package org.acmsl.commons.patterns;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.util.List;

/**
 * Marks all classes in charge of the Application layer.
 * This interface now supports generic event handling while maintaining type safety
 * through internal dispatching mechanisms in implementing classes.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@SuppressWarnings("unused")
public interface Application {

    /**
     * Accepts any domain event and returns appropriate response events.
     * Implementing classes should provide internal dispatching to handle
     * different event types while maintaining type safety.
     * @param event the domain event to process
     * @return a list of response events, possibly empty
     */
    List<? extends DomainResponseEvent<?>> accept(final DomainEvent event);
}
