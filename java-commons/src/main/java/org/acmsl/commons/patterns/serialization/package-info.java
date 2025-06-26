/*
                        ACM-SL Commons

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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * JSON serialization framework for ACM-SL Commons.
 *
 * This package provides a comprehensive JSON marshalling and unmarshalling framework
 * with support for:
 * 
 * - Type-safe JSON serialization/deserialization
 * - Configurable serialization behavior  
 * - EventSourcing-optimized configurations
 * - Bug report generation support
 * - Collection and Map handling
 * - Temporal type support with Java 8 Time API
 * - Custom error handling with detailed context
 * 
 * Main components:
 * - {@link org.acmsl.commons.patterns.serialization.JsonMarshaller}: Main interface for JSON operations
 * - {@link org.acmsl.commons.patterns.serialization.JsonMarshallerFactory}: Factory for creating marshallers
 * - {@link org.acmsl.commons.patterns.serialization.JsonSerializationConfig}: Configuration options
 * - {@link org.acmsl.commons.patterns.serialization.JacksonJsonMarshaller}: Jackson-based implementation
 * - {@link org.acmsl.commons.patterns.serialization.SerializationException}: Error handling
 *
 * Example usage:
 * <pre>{@code
 * // Create a marshaller with default configuration
 * JsonMarshaller marshaller = JsonMarshallerFactory.createDefault();
 * 
 * // Marshall an object to JSON
 * String json = marshaller.marshall(myObject);
 * 
 * // Unmarshall JSON back to object
 * MyClass restored = marshaller.unmarshall(json, MyClass.class);
 * 
 * // Use EventSourcing-optimized configuration
 * JsonMarshaller eventMarshaller = JsonMarshallerFactory.createForEventSourcing();
 * }</pre>
 *
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
package org.acmsl.commons.patterns.serialization;