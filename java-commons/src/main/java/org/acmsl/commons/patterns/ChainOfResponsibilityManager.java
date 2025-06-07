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
 * Filename: ChainOfResponsibilityManager.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Keeps tracks of the order in which objects are invoked to make
 *              them participate in the chain of responsibility pattern.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing JetBrains annotations.
 */
import org.acmsl.commons.CheckedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Keeps track of the order in which objects are invoked to
 * make them participate in the chain of responsibility pattern.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public abstract class ChainOfResponsibilityManager<C extends Command, E extends CheckedException, CH extends CommandHandler<C, E>>
    implements  ChainOfResponsibility<C, E, CH>,
                CommandSender<C, E>,
                Manager
{
    /**
     * Flag indicating whether the chain has been initialized or not.
     */
    private boolean m__bChainInitialized;

    /**
     * Constructs a manager object by calling initialization code.
     */
    @SuppressWarnings("unused")
    protected ChainOfResponsibilityManager()
    {
        immutableSetChainInitialized(false);
    }

    /**
     * Specifies whether the chain has been initialized.
     * @param flag such flag.
     */
    protected final void immutableSetChainInitialized(final boolean flag)
    {
        m__bChainInitialized = flag;
    }

    /**
     * Retrieves whether the chain has been initialized.
     * @return such flag.
     */
    protected final boolean immutableGetChainInitialized()
    {
        return m__bChainInitialized;
    }

    /**
     * Retrieves the chain.
     * @return the chain.
     */
    @SuppressWarnings("unused")
    protected final Chain<C, E, CH> immutableGetChain()
    {
        return immutableGetChain(immutableGetChainInitialized());
    }

    /**
     * Retrieves the chain.
     * @param chainInitialized whether the chain was initialized already.
     * @return the chain.
     */
    @NotNull
    protected Chain<C, E, CH> immutableGetChain(final boolean chainInitialized)
    {
        @NotNull final Chain<C, E, CH> result;

        if  (!chainInitialized)
        {
            buildChain();
        }

        result = getChain();

        if  (!chainInitialized)
        {
            immutableSetChainInitialized(true);
        }

        return result;
    }

    /**
     * Retrieves the concrete chain.
     * @return the chain itself.
     */
    @NotNull
    protected abstract Chain<C, E, CH> getChain();

    /**
     * Adds given command handler to the chain.
     * @param commandHandler the command handler to add.
     */
    public void add(@NotNull final CH commandHandler)
    {
        add(commandHandler, immutableGetChainInitialized());
    }

    /**
     * Adds given command handler to the chain.
     * @param commandHandler the command handler to add.
     * @param chainInitialized whether the chain is initialized.
     */
    protected void add(@NotNull final CH commandHandler, final boolean chainInitialized)
    {
        @NotNull final Chain<C, E, CH> t_Chain;

        if  (chainInitialized)
        {
            t_Chain = getChain();
        }
        else
        {
            t_Chain = immutableGetChain(false);
        }

        add(commandHandler, t_Chain);
    }

    /**
     * Adds given command handler to the chain.
     * @param commandHandler the command handler to add.
     * @param chain the chain.
     */
    protected void add(
        @NotNull final CH commandHandler, @NotNull final Chain<C, E, CH> chain)
    {
        if  (!chain.contains(commandHandler))
        {
            chain.add(commandHandler);
        }
    }

    /**
     * Adds given command handler to the first position in the chain.
     * @param commandHandler the command handler to add.
     */
    @SuppressWarnings("unused")
    public void addFirst(@NotNull final CH commandHandler)
    {
        addFirst(commandHandler, immutableGetChainInitialized());
    }

    /**
     * Adds given command handler to the first position in the chain.
     * @param commandHandler the command handler to add.
     * @param chainInitialized whether the chain is initialized.
     */
    protected void addFirst(
        @NotNull final CH commandHandler, final boolean chainInitialized)
    {
        @NotNull final Chain<C, E, CH> t_Chain;

        if  (chainInitialized)
        {
            t_Chain = getChain();
        }
        else
        {
            t_Chain = immutableGetChain(false);
        }

        addFirst(commandHandler, t_Chain);
    }

    /**
     * Adds given command handler to the first position in the chain.
     * @param commandHandler the command handler to add.
     * @param chain the chain.
     */
    protected void addFirst(
        @NotNull final CH commandHandler, @NotNull final Chain<C, E, CH> chain)
    {
        if  (!chain.contains(commandHandler))
        {
            chain.addFirst(commandHandler);
        }
    }

    /**
     * Retrieves the link of the chain just after the one given commandHandler
     * takes.
     * @param commandHandler the handler just before the desired link.
     * @return the next handler in the chain.
     */
    @Override
    @Nullable
    public CH getNextChainLink(@Nullable final CH commandHandler)
    {
        return
            getNextChainLink(
                commandHandler,
                immutableGetChainInitialized());
    }

    /**
     * Retrieves the link of the chain just after the one given commandHandler
     * takes.
     * @param commandHandler the handler just before the desired link.
     * @param chainInitialized whether the chain is already initialized.
     * @return the next handler in the chain.
     */
    @Nullable
    protected CH getNextChainLink(
        @Nullable final CH commandHandler, final boolean chainInitialized)
    {
        @NotNull final Chain<C, E, CH> t_Chain;

        if  (chainInitialized)
        {
            t_Chain = getChain();
        }
        else
        {
            t_Chain = immutableGetChain(false);
        }

        return getNextChainLink(commandHandler, t_Chain);
    }

    /**
     * Retrieves the link of the chain just after the one given commandHandler
     * takes.
     * @param commandHandler the handler just before the desired link.
     * @param chain the chain.
     * @return the next handler in the chain.
     */
    @Nullable
    protected CH getNextChainLink(
        @Nullable final CH commandHandler, @NotNull final Chain<C, E, CH> chain)
    {
        @Nullable final CH result;

        @NotNull final Chain<C, E, CH> t_Chain = chain;

        if  (!t_Chain.isEmpty())
        {
            if  (   (commandHandler == null)
                 || (!t_Chain.contains(commandHandler)))
            {
                result = t_Chain.get(0);
            }
            else
            {
                final int t_iCurrentIndex = t_Chain.indexOf(commandHandler);

                if  (   (t_iCurrentIndex >= 0)
                     && (t_iCurrentIndex < t_Chain.size() - 1))
                {
                    result = t_Chain.get(t_iCurrentIndex + 1);
                }
                else
                {
                    result = null;
                }
            }
        }
        else
        {
            result = null;
        }

        return result;
    }

    /**
     * Sends given command to the chain.
     * @param command the command that represents which actions should be done.
     * @return <code>true</code> if the command is processed by the chain.
     */
    @Override
    public boolean send(@NotNull final C command)
        throws E
    {
        boolean result = true;

        @Nullable CH t_CurrentCommandHandler = null;

        do
        {
            t_CurrentCommandHandler =
                getNextChainLink(t_CurrentCommandHandler);

            if  (t_CurrentCommandHandler != null)
            {
                result = t_CurrentCommandHandler.handle(command);
            }
        }
        while  (   (!result)
                && (t_CurrentCommandHandler != null));

        return result;
    }

    /**
     * Builds the chain, that is, specifies the concrete order in which all
     * handlers are organized.
     */
    protected abstract void buildChain();

    @Override
    public String toString()
    {
        return "ChainOfResponsibilityManager{ chainInitialized=" + m__bChainInitialized + '}';
    }
}
