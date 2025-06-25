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
 * Filename: RegexpEngineNotFoundException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Designed to be thrown at runtime when the
 *              specified regexp engine is not found.
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.ConfigurationException;

/**
 * Designed to be thrown at runtime when the specified regexp engine
 * is not found.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class RegexpEngineNotFoundException
    extends  ConfigurationException
{

    /**
     * The key for this exception in the bundle.
     */
    protected static final String MESSAGE_KEY = "regexp.engine.not.found";

    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 1546850819042379494L;

    /**
     * Engine name.
     */
    private String m__strEngineClass;

    /**
     * Builds an exception to indicate the regexp engine was not found.
     * @param engineClass the class name of the Regexp implementation whose
     * instantiation has failed.
     */
    public RegexpEngineNotFoundException(final String engineClass)
    {
        super(
            MESSAGE_KEY,
            new Object[]
            {
                engineClass,
            });

        immutableSetEngineClass(engineClass);
    }

    /**
     * Specifies the implementation class name.
     * @param engineClass the implementation class.
     */
    protected final void immutableSetEngineClass(final String engineClass)
    {
        m__strEngineClass = engineClass;
    }

    /**
     * Specifies the implementation class name.
     * @param engineClass the implementation class.
     */
    @SuppressWarnings("unused")
    protected void setEngineClass(final String engineClass)
    {
        immutableSetEngineClass(engineClass);
    }

    /**
     * Retrieves the implementation class name.
     * @return implementation class.
     */
    @SuppressWarnings("unused")
    public String getEngineClass()
    {
        return m__strEngineClass;
    }
}
