//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA


    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: NumericUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when working with numbers.
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.patterns.Utils;

/*
 * Importing JetBrains annotations.
 */

/*
 * Importing some JDK classes.
 */
import java.util.StringTokenizer;

/**
 * Provides some useful methods when working with numbers.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public class NumericUtils
    implements  Utils,
                Singleton
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class NumericUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        public static final NumericUtils SINGLETON = new NumericUtils();
    }

    /**
     * Private constructor to avoid accidental instantiation.
     */
    private NumericUtils() {}

    /**
     * Retrieves a NumericUtils instance.
     * @return such instance.
     */
    
    public static NumericUtils getInstance()
    {
        return NumericUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Checks if given object contains numeric information or not.
     * @param object the object to check if it represents a number.
     * @return true if such object actually is a number.
     */
    @SuppressWarnings("unused")
    public boolean isNumeric(final Object object)
    {
        boolean result = false;

        if  (object != null)
        {
            final String t_strValue = object.toString();

            final StringTokenizer t_strtokFigures =
                new StringTokenizer(t_strValue, "0123456789.,", false);

            result = !t_strtokFigures.hasMoreTokens();
        }

        return result;
    }

    /**
     * Retrieves the maximum between a list of integers.
     * @param numbers the collection of all numbers.
     * @return the maximum number,
     */
    @SuppressWarnings("unused")
    public int getMax(final int[] numbers)
    {
        int result = 0;

        for (int t_iIndex = 0; t_iIndex < numbers.length; t_iIndex++)
        {
            result = Math.max(result, numbers[t_iIndex]);
        }

        return result;
    }

    /**
     * Retrieves the maximum between a list of doubles.
     * @param numbers the collection of all numbers.
     * @return the maximum number,
     */
    @SuppressWarnings("unused")
    public double getMax(final double[] numbers)
    {
        double result = 0.0;

        for (int t_iIndex = 0; t_iIndex < numbers.length; t_iIndex++)
        {
            result = Math.max(result, numbers[t_iIndex]);
        }

        return result;
    }
}
