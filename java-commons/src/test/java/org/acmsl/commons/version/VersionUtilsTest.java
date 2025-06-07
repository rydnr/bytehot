/*
                      Project tests

Copyright (C) 2003  Jose San Leandro Armend?riz
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
Urb. Valdecaba?as
Boadilla del monte
28660 Madrid
Spain

******************************************************************************
*
* Filename: VersionUtilsTest.java
*
* Author: Jose San Leandro Armendariz
*
* Description: Executes all tests defined for package
*              org.acmsl.commons.version.
*
* Last modified by: $Author: chous $ at $Date: 2006-04-02 12:15:23 +0200 (Sun, 02 Apr 2006) $
*
*/
package org.acmsl.commons.version;

/*
* Importing JUnit classes.
*/
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

/**
 * Tests VersionUtilsTest class.
 * @see org.acmsl.commons.version.VersionUtils
 */
public class VersionUtilsTest
extends TestCase
{
    private VersionUtils versionutils = null;

    /**
     * Creates a VersionUtilsTest with given name.
     * @param name such name.
     */
    public VersionUtilsTest(@NotNull final String name)
    {
        super(name);
    }

    public static void main(@NotNull final String[] args)
    {
        junit.textui.TestRunner.run(VersionUtilsTest.class);
    }

    /**
     * Creates an instance of the tested class.
     * @return such instance.
     */
    @NotNull
    public VersionUtils createInstance()
    throws Exception
    {
        return VersionUtils.getInstance();
    }

    /**
     * Performs any required steps before each test.
     * @throws Exception if an unexpected situation occurs.
     */
    protected void setUp()
    throws Exception
    {
        super.setUp();
        versionutils = createInstance();
    }

    /**
     * Performs any required steps after each test.
     * @throws Exception if an unexpected situation occurs.
     */
    protected void tearDown()
    throws Exception
    {
        versionutils = null;
        super.tearDown();
    }

    /**
     * Tests VersionUtils#getInstance()
     * @throws Exception if an unexpected situation occurs.
     * @see VersionUtils#getInstance()
     */
    public void testGetInstance()
    throws Exception
    {
          assertNotNull("getInstance() is null", VersionUtils.getInstance());
    }

    /**
     * Tests VersionUtils#matches()
     * @throws Exception if an unexpected situation occurs.
     * @see VersionUtils#matches(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unused")
    public void testMatches()
    throws Exception
    {
        @NotNull final VersionUtils t_VersionUtils = VersionUtils.getInstance();
        assertNotNull("getInstance() is null", t_VersionUtils);

        assertTrue(
            "'4.x' should match '4.3.2'",
            t_VersionUtils.matches("4.3.2", "4.x"));

        assertFalse(
            "'5.x' should not match '2.1'",
            t_VersionUtils.matches("2.1", "5.x"));

        assertTrue(
            "'6.1.x' should match '6.1.3-pre'",
            t_VersionUtils.matches("6.1.3-pre", "6.1.x"));
    }

    /**
    * Tests VersionUtils#versionNumbersMatch()
    * @throws Exception if an unexpected situation occurs.
    * @see VersionUtils#versionNumbersMatch(java.lang.String, java.lang.String)
    */
    @SuppressWarnings("unused")
    public void testVersionNumbersMatch()
    throws Exception
    {
        @NotNull final VersionUtils t_VersionUtils = VersionUtils.getInstance();

        assertNotNull("getInstance() is null", t_VersionUtils);

        assertTrue(
            "'x' should match '4'",
            t_VersionUtils.versionNumbersMatch("4", "x"));

        assertTrue(
            "'5' should match '5'",
            t_VersionUtils.versionNumbersMatch("5", "5"));
    }

    @Override
    public String toString()
    {
        return "VersionUtilsTest{ " +
               "versionutils=" + versionutils +
               " }";
    }
}
