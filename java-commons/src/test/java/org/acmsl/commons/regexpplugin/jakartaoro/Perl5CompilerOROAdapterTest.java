/*
                      ACM S.L. Java Commons

    Copyright (C) 2013-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: Perl5CompilerOROAdapterTest.java
 *
 * Author: Jose San Leandro
 *
 * Description: Tests Perl5CompilerOROAdapter.
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing NotNull annotations.
 */

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests Perl5CompilerOROAdapter class.
 * @see Perl5CompilerOROAdapter
 */
@RunWith(JUnit4.class)
public class Perl5CompilerOROAdapterTest
{
    /**
     * Creates a new instance.
     * @return such instance.
     */
    
    public Perl5CompilerOROAdapter createInstance()
    {
        return new Perl5CompilerOROAdapter();
    }

    /**
     * Tests whether isCaseSensitive() works.
     */
    @Test
    public void isCaseSensitive_works()
    {
        final Perl5CompilerOROAdapter instance = createInstance();

        final boolean[] t_aTests = {true, false};

        for (int t_iTestIndex = 0; t_iTestIndex < t_aTests.length; t_iTestIndex++)
        {
            instance.setCaseSensitive(t_aTests[t_iTestIndex]);
            Assert.assertEquals(t_aTests[t_iTestIndex], instance.isCaseSensitive());
        }
    }

    /**
     * Tests whether isMultiline() works.
     */
    @Test
    public void isMultiline_works()
    {
        final Perl5CompilerOROAdapter instance = createInstance();

        final boolean[] t_aTests = {true, false};

        for (int t_iTestIndex = 0; t_iTestIndex < t_aTests.length; t_iTestIndex++)
        {
            instance.setMultiline(t_aTests[t_iTestIndex]);
            Assert.assertEquals(t_aTests[t_iTestIndex], instance.isMultiline());
        }
    }
}
