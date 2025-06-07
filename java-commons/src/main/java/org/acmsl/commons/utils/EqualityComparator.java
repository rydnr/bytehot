//;-*- mode: java -*-
/*
                        ACM-SL Commons

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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA


    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: EqualityComparator.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Responsible of checking the equality of values or objects.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes
 */
import org.acmsl.commons.patterns.Comparator;
import org.acmsl.commons.patterns.Singleton;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Responsible of checking the equality of values or objects.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class EqualityComparator
    implements  Comparator,
                Singleton
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class EqualityComparatorSingletonContainer
    {
        /**
         * The actual singleton.
         */
        public static final EqualityComparator SINGLETON =
            new EqualityComparator();
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected EqualityComparator() {}

    /**
     * Retrieves an <code>EqualityComparator</code> instance.
     * @return such instance.
     */
    @NotNull
    public static EqualityComparator getInstance()
    {
        return EqualityComparatorSingletonContainer.SINGLETON;
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    public boolean areEqual(final int first, final int second)
    {
        return (first == second);
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    @SuppressWarnings("unused")
    public boolean areEqual(@Nullable final int[] first, @Nullable final int[] second)
    {
        boolean result = false;

        if  (   (first != null)
             && (second != null))
        {
            result = (first.length == second.length);
        }

        if  (result)
        {
            for  (int t_iIndex = 0; t_iIndex < first.length; t_iIndex++)
            {
                result = areEqual(first[t_iIndex], second[t_iIndex]);

                if  (!result)
                {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    public boolean areEqual(final long first, final long second)
    {
        return (first == second);
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    @SuppressWarnings("unused")
    public boolean areEqual(@Nullable final long[] first, @Nullable final long[] second)
    {
        boolean result = false;

        if  (   (first != null)
             && (second != null))
        {
            result = (first.length == second.length);
        }

        if  (result)
        {
            for  (int t_iIndex = 0; t_iIndex < first.length; t_iIndex++)
            {
                result = areEqual(first[t_iIndex], second[t_iIndex]);

                if  (!result)
                {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    public boolean areEqual(final float first, final float second)
    {
        return (first == second);
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    @SuppressWarnings("unused")
    public boolean areEqual(@Nullable final float[] first, @Nullable final float[] second)
    {
        boolean result = false;

        if  (   (first != null)
             && (second != null))
        {
            result = (first.length == second.length);
        }

        if  (result)
        {
            for  (int t_iIndex = 0; t_iIndex < first.length; t_iIndex++)
            {
                result = areEqual(first[t_iIndex], second[t_iIndex]);

                if  (!result)
                {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    public boolean areEqual(final double first, final double second)
    {
        return (first == second);
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    @SuppressWarnings("unused")
    public boolean areEqual(@Nullable final double[] first, @Nullable final double[] second)
    {
        boolean result = false;

        if  (   (first != null)
             && (second != null))
        {
            result = (first.length == second.length);
        }

        if  (result)
        {
            for  (int t_iIndex = 0; t_iIndex < first.length; t_iIndex++)
            {
                result = areEqual(first[t_iIndex], second[t_iIndex]);

                if  (!result)
                {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * Checks if given objects are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    public boolean areEqual(@Nullable final Object first, @Nullable final Object second)
    {
        return
            (   (first == second)
             || (   (first  != null)
                 && (second != null)
                 && (first.equals(second))
                 && (second.equals(first))));
    }

    /**
     * Checks if given values are equal.
     * @param first the first value to compare.
     * @param second the second value to compare.
     * @return <code>true</code> if both values are equal.
     */
    @SuppressWarnings("unused")
    public boolean areEqual(@Nullable final Object[] first, @Nullable final Object[] second)
    {
        boolean result = false;

        if  (   (first != null)
             && (second != null))
        {
            result = (first.length == second.length);
        }

        if  (result)
        {
            for  (int t_iIndex = 0; t_iIndex < first.length; t_iIndex++)
            {
                result = areEqual(first[t_iIndex], second[t_iIndex]);

                if  (!result)
                {
                    break;
                }
            }
        }
        
        return result;
    }

}
