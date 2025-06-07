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
 * Filename: MalformedPatternExceptionJDKAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts JDK1.4 malformed pattern exceptions to follow
 *              this API.
 *
 * Last modified by: $Author: chous $ at $Date: 2004-12-01 09:50:27 +0100 (Wed, 01 Dec 2004) $
 *
 * File version: $Revision: 473 $
 *
 * Project version: $Name$
 *                  ("Name" means no concrete version has been checked out)
 *
 * $Id: MalformedPatternExceptionJDKAdapter.java 473 2004-12-01 08:50:27Z chous $
 *
 */
package org.acmsl.commons.regexpplugin.jdk14regexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.MalformedPatternException;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing some JDK1.4 regexp classes.
 */
import java.util.regex.PatternSyntaxException;

/**
 * Adapts JDK1.4's {@link PatternSyntaxException}s to follow this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MalformedPatternExceptionJDKAdapter
    extends  MalformedPatternException
{
    /**
     * The serial vension id.
     */
    private static final long serialVersionUID = -6996833758337854017L;

    /**
     * Private reference to the actual exception.
     */
    private PatternSyntaxException m__Adaptee;

    /**
     * Constructs an adapter for given JDK1.4 exception.
     * @param exception concrete exception instance to adapt.
     */
    public MalformedPatternExceptionJDKAdapter(
        @NotNull final PatternSyntaxException exception)
    {
        super(exception.getMessage());

        immutableSetPatternSyntaxException(exception);
    }

    /**
     * Specifies the adapted exception.
     * @param exception the PatternSyntaxException to adapt.
     */
    protected final void immutableSetPatternSyntaxException(
        @NotNull final PatternSyntaxException exception)
    {
        m__Adaptee = exception;
    }

    /**
     * Specifies the adapted exception.
     * @param exception the PatternSyntaxException to adapt.
     */
    @SuppressWarnings("unused")
    protected void setPatternSyntaxException(
        @NotNull final PatternSyntaxException exception)
    {
        immutableSetPatternSyntaxException(exception);
    }

    /**
     * Retrieves the adapted exception.
     * @return such exception.
     */
    @NotNull
    @SuppressWarnings("unused")
    protected PatternSyntaxException getPatternSyntaxException()
    {
        return m__Adaptee;
    }
}
