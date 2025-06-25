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
 * Filename: ValueObjectPrimaryKey.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents primary keys that uniquely identifies each
 *              value object.
 *
 */
package org.acmsl.commons.patterns.dao;

/**
 * Importing some JDK classes.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents primary keys that uniquely identifies each {@link ValueObject}.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class ValueObjectPrimaryKey<V>
    implements Iterable<ValueObjectField<V>>
{
    /**
     * Field collection.
     */
    private List<ValueObjectField<V>> m__alFields= new ArrayList<ValueObjectField<V>>();

    /**
     * Adds a field to this primary key.
     * @param field the field to add.
     */
    protected void add(final ValueObjectField<V> field)
    {
        m__alFields.add(field);
    }

    /**
     * Retrieves a list of all the fields included in the PK.
     * @return the iterator to browse the list.
     */
    
    public Iterator<ValueObjectField<V>> iterator()
    {
        return m__alFields.iterator();
    }

    @Override
    public String toString()
    {
        return "ValueObjectPrimaryKey{" +
               "fields=" + m__alFields +
               '}';
    }
}
