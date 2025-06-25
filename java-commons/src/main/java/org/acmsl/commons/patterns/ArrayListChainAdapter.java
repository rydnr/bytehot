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
 * Filename: ArrayListChainAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts ArrayList objects to implement the Chain
 *              interface.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing JetBrains annotations.
 */

/*
 * Importing some JDK classes.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Adapts {@link ArrayList} objects to implement the {@link Chain} interface.
 * @param <C> the command.
 * @param <E> the exception.
 * @param <CH> the command handler.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro Armendariz</a>
 * @since 2.0
 */
public class ArrayListChainAdapter<C extends Command, E extends Exception, CH extends CommandHandler<C, E>>
    implements  Chain<C, E, CH>
{
    /**
     * Actual storing object.
     */
    private List<CH> m__lCore;

    /**
     * Constructs an ArrayListChainAdapter.
     */
    public ArrayListChainAdapter()
    {
        immutableSetCore(new ArrayList<CH>());
    }

    /**
     * Specifies a new chain.
     * @param list the new list.
     */
    private void immutableSetCore(final List<CH> list)
    {
        m__lCore = list;
    }

    /**
     * Specifies a new chain.
     * @param list the new list.
     */
    @SuppressWarnings("unused")
    protected void setCore(final List<CH> list)
    {
        immutableSetCore(list);
    }

    /**
     * Retrieves the core of this chain.
     * @return such collection.
     */
    protected List<CH> getCore()
    {
        return m__lCore;
    }

    /**
     * Retrieves the core of this chain.
     * @return such collection.
     */
    
    public List<CH> getHandlers()
    {
        return new ArrayList<>(getCore());
    }

    /**
     * Adds a new commandHandler to the chain.
     * @param commandHandler the commandHandler to be added.
     */
    @Override
    public void add(final CH commandHandler)
    {
        final List<CH> t_lCore = getCore();

        if  (t_lCore != null)
        {
            t_lCore.add(commandHandler);
        }
    }

    /**
     * Adds a new commandHandler to the first position of the chain.
     * @param commandHandler the commandHandler to be added.
     */
    @Override
    @SuppressWarnings("unused")
    public void addFirst(final CH commandHandler)
    {
        final List<CH> t_lCore = getCore();

        if  (t_lCore != null)
        {
            t_lCore.add(0, commandHandler);
        }
    }

    /**
     * Checks whether or not this chain contains given command handler.
     * @param commandHandler the handler to look for inside this chain.
     * @return <code>true</code> if such handler is already present in
     * this chain.
     */
    @Override
    public boolean contains(final CH commandHandler)
    {
        boolean result = false;

        final List<CH> t_lCore = getCore();

        if  (t_lCore != null)
        {
            result = t_lCore.contains(commandHandler);
        }

        return result;
    }

    /**
     * Retrieves the position given handler occupies in this chain.
     * @param commandHandler the handler to locate.
     * @return the first position in which given handler is found,
     * or -1 if doesn't participate in this chain.
     */
    @Override
    @SuppressWarnings("unused")
    public int indexOf(final CH commandHandler)
    {
        int result = -1;

        final List<CH> t_lCore = getCore();

        if  (t_lCore != null)
        {
            result = t_lCore.indexOf(commandHandler);
        }

        return result;
    }

    /**
     * Retrieves the command handler that is located at given position.
     * @param commandHandlerIndex the position of the chain.
     * @return the command handler referred at such position.
     */
    @Override
    
    public CH get(final int commandHandlerIndex)
    {
        final CH result;

        if  (commandHandlerIndex >= 0)
        {
            final List<CH> t_lCore = getCore();

            if  (t_lCore != null)
            {
                result = t_lCore.get(commandHandlerIndex);
            }
            else
            {
                result = null;
            }
        }
        else
        {
            result = null;
        }

        return result;
    }

    /**
     * Checks whether this chain is empty.
     * @return true if this chain is empty.
     */
    @Override
    public boolean isEmpty()
    {
        boolean result = false;

        final List<CH> t_lCore = getCore();

        if  (t_lCore != null)
        {
            result = t_lCore.isEmpty();
        }

        return result;
    }

    /**
     * Retrieves the size of the chain.
     * @return current number of command handlers referred by this chain.
     */
    @Override
    public int size()
    {
        int result = 0;

        final List<CH> t_lCore = getCore();

        if  (t_lCore != null)
        {
            result = t_lCore.size();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public String toString()
    {
        return
            "{ \"class\": \"" + ArrayListChainAdapter.class.getSimpleName() + "\""
            + ", \"core\": \"" + this.m__lCore + "\" }";
    }
}
