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
 * Filename: MatchResultGNUAdapterTest.java
 *
 * Author: Jose San Leandro
 *
 * Description: Tests MatchResultGNUAdapter.
 */
package org.acmsl.commons.regexpplugin.gnuregexp;

/*
 * Importing GNU Regex classes.
 */
import gnu.regexp.RE;
import gnu.regexp.REMatchEnumeration;

/*
 * Importing NotNull annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
* Tests MatchResultGNUAdapterTest class.
* @see MatchResultGNUAdapter
*/
@RunWith(JUnit4.class)
public class MatchResultGNUAdapterTest
{
    /**
     * Creates an instance of the tested class.
     * @return such instance.
     */
    public MatchResultGNUAdapter createInstance()
        throws Exception
    {
        @NotNull final RE t_RE = new RE("(.*?):(.*)");
        @NotNull final REMatchEnumeration t_REMatchEnumeration = t_RE.getMatchEnumeration("one:two");

        return new MatchResultGNUAdapter(t_REMatchEnumeration.nextMatch(), t_RE.getNumSubs());
    }

    /**
     * Tests MatchResultGNUAdapter#group()
     * @throws Exception if an unexpected situation occurs.
     * @see MatchResultGNUAdapter#group(int)
    */
    @Test
    public void group_works()
        throws Exception
    {
        @NotNull final MatchResultGNUAdapter instance = createInstance();
        Assert.assertEquals(instance.group(1), "one");
        Assert.assertEquals(instance.group(2), "two");
    }

    /**
     * Tests MatchResultGNUAdapter#groups()
     * @throws Exception if an unexpected situation occurs.
     * @see MatchResultGNUAdapter#groups()
     */
    @Test
    public void groups_works()
        throws Exception
    {
        Assert.assertTrue(createInstance().groups() == 2);
    }
}
