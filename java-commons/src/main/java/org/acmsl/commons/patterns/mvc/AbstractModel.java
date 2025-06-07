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
 * Filename: Model.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents an information model, so it can be displayed and
 *              modified following the Model-View-Controller architecture.
 *
 */
package org.acmsl.commons.patterns.mvc;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.patterns.Subject;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/**
 * Represents an information model, so it can be displayed and modified
 * following the Model-View-Controller architecture.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public abstract class AbstractModel<C extends Controller>
    extends  Subject<C>
    implements Model<C>
{
    /**
     * To know if it's new or contains information already present in the
     * persistence layer.
     */
    private boolean m__bIsNew = true;

    /**
     * Builds a Model object managed by given controller.
     * @param controller the controller entity.
     */
    protected AbstractModel(@NotNull final C controller)
    {
        attach(controller);
    }

    /**
     * Sets if this model represents new information or not.
     * @param isNew true if so.
     */
    @SuppressWarnings("unused")
    public void isNew(final boolean isNew)
    {
        m__bIsNew = isNew;
    }

    /**
     * Retrieves true if the model contains new information.
     * @return true if so.
     */
    @Override
    @SuppressWarnings("unused")
    public boolean isNew()
    {
        return m__bIsNew;
    }

    @Override
    public String toString()
    {
        return "AbstractModel{" +
               "m__bIsNew=" + m__bIsNew +
               '}';
    }
}
