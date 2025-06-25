/*
                        ACM-SL Commons

    Copyright (C) 2002-2005  Jose San Leandro Armendariz
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
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: StringUtilsTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Performs some unit tests on StringUtils class.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some JDK classes.
 */
import java.io.File;
import java.util.Locale;

/*
 * Importing JetBrains annotations.
 */

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Performs some unit tests on StringUtils class.
 * @author <a href="mailto:java-commons@acm-sl.org">Jose San Leandro Armendariz</a>
 * @see org.acmsl.commons.utils.StringUtils
 */
@RunWith(JUnit4.class)
public class StringUtilsTest
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * Tests the <code>StringUtils.replace(text, original, replacement)</code>
     * method.
     * @see StringUtils#replace(String,String,String)
     */
    @Test
    public void testReplace()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        Assert.assertEquals(
            "replace(\"com.foo.bar\", \".\", \"-\") failed.",
            "com-foo-bar",
            t_StringUtils.replace("com.foo.bar", ".", "-"));

        Assert.assertEquals(
            "replace(\"com.foo.bar\", \"f\", \"-\") failed.",
            "com.-oo.bar",
            t_StringUtils.replace("com.foo.bar", "f", "-"));
    }

    /**
     * Tests the <code>StringUtils.escapeRegexp(text)</code>
     * method.
     * @see StringUtils#escapeRegexp(String)
     */
    @Test
    public void testEscapeRegexp()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        final String t_strInput = "c$om.(foo)*.bar^";

        Assert.assertEquals(
            "escapeRegexp(\"" + t_strInput + "\") failed.",
            "c\\$om\\.\\(foo\\)\\*\\.bar\\^", // c\$om\.\(foo\)\*\.bar\^
            t_StringUtils.escapeRegexp(t_strInput));
    }

    /**
     * Tests the StringUtils.packageToFilePath(packageName) method.
     * @see StringUtils#packageToFilePath(String)
     */
    @Test
    public void testPackageToFilePath()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        Assert.assertEquals(
            "packageToFilePath(\"com.foo.bar\") failed.",
            "com" + File.separator + "foo" + File.separator + "bar",
            t_StringUtils.packageToFilePath("com.foo.bar"));

        Assert.assertEquals(
            "packageToFilePath(\"com\") failed.",
            "com",
            t_StringUtils.packageToFilePath("com"));

        Assert.assertEquals(
            "packageToFilePath(\"com.\") failed.",
            "com",
            t_StringUtils.packageToFilePath("com."));

        Assert.assertEquals(
            "packageToFilePath(\"com..\") failed.",
            "com",
            t_StringUtils.packageToFilePath("com.."));

        Assert.assertEquals(
            "packageToFilePath(\"..com...\") failed.",
            "com",
            t_StringUtils.packageToFilePath("..com..."));

        Assert.assertEquals(
            "packageToFilePath(\"..com...foo..bar....\") failed.",
            "com" + File.separator + "foo" + File.separator + "bar",
            t_StringUtils.packageToFilePath("..com...foo..bar...."));
    }

    /**
     * Tests the StringUtils.justify(text, margin) method.
     * @see StringUtils#justify(String,int)
     */
    @Test
    public void testJustify()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput1 = "To be justified just after the \"just\"";

        int t_iInput2 = "To be justified just ".length();

        String t_strJustified =
            t_StringUtils.justify(t_strInput1, t_iInput2);

        Assert.assertNotNull(t_strJustified);

        Assert.assertEquals(
            "justify(\"" + t_strInput1 + "\", \"" + t_iInput2 + "\") failed.",
            "To be justified just\nafter the \"just\"",
            t_strJustified);

        t_iInput2 = "To be justified just".length();

        t_strJustified = t_StringUtils.justify(t_strInput1, t_iInput2);

        Assert.assertNotNull(t_strJustified);

        Assert.assertEquals(
            "justify(\"" + t_strInput1 + "\", \"" + t_iInput2 + "\") failed.",
            "To be justified just\nafter the \"just\"",
            t_strJustified);

        t_strInput1 = "To be justified just  after the \"after\"";

        t_iInput2 = "To be justified just  after".length();

        t_strJustified =
            t_StringUtils.justify(t_strInput1, t_iInput2);

        Assert.assertNotNull(t_strJustified);

        Assert.assertEquals(
            "justify(\"" + t_strInput1 + "\", \"" + t_iInput2 + "\") failed.",
            "To be justified just after\nthe \"after\"",
            t_strJustified);
    }

    /**
     * Tests the StringUtils.uncapitalize(text, separator) method.
     * @see StringUtils#unCapitalize(String,String)
     */
    @Test
    public void testUncapitalize()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput = "thisIsATest";

        String t_strUnCapitalized =
            t_StringUtils.unCapitalize(t_strInput, "-");

        Assert.assertEquals(
            "unCapitalize(" + t_strInput + ", \"-\") failed.",
            "this-is-a-test",
            t_strUnCapitalized);

        t_strInput = "thisIsAnotherTest";

        t_strUnCapitalized =
            t_StringUtils.unCapitalize(t_strInput, "-");

        Assert.assertEquals(
            "unCapitalize(" + t_strInput + ", \"-\") failed.",
            "this-is-another-test",
            t_strUnCapitalized);

        t_strInput = "this Is yetAnotherTest";

        t_strUnCapitalized =
            t_StringUtils.unCapitalize(t_strInput, "-");

        Assert.assertEquals(
            "unCapitalize(" + t_strInput + ", \"-\") failed.",
            "this-is-yet-another-test",
            t_strUnCapitalized);
    }

    /**
     * Tests the StringUtils.uncapitalizeStart(text, separator) method.
     * @see StringUtils#unCapitalize(String,String)
     */
    @Test
    public void testUncapitalizeStart()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput = "ThisIsATest";

        String t_strUnCapitalized =
            t_StringUtils.unCapitalizeStart(t_strInput);

        Assert.assertEquals(
            "unCapitalizeStart(" + t_strInput + ", \"-\") failed.",
            "thisIsATest",
            t_strUnCapitalized);

        t_strInput = "ThisIsAnotherTest";

        t_strUnCapitalized =
            t_StringUtils.unCapitalizeStart(t_strInput);

        Assert.assertEquals(
            "unCapitalizeStart(" + t_strInput + ", \"-\") failed.",
            "thisIsAnotherTest",
            t_strUnCapitalized);
    }

    /**
     * Tests the StringUtils.applyToEachLine(text, format) method.
     * @see StringUtils#applyToEachLine(String,String)
     */
    @Test
    public void testApplyToEachLine()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput = " line 1   \n    and line 2";

        String t_strOutput =
            t_StringUtils.applyToEachLine(
                t_strInput, "||{0}{1}//");

        Assert.assertEquals(
            "applyToEachLine(\"" + t_strInput + "\"||{0}{1}//\") failed.",
            "||line 1//\n||   and line 2//\n",
            t_strOutput);

        t_strInput = "    line 1   \n .   and line 2";

        t_strOutput =
            t_StringUtils.applyToEachLine(
                t_strInput, "||{0}{1}//");

        Assert.assertEquals(
            "applyToEachLine(\"" + t_strInput + "\"||{0}{1}//\") failed.",
            "||line 1//\n||.   and line 2//\n",
            t_strOutput);

        t_strInput =
              "      update customers\n" 
            + "      set name = ?\n" 
            + "      where customer_id in\n" 
            + "      (\n" 
            + "        select person_id\n" 
            + "        from people\n" 
            + "        where last_name like '?%'\n" 
            + "      )\n";

        t_strOutput =
            t_StringUtils.applyToEachLine( 
                t_strInput, "{0}|{1}");

        Assert.assertEquals(
            "applyToEachLine(\"" + t_strInput + "\", \"{0}{1}\") failed.",
              "|update customers\n" 
            + "|set name = ?\n" 
            + "|where customer_id in\n" 
            + "|(\n" 
            + "  |select person_id\n" 
            + "  |from people\n" 
            + "  |where last_name like '?%'\n" 
            + "|)\n",
            t_strOutput);
    }

    /**
     * Tests the StringUtils.retrieveMinimumIndentInAllLines(text) method.
     * @see StringUtils#retrieveMinimumIndentInAllLines(String)
     */
    @Test
    public void testRetrieveMinimumIndentInAllLines()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput = " line 1   \n    and line 2";

        int t_iOutput =
            t_StringUtils.retrieveMinimumIndentInAllLines(t_strInput);

        Assert.assertEquals(
            "retrieveMinimumIndentInAllLines(\"" + t_strInput + "\") failed.",
            1,
            t_iOutput);

        t_strInput = "    line 1   \n .   and line 2";

        t_iOutput =
            t_StringUtils.retrieveMinimumIndentInAllLines(t_strInput);

        Assert.assertEquals(
            "retrieveMinimumIndentInAllLines(\"" + t_strInput + "\") failed.",
            1,
            t_iOutput);
    }

    /**
     * Tests the StringUtils.removeFirstAndLastBlankLines(text) method.
     * @see StringUtils#removeFirstAndLastBlankLines(String)
     */
    @Test
    public void testRemoveFirstAndLastBlankLines()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput = " line 1   \n    and line 2";

        String t_strOutput =
            t_StringUtils.removeFirstAndLastBlankLines(t_strInput);

        Assert.assertEquals(
            "removeFirstAndLastBlankLines(\"" + t_strInput + "\") failed.",
            t_strInput,
            t_strOutput);

        t_strInput = " \nline 1   \n    and line 2\n     ";

        t_strOutput =
            t_StringUtils.removeFirstAndLastBlankLines(t_strInput);

        Assert.assertEquals(
            "removeFirstAndLastBlankLines(\"" + t_strInput + "\") failed.",
            "line 1   \n    and line 2\n",
            t_strOutput);

        t_strInput =
            "insert into customers (customer_id, name) values (?, \"test\")";

        t_strOutput =
            t_StringUtils.removeFirstAndLastBlankLines(t_strInput);

        Assert.assertEquals(
            "removeFirstAndLastBlankLines(\"" + t_strInput + "\") failed.",
            t_strInput,
            t_strOutput);
    }

    /**
     * Tests the <code>StringUtils.escape(String, char)</code> method.
     * @see org.acmsl.commons.utils.StringUtils#escape(String, char)
     */
    @Test
    public void testEscape()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        final String t_strInput = "ab ()\" .cde  \" f g ^\\\"";

        final String t_strOutput = t_StringUtils.escape(t_strInput, '\"');

        Assert.assertEquals(
            "escape(\"" + t_strInput + "\") failed.",
            "ab ()\\\" .cde  \\\" f g ^\\\\\"",
            t_strOutput);
    }

    /**
     * Tests the <code>StringUtils.softNormalize()</code>
     * method.
     * @see org.acmsl.commons.utils.StringUtils#softNormalize(String, String)
     */
    @Test
    public void testSoftNormalize()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        String t_strInput;
        t_strInput = "number of    \nbets";

        String t_strOutput = t_StringUtils.softNormalize(t_strInput, "_");

        Assert.assertEquals(
            "softNormalize(\"" + t_strInput + "\") failed.",
            "number_of_bets",
            t_strOutput);

        t_strInput = "number_of_bets";

        t_strOutput = t_StringUtils.softNormalize(t_strInput, "_");

        Assert.assertEquals(
            "softNormalize(\"" + t_strInput + "\") failed.",
            t_strInput,
            t_strOutput);

        t_strInput = "allocate in different clubs";

        t_strOutput = t_StringUtils.softNormalize(t_strInput);

        Assert.assertEquals(
            "softNormalize(\"" + t_strInput + "\") failed.",
            "allocate_in_different_clubs",
            t_strOutput);

        t_strInput = "n-1";

        t_strOutput = t_StringUtils.softNormalize(t_strInput);

        Assert.assertEquals(
            "softNormalize(\"" + t_strInput + "\") failed.",
            "n_1",
            t_strOutput);

        t_strInput = "n_1";

        t_strOutput = t_StringUtils.softNormalize(t_strInput);

        Assert.assertEquals(
            "softNormalize(\"" + t_strInput + "\") failed.",
            "n_1",
            t_strOutput);
    }

    /**
     * Tests the <code>StringUtils.normalize()</code>
     * method.
     * @see org.acmsl.commons.utils.StringUtils#normalize(String, Locale)
     */
    @Test
    public void testNormalize()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        final String t_strInput = "multiple.bet.result";

        final String t_strOutput = t_StringUtils.normalize(t_strInput, Locale.ENGLISH);

        Assert.assertEquals(
            "normalize(\"" + t_strInput + "\") failed.",
            "Multiple_bet_result",
            t_strOutput);
    }

    /**
     * Tests the <code>StringUtils.capitalize(String)</code> method.
     * @see org.acmsl.commons.utils.StringUtils#capitalize(String, Locale)
     */
    @Test
    public void capitalize_works_for_lowercase()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        final String t_strInput = "ab-cd_ef  gh+ijk;;_|l~~@m  2n=o4p";

        final String t_strOutput = t_StringUtils.capitalize(t_strInput, Locale.ENGLISH);

        Assert.assertEquals(
            "capitalize(\"" + t_strInput + "\") failed.",
            "AbCdEfGhIjkLM2nO4p",
            t_strOutput);
    }

    /**
     * Checks whether capitalize() works for upper cased strings.
     */
    @Test
    public void capitalize_works_for_upper_cased_strings()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        Assert.assertEquals("GCycleTypes", t_StringUtils.capitalize("G_CYCLE_TYPES", Locale.ENGLISH));
        Assert.assertEquals("Name", t_StringUtils.capitalize("NAME", Locale.ENGLISH));
    }

    /**
     * Checks whether capitalize() works for mixed-cased strings.
     */
    @Test
    public void capitalize_works_for_mixed_cased_strings()
    {
        final StringUtils t_StringUtils = StringUtils.getInstance();

        Assert.assertNotNull(t_StringUtils);

        Assert.assertEquals("GCycleTypes", t_StringUtils.capitalize("GCycleTypes", Locale.ENGLISH));
    }

    /**
     * Checks whether capitalizeFirst() works.
     */
    @Test
    public void capitalizeFirst_works()
    {
        final StringUtils instance = StringUtils.getInstance();

        Assert.assertNotNull(instance);

        Assert.assertEquals("Abc", instance.capitalizeFirst("abc", Locale.ENGLISH));
    }

    /**
     * Checks whether isEmpty() works for empty strings.
     */
    @Test
    public void isEmpty_works_for_empty_strings()
    {
        final StringUtils instance = StringUtils.getInstance();

        Assert.assertTrue(instance.isEmpty(""));
    }

    /**
     * Checks whether capitalize() preserves camel case.
     */
    @Test
    public void capitalize_preserves_camelCase()
    {
        final StringUtils instance = StringUtils.getInstance();

        Assert.assertEquals(
            "TestWithCamelCaseExample", instance.capitalize("test.with.CamelCase.example", Locale.ENGLISH));
    }

    /**
     * Checks whether unquote() removes leading and trailing quotes.
     */
    @Test
    public void unquote_removes_quotes()
    {
        final StringUtils instance = StringUtils.getInstance();

        Assert.assertEquals("abc", instance.unquote("\"abc\"", '"'));
        Assert.assertEquals("abcd", instance.unquote("'abcd'"));
        Assert.assertEquals(" ", instance.unquote("\" \"", '"'));
        Assert.assertEquals(" ", instance.unquote("' '"));
        Assert.assertEquals(" ", instance.unquote(" "));
    }                            
}
