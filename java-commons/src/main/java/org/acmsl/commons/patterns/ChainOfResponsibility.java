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
 * Filename: ChainOfResponsibility.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents classes that know about the ordering in which
 *              commands are handled by CommandHandler objects, under the
 *              model defined in GoF's Chain Of Responsibility pattern.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing JetBrains annotations.
 */

/**
 * Represents classes that know about the ordering in which {@link Command} instances are
 * handled by {@link CommandHandler} objects, under the model defined in GoF's Chain Of
 * Responsibility design pattern.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface ChainOfResponsibility<C extends Command, E extends Exception, CH extends CommandHandler<C, E>>
{
    /**
     * Retrieves the link of the chain just after the one given commandHandler
     * takes.
     * @param commandHandler the handler just before the desired link.
     * @return the next handler in the chain.
     */
    
    @SuppressWarnings("unused")
    public CH getNextChainLink(final CH commandHandler);
}
