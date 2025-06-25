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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA


    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: StringValidator.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Responsible of checking the validness of strings.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes
 */
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.patterns.Validator;

/*
 * Importing JetBrains annotations.
 */

/**
 * Responsible of checking the validness of strings.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class StringValidator
    implements  Validator,
                Singleton
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class StringValidatorSingletonContainer
    {
        /**
         * The actual singleton.
         */
        
        public static final StringValidator SINGLETON = new StringValidator();
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected StringValidator() {}

    /**
     * Retrieves a StringValidator instance.
     * @return such instance.
     */
    
    public static StringValidator getInstance()
    {
        return StringValidatorSingletonContainer.SINGLETON;
    }

    /**
     * Checks the emptyness of given string.
     * @param value the string to be checked.
     * @return true whenever given value is empty or null.
     */
    public boolean isEmpty(final String value)
    {
        return
            (   (value == null)
             || (value.trim().length() == 0));
    }
}
