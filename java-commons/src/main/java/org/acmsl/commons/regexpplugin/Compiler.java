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
 * Filename: Compiler.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents a regexp compiler. Different implementations vary
 *              but they all must respect this set of methods.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * Represents a regexp compiler. Different implementations vary but they all
 * must respect this set of methods.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface Compiler
{
    /**
     * Compiles given regular expression and creates a Pattern object to
     * apply such rule on concrete text contents.
     * @param regexp the regular expression to compile.
     * @return the Pattern associated to such regular expression.
     * @throws MalformedPatternException if given regexp is malformed.
     */
    public Pattern compile(@NotNull final String regexp)
        throws  MalformedPatternException;

    /**
     * Sets whether the compiler should care about case sensitiveness
     * or not.
     * @param caseSensitive true for differentiate upper from lower case.
     */
    public void setCaseSensitive(boolean caseSensitive);

    /**
     * Retrieves if the compiler is configured to be case sensitive or not.
     * @return  true if the compiler differentiates upper from lower case.
     */
    public boolean isCaseSensitive();

    /**
     * Sets whether the compiler should care about new line delimiters
     * or not.
     * @param multiline false for parsing each line at a time.
     */
    public void setMultiline(boolean multiline);

    /**
     * Retrieves if the compiler is configured to care about new line
     * delimiters or not.
     * @return  true if the compiler understands new line delimiters.
     */
    public boolean isMultiline();
}
