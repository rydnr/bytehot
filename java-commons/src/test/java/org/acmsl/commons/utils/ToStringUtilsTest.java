/*
                        Java Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
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
 * Filename: ToStringUtilsTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Defines some tests on ToStringUtils.
 *
 * Date: 2013/11/24
 * Time: 07:18
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing project classes.
 */
import org.acmsl.commons.Literals;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

/*
 * Importing JDK classes.
 */
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines some tests on {@link ToStringUtils}.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2013/11/24 07:18
 */
@RunWith(JUnit4.class)
public class ToStringUtilsTest
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * Dummy class with attributes.
     */
    protected static class Dummy
    {
        /**
         * The fancy name.
         */
        @Nullable private String fancyName;
        /**
         * The fancy age.
         */
        private long fancyAge;
        /**
         * The fancy value.
         */
        private double fancyValue;
        /**
         * The fancy amount.
         */
        @Nullable private BigDecimal fancyAmount;
        /**
         * The fancy date.
         */
        @Nullable private Date fancyDate;
        /**
         * The fancy child.
         */
        @Nullable private Dummy fancyChild;
        /**
         * The other names.
         */
        @Nullable private String[] otherNames;
        /**
         * The other stuff.
         */
        @Nullable private List<String> otherStuff;
        /**
         * The children.
         */
        @Nullable private List<Dummy> children;

        /**
         * {@inheritDoc}
         */
        @NotNull
        @Override
        public String toString()
        {
            @NotNull final Map<String, Object> args = new HashMap<>();
            if (fancyName != null)
            {
                args.put("fancyName", fancyName);
            }
            args.put("fancyAge", fancyAge);
            args.put("fancyValue", fancyValue);
            if (fancyAmount != null)
            {
                args.put("fancyAmount", fancyAmount);
            }
            if (fancyDate != null)
            {
                args.put("fancyDate", fancyDate);
            }

            if (fancyChild != null)
            {
                args.put("fancyChild", fancyChild);
            }
            if (otherNames != null)
            {
                args.put("otherNames", otherNames);
            }
            if (otherStuff != null)
            {
                args.put(Literals.OTHER_STUFF, otherStuff);
            }
            if (children != null)
            {
                args.put("children", children);
            }

            return ToStringUtils.getInstance().toJson(this, Dummy.class, args);
        }
    }

    /**
     * Tests the <code>ToStringUtils.toJson(T, String, Map)</code>
     * method.
     * @see ToStringUtils
     */
    public void to_json_works()
    {
        @NotNull final ToStringUtils t_ToStringUtils = ToStringUtils.getInstance();

        Assert.assertNotNull(t_ToStringUtils);

        @NotNull final Dummy d = new Dummy();
        d.fancyName = "d1";
        d.fancyAge = 15L;
        d.fancyValue = 38D;
        d.fancyAmount = new BigDecimal("13131");
        @NotNull final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 33);
        calendar.set(Calendar.SECOND, 44);
        calendar.set(Calendar.MINUTE, 55);
        d.fancyDate = calendar.getTime();

        @NotNull final String expected =
            normalize(
                "{ \"class\": \"" + Dummy.class.getName() + '"'
                + ", \"fancyAmount\": " + d.fancyAmount
                + ", \"fancyDate\": \"" + new SimpleDateFormat("YYYY-MM-ddHH:mm:ss.SSSZ").format(d.fancyDate) + '"'
                + ", \"fancyValue\": " + d.fancyValue
                + ", \"fancyName\": \"" + d.fancyName + '"'
                + ", \"fancyAge\": " + d.fancyAge + " }");

        @NotNull final String actual = normalize(d.toString());

        Assert.assertEquals("Invalid JSON", expected, actual);

        @NotNull final Dummy otherDummy = new Dummy();
        otherDummy.fancyName = "d1_1";
        otherDummy.fancyAge = 155L;
        otherDummy.fancyValue = 39D;
        otherDummy.fancyAmount = new BigDecimal("5");
        calendar.set(Calendar.YEAR, 2001);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 4);
        calendar.set(Calendar.MINUTE, 5);
        otherDummy.fancyDate = calendar.getTime();
        d.fancyChild = otherDummy;

        @NotNull final String expected2 =
            normalize(
                  "{ \"class\": \"" + Dummy.class.getName() + '"'
                + ", \"fancyAmount\": " + d.fancyAmount
                + ", \"fancyChild\": "
                +   " { \"class\": \"" + Dummy.class.getName() + '"'
                +    ", \"fancyAmount\": " + otherDummy.fancyAmount
                +    ", \"fancyDate\": \"" + new SimpleDateFormat("YYYY-MM-ddHH:mm:ss.SSSZ").format(otherDummy.fancyDate) + '"'
                +    ", \"fancyValue\": " + otherDummy.fancyValue
                +    ", \"fancyName\": \"" + otherDummy.fancyName + '"'
                +    ", \"fancyAge\": " + otherDummy.fancyAge
                +    " } "
                + ", \"fancyDate\": \"" + new SimpleDateFormat("YYYY-MM-ddHH:mm:ss.SSSZ").format(d.fancyDate) + '"'
                + ", \"fancyValue\": " + d.fancyValue
                + ", \"fancyName\": \"" + d.fancyName + '"'
                + ", \"fancyAge\": " + d.fancyAge
                + " }");

        @NotNull final String actual2 = normalize(d.toString());

        Assert.assertEquals("Invalid JSON (2)", expected2, actual2);

        @NotNull final List<Dummy> otherDummies = new ArrayList<Dummy>();
        @NotNull final StringBuilder otherDummiesToString = new StringBuilder();
        for (int i = 0; i < 3; i++)
        {
            @NotNull final Dummy dummy1 = new Dummy();
            dummy1.fancyName = "d1_1_" + i;
            dummy1.fancyAge = 156L + i;
            dummy1.fancyValue = 40D + i;
            dummy1.fancyAmount = new BigDecimal(i);
            calendar.set(Calendar.YEAR, 2001 + i);
            calendar.set(Calendar.MONTH, Calendar.FEBRUARY + i);
            calendar.set(Calendar.DAY_OF_MONTH, 2 + i);
            calendar.set(Calendar.HOUR_OF_DAY, 12 + i);
            calendar.set(Calendar.MINUTE, 3 + i);
            calendar.set(Calendar.SECOND, 4 + i);
            calendar.set(Calendar.MINUTE, 5 + i);
            dummy1.fancyDate = calendar.getTime();
            otherDummies.add(dummy1);
            if (i > 0)
            {
                otherDummiesToString.append(',');
            }
            otherDummiesToString.append(dummy1.toString());
        }

        d.children = otherDummies;

        @NotNull final List<String> otherStuff = new ArrayList<String>(5);
        @NotNull final StringBuilder otherStuffToString = new StringBuilder("\"otherStuff\": [");
        for (int i = 0; i < 5; i++)
        {
            @NotNull final String stuff = "stuff_" + i;
            otherStuff.add(stuff);
            if (i > 0)
            {
                otherStuffToString.append(',');
            }
            otherStuffToString.append('"');
            otherStuffToString.append(stuff);
            otherStuffToString.append('"');
        }
        otherStuffToString.append(']');

        d.otherStuff = otherStuff;

        @NotNull final String aux2 =
            new ToStringUtils.CollectionDecorator<>(
                Literals.OTHER_STUFF, otherStuff)
                .toString();

        Assert.assertEquals(
            "otherStuff failed",
            normalize(otherStuffToString.toString()),
            normalize(aux2));

        @NotNull final String expected3 =
            normalize(
                  "{ \"class\": \"" + Dummy.class.getName() + '"'
                + ", \"fancyAge\": 15"
                + ", \"children\": [" + otherDummiesToString + "]"
                + ", \"otherStuff\": [" + otherStuffToString + "]"
                + ", \"fancyAmount\": 13131"
                + ", \"fancyChild\":"
                +   " { \"class\": \"" + Dummy.class.getName() + '"'
                +    ", \"fancyAmount\": " + otherDummy.fancyAmount
                +    ", \"fancyDate\": \"" + new SimpleDateFormat("YYYY-MM-ddHH:mm:ss.SSSZ").format(otherDummy.fancyDate) + '"'
                +    ", \"fancyValue\": " + otherDummy.fancyValue
                +    ", \"fancyName\": \"" + otherDummy.fancyName + '"'
                +    ", \"fancyAge\": " + otherDummy.fancyAge
                +    " } "
                + ", \"fancyDate\": \"" + new SimpleDateFormat("YYYY-MM-ddHH:mm:ss.SSSZ").format(d.fancyDate) + '"'
                + ", \"fancyValue\": " + d.fancyValue
                + ", \"fancyName\": \"" + d.fancyName + '"'
                + "}");

        @NotNull final String actual3 = normalize(d.toString());

        Assert.assertEquals("Invalid JSON (3)", expected3, actual3);
    }

    /**
     * Normalizes given text.
     * @param text the text to normalize.
     * @return the normalized test.
     */
    @NotNull
    protected String normalize(@NotNull final String text)
    {
        return text.replaceAll("\n", "").replaceAll("\\s*", "");
    }

    /**
     * Checks whether auditToString() detects recursive calls.
     */
    // @Test -> disabled since the forked vm crashes
    public void auditToString_detects_recursive_calls()
    {
        @NotNull final ToStringUtils instance = ToStringUtils.getInstance();

        @NotNull final Dummy tested1 =
            new Dummy()
            {
                /**
                 * Recursive toString().
                 * @return the text representing the state of the instance.
                 */
                @NotNull
                @Override
                public String toString()
                {
                    return "Me, " + instance.auditToString(this);
                }
            };

        Assert.assertEquals("Me, Me, ", tested1.toString());
    }

    /**
     * Checks whether stackTraceContainsToString() detects a recursive call to toString()
     * correctly.
     */
    @Test
    public void stackTraceContainsToString_detects_recursive_calls_to_toString()
    {
        @NotNull final ToStringUtils instance = ToStringUtils.getInstance();

        @NotNull final StackTraceElement[] stackTrace = new StackTraceElement[6];

        stackTrace[0] = new StackTraceElement(String.class.getName(), "toString", "String.java", 33);
        stackTrace[1] = new StackTraceElement(Long.class.getName(), "toString", "Long.java", 37);
        stackTrace[2] = new StackTraceElement(Dummy.class.getName(), "toString", "Dummy.java", 68);
        stackTrace[3] = new StackTraceElement(Float.class.getName(), "toString", "Float.java", 133);
        stackTrace[4] = new StackTraceElement(Integer.class.getName(), "toString", "Integer.java", 192);
        stackTrace[5] = new StackTraceElement(Character.class.getName(), "toString", "Character.java", 115);

        Assert.assertFalse(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, String.class));
        Assert.assertFalse(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, Long.class));
        Assert.assertFalse(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, Dummy.class));
        Assert.assertFalse(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, Double.class));

        stackTrace[3] = new StackTraceElement(String.class.getName(), "toString", "String.java", 33);
        stackTrace[4] = new StackTraceElement(Long.class.getName(), "toString", "Long.java", 37);
        stackTrace[5] = new StackTraceElement(Dummy.class.getName(), "toString", "Dummy.java", 68);

        Assert.assertTrue(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, String.class));
        Assert.assertTrue(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, Long.class));
        Assert.assertTrue(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, Dummy.class));
        Assert.assertFalse(instance.stackTraceContainsRecursiveToStringCalls(stackTrace, Double.class));

    }
}
