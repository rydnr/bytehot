/*
                        ACM-SL Commons

    Copyright (C) 2002-2003  Jose San Leandro Armend�riz
                             jsanleandro@yahoo.es
                             chousz@yahoo.com

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


    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jsr000@terra.es
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecaba�as
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: $RCSfile$
 *
 * Author: Jose San Leandro Armend�riz
 *
 * Description: Performs some unit tests on URLUtils class.
 *
 * File version: $Revision: 548 $
 *
 * Project version: $Name$
 *                  ("Name" means no concrete version has been checked out)
 *
 * $Id: URLUtilsTest.java 548 2006-04-02 10:15:23Z chous $
 *
 */
package org.acmsl.commons.utils.net;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.utils.net.URLUtils;

/*
 * Importing JUnit classes.
 */
import junit.framework.TestCase;

/**
 * Performs some unit tests on URLUtils class.
 * @testfamily JUnit
 * @testkind testcase
 * @testsetup Default TestCase
 * @testedclass org.acmsl.commons.utils.net.URLUtils
 * @see org.acmsl.commons.utils.net.URLUtils
 * @author <a href="mailto:jsanleandro@yahoo.es"
           >Jose San Leandro Armend�riz</a>
 * @version $Revision: 548 $
 */
public class URLUtilsTest
    extends     TestCase
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * Constructs a test case with the given name.
     * @param name the test case name.
     */
    public URLUtilsTest(String name)
    {
        super(name);
    }

    /**
     * Executes the tests.
     */
    protected void runTest()
    {
        testIsMultiple();
        testValuePresent();
    }

    /**
     * Tests the URLUtils.isMultiple(String, String) method.
     * @see URLUtils#isMultiple(String, String)
     */
    public void testIsMultiple()
    {
        URLUtils t_URLUtils = URLUtils.getInstance();

        assertNotNull(t_URLUtils);

        assertTrue(t_URLUtils.isMultiple("abc?a=4&a=4", "a"));

        assertTrue(!t_URLUtils.isMultiple("abc?a=4", "a"));

        assertTrue(t_URLUtils.isMultiple("abc?a=4&b=3&a=4", "a"));

        assertTrue(t_URLUtils.isMultiple("a=4&b=3&a=3", "a"));

        assertTrue(!t_URLUtils.isMultiple("a=4&b=3&a=4", "b"));

        assertTrue(t_URLUtils.isMultiple("??a=4&b=3&a=4", "a"));
    }

    /**
     * Tests the URLUtils.valuePresent(String, String) method.
     */
    public void testValuePresent()
    {
        URLUtils t_URLUtils = URLUtils.getInstance();

        assertNotNull(t_URLUtils);

        assertTrue(t_URLUtils.valuePresent("abc?a=4", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("abc?a=4&b=3", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("a=4", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("?a=4", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("abc?b=3&a=4", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("abc?b=3&a=4&a=5", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("abc?b=3&a=4&a=4", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("abc?b=3&a=4&c=3&a=5", "a", "4"));

        assertTrue(t_URLUtils.valuePresent("abc?b=3&a=3&c=3&a=4", "a", "4"));
    }

    /**
     * Tests the URLUtils.putParamInQueryString(String, String, String) method.
     * @see URLUtils#valuePresent(String, String, String)
     */
    public void testPutParamInQueryString()
    {
        URLUtils t_URLUtils = URLUtils.getInstance();

        assertNotNull(t_URLUtils);

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "www.foo.bar/search?a=1", "a", "2")
                    .equals("www.foo.bar/search?a=2"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "www.foo.bar/search?a=1&b=1", "a", "2")
                    .equals("www.foo.bar/search?a=2&b=1"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "www.foo.bar/search?b=1&a=1", "a", "2")
                    .equals("www.foo.bar/search?b=1&a=2"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "www.foo.bar/search?a=1&a=2", "a", "3")
                    .equals("www.foo.bar/search?a=1&a=2&a=3"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "a=1&a=2", "a", "2")
                    .equals("?a=1&a=2"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "a=1&a=2", "a", "3")
                    .equals("?a=1&a=2&a=3"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "?a=1&a=2&b=45435", "a", "3")
                    .equals("?a=1&a=2&b=45435&a=3"));

        assertTrue(
            t_URLUtils.putParamInQueryString(
                "www.foo.bar/search?a=1&a=2", "a", "2")
                    .equals("www.foo.bar/search?a=1&a=2"));
    }
}
