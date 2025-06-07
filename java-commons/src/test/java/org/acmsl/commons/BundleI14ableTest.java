/*
                      Project tests

Copyright (C) 2003  Jose San Leandro Armend?riz
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
Urb. Valdecaba?as
Boadilla del monte
28660 Madrid
Spain

******************************************************************************
*
* Filename: $RCSfile$
*
* Author: Jose San Leandro Armend?riz
*
* Description: Executes all tests defined for package
*              unittests.org.acmsl.commons.
*
* Last modified by: $Author: chous $ at $Date: 2006-04-02 12:15:23 +0200 (Sun, 02 Apr 2006) $
*
* File version: $Revision: 548 $
*
* Project version: $Name$
*
* $Id: BundleI14ableTest.java 548 2006-04-02 10:15:23Z chous $
*/
package org.acmsl.commons;

/*
* Importing project classes.
*/
// JUnitDoclet begin import
// JUnitDoclet end import

/*
* Importing JUnit classes.
*/
import junit.framework.TestCase;

/*
This file is part of  JUnitDoclet, a project to generate basic
test cases  from source code and  helping to keep them in sync
during refactoring.

Copyright (C) 2002  ObjectFab GmbH  (http://www.objectfab.de/)

This library is  free software; you can redistribute it and/or
modify  it under the  terms of  the  GNU Lesser General Public
License as published  by the  Free Software Foundation; either
version 2.1  of the  License, or  (at your option)  any  later
version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or  FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You  should  have  received a  copy of the  GNU Lesser General
Public License along with this  library; if not, write  to the
Free  Software  Foundation, Inc.,  59 Temple Place,  Suite 330,
Boston, MA  02111-1307  USA
*/


/**
* Tests BundleI14ableTest class.
* @version $Revision: 548 $
* @see org.acmsl.commons.BundleI14able
*/
public class BundleI14ableTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  org.acmsl.commons.BundleI14able bundlei14able = null;

    public class _BundleI14able
        extends  org.acmsl.commons.BundleI14able
    {
        public _BundleI14able(
            final String key, final Object[] params, final String bundleName)
        {
            super(key, params, "", bundleName);
        }
    }

  // JUnitDoclet end class
  
  /**
  * Creates a BundleI14ableTest with given name.
  * @param name such name.
  */
  public BundleI14ableTest(final String name)
  {
    // JUnitDoclet begin method BundleI14ableTest
    super(name);
    // JUnitDoclet end method BundleI14ableTest
  }
  
  /**
  * Creates an instance of the tested class.
  * @return such instance.
  
  */
  public org.acmsl.commons.BundleI14able createInstance()
  throws Exception
  {
    // JUnitDoclet begin method testcase.createInstance
      return new _BundleI14able("key", new Object[0], "bundle");
    // JUnitDoclet end method testcase.createInstance
  }
  
  /**
  * Performs any required steps before each test.
  * @throws Exception if an unexpected situation occurs.
  */
  protected void setUp()
  throws Exception
  {
    // JUnitDoclet begin method testcase.setUp
    super.setUp();
    bundlei14able = createInstance();
    // JUnitDoclet end method testcase.setUp
  }
  
  /**
  * Performs any required steps after each test.
  * @throws Exception if an unexpected situation occurs.
  */
  protected void tearDown()
  throws Exception
  {
    // JUnitDoclet begin method testcase.tearDown
    bundlei14able = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  /**
  * Tests BundleI14ableTestgetMessageKey()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.BundleI14able#getMessageKey()
  */
  @SuppressWarnings("unused")
  public void testGetMessageKey()
  throws Exception
  {
    // JUnitDoclet begin method getMessageKey
      assertEquals("key", bundlei14able.getMessageKey());
    // JUnitDoclet end method getMessageKey
  }
  
  /**
  * Tests BundleI14able#getParams()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.BundleI14able#getParams()
  */
  @SuppressWarnings("unused")
  public void testGetParams()
  throws Exception
  {
    // JUnitDoclet begin method getParams
      assertNotNull(bundlei14able.getParams());
    // JUnitDoclet end method getParams
  }
  
  /**
  * Tests BundleI14able#getSystemProperty()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.BundleI14able#getSystemProperty()
  */
  @SuppressWarnings("unused")
  public void testGetSystemProperty()
  throws Exception
  {
    // JUnitDoclet begin method getSystemProperty
    // JUnitDoclet end method getSystemProperty
  }
  
  /**
  * Tests BundleI14able#getBundleName()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.BundleI14able#getBundleName()
  */
  @SuppressWarnings("unused")
  public void testGetBundleName()
  throws Exception
  {
    // JUnitDoclet begin method getBundleName
      assertEquals("bundle", bundlei14able.getBundleName());
    // JUnitDoclet end method getBundleName
  }
  
  /**
  * Tests BundleI14ableTest accessor methods.
  * @throws Exception if an unexpected situation occurs.
  */
  @SuppressWarnings("unused")
  public void testSetIsUsingClassLoader()
  throws Exception
  {
    // JUnitDoclet begin method setUsingClassLoader isUsingClassLoader
    final boolean[] t_aTests = {true, false};
    
    for (int t_iTestIndex = 0; t_iTestIndex < t_aTests.length; t_iTestIndex++)
    {
      bundlei14able.setUsingClassLoader(t_aTests[t_iTestIndex]);
      assertEquals(
      t_aTests[t_iTestIndex],
      bundlei14able.isUsingClassLoader());
    }
    // JUnitDoclet end method setUsingClassLoader isUsingClassLoader
  }
  
  /**
  * Tests BundleI14able#toString()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.BundleI14able#toString()
  */
  @SuppressWarnings("unused")
  public void testToString()
  throws Exception
  {
    // JUnitDoclet begin method toString
    // JUnitDoclet end method toString
  }
  
  
  
  /**
  * JUnitDoclet moves marker to this method, if there is not match
  * for them in the regenerated code and if the marker is not empty.
  * This way, no test gets lost when regenerating after renaming.
  * Method testVault is supposed to be empty.
  * @throws Exception if an unexpected situation occurs.
  */
  public void testVault()
  throws Exception
  {
    // JUnitDoclet begin method testcase.testVault
    // JUnitDoclet end method testcase.testVault
  }
  
  public static void main(final String[] args)
  {
    // JUnitDoclet begin method testcase.main
    junit.textui.TestRunner.run(BundleI14ableTest.class);
    // JUnitDoclet end method testcase.main
  }
}
