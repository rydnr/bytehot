/*
                        QueryJ

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
 * Filename: Model.java
 *
 * Author: Jose San Leandro Armendariz (chous)
 *
 * Description: 
 *
 * Date: 5/30/13
 * Time: 8:03 PM
 *
 */
package org.acmsl.commons.patterns.mvc;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.Observable;


/**
 * Represents an information model, so it can be displayed and modified
 * following the Model-View-Controller architecture.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 * @since 2013/05/30
 */
public interface Model<C extends Controller>
    extends Observable<C>
{
    /**
     * Checks whether the model is new.
     * @return such condition.
     */
    @SuppressWarnings("unused")
    boolean isNew();
}
