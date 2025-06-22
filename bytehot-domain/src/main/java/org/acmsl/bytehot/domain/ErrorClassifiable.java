/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ErrorClassifiable.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorClassifiable
 *
 * Responsibilities:
 *   - Define interface for error classification using double dispatch
 *   - Enable polymorphic error type determination without instanceof
 *
 * Collaborators:
 *   - ErrorClassifier: Visitor that performs classification
 */
package org.acmsl.bytehot.domain;

/**
 * Interface for exceptions that can be classified using double dispatch pattern.
 * @author Claude Code
 * @since 2025-06-19
 */
public interface ErrorClassifiable {
    
    /**
     * Accepts an error classifier and returns the appropriate error type.
     * This implements the double dispatch pattern to avoid instanceof chains.
     * @param classifier the error classifier visitor
     * @return the classified error type
     */
    ErrorType acceptClassifier(ErrorClassifier classifier);
    
    /**
     * Accepts an error severity assessor and returns the appropriate severity.
     * This implements the double dispatch pattern to avoid instanceof chains.
     * @param assessor the error severity assessor visitor
     * @return the assessed error severity
     */
    ErrorSeverity acceptSeverityAssessor(ErrorSeverityAssessor assessor);
}