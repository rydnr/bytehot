/*
                      ACM S.L. Java Commons

    Copyright (C) 2013-today  Jose San Leandro Armendariz
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
 * Filename: HelperOROAdapterTest.java
 *
 * Author: Jose San Leandro
 *
 * Description: Tests HelperOROAdapter.
 */
package org.acmsl.commons.regexpplugin.jakartaoro;

/*
 * Importing NotNull annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
* Tests HelperOROAdapter class.
* @see HelperOROAdapter
*/
@RunWith(JUnit4.class)
public class HelperOROAdapterTest
{
    /**
     * Creates an instance of the tested class.
     * @return such instance.
     */
    public HelperOROAdapter createInstance()
    {
        return new HelperOROAdapter();
    }

    /**
     * Tests whether replaceAll() works.
     * @see HelperOROAdapter#replaceAll(String, String, String)
     */
    @Test
    public void replaceAll_works()
    {
        @NotNull final HelperOROAdapter instance = createInstance();

        Assert.assertNotNull(instance);
        Assert.assertEquals(
            "replaceAll(\"tex\", \"x\", \"sst\") failed.",
            "tesst",
            instance.replaceAll("tex", "x", "sst"));
        Assert.assertEquals(
            "replaceAll(\"text\", \"e\", \".\") failed.",
            "t.xt",
            instance.replaceAll("text", "e", "."));
        Assert.assertEquals(
            "replaceAll(\"text.\", \"\\.\", \"\") failed.",
            "text",
            instance.replaceAll("text.", "\\.", ""));
        Assert.assertEquals(
            "replaceAll(\"" + "simple.text\", \"simple\\.text\", \"textual\") failed.",
            "textual",
            instance.replaceAll("simple.text", "simple\\.text", "textual"));
    }
}
