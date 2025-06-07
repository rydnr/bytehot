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
 * Filename: PatternOROAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts Jakarta ORO's Pattern objects to follow the generic
 *              interfacedefined in this API.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing some Jakarta Oro classes.
 */
import org.apache.oro.text.regex.Pattern;

/**
 * Adapts Jakarta ORO Pattern objects to follow the generic interface defined
 * in this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class PatternOROAdapter
    implements  org.acmsl.commons.regexpplugin.Pattern
{
    /**
     * Delegated instance.
     */
    private Pattern m__Instance;

    /**
     * Constructs a PatternOROAdapter for given object.
     * @param adaptee the instance to be adapted.
     */
    public PatternOROAdapter(final Pattern adaptee)
    {
        immutableSetPattern(adaptee);
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    protected final void immutableSetPattern(final Pattern adaptee)
    {
        m__Instance = adaptee;
    }

    /**
     * Specifies the adaptee.
     * @param adaptee the instance to adapt.
     */
    protected void setPattern(final Pattern adaptee)
    {
        immutableSetPattern(adaptee);
    }

    /**
     * Retrieves an instance of a Perl5Pattern class.
     * Note: Other classes in this package can access this method.
     * @return such instance.
     */
    Pattern getPattern()
    {
        return m__Instance;
    }
}
