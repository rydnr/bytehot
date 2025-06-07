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
 * Filename: MalformedPatternExceptionRegexpAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts jakarta ORO malformed pattern exceptions to follow this
 *              API.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.MalformedPatternException;

/*
 * Importing some Jakarta Regexp classes.
 */
import org.apache.regexp.RESyntaxException;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * Adapts jakarta ORO malformed pattern exceptions to follow this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MalformedPatternExceptionRegexpAdapter
    extends  MalformedPatternException
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 932988612842352909L;

    /**
     * Private reference to the actual exception.
     */
    private RESyntaxException m__Adaptee;

    /**
     * Constructs an adapter for given Jakarta Regexp exception.
     * @param exception concrete exception instance to adapt.
     */
    public MalformedPatternExceptionRegexpAdapter(
        @NotNull final RESyntaxException exception)
    {
        super(exception.getMessage());

        immutableSetRESyntaxException(exception);
    }

    /**
     * Specifies the adaptee.
     * @param exception the exception to adapt.
     */
    protected final void immutableSetRESyntaxException(
        @NotNull final RESyntaxException exception)
    {
        m__Adaptee = exception;
    }

    /**
     * Specifies the adaptee.
     * @param exception the exception to adapt.
     */
    @SuppressWarnings("unused")
    protected void setRESyntaxException(@NotNull final RESyntaxException exception)
    {
        immutableSetRESyntaxException(exception);
    }

    /**
     * Retrieves the adapted exception.
     * @return such exception.
     */
    @SuppressWarnings("unused")
    @NotNull
    protected RESyntaxException getRESyntaxException()
    {
        return m__Adaptee;
    }
}
