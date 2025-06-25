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

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: RegexpUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some stateless helper regexp-related services.
 *
 */
package org.acmsl.commons.utils.regexp;

/*
 * Importing some project classes.
 */
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.MalformedPatternException;
import org.acmsl.commons.regexpplugin.Pattern;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.acmsl.commons.regexpplugin.RegexpEngineNotFoundException;
import org.acmsl.commons.regexpplugin.RegexpManager;
import org.acmsl.commons.regexpplugin.RegexpPluginMisconfiguredException;

/*
 * Importing JetBrains annotations.
 */

/**
 * Provides some stateless helper regexp-related services.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class RegexpUtils
    implements  Utils,
                Singleton
{
    /**
     * Compiler used.
     */
    private static Compiler m__Compiler;

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class RegexpUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        
        public static final RegexpUtils SINGLETON = new RegexpUtils();
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected RegexpUtils() {}

    /**
     * Retrieves a RegexpUtils instance.
     * @return such instance.
     * @throws RegexpEngineNotFoundException if the system is not configured
     * properly in order to provide regexp services.
     */
    
    public static RegexpUtils getInstance()
        throws  RegexpEngineNotFoundException
    {
        final RegexpUtils result = RegexpUtilsSingletonContainer.SINGLETON;

        synchronized  (RegexpUtils.class)
        {
            initializeRegexpCompiler();
        }
        
        return result;
    }

    /**
     * Specifies the regexp compiler.
     * @param compiler the compiler.
     */
    protected static final void immutableSetRegexpCompiler(final Compiler compiler)
    {
        m__Compiler = compiler;
    }

    /**
     * Specifies the regexp compiler.
     * @param compiler the compiler.
     */
    @SuppressWarnings("unused")
    protected static void setRegexpCompiler(final Compiler compiler)
    {
        immutableSetRegexpCompiler(compiler);
    }

    /**
     * Retrieves the regexp compiler.
     * @return such compiler.
     */
    
    protected final static Compiler immutableGetRegexpCompiler()
    {
        return m__Compiler;
    }

    /**
     * Retrieves the regexp compiler.
     * @return such compiler.
     */
    
    public static Compiler getRegexpCompiler()
    {
        initializeRegexpCompiler();
        return immutableGetRegexpCompiler();
    }

    /**
     * Initializes the regexp compiler.
     */
    public static void initializeRegexpCompiler()
    {
        Compiler t_Compiler = immutableGetRegexpCompiler();

        if (t_Compiler == null)
        {
            t_Compiler = createCompiler(RegexpManager.getInstance());
            immutableSetRegexpCompiler(t_Compiler);

            t_Compiler.setMultiline(false);
            t_Compiler.setCaseSensitive(false);
        }
    }

    /**
     * Retrieves a compiled pattern for given regexp.
     * @param regexp the regexp to compile.
     * @return the regexp pattern.
     * @throws MalformedPatternException if the pattern is invalid.
     */
    
    public Pattern buildPattern(final String regexp)
      throws  MalformedPatternException
    {
        return buildPattern(regexp, getRegexpCompiler());
    }

    /**
     * Retrieves a compiled pattern for given regexp.
     * @param regexp the regexp to compile.
     * @param compiler the regexp compiler.
     * @return the regexp pattern.
     * @throws MalformedPatternException if the pattern is invalid.
     */
    
    protected Pattern buildPattern(
        final String regexp, final Compiler compiler)
      throws  MalformedPatternException
    {
        return compile(compiler, regexp);
    }

    /**
     * Compiles given pattern.
     * @param compiler the regexp compiler.
     * @param pattern the regexp pattern to compile.
     * @return the compiled pattern.
     * @throws MalformedPatternException if the pattern is invalid.
     */
    
    protected static Pattern compile(
        final Compiler compiler, final String pattern)
      throws  MalformedPatternException
    {
        return compiler.compile(pattern);
    }

    /**
     * Creates the compiler.
     * @return the regexp compiler.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     */
    
    protected static synchronized Compiler createCompiler()
      throws RegexpEngineNotFoundException,
             RegexpPluginMisconfiguredException
    {
        return createCompiler(RegexpManager.getInstance());
    }

    /**
     * Creates the compiler.
     * @param regexpManager the RegexpManager instance.
     * @return the regexp compiler.
     * @throws RegexpEngineNotFoundException if a suitable instance
     * cannot be created.
     * @throws RegexpPluginMisconfiguredException if RegexpPlugin is
     * misconfigured.
     */
    
    protected static synchronized Compiler createCompiler(
        final RegexpManager regexpManager)
      throws RegexpEngineNotFoundException,
             RegexpPluginMisconfiguredException
    {
        return createCompiler(regexpManager.getEngine());
    }

    /**
     * Creates the compiler.
     * @param regexpEngine the RegexpEngine instance.
     * @return the regexp compiler.
     */
    
    protected static synchronized Compiler createCompiler(
        final RegexpEngine regexpEngine)
    {
        return regexpEngine.createCompiler();
    }
}
