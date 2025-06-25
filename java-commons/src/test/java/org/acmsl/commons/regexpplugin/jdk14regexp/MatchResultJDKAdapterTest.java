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
 * Filename: MatchResultJDKAdapterTest.java
 *
 * Author: Jose San Leandro
 *
 * Description: Tests MatchResultJDKAdapter.
 */
package org.acmsl.commons.regexpplugin.jdk14regexp;

/*
 * Importing JDK classes.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Tests MatchResultJDKAdapter class.
 * @see MatchResultJDKAdapter
 */
@RunWith(JUnit4.class)
public class MatchResultJDKAdapterTest
{
    /**
     * Creates an instance of the tested class.
     * @return such instance.
     */
    public MatchResultJDKAdapter createInstance()
    {
        final Pattern t_Pattern = Pattern.compile("(.*?):(.*)");

        final Matcher t_Matcher = t_Pattern.matcher("one:two");

        t_Matcher.matches();

        return new MatchResultJDKAdapter(t_Matcher);
    }

    /**
     * Tests whether getMatcher() works.
     * @see MatchResultJDKAdapter#getMatcher()
     */
    @Test
    public void getMatcher_works()
    {
        Assert.assertNotNull(createInstance().getMatcher());
    }

    /**
    * Tests whether group() works.
    * @see MatchResultJDKAdapter#group(int)
    */
    @Test
    public void group_works()
    {
        final MatchResultJDKAdapter instance = createInstance();

        Assert.assertEquals("one", instance.group(1));
        Assert.assertEquals("two", instance.group(2));
    }

    /**
     * Tests whether groups() works.
     * @see MatchResultJDKAdapter#groups()
     */
    @Test
    public void groups_works()
    {
        Assert.assertTrue(createInstance().groups() == 2);
    }
}
