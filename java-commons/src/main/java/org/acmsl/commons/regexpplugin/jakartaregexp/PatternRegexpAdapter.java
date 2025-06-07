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
 * Filename: PatternRegexpAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts Jakarta Regexp REProgram objects to follow the
 *              generic Pattern interface defined in this API.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaregexp;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Pattern;

/*
 * Importing some Jakarta Regexp classes.
 */
import org.apache.regexp.RE;
import org.apache.regexp.REProgram;

/**
 * Adapts Jakarta Regexp REProgram objects to follow the generic
 * {@link Pattern} interface defined in this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class PatternRegexpAdapter
    implements  Pattern
{
    /**
     * Delegated instance.
     */
    private REProgram m__Instance;

    /**
     * RE reference needed to pass compiler configuration to the
     * mather.
     */
    private RE m__RE;

    /**
     * Constructs a PatternRegexpAdapter for given object.
     * @param adaptee the instance to be adapted.
     * @param re the required RE instance.
     */
    public PatternRegexpAdapter(final REProgram adaptee, final RE re)
    {
        immutableSetREProgram(adaptee);
        immutableSetRE(re);
    }

    /**
     * Specifies the instance to adapt.
     * @param adaptee the REProgram to be adapted.
     */
    protected final void immutableSetREProgram(final REProgram adaptee)
    {
        m__Instance = adaptee;
    }

    /**
     * Specifies the instance to adapt.
     * @param adaptee the REProgram to be adapted.
     */
    protected void setREProgram(final REProgram adaptee)
    {
        immutableSetREProgram(adaptee);
    }

    /**
     * Retrieves the adapted REProgram instance.
     * @return such instance.
     */
    protected REProgram getREProgram()
    {
        return m__Instance;
    }

    /**
     * Sets the RE reference.
     * @param re such reference.
     */
    protected final void immutableSetRE(final RE re)
    {
        m__RE = re;
    }

    /**
     * Sets the RE reference.
     * @param re such reference.
     */
    void setRE(final RE re)
    {
        immutableSetRE(re);
    }

    /**
     * Retrieves the associated RE reference.
     * Note: Other classes in this package cannot access this method.
     * @return the RE with compiler configuration.
     */
    RE getRE()
    {
        return m__RE;
    }
}
