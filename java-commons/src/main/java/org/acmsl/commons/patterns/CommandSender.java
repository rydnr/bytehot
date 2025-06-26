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
 * Filename: CommandSender.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Defines the behavior of any class capable to send command
 *              objects.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing project classes.
 */
import org.acmsl.commons.CheckedException;

/*
 * Importing JetBrains annotations.
 */

/**
 * Defines the behavior of any class capable to send {@link Command} objects.
 * @param <C> the command type parameter
 * @param <E> the exception type parameter
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface CommandSender<C extends Command, E extends CheckedException>
{
    /**
     * Sends given command to the chain.
     * @param command the command that represents which actions should
     * be done.
     * @return <code>true</code> if the command is processed by the chain.
     * @throws E in case of error.
     */
    @SuppressWarnings("unused")
    public boolean send(final C command)
        throws E;
}
