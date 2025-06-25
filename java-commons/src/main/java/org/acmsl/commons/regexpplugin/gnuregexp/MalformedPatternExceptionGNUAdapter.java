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
 * Filename: MalformedPatternExceptionGNUAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts GNU Regexp 1.1.4 malformed pattern exceptions
 *              to follow this API.
 *
 */
package org.acmsl.commons.regexpplugin.gnuregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.MalformedPatternException;

/*
 * Importing some GNU Regexp 1.1.4 classes.
 */
import gnu.regexp.REException;

/*
 * Importing JetBrains annotations.
 */

/**
 * Adapts GNU Regexp 1.1.4 malformed pattern exceptions to follow this
 * API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class MalformedPatternExceptionGNUAdapter
    extends MalformedPatternException
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 4223179960140816813L;

    /**
     * Private reference to the actual exception.
     */
    private REException m__Adaptee;

    /**
     * Constructs an adapter for given GNU Regexp 1.1.4 exception.
     * @param exception concrete exception instance to adapt.
     */
    public MalformedPatternExceptionGNUAdapter(final REException exception)
    {
        super(exception.getMessage());

        immutableSetREException(exception);
    }

    /**
     * Specifies the exception to adapt.
     * @param adaptee the instance to adapt.
     */
    protected final void immutableSetREException(final REException adaptee)
    {
        m__Adaptee = adaptee;
    }

    /**
     * Specifies the exception to adapt.
     * @param adaptee the instance to adapt.
     */
    @SuppressWarnings("unused")
    protected void setREException(final REException adaptee)
    {
        immutableSetREException(adaptee);
    }

    /**
     * Retrieves the original exception.
     * @return such exception.
     */
    @SuppressWarnings("unused")
    
    protected REException getREException()
    {    
        return m__Adaptee;
    }
}
