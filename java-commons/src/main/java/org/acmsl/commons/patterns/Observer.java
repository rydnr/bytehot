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
 * Filename: Observer.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents objects that listen for changes in
 *              Observable instances' state.
 *
 * Last modified by: $Author: chous $ at $Date: 2004-09-06 09:05:48 +0200 (Mon, 06 Sep 2004) $
 *
 * File version: $Revision: 397 $
 *
 * Project version: $Name$
 *                  ("Name" means no concrete version has been checked out)
 *
 * $Id: Observer.java 397 2004-09-06 07:05:48Z chous $
 *
 */
package org.acmsl.commons.patterns;

/**
 * Represents objects that listen for changes in {@link Observable}
 * instances' state.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface Observer
{
    /**
     * Invoked by subject objects whenever the need to notify their
     * observers of any change in their state.
     */
    public void update();
}
