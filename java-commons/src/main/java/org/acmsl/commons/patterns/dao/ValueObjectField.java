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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ValueObjectField.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents any information stored inside a value object.
 *
 */
package org.acmsl.commons.patterns.dao;


/**
 * Represents any information stored inside a {@link ValueObject}.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class ValueObjectField<V>
{
    /**
     * Field name.
     */
    private java.lang.String m__strName;

    /**
     * Formatter.
     */
    private ValueObjectFieldFormatter<V, ValueObjectField<V>> m__Formatter;

    /**
     * The value.
     */
    private V m__Value;

    /**
     * Constructs a field using given name.
     * @param name the name of the field.
     * @param value the value.
     * @param formatter the entity able to format this field correctly.
     */
    public ValueObjectField(
        final java.lang.String name,
        final V value,
        final ValueObjectFieldFormatter<V, ValueObjectField<V>> formatter)
    {
        immutableSetName(name);
        immutableSetValue(value);
        immutableSetFormatter(formatter);
    }

    /**
     * Sets field's name.
     * @param name the name of the field.
     */
    private void immutableSetName(final java.lang.String name)
    {
        m__strName = name;
    }

    /**
     * Sets field's name.
     * @param name the name of the field.
     */
    @SuppressWarnings("unused")
    protected void setName(final java.lang.String name)
    {
        immutableSetName(name);
    }

    /**
     * Retrieves field's name.
     * @return the name of the field.
     */
    
    public java.lang.String getName()
    {
        return m__strName;
    }

    /**
     * Sets the value.
     * @param value the value
     */
    protected final void immutableSetValue(final V value)
    {
        m__Value = value;
    }

    /**
     * Sets the value.
     * @param value the value
     */
    @SuppressWarnings("unused")
    protected void setValue(final V value)
    {
        immutableSetValue(value);
    }

    /**
     * Retrieves the value.
     * @return field's value.
     */
    
    public V getValue()
    {
        return m__Value;
    }

    /**
     * Sets the formatter.
     * @param formatter the formatter itself.
     */
    protected final void immutableSetFormatter(
        final ValueObjectFieldFormatter<V, ValueObjectField<V>> formatter)
    {
        m__Formatter = formatter;
    }

    /**
     * Sets the formatter.
     * @param formatter the formatter itself.
     */
    @SuppressWarnings("unused")
    protected void setFormatter(
        final ValueObjectFieldFormatter<V, ValueObjectField<V>> formatter)
    {
        immutableSetFormatter(formatter);
    }

    /**
     * Retrieves the formatter.
     * @return this field's associated formatter.
     */
    
    public ValueObjectFieldFormatter<V, ValueObjectField<V>> getFormatter()
    {
        return m__Formatter;
    }

    /**
     * Formats the field in a correct way.
     * @return this field in String format.
     */
    @Override
    public java.lang.String toString()
    {
        return "ValueObjectField{" +
               "formatter=" + m__Formatter +
               ", name='" + m__strName + '\'' +
               ", value=" + m__Value +
               '}';
    }

    /**
     * Formats the field in a correct way.
     * @param formatter the formatter.
     * @return this field in String format.
     */
    
    protected java.lang.String toString(final ValueObjectFieldFormatter<V, ValueObjectField<V>> formatter)
    {
        return formatter.format(this);
    }

    /**
     * Represents int fields.
     */
    public static class Int
        extends ValueObjectField<Integer>
    {
        /**
         * Constructs a field using given name.
         * @param name the name of the field.
         * @param value the value.
         */
        public Int(final java.lang.String name, final int value)
        {
            super(name, value, IntFormatter.getInstance());
        }
    }

    /**
     * Represents String fields.
     */
    public static class String
        extends  ValueObjectField<java.lang.String>
    {
        /**
         * Constructs a field using given name and value.
         * @param name the name of the field.
         * @param value field's value.
         */
        public String(
            final java.lang.String name, final java.lang.String value)
        {
            super(name, value, StringFormatter.getInstance());
        }
    }

    /**
     * Represents long fields.
     * @author <a href="mailto:jsanleandro@yahoo.es"
               >Jose San Leandro Armend�riz</a>
     * @version $Revision: 419 $
     */
    public static class Long
        extends  ValueObjectField<java.lang.Long>
    {
        /**
         * Constructs a field using given name and value.
         * @param name the name of the field.
         * @param value field's value.
         */
        public Long(final java.lang.String name, final long value)
        {
            super(name, value, LongFormatter.getInstance());
        }
    }
}
