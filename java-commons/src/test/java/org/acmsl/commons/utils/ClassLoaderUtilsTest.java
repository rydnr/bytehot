/*
                        ACM-SL Commons

    Copyright (C) 2002-2005  Jose San Leandro Armendariz
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
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: ClassLoaderUtilsTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Performs some unit tests on ClassLoaderUtils class.
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing some JDK classes.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Performs some unit tests on StringUtils class.
 * @author <a href="mailto:chous@acm-sl.org"
 *         >Jose San Leandro Armend�riz</a>
 * @version $Revision: 548 $
 * @see org.acmsl.commons.utils.ClassLoaderUtils
 */
@RunWith(JUnit4.class)
public class ClassLoaderUtilsTest
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * Tests the <code>ClassLoaderUtils.findLocation(Class)</code> method.
     * @see org.acmsl.commons.utils.ClassLoaderUtils#findLocation(Class)
     */
    @Test
    public void findLocation_works()
    {
        @NotNull final ClassLoaderUtils classLoaderUtils =
            ClassLoaderUtils.getInstance();
        
        Assert.assertNotNull("getInstance() failed.", classLoaderUtils);

        @Nullable final String location =
            classLoaderUtils.findLocation(String.class);

        Assert.assertNotNull(
            "findLocation(String.class) failed.",
            location);

        // See #161 (http://www.acm-sl.org/issues/161)
//        Assert.assertFalse(
//            "findLocation(String.class) returned an empty string.",
//            "".equals(location));

//        Assert.assertTrue(
//            "findLocation(String.class) failed. (" + location + ") doesn't end with rt.jar",
//            location.endsWith("rt.jar"));
    }

    /**
     * Tests the <code>ClassLoaderUtils.pathContainsClass(String,String)</code>
     * method.
     * @see org.acmsl.commons.utils.ClassLoaderUtils#pathContainsResource(String,String)
     */
    @Test
    public void pathContainsClass_works()
    {
        @NotNull final ClassLoaderUtils classLoaderUtils = ClassLoaderUtils.getInstance();
        
        Assert.assertNotNull(classLoaderUtils);

        try
        {
            @NotNull final File tempFile =
                File.createTempFile(
                    "acmslcommons", "classloaderutilstest.zip");

            @NotNull final OutputStream fileOutputStream = new FileOutputStream(tempFile);

            @NotNull final ZipOutputStream zipOutputStream =
                new ZipOutputStream(fileOutputStream);

            @NotNull final ZipEntry zipEntry = new ZipEntry("test/package/my.class");

            zipOutputStream.putNextEntry(zipEntry);

            zipOutputStream.close();

            fileOutputStream.close();

            @NotNull final boolean pathContainsResource =
                classLoaderUtils.pathContainsResource(
                    tempFile.getAbsolutePath(), "test.package.my", "class");

            tempFile.delete();

        // See #161 (http://www.acm-sl.org/issues/161)
//            Assert.assertTrue(
//                  "pathContainsClass(\""
//                + tempFile.getAbsolutePath()
//                + "\", \"test.package.my\") failed.",
//                pathContainsResource);

        }
        catch  (final Throwable throwable)
        {
            Assert.fail(throwable.getMessage());
        }
    }
}
