/*
                        ACM-SL Commons

    Copyright (C) 2002-2003  Jose San Leandro Armendariz
                             jsanleandro@yahoo.es
                             chousz@yahoo.com

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
    Contact info: jsr000@terra.es
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: CompilerTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Performs some unit tests on RegexpManager class.
 *
 * Last modified by: $Author: chous $ at $Date: 2006-04-02 12:15:23 +0200 (Sun, 02 Apr 2006) $
 *
 * File version: $Revision: 548 $
 *
 * Project version: $Name$
 *                  ("Name" means no concrete version has been checked out)
 *
 * $Id: CompilerTest.java 548 2006-04-02 10:15:23Z chous $
 *
 */
package org.acmsl.commons.regexpplugin;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.regexpplugin.Compiler;
import org.acmsl.commons.regexpplugin.gnuregexp.CompilerGNUAdapter;
import org.acmsl.commons.regexpplugin.gnuregexp.GNURegexpEngine;
import org.acmsl.commons.regexpplugin.gnuregexp.PatternGNUAdapter;
import org.acmsl.commons.regexpplugin.jakartaoro.ORORegexpEngine;
import org.acmsl.commons.regexpplugin.jakartaoro.PatternOROAdapter;
import org.acmsl.commons.regexpplugin.jakartaoro.Perl5CompilerOROAdapter;
import org.acmsl.commons.regexpplugin.jakartaregexp.CompilerRegexpAdapter;
import org.acmsl.commons.regexpplugin.jakartaregexp.JakartaRegexpEngine;
import org.acmsl.commons.regexpplugin.jakartaregexp.PatternRegexpAdapter;
import org.acmsl.commons.regexpplugin.jdk14regexp.CompilerJDKAdapter;
import org.acmsl.commons.regexpplugin.jdk14regexp.JDKRegexpEngine;
import org.acmsl.commons.regexpplugin.jdk14regexp.PatternJDKAdapter;
import org.acmsl.commons.regexpplugin.Pattern;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.acmsl.commons.regexpplugin.RegexpEngineNotFoundException;
import org.acmsl.commons.regexpplugin.RegexpManager;

/**
 * Importing JUnit classes.
 */
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Performs some unit tests on Compiler class.
 * @see org.acmsl.commons.regexpplugin.RegexpManager
 * @author <a href="mailto:jsanleandro@yahoo.es"
           >Jose San Leandro Armend�riz</a>
 * @version $Revision: 548 $
 */
@RunWith(JUnit4.class)
public class CompilerTest
    implements  org.acmsl.commons.patterns.Test
{
    /**
     * The test pattern.
     */
    public static final String REGEXP = "A*b";

    /**
     * Tests the compiler.compile() method.
     * @see org.acmsl.commons.regexpplugin.Compiler#compile(String)
     */
    @Test
    public void testDefaultCompile()
    {
        try
        {
            final RegexpManager t_RegexpManager = RegexpManager.getInstance();

            Assert.assertNotNull(t_RegexpManager);

            final RegexpEngine t_RegexpEngine = t_RegexpManager.getEngine();

            Assert.assertNotNull(t_RegexpEngine);

            final Compiler t_Compiler = t_RegexpEngine.createCompiler();

            Assert.assertNotNull(t_Compiler);

            final Pattern t_Pattern = t_Compiler.compile(REGEXP);

            Assert.assertNotNull(t_Pattern);
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the compiler.compile() method.
     * @see org.acmsl.commons.regexpplugin.Compiler#compile(String)
     */
    @Test
    public void testJakartaOroCompile()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new ORORegexpEngine();

            final Compiler t_Compiler = t_RegexpEngine.createCompiler();

            Assert.assertTrue(t_Compiler instanceof Perl5CompilerOROAdapter);

            final Pattern t_Pattern = t_Compiler.compile(REGEXP);

            Assert.assertTrue(t_Pattern instanceof PatternOROAdapter);
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the compiler.compile() method.
     * @see org.acmsl.commons.regexpplugin.Compiler#compile(String)
     */
    @Test
    public void testJakartaRegexpCompile()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new JakartaRegexpEngine();

            final Compiler t_Compiler = t_RegexpEngine.createCompiler();

            Assert.assertTrue(t_Compiler instanceof CompilerRegexpAdapter);

            final Pattern t_Pattern = t_Compiler.compile(REGEXP);

            Assert.assertTrue(t_Pattern instanceof PatternRegexpAdapter);
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the compiler.compile() method.
     * @see org.acmsl.commons.regexpplugin.Compiler#compile(String)
     */
    @Test
    public void testJDKCompile()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new JDKRegexpEngine();

            final Compiler t_Compiler = t_RegexpEngine.createCompiler();

            Assert.assertTrue(t_Compiler instanceof CompilerJDKAdapter);

            final Pattern t_Pattern = t_Compiler.compile(REGEXP);

            Assert.assertTrue(t_Pattern instanceof PatternJDKAdapter);
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }

    /**
     * Tests the compiler.compile() method.
     * @see org.acmsl.commons.regexpplugin.Compiler#compile(String)
     */
    @Test
    public void testGNUCompile()
    {
        try
        {
            final RegexpEngine t_RegexpEngine = new GNURegexpEngine();

            final Compiler t_Compiler = t_RegexpEngine.createCompiler();

            Assert.assertTrue(t_Compiler instanceof CompilerGNUAdapter);

            final Pattern t_Pattern = t_Compiler.compile(REGEXP);

            Assert.assertTrue(t_Pattern instanceof PatternGNUAdapter);
        }
        catch  (final Throwable throwable)
        {
            Assert.fail("" + throwable);
        }
    }
}
