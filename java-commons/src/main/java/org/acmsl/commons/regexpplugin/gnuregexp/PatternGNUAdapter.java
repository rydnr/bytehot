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
 * Filename: PatternGNUAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts GNU Regexp 1.1.4 RE objects to follow the
 *              generic Pattern interface defined in this API.
 *
 */
package org.acmsl.commons.regexpplugin.gnuregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Pattern;

/*
 * Importing some GNU Regexp classes.
 */
import gnu.regexp.RE;

/*
 * Importing JetBrains annotations,
 */
import org.jetbrains.annotations.NotNull;

/**
 * Adapts GNU Regexp 1.1.4 RE objects to follow the generic
 * Pattern interface defined in this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class PatternGNUAdapter
    implements  Pattern
{
    /**
     * Delegated instance.
     */
    private RE m__Instance;

    /**
     * Constructs a PatternGNUAdapter for given object.
     * @param adaptee the instance to be adapted.
     */
    public PatternGNUAdapter(@NotNull final RE adaptee)
    {
        immutableSetRE(adaptee);
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    protected final void immutableSetRE(@NotNull final RE adaptee)
    {
        m__Instance = adaptee;
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    @SuppressWarnings("unused")
    protected void setRE(@NotNull final RE adaptee)
    {
        immutableSetRE(adaptee);
    }

    /**
     * Retrieves the adaptee.
     * @return such adapted instance.
     */
    @NotNull
    protected RE getRE()
    {
        return m__Instance;
    }

    /**
     * Retrieves the adapted instance of the RE class.
     * Note: This method has package private access rights.
     * @return such instance.
     */
    @NotNull
    RE getDelegatedInstance()
    {
        return getRE();
    }

    @Override
    public String toString()
    {
        return "PatternGNUAdapter{" +
               " instance=" + m__Instance +
               '}';
    }
}
