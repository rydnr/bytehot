/*
                        ACM-SL Commons

    Copyright (C) 2002-2003  Jose San Leandro Armendariz
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
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: HelperTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Performs some unit tests on Helper class.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing some ACM-SL Commons classes.
 */
import org.acmsl.commons.regexpplugin.gnuregexp.GNURegexpEngine;
import org.acmsl.commons.regexpplugin.gnuregexp.HelperGNUAdapter;
import org.acmsl.commons.regexpplugin.jakartaoro.HelperOROAdapter;
import org.acmsl.commons.regexpplugin.jakartaoro.ORORegexpEngine;
import org.acmsl.commons.regexpplugin.jakartaregexp.HelperRegexpAdapter;
import org.acmsl.commons.regexpplugin.jakartaregexp.JakartaRegexpEngine;
import org.acmsl.commons.regexpplugin.jdk14regexp.HelperJDKAdapter;
import org.acmsl.commons.regexpplugin.jdk14regexp.JDKRegexpEngine;

/**
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Performs some unit tests on Helper class.
 * @see org.acmsl.commons.regexpplugin.RegexpManager
 * @author <a href="mailto:jsanleandro@yahoo.es"
           >Jose San Leandro Armend�riz</a>
 */
@RunWith(JUnit4.class)
public class HelperTest
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * The input text.
     */
    public static final String INPUT = "This is an untested test.";

    /**
     * The text to replace.
     */
    public static final String TEXT_TO_REPLACE = "n untested";

    /**
     * The replacement text.
     */
    public static final String REPLACEMENT_TEXT = " tested";

    /**
     * The successful replacement result.
     */
    public static final String SUCCESS = "This is a tested test.";

    /**
     * Tests the helper.replaceAll() method with the default engine.
     * @see Helper#replaceAll(String,String,String)
     */
    @Test
    public void replaceAll_works_with_the_default_engine()
    {
        try
        {
            final RegexpManager t_RegexpManager = RegexpManager.getInstance();

            final RegexpEngine t_RegexpEngine = t_RegexpManager.getEngine();

            final Helper t_Helper = t_RegexpEngine.createHelper();

            final String t_strResult =
                t_Helper.replaceAll(INPUT, TEXT_TO_REPLACE, REPLACEMENT_TEXT);

            Assert.assertNotNull(t_strResult);

            Assert.assertTrue(t_strResult.equals(SUCCESS));
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the helper.replaceAll() method with the ORO engine.
     * @see HelperOROAdapter#replaceAll(String,String,String)
     */
    @Test
    public void replaceAll_works_with_the_ORO_engine()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new ORORegexpEngine();

            final Helper t_Helper = t_RegexpEngine.createHelper();

            Assert.assertTrue(t_Helper instanceof HelperOROAdapter);

            final String t_strResult =
                t_Helper.replaceAll(INPUT, TEXT_TO_REPLACE, REPLACEMENT_TEXT);

            Assert.assertTrue(t_strResult.equals(SUCCESS));
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the helper.replaceAll() method with the Jakarta Regexp engine.
     * @see HelperRegexpAdapter#replaceAll(String,String,String)
     */
    @Test
    public void replaceAll_works_with_the_JakartaRegexp_engine()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new JakartaRegexpEngine();

            final Helper t_Helper = t_RegexpEngine.createHelper();

            Assert.assertTrue(t_Helper instanceof HelperRegexpAdapter);

            final String t_strResult =
                t_Helper.replaceAll(INPUT, TEXT_TO_REPLACE, REPLACEMENT_TEXT);

            Assert.assertTrue(t_strResult.equals(SUCCESS));
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the helper.replaceAll() method with the JDK engine.
     * @see HelperJDKAdapter#replaceAll(String,String,String)
     */
    @Test
    public void replaceAll_works_with_the_JDK_engine()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new JDKRegexpEngine();

            final Helper t_Helper = t_RegexpEngine.createHelper();

            Assert.assertTrue(t_Helper instanceof HelperJDKAdapter);

            final String t_strResult =
                t_Helper.replaceAll(INPUT, TEXT_TO_REPLACE, REPLACEMENT_TEXT);

            Assert.assertTrue(t_strResult.equals(SUCCESS));
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the helper.replaceAll() method with the GNU Regexp engine.
     * @see HelperGNUAdapter#replaceAll(String,String,String)
     */
    @Test
    public void replaceAll_works_with_the_GNURegex_engine()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new GNURegexpEngine();

            final Helper t_Helper = t_RegexpEngine.createHelper();

            Assert.assertTrue(t_Helper instanceof HelperGNUAdapter);

            final String t_strResult =
                t_Helper.replaceAll(INPUT, TEXT_TO_REPLACE, REPLACEMENT_TEXT);

            Assert.assertTrue(t_strResult.equals(SUCCESS));
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }
}
