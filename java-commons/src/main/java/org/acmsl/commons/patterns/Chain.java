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
 * Filename: Chain.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents chain objects, as they are modelled by GoF's Chain
 *              Of Responsibility design pattern.
 */
package org.acmsl.commons.patterns;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents chain objects, as they are modelled by GoF's Chain Of
 * Responsibility design pattern.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface Chain<C extends Command, E extends Exception, CH extends CommandHandler<C, E>>
{
    /**
     * Adds a new commandHandler to the chain.
     * @param commandHandler the commandHandler to be added.
     */
    public void add(@NotNull final CH commandHandler);

    /**
     * Adds a new commandHandler to the first position of the chain.
     * @param commandHandler the commandHandler to be added.
     */
    public void addFirst(@NotNull final CH commandHandler);

    /**
     * Checks whether or not this chain contains given command handler.
     * @param commandHandler the handler to look for inside this chain.
     * @return true if such handler is already included in this chain.
     */
    public boolean contains(@NotNull final CH commandHandler);

    /**
     * Retrieves the position given handler occupies in this chain.
     * @param commandHandler the handler to locate.
     * @return the first position in which given handler is found, or
     * -1 if doesn't participate in this chain.
     */
    public int indexOf(@NotNull final CH commandHandler);

    /**
     * Retrieves the command handler that is located at given position.
     * @param commandHandlerIndex the position of the chain.
     * @return the command handler referred at such position.
     */
    @Nullable
    public CH get(final int commandHandlerIndex);

    /**
     * Retrieves the list of handlers.
     * @return such list.
     */
    @NotNull
    public List<CH> getHandlers();

    /**
     * Checks this chain is empty.
     * @return true if this chain is empty.
     */
    public boolean isEmpty();

    /**
     * Retrieves the size of the chain.
     * @return current number of command handlers referred by this chain.
     */
    public int size();
}
