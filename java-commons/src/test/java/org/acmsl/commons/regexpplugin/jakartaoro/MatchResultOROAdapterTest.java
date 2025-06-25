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
 * Filename: MatchResultOroAdapterTest.java
 *
 * Author: Jose San Leandro
 *
 * Description: Tests MatchResultOroAdapter.
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing Jakarta ORO classes.
 */
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/*
 * Importing NotNull classes.
 */

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests MatchResultOROAdapterTest class.
 * @see MatchResultOROAdapter
*/
@RunWith(JUnit4.class)
public class MatchResultOROAdapterTest
{
    /**
     * Creates an instance of the tested class.
     * @return such instance.
     */
    public MatchResultOROAdapter createInstance()
        throws Exception
    {
        MatchResultOROAdapter result = null;

        final Perl5Compiler t_Compiler = new Perl5Compiler();

        final Pattern t_Pattern = t_Compiler.compile("^(.*)?\\:(.*)$");

        final Perl5Matcher t_Perl5Matcher = new Perl5Matcher();

        if  (t_Perl5Matcher.contains("one:two", t_Pattern))
        {
            result = new MatchResultOROAdapter(t_Perl5Matcher.getMatch());
        }

        return result;
    }
  
    /**
     * Tests MatchResultOROAdapter.group(int)
     * @throws Exception if an unexpected situation occurs.
     * @see MatchResultOROAdapter#group(int)
     */
    @Test
    public void group_matches_groups()
        throws Exception
    {
        final MatchResultOROAdapter instance = createInstance();

        Assert.assertEquals(instance.group(1), "one");
        Assert.assertEquals(instance.group(2), "two");
    }

    /**
     * Tests MatchResultOROAdapter.group(int)
     * @throws Exception if an unexpected situation occurs.
     * @see MatchResultOROAdapter#group(int)
     */
    @Test
    public void groups_matches_groups()
        throws Exception
    {
        final MatchResultOROAdapter instance = createInstance();

        Assert.assertTrue(
            ((instance.groups() == 2)
             || ((instance.groups() == 3)
                 && (instance.group(3) == null))));
    }
}
