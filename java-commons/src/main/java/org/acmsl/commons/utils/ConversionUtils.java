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
 * Filename: ConversionUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Responsible of converting values from one type to another.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes
 */
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.patterns.Singleton;

/*
 * Importing Commons-BeanUtils classes.
 */
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing some JDK classes.
 */
import java.util.Date;
import java.math.BigDecimal;

/**
 * Responsible of converting values from one type to another.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class ConversionUtils
    implements  Utils,
                Singleton
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class ConversionUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        @NotNull public static final ConversionUtils SINGLETON = new ConversionUtils();
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected ConversionUtils() {}

    /**
     * Retrieves a ConversionUtils instance.
     * @return such instance.
     */
    @NotNull
    public static ConversionUtils getInstance()
    {
        return ConversionUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Retrieves the given value (useful for code generators, to avoid
     * type-aware logic, and instead using toXXX(value), where XXX is the type).
     * @param value the value.
     * @return given value.
     */
    @NotNull
    public String toString(@NotNull final String value)
    {
        return toStringIfNotNull(value);
    }

    /**
     * Retrieves the given value (useful for code generators, to avoid
     * type-aware logic, and instead using toXXX(value), where XXX is the type).
     * @param value the value.
     * @return given value.
     */
    @NotNull
    public String toStringIfNotNull(@Nullable final String value)
    {
        return (value != null) ? value : "";
    }

    /**
     * Converts given String to boolean.
     * @param value the value to convert.
     * @return the converted value.
     */
    public boolean toBoolean(@Nullable final String value)
    {
        boolean result = false;

        @Nullable final Boolean t_Result = toBooleanIfNotNull(value);

        if  (t_Result != null) 
        {
            result = t_Result.booleanValue();
        }

        return result;
    }

    /**
     * Converts given String to boolean, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Boolean toBooleanIfNotNull(@Nullable final String value)
    {
        Boolean result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Boolean.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Boolean.TYPE, value);

            if  (t_Result instanceof Boolean)
            {
                result = (Boolean) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to int.
     * @param value the value to convert.
     * @return the converted value.
     */
    public int toInt(@Nullable final String value)
    {
        int result = 0;

        @Nullable final Integer t_Result = toIntIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.intValue();
        }

        return result;
    }

    /**
     * Converts given String to int if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Integer toIntIfNotNull(@Nullable final String value)
    {
        Integer result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Integer.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Integer.TYPE, value);

            if  (t_Result instanceof Integer)
            {
                result = (Integer) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to long.
     * @param value the value to convert.
     * @return the converted value.
     */
    public long toLong(@Nullable final String value)
    {
        long result = 0L;

        @Nullable final Long t_Result = toLongIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.longValue();
        }

        return result;
    }

    /**
     * Converts given String to long if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Long toLongIfNotNull(@Nullable final String value)
    {
        Long result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Long.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Long.TYPE, value);

            if  (t_Result instanceof Long)
            {
                result = (Long) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to float.
     * @param value the value to convert.
     * @return the converted value.
     */
    public float toFloat(@Nullable final String value)
    {
        float result = 0.0f;

        @Nullable final Float t_Result = toFloatIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.floatValue();
        }

        return result;
    }

    /**
     * Converts given String to float, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Float toFloatIfNotNull(@Nullable final String value)
    {
        Float result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Float.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Float.TYPE, value);

            if  (t_Result instanceof Float)
            {
                result = (Float) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to double.
     * @param value the value to convert.
     * @return the converted value.
     */
    public double toDouble(@Nullable final String value)
    {
        double result = 0.0d;

        @Nullable final Double t_Result = toDoubleIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.doubleValue();
        }

        return result;
    }

    /**
     * Converts given String to double, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Double toDoubleIfNotNull(@Nullable final String value)
    {
        Double result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Double.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Double.TYPE, value);

            if  (t_Result instanceof Double)
            {
                result = (Double) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to char.
     * @param value the value to convert.
     * @return the converted value.
     */
    public char toChar(@Nullable final String value)
    {
        char result = (char) -1;

        @Nullable final Character t_Result = toCharIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.charValue();
        }

        if  (   (value == null)
             || (!value.equals(Character.toString(result))))
        {
            result = (char) -1;
        }

        return result;
    }

    /**
     * Converts given String to char, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Character toCharIfNotNull(@Nullable final String value)
    {
        Character result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Character.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Character.TYPE, value);

            if  (t_Result instanceof Character)
            {
                result = (Character) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to short.
     * @param value the value to convert.
     * @return the converted value.
     */
    public short toShort(@Nullable final String value)
    {
        short result = 0;

        @Nullable final Short t_Result = toShortIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.shortValue();
        }

        return result;
    }

    /**
     * Converts given String to short, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Short toShortIfNotNull(@Nullable final String value)
    {
        Short result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Short.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Short.TYPE, value);

            if  (t_Result instanceof Short)
            {
                result = (Short) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to byte.
     * @param value the value to convert.
     * @return the converted value.
     */
    public byte toByte(@Nullable final String value)
    {
        byte result = 0;

        @Nullable final Byte t_Result = toByteIfNotNull(value);

        if  (t_Result != null)
        {
            result = t_Result.byteValue();
        }

        return result;
    }

    /**
     * Converts given String to byte, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Byte toByteIfNotNull(@Nullable final String value)
    {
        Byte result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Byte.TYPE);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Byte.TYPE, value);

            if  (t_Result instanceof Byte)
            {
                result = (Byte) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to bigDecimal.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public BigDecimal toBigDecimal(@Nullable final String value)
    {
        return toBigDecimalIfNotNull(value);
    }

    /**
     * Converts given String to BigDecimal, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public BigDecimal toBigDecimalIfNotNull(@Nullable final String value)
    {
        BigDecimal result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(BigDecimal.class);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(BigDecimal.class, value);

            if  (t_Result instanceof BigDecimal)
            {
                result = (BigDecimal) t_Result;
            }
        }

        return result;
    }

    /**
     * Converts given String to date.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Date toDate(@Nullable final String value)
    {
        return toDateIfNotNull(value);
    }

    /**
     * Converts given String to date, if given value is not null.
     * @param value the value to convert.
     * @return the converted value.
     */
    @Nullable
    public Date toDateIfNotNull(@Nullable final String value)
    {
        Date result = null;

        @Nullable final Converter t_Converter = ConvertUtils.lookup(Date.class);

        if  (t_Converter != null)
        {
            @Nullable final Object t_Result = t_Converter.convert(Date.class, value);

            if  (t_Result instanceof Date)
            {
                result = (Date) t_Result;
            }
        }

        return result;
    }
}
