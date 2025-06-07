/*
                        ACM S.L. Commons

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
 * Filename: SaxUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some methods on SAX-related classes.
 *
 * Date: 2014/03/03
 * Time: 09:50
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing SAX classes.
 */
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*
 * Importing JDK classes.
 */
import java.io.File;
import java.lang.reflect.Field;

/**
 * Provides some methods on SAX-related classes.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/03/03 09:50
 */
public class SaxUtils
{
    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class SaxUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        @NotNull public static final SaxUtils SINGLETON = new SaxUtils();
    }

    /**
     * Default protected constructor to avoid accidental instantiation.
     */
    protected SaxUtils() {}

    /**
     * Retrieves a ReflectionUtils instance.
     * @return such instance.
     */
    @NotNull
    public static SaxUtils getInstance()
    {
        return SaxUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Retrieves the file causing the exception, if possible.
     * @param exception the {@link SAXException} to process.
     * @return the {@link File}, or {@code null}.
     */
    @Nullable
    public File retrieveFailingFile(@NotNull final SAXParseException exception)
    {
        @Nullable final File result;

        @Nullable String systemId = null;

        try
        {
            @Nullable final Field t_Field = exception.getClass().getDeclaredField("systemId");
            t_Field.setAccessible(true);
            systemId = (String) t_Field.get(exception);

            if (   (systemId != null)
                && (systemId.startsWith("file:")))
            {
                systemId = systemId.substring("file:".length());
            }
        }
        catch (@NotNull final NoSuchFieldException | IllegalAccessException cannotAccessField)
        {
        }

        if (systemId == null)
        {
            result = null;
        }
        else
        {
            result = new File(systemId);
        }

        return result;
    }

}
