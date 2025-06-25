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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: DataAccessException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents abnormal situations regarding data accessing.
 *
 */
package org.acmsl.commons.patterns.dao;

/*
 * Importing JetBrains annotations.
 */

/**
 * Represents abnormal situations regarding data accessing.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public class DataAccessException
    extends  RuntimeException
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 2013445708507857209L;

    /**
     * Specifies the DAO which throws the error.
     */
    private DAO m__DAO;

    /**
     * Builds a DataAccessException with a certain message.
     * @param message the message.
     * @param dao the DAO entity.
     */
    public DataAccessException(final String message, final DAO dao)
    {
        super(message);
        immutableSetDAO(dao);
    }

    /**
     * Builds a DataAccessException to wrap given one.
     * @param message the message.
     * @param cause the exception to wrap.
     * @param dao the {@link DAO}.
     */
    public DataAccessException(
        final String message, final Throwable cause, final DAO dao)
    {
        super(message, cause);
        immutableSetDAO(dao);
    }

    /**
     * Specifies the DAO entity which throws the error.
     * @param dao the DAO instance.
     */
    private void immutableSetDAO(final DAO dao)
    {
        m__DAO = dao;
    }

    /**
     * Specifies the DAO entity which throws the error.
     * @param dao the DAO instance.
     */
    @SuppressWarnings("unused")
    protected void setDAO(final DAO dao)
    {
        immutableSetDAO(dao);
    }

    /**
     * Retrieves the DAO entity with threw this error.
     * @return such instance.
     */
    
    public DAO getDAO()
    {
        return m__DAO;
    }

    /**
     * Outputs a text representation of this exception.
     * @return the error description.
     */
    @Override
    
    public String toString()
    {
        final StringBuilder t_sbResult = new StringBuilder();

        t_sbResult.append(getMessage());

        final Throwable t_Cause = getCause();

        if  (t_Cause != null) 
        {
            t_sbResult.append(" (");
            t_sbResult.append(t_Cause.getMessage());
            t_sbResult.append(")");
        }

        t_sbResult.append(" [DAO:");
        t_sbResult.append("" + getDAO());
        t_sbResult.append("]");

        return t_sbResult.toString();
    }
}
