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
 * Filename: I14able.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Represents all classes with multi-language support.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing Checker Framework annotations.
 */
import org.checkerframework.checker.nullness.qual.NonNull;

/*
 * Importing JDK classes.
 */
import java.util.Locale;

/**
 * Represents all classes with multi-language support.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public interface I14able
{
    /**
     * Outputs a localized text about the instance.
     * @param locale the locale.
     * @return such text.
     */
    @NonNull
    public String toString(@NonNull final Locale locale);
}
