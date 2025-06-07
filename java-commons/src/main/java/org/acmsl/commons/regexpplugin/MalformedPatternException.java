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
 * Filename: MalformedPatternException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Models runtime-malformed regular expressions.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing some JDK classes.
 */
import java.io.Serializable;
import java.lang.RuntimeException;

/**
 * Models runtime-malformed regular expressions.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MalformedPatternException
    extends     RuntimeException
    implements  Serializable
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 4064719537415799374L;

    /**
     * Just constructs the exception with the default logic defined in its
     * super class.
     */
    public MalformedPatternException() {}

    /**
     * Constructs the exception with given message.
     * @param message the error message.
     */
    public MalformedPatternException(@NotNull final String message)
    {
        super(message);
    }

    /**
     * Constructs the exception with given message.
     * @param message the error message.
     * @param cause the cause.
     */
    public MalformedPatternException(
        @NotNull final String message, @NotNull final Throwable cause)
    {
        super(message, cause);
    }
}
