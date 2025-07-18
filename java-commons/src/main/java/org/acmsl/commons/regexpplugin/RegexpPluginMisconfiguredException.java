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
 * Filename: RegexpPluginMisconfiguredException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Designed to be thrown at runtime when RegexpPlugin is not
 *              properly configured.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.ConfigurationException;

/*
 * Importing JetBrains annotations.
 */

/**
 * Designed to be thrown at runtime when RegexpPlugin is not
 * properly configured.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class RegexpPluginMisconfiguredException
    extends  ConfigurationException
{

    /**
     * The key for this exception in the bundle.
     */
    protected static final String MESSAGE_KEY = "regexpplugin.misconfigured";

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 3584386048849432893L;

    /**
     * Builds a misconfiguration exception with given information.
     * @param detail the detail.
     */
    public RegexpPluginMisconfiguredException(final String detail)
    {
        super(
            MESSAGE_KEY,
            new Object[]
            {
                detail
            });
    }

    /**
     * Builds a misconfiguration exception with given information.
     * @param detail the detail.
     * @param throwable the cause.
     */
    public RegexpPluginMisconfiguredException(
        final String detail, final Throwable throwable)
    {
        super(
            MESSAGE_KEY,
            new Object[]
            {
                MESSAGE_KEY + "." + detail,
                throwable.getMessage(),
                throwable
            });
    }
}
