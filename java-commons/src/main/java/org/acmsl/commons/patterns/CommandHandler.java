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
 * Filename: CommandHandler.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents objects responsible of receiving commands,
 *              and depending on the command passed, handle it or
 *              pass it to the next handler in the chain.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * Represents objects responsible of receiving {@link Command} instances, and
 * depending on the command passed, handle it or pass it to the next
 * handler in the chain.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface CommandHandler<C extends Command, E extends Exception>
{
    /**
     * Asks the handler to process the command. The idea is that each
     * command handler decides if such command is suitable of being
     * processed, and if so perform the concrete actions the command
     * represents.
     * @param command the command to process (or not).
     * @return <code>true</code> if the handler actually process the command,
     * or maybe because it's not desirable to continue the chain.
     * @throws E in case of error.
     */
    public boolean handle(@NotNull final C command)
        throws E;
}
