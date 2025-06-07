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
 * Filename: Observable.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents generic Observable objects.
 *
 */
package org.acmsl.commons.patterns;

/**
 * Represents generic Observable objects.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface Observable<O extends Observer>
{
    /**
     * Attaches given observer in order to be notified of state
     * changes.
     * @param observer the new observer to attach.
     */
    public void attach(final O observer);

    /**
     * Detaches given observer in order to stop being notified of
     * state changes.
     * @param observer the new observer to attach.
     */
    public void detach(final O observer);

    /**
     * Notifies all attached observers of state changes.
     */
    void inform();
}
