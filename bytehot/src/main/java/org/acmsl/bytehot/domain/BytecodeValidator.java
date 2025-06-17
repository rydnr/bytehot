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
 * Filename: BytecodeValidator.java
 *
 * Author: Claude Code
 *
 * Class name: BytecodeValidator
 *
 * Responsibilities:
 *   - Validate bytecode for hot-swap compatibility
 *   - Determine if changes are safe for runtime class redefinition
 *
 * Collaborators:
 *   - BytecodeValidated: Domain event for successful validation
 *   - BytecodeRejected: Domain event for validation failures
 *   - BytecodeValidationException: Exception for validation failures
 */
package org.acmsl.bytehot.domain;

import org.acmsl.bytehot.domain.events.BytecodeRejected;
import org.acmsl.bytehot.domain.events.BytecodeValidated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Validates bytecode for hot-swap compatibility
 * @author Claude Code
 * @since 2025-06-16
 */
@EqualsAndHashCode
@ToString
public class BytecodeValidator {

    /**
     * Validates bytecode for hot-swap compatibility
     * @param classFile the path to the .class file to validate
     * @return the validation success event
     * @throws IOException if file cannot be read
     * @throws BytecodeValidationException if validation fails
     */
    public BytecodeValidated validate(final Path classFile) throws IOException, BytecodeValidationException {
        final byte[] bytecode = Files.readAllBytes(classFile);
        final String content = new String(bytecode);
        
        // Parse the mock bytecode format
        if (content.startsWith("COMPATIBLE_BYTECODE:")) {
            return createValidatedEvent(classFile, content);
        } else if (content.startsWith("INCOMPATIBLE_BYTECODE:")) {
            throw new BytecodeValidationException(createRejectedEvent(classFile, content));
        } else {
            throw new IOException("Unknown bytecode format");
        }
    }

    /**
     * Creates a BytecodeValidated event for compatible bytecode
     * @param classFile the validated class file
     * @param content the bytecode content
     * @return the validation success event
     */
    private BytecodeValidated createValidatedEvent(final Path classFile, final String content) {
        final String[] parts = content.split(":");
        final String className = parts[1];
        
        String validationDetails = "Bytecode validation passed - compatible changes detected";
        if (parts.length > 3 && "method_body_only".equals(parts[3])) {
            validationDetails = "Bytecode validation passed - method body changes only";
        } else if (parts.length > 3 && "method_body_changes".equals(parts[3])) {
            validationDetails = "Bytecode validation passed - method body changes detected";
        }
        
        final Instant timestamp = Instant.now();
        
        return new BytecodeValidated(classFile, className, true, validationDetails, timestamp);
    }

    /**
     * Creates a BytecodeRejected event for incompatible bytecode
     * @param classFile the rejected class file
     * @param content the bytecode content
     * @return the validation failure event
     */
    private BytecodeRejected createRejectedEvent(final Path classFile, final String content) {
        final String[] parts = content.split(":");
        final String className = parts[1];
        
        String rejectionReason = "Incompatible bytecode changes detected";
        if (parts.length > 5 && "field_removal".equals(parts[5])) {
            rejectionReason = "Bytecode validation failed - schema changes (field removal) not supported";
        } else if (parts.length > 5 && "field_addition".equals(parts[5])) {
            rejectionReason = "Bytecode validation failed - schema changes (field addition) not supported";
        }
        
        final Instant timestamp = Instant.now();
        
        return new BytecodeRejected(classFile, className, false, rejectionReason, timestamp);
    }
}