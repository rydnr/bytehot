/*
                        ACM-SL Commons

    Copyright (C) 2002-2003  Jose San Leandro Armendariz
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
    Contact info: josesanleandro@gmail.com
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: EqualityComparatorTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Performs some unit tests on EqualityComparator class.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.utils.EqualityComparator;

/*
 * Importing some JDK classes.
 */
import java.io.File;

/*
 * Importing JUnit classes.
 */
import junit.framework.TestCase;

/**
 * Performs some unit tests on EqualityComparator class.
 * @author <a href="mailto:chous@acm-sl.org"
           >Jose San Leandro Armendariz</a>
 * @see org.acmsl.commons.utils.EqualityComparator
 */
public class EqualityComparatorTest
    extends     TestCase
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * Constructs a test case with the given name.
     * @param name the test case name.
     */
    public EqualityComparatorTest(String name)
    {
        super(name);
    }

    /**
     * Tests the EqualityComparator.areEqual(int,int) method.
     * @see EqualityComparator#areEqual(int,int)
     */
    public void testAreEqualInt()
    {
        EqualityComparator t_EqualityComparator =
            EqualityComparator.getInstance();

        assertNotNull(t_EqualityComparator);

        assertTrue(!t_EqualityComparator.areEqual(1, 3));

        assertTrue(!t_EqualityComparator.areEqual(-1, 0));

        assertTrue(t_EqualityComparator.areEqual(0, 0));

        assertTrue(t_EqualityComparator.areEqual(400, 400));
    }

    /**
     * Tests the EqualityComparator.areEqual(long,long) method.
     * @see EqualityComparator#areEqual(long,long)
     */
    public void testAreEqualLong()
    {
        EqualityComparator t_EqualityComparator =
            EqualityComparator.getInstance();

        assertNotNull(t_EqualityComparator);

        assertTrue(!t_EqualityComparator.areEqual(1L, 3L));

        assertTrue(!t_EqualityComparator.areEqual(-1L, 0L));

        assertTrue(t_EqualityComparator.areEqual(0L, 0L));

        assertTrue(t_EqualityComparator.areEqual(400L, 400L));
    }

    /**
     * Tests the EqualityComparator.areEqual(float,float) method.
     * @see EqualityComparator#areEqual(float,float)
     */
    public void testAreEqualFloat()
    {
        EqualityComparator t_EqualityComparator =
            EqualityComparator.getInstance();

        assertNotNull(t_EqualityComparator);

        assertTrue(!t_EqualityComparator.areEqual(1.0f, 3.0));

        assertTrue(!t_EqualityComparator.areEqual(-1.0f, 0.0f));

        assertTrue(t_EqualityComparator.areEqual(0.0f, 0.0f));

        assertTrue(t_EqualityComparator.areEqual(400.0f, 400.0f));
    }

    /**
     * Tests the EqualityComparator.areEqual(double,double) method.
     * @see EqualityComparator#areEqual(double,double)
     */
    public void testAreEqualDouble()
    {
        EqualityComparator t_EqualityComparator =
            EqualityComparator.getInstance();

        assertNotNull(t_EqualityComparator);

        assertTrue(!t_EqualityComparator.areEqual(1.0, 3.0));

        assertTrue(!t_EqualityComparator.areEqual(-1.0, 0.0));

        assertTrue(t_EqualityComparator.areEqual(0.0, 0.0));

        assertTrue(t_EqualityComparator.areEqual(400.0, 400.0));
    }

    /**
     * Tests the EqualityComparator.areEqual(Object,Object) method.
     * @see EqualityComparator#areEqual(Object,Object)
     */
    public void testAreEqualObject()
    {
        EqualityComparator t_EqualityComparator =
            EqualityComparator.getInstance();

        assertNotNull(t_EqualityComparator);

        assertTrue(!t_EqualityComparator.areEqual("1.0", "3.0"));

        assertTrue(!t_EqualityComparator.areEqual("-1.0", "0.0"));

        assertTrue(t_EqualityComparator.areEqual("0.0", "0.0"));

        assertTrue(t_EqualityComparator.areEqual("400.0", "400.0"));
    }
}
