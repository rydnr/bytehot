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
 * Filename: Version.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Is included in all classes in which version
 *              information is susceptible of being requested.
 *
 */
package org.acmsl.commons.version;


/**
 * Is included in all classes in which version information is
 * susceptible of being requested.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class Version
{
    /**
     * Actual version information.
     */
    private String m__strVersionInformation;

    /**
     * Constructs a Version object with given information.
     * @param versionInfo the information about the version.
     */
    public Version(final String versionInfo)
    {
        immutableSetVersionInformation(versionInfo);
    }

    /**
     * Specifies the version information.
     * @param versionInfo the new version.
     */
    private void immutableSetVersionInformation(final String versionInfo)
    {
        m__strVersionInformation = versionInfo;
    }

    /**
     * Specifies the version information.
     * @param versionInfo the new version.
     */
    @SuppressWarnings("unused")
    protected void setVersionInformation(final String versionInfo)
    {
        immutableSetVersionInformation(versionInfo);
    }

    /**
     * Retrieves the version information.
     * @return a detailed description of the version.
     */
    
    public String getVersionInformation()
    {
        return m__strVersionInformation;
    }

    /**
     * {@inheritDoc}
     */
    
    @Override
    public String toString()
    {
        return
              "{"
            + " \"versionInformation\": \"" + this.m__strVersionInformation + '"'
            + ", \"class\": \"" + Version.class.getSimpleName() + '"'
            + ", \"package\": \"" + Version.class.getPackage().getName() + '"'
            + " }";
    }
}
