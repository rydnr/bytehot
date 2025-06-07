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

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ConfigurationException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents misconfiguration errors.
 *
 */
package org.acmsl.commons;

/*
 * Importing some JDK classes.
 */
import org.jetbrains.annotations.NotNull;

import java.lang.Throwable;

/**
 * Represents misconfiguration errors.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class ConfigurationException
    extends  NonCheckedException
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 8914595828268642575L;

    /**
     * Creates a ConfigurationException with given message.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     */
    public ConfigurationException(
        @NotNull final String messageKey, @NotNull final Object[] params)
    {
        super(messageKey, params);
    }

    /**
     * Creates a ConfigurationException with given cause.
     * @param messageKey the key to build the exception message.
     * @param params the parameters to build the exception message.
     * @param cause the error cause.
     */
    public ConfigurationException(
        @NotNull final String messageKey,
        @NotNull final Object[] params,
        @NotNull final Throwable cause)
    {
        super(messageKey, params, cause);
    }
}
