/*
                        ACM-SL Commons

    Copyright (C) 2002-2003  Jose San Leandro Armendariz
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


    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: josesanleandro@gmail.com
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: StringValidatorTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Performs some unit tests on StringValidator class.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.utils.StringValidator;

/*
 * Importing some JDK classes.
 */
import java.io.File;

/*
 * Importing JUnit classes.
 */
import junit.framework.TestCase;

/**
 * Performs some unit tests on StringValidator class.
 * @author <a href="mailto:chous@acm-sl.org"
           >Jose San Leandro Armendariz</a>
 * @see org.acmsl.commons.utils.StringValidator
 */
public class StringValidatorTest
    extends     TestCase
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * Constructs a test case with the given name.
     * @param name the test case name.
     */
    public StringValidatorTest(String name)
    {
        super(name);
    }

    /**
     * Tests the StringValidator.isEmpty() method.
     * @see StringValidator#isEmpty(String)
     */
    public void testIsEmpty()
    {
        StringValidator t_StringValidator = StringValidator.getInstance();

        assertNotNull(t_StringValidator);

        assertTrue(!t_StringValidator.isEmpty("com.foo.bar"));

        assertTrue(!t_StringValidator.isEmpty("  something "));

        assertTrue(t_StringValidator.isEmpty("   "));

        assertTrue(t_StringValidator.isEmpty(null));
    }
}
