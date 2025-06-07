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
 * Filename: PatternJDKAdapter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts JDK1.4 Pattern objects to follow the
 *              generic Pattern interface defined in this API.
 *
 */
package org.acmsl.commons.regexpplugin.jdk14regexp;

/*
 * Importing some Jetbrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing some JDK1.4 classes.
 */
import java.util.regex.Pattern;

/**
 * Adapts JDK1.4 pattern objects to follow the generic
 * {@link Pattern} interface defined in this API.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class PatternJDKAdapter
    implements  org.acmsl.commons.regexpplugin.Pattern
{
    /**
     * Delegated instance.
     */
    private Pattern m__Instance;

    /**
     * Constructs a PatternJDKAdapter for given object.
     * @param adaptee the instance to be adapted.
     */
    public PatternJDKAdapter(@NotNull final Pattern adaptee)
    {
        immutableSetPattern(adaptee);
    }

    /**
     * Specifies the pattern to adapt.
     * Note: This method has package private access rights.
     * @param adaptee the adaptee.
     */
    protected final void immutableSetPattern(@NotNull final Pattern adaptee)
    {
        m__Instance = adaptee;
    }

    /**
     * Specifies the pattern to adapt.
     * Note: This method has package private access rights.
     * @param adaptee the adaptee.
     */
    @SuppressWarnings("unused")
    void setPattern(@NotNull final Pattern adaptee)
    {
        immutableSetPattern(adaptee);
    }

    /**
     * Retrieves the adapted Pattern instance.
     * Note: This method has package private access rights.
     * @return such instance.
     */
    @NotNull
    Pattern getPattern()
    {
        return m__Instance;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "{ 'class': 'PatternJDKAdapter', 'instance': '" + m__Instance + "' }";
    }
}
