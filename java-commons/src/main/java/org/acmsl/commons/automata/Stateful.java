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
 * Filename: Stateful.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Models stateful entities.
 */
package org.acmsl.commons.automata;

/*
 * Importing some JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing some JDK classes.
 */
import java.io.Serializable;

/**
 * Models stateful entities.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public interface Stateful<S extends State, T extends Transition<S, ?>>
    extends  Serializable
{
    /**
     * Retrieves the current state.
     * @return such state.
     */
    @SuppressWarnings("unused")
    @NotNull
    public S getCurrentState();

    /**
     * Accepts given transition.
     * @param transition the transition.
     * @return the new state this stateful instance has just reached.
     */
    @SuppressWarnings("unused")
    @NotNull
    public S acceptTransition(@NotNull T transition);
}
