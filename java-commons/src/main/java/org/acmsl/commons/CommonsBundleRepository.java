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

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: CommonsBundleRepository.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides the bundles used by ACM-SL Commons.
 *
 */
package org.acmsl.commons;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.patterns.Repository;
import org.acmsl.commons.patterns.Singleton;

/*
 * Importing JetBrains annotations.
 */

/**
 * Provides the bundles used by ACM-SL Commons.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class CommonsBundleRepository
    implements  Repository,
                Singleton
{
    /**
     * The exceptions system property.
     */
    protected static final String EXCEPTIONS_SYSTEM_PROPERTY = "org.acmsl.commons.exceptions";

    /**
     * The exceptions bundle.
     */
    protected static final String EXCEPTIONS_BUNDLE = "commons-exceptions";

    /**
     * The constants bundle.
     */
    protected static final String CONSTANTS_BUNDLE = "commons-constants";

    /**
     * The grammar system property.
     */
    protected static final String GRAMMAR_SYSTEM_PROPERTY = "org.acmsl.commons.utils.grammar";

    /**
     * The grammar bundle.
     */
    protected static final String GRAMMAR_BUNDLE = "commons-grammar";

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class CommonsBundleRepositorySingletonContainer
    {
        /**
         * The actual singleton.
         */
        public static final CommonsBundleRepository SINGLETON =
            new CommonsBundleRepository();
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected CommonsBundleRepository() {}

    /**
     * Retrieves a (the) repository instance.
     * @return such instance.
     */
    
    public static CommonsBundleRepository getInstance()
    {
        return CommonsBundleRepositorySingletonContainer.SINGLETON;
    }

    /**
     * Retrieves the exceptions' system property.
     * @return such property.
     */
    
    public String getExceptionsBundleProperty()
    {
        return EXCEPTIONS_SYSTEM_PROPERTY;
    }

    /**
     * Retrieves the exceptions bundle name.
     * @return such name.
     */
    
    public String getExceptionsBundleName()
    {
        return EXCEPTIONS_BUNDLE;
    }

    /**
     * Retrieves the constants bundle name.
     * @return such name.
     */
    
    public String getConstantsBundleName()
    {
        return CONSTANTS_BUNDLE;
    }

    /**
     * Retrieves the grammar's system property.
     * @return such property.
     */
    
    public String getGrammarBundleProperty()
    {
        return GRAMMAR_SYSTEM_PROPERTY;
    }

    /**
     * Retrieves the grammar bundle name.
     * @return such name.
     */
    
    public String getGrammarBundleName()
    {
        return GRAMMAR_BUNDLE;
    }
}
