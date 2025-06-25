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
 * Filename: ValueObjectDAO.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Data Access Object able to access and retrieve ValueObject
 *              instances.
 *
 */
package org.acmsl.commons.patterns.dao;

/*
 * Importing JetBrains annotations.
 */

/**
 * Data Access Object able to access and retrieve {@link ValueObject} instances.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public interface ValueObjectDAO<VO extends ValueObject>
    extends DAO
{
    /**
     * Loads a value object from the persistence layer with given id.
     * @param pk the identifier used to select the concrete value object to
     * load.
     * @return the information associated to such id in the underlying
     * persistence layer.
     */
    public VO find(final ValueObjectPrimaryKey<String> pk);

    /**
     * Stores the value object into the persistent layer.
     * @param valueObject the information to be persisted.
     * @param reload whether or not the reload is needed.
     * @return true if the operation ends up successfully.
     */
    public VO insert(
        final VO valueObject, final boolean reload);

    /**
     * Removes a value object in the persistence layer.
     * @param valueObject the value object to be removed.
     * @return true if the operation ends up successfully.
     */
    public boolean delete(final VO valueObject);

    /**
     * Removes a value object in the persistence layer.
     * @param valueObject the value object to be removed.
     * @param reload whether or not the reload is needed.
     * @return true if the operation ends up successfully.
     */
    public VO update(
       final VO valueObject, final boolean reload);
}
