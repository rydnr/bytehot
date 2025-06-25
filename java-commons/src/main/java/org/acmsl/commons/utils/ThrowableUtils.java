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


    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ThrowableUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when working with throwable
 *              instances.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.patterns.Singleton;

/*
 * Importing JDK1.3 classes.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Throwable;

/*
 * Importing commons-logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */

/**
 * Provides some useful methods when working with throwable instances.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public class ThrowableUtils
    implements  Utils,
                Singleton
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class ThrowableUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        
        public static final ThrowableUtils SINGLETON = new ThrowableUtils();
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected ThrowableUtils() {}

    /**
     * Retrieves a ThrowableUtils instance.
     * @return such instance.
     */
    
    public static ThrowableUtils getInstance()
    {
        return ThrowableUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Retrieves given throwable's stack trace.
     * @param throwable the error to output.
     * @return its stack trace.
     */
    @SuppressWarnings("unused")
    
    public String getStackTrace(final Throwable throwable)
    {
        final String result;

        final StringWriter t_swResult = new StringWriter();

        final PrintWriter t_PrintWriter = new PrintWriter(t_swResult);

        throwable.printStackTrace(t_PrintWriter);

        result = t_swResult.toString();

        t_PrintWriter.close();

        try
        {
            t_swResult.close();
        }
        catch  (final IOException ioException)
        {
            LogFactory.getLog(ThrowableUtils.class).fatal(
                "Strange and exceptional error",
                ioException);
        }

        return result;
    }
}
