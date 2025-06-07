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
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: LongFormatter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Is able to format ValueObjectField.Long objects.
 *
 */
package org.acmsl.commons.patterns.dao;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.Singleton;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * Is able to format {@link ValueObjectField.Long} objects.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class LongFormatter
    implements  ValueObjectFieldFormatter<Long, ValueObjectField<Long>>, Singleton
{
    /**
     * Singleton instance implemented to avoid double-check locking.
     */
    private static class LongFormatterSingletonContainer
    {
        /**
         * The singleton instance.
         */
        @NotNull public static final LongFormatter SINGLETON = new LongFormatter();
    }

    /**
     * Retrieves a LongFormatter instance.
     * @return a long formatter.
     */
    @NotNull
    public static LongFormatter getInstance()
    {
        return LongFormatterSingletonContainer.SINGLETON;
    }

    /**
     * Formats the long field in a correct way.
     * @param longField the field to format.
     * @return the String format.
     */
    @Override
    @NotNull
    public String format(@NotNull final ValueObjectField<Long> longField)
    {
        return longField.getValue() + "";
    }
}
