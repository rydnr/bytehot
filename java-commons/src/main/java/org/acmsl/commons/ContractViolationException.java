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
 * Filename: ContractViolationException.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Thrown whenever the general contract of a method has been
 *              violated.
 *
 */
package org.acmsl.commons;

/*
 * Importing JetBrains annotations..
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing some JDK classes.
 */
import java.io.Serializable;
import java.lang.Exception;

/**
 * Thrown whenever the general contract of a method has been violated.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public class ContractViolationException
    extends     Exception
    implements  Serializable
{
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = -7559538644770345174L;

    /**
     * Builds the exception with the default logic defined in its
     * super class.
     */
    public ContractViolationException() {}

    /**
     * Constructs the exception with given message.
     * @param message the error message.
     */
    public ContractViolationException(@NotNull final String message)
    {
        super(message);
    }
}
