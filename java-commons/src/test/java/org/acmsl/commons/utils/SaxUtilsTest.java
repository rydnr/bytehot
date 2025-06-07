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
 * Filename: SaxUtilsTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Checks on SaxUtils.
 *
 * Date: 2014/03/03
 * Time: 09:46
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

/*
 * Importing SAX classes.
 */
import org.xml.sax.SAXParseException;

/*
 * Importing JDK classes.
 */
import java.io.File;
import java.io.IOException;

/**
 * Checks on {@link SaxUtils}.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/03/03 09:46
 */
@RunWith(JUnit4.class)
public class SaxUtilsTest
{
    /**
     * Tests retrieveSystemIdFile()
     * @throws IOException
     */
    @Test
    public void retrieves_the_actual_file_in_a_SAXException()
        throws IOException
    {
        @NotNull final SaxUtils instance = SaxUtils.getInstance();

        @NotNull final File t_File = File.createTempFile("test", "");

        @NotNull final SAXParseException t_Exception =
            new SAXParseException("Fake error", "publicId", t_File.getAbsolutePath(), -1, -1);

        Assert.assertEquals(t_File, instance.retrieveFailingFile(t_Exception));
    }
}
