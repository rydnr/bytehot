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
*              org.acmsl.commons.utils.http.
*
* Last modified by: $Author: chous $ at $Date: 2006-04-02 12:15:23 +0200 (Sun, 02 Apr 2006) $
*
* File version: $Revision: 548 $
*
* Project version: $Name$
*
* $Id: HttpServletUtilsTest.java 548 2006-04-02 10:15:23Z chous $
*/
package org.acmsl.commons.utils.http;

/*
* Importing project classes.
*/
// JUnitDoclet begin import
import org.acmsl.commons.utils.http.HttpServletUtils;
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
* Tests HttpServletUtilsTest class.
* @version $Revision: 548 $
* @see org.acmsl.commons.utils.http.HttpServletUtils
*/
public class HttpServletUtilsTest
// JUnitDoclet begin extends_implements
extends TestCase
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  org.acmsl.commons.utils.http.HttpServletUtils httpservletutils = null;
  // JUnitDoclet end class
  
  /**
  * Creates a HttpServletUtilsTest with given name.
  * @param name such name.
  */
  public HttpServletUtilsTest(String name)
  {
    // JUnitDoclet begin method HttpServletUtilsTest
    super(name);
    // JUnitDoclet end method HttpServletUtilsTest
  }
  
  /**
  * Creates an instance of the tested class.
  * @return such instance.
  
  */
  public org.acmsl.commons.utils.http.HttpServletUtils createInstance()
  throws Exception
  {
    // JUnitDoclet begin method testcase.createInstance
    return HttpServletUtils.getInstance();
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
    httpservletutils = createInstance();
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
    httpservletutils = null;
    super.tearDown();
    // JUnitDoclet end method testcase.tearDown
  }
  
  /**
  * Tests HttpServletUtilsTestgetInstance()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#getInstance()
  */
  public void testGetInstance()
  throws Exception
  {
    // JUnitDoclet begin method getInstance
    // JUnitDoclet end method getInstance
  }
  
  /**
  * Tests HttpServletUtilsTesti14e()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#i14e(java.lang.String, java.util.Locale)
  */
  public void testI14e()
  throws Exception
  {
    // JUnitDoclet begin method i14e
    // JUnitDoclet end method i14e
  }
  
  /**
  * Tests HttpServletUtilsTestaddParameter()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#addParameter(java.lang.String, java.lang.String, java.lang.String)
  */
  public void testAddParameter()
  throws Exception
  {
    // JUnitDoclet begin method addParameter
    // JUnitDoclet end method addParameter
  }
  
  /**
  * Tests HttpServletUtilsTestappend()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#append(java.lang.String, java.lang.String)
  */
  public void testAppend()
  throws Exception
  {
    // JUnitDoclet begin method append
    // JUnitDoclet end method append
  }
  
  /**
  * Tests HttpServletUtilsTestgetIntParam()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#getIntParam(javax.servlet.ServletRequest, java.lang.String, int)
  */
  public void testGetIntParam()
  throws Exception
  {
    // JUnitDoclet begin method getIntParam
    // JUnitDoclet end method getIntParam
  }
  
  /**
  * Tests HttpServletUtilsTestgetContextPath()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#getContextPath(javax.servlet.http.HttpServletRequest)
  */
  public void testGetContextPath()
  throws Exception
  {
    // JUnitDoclet begin method getContextPath
    // JUnitDoclet end method getContextPath
  }
  
  /**
  * Tests HttpServletUtilsTestgetServletPath()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#getServletPath(javax.servlet.http.HttpServletRequest)
  */
  public void testGetServletPath()
  throws Exception
  {
    // JUnitDoclet begin method getServletPath
    // JUnitDoclet end method getServletPath
  }
  
  /**
  * Tests HttpServletUtilsTestretrieve()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#retrieve(java.lang.String, javax.servlet.http.HttpServletRequest)
  */
  public void testRetrieve()
  throws Exception
  {
    // JUnitDoclet begin method retrieve
    // JUnitDoclet end method retrieve
  }
  
  /**
  * Tests HttpServletUtilsTeststore()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#store(java.lang.Object, java.lang.String, javax.servlet.ServletRequest, javax.servlet.http.HttpSession)
  */
  public void testStore()
  throws Exception
  {
    // JUnitDoclet begin method store
    // JUnitDoclet end method store
  }
  
  /**
  * Tests HttpServletUtilsTestremove()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#remove(java.lang.String, javax.servlet.ServletRequest, javax.servlet.http.HttpSession, javax.servlet.ServletContext)
  */
  public void testRemove()
  throws Exception
  {
    // JUnitDoclet begin method remove
    // JUnitDoclet end method remove
  }
  
  /**
  * Tests HttpServletUtilsTestparseNameValuePair()
  * @throws Exception if an unexpected situation occurs.
  * @see org.acmsl.commons.utils.http.HttpServletUtils#parseNameValuePair(java.lang.String)
  */
  public void testParseNameValuePair()
  throws Exception
  {
    // JUnitDoclet begin method parseNameValuePair
    // JUnitDoclet end method parseNameValuePair
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
  
  public static void main(String[] args)
  {
    // JUnitDoclet begin method testcase.main
    junit.textui.TestRunner.run(HttpServletUtilsTest.class);
    // JUnitDoclet end method testcase.main
  }
}
