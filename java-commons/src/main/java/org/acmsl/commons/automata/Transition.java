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
 * Filename: Transition.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents nodes in automatons or state diagrams, that is,
 *              changes in the behavior of entities that follow a
 *              deterministic finite-state automata (DFSA).
 */
package org.acmsl.commons.automata;

/*
 * Importing some JetBrains annotations..
 */

/*
 * Importing some JDK classes.
 */
import java.io.Serializable;

/**
 * Represents transitions in automatons or state diagrams, that is, changes
 * in the behavior of entities that follow a deterministic finite-state
 * automata (DFSA).
 * @param <S1> the source state type parameter
 * @param <S2> the target state type parameter
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface Transition<S1 extends State, S2 extends State>
    extends  Serializable
{
    /**
     * Retrieves the origin of the directed link represented by this
     * transition.
     * @return the origin state.
     */
    @SuppressWarnings("unused")
    
    public S1 getOrigin();

    /**
     * Retrieves the destination of the directed link represented by
     * this transition.
     * @return the destination state.
     */
    
    @SuppressWarnings("unused")
    public S2 getDestination();
}
