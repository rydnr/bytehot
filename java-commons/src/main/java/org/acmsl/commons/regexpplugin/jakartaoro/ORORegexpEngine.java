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
 * Filename: ORORegexpEngine.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Adapts Jakarta ORO package to be used as a
 *              RegexpPlugin implementation.
 *
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.Helper;
import org.acmsl.commons.regexpplugin.Matcher;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adapts Jakarta ORO package to be used as a RegexpPlugin implementation.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class ORORegexpEngine
    implements RegexpEngine
{
    /**
     * Creates a compiler instance.
     * @return such instance.
     */
    @Nullable
    @NotNull
    @Override
    public Compiler createCompiler()
    {
        return new Perl5CompilerOROAdapter();
    }

    /**
     * Creates a matcher instance.
     * @return such instance.
     */
    @NotNull
    @Override
    public Matcher createMatcher()
    {
        return new Perl5MatcherOROAdapter();
    }

    /**
     * Creates a helper instance.
     * @return such instance.
     */
    @NotNull
    @Override
    public Helper createHelper()
    {
        return new HelperOROAdapter();
    }
}
