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
 * Filename: DocumentationRequested.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationRequested
 *
 * Responsibilities:
 *   - Represent when a component requests its documentation URL
 *   - Capture context about the documentation request
 *   - Enable documentation usage tracking and analysis
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - DocumentationType: Specifies the type of documentation requested
 *   - Flow: Optional flow context for contextual documentation
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.DocumentationType;
import org.acmsl.bytehot.domain.EventMetadata;
import org.acmsl.bytehot.domain.Flow;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Optional;

/**
 * Event triggered when a component requests its documentation URL.
 * This event enables tracking of documentation usage patterns and provides
 * context for generating appropriate documentation links.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class DocumentationRequested 
    extends AbstractVersionedDomainEvent {

    /**
     * The class requesting documentation.
     */
    @Getter
    private final Class<?> requestingClass;

    /**
     * Optional method name for method-specific documentation.
     */
    @Getter
    private final Optional<String> methodName;

    /**
     * Type of documentation being requested.
     */
    @Getter
    private final DocumentationType documentationType;

    /**
     * Optional explicit flow context for contextual documentation.
     */
    @Getter
    private final Optional<Flow> explicitFlowContext;

    /**
     * ID of the user requesting documentation.
     */
    @Getter
    private final String requestingUserId;

    /**
     * Timestamp when the documentation was requested.
     */
    @Getter
    private final Instant requestedAt;

    /**
     * Creates a new DocumentationRequested event.
     * @param metadata event metadata
     * @param requestingClass the class requesting documentation
     * @param methodName optional method name for method-specific documentation
     * @param documentationType type of documentation being requested
     * @param explicitFlowContext optional explicit flow context
     * @param requestingUserId ID of the user requesting documentation
     * @param requestedAt timestamp when the documentation was requested
     */
    public DocumentationRequested(
        final EventMetadata metadata,
        final Class<?> requestingClass,
        final Optional<String> methodName,
        final DocumentationType documentationType,
        final Optional<Flow> explicitFlowContext,
        final String requestingUserId,
        final Instant requestedAt
    ) {
        super(metadata);
        this.requestingClass = requestingClass;
        this.methodName = methodName;
        this.documentationType = documentationType;
        this.explicitFlowContext = explicitFlowContext;
        this.requestingUserId = requestingUserId;
        this.requestedAt = requestedAt;
    }

    /**
     * Factory method for creating a basic class documentation request.
     * @param requestingClass the class requesting documentation
     * @param requestingUserId ID of the user requesting documentation
     * @return new DocumentationRequested event
     */
    public static DocumentationRequested forClass(
        final Class<?> requestingClass,
        final String requestingUserId
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "documentation", 
            requestingClass.getSimpleName()
        );
        
        return new DocumentationRequested(
            metadata,
            requestingClass,
            Optional.empty(),
            DocumentationType.BASIC,
            Optional.empty(),
            requestingUserId,
            Instant.now()
        );
    }

    /**
     * Factory method for creating a method-specific documentation request.
     * @param requestingClass the class containing the method
     * @param methodName name of the method requesting documentation
     * @param requestingUserId ID of the user requesting documentation
     * @return new DocumentationRequested event
     */
    public static DocumentationRequested forMethod(
        final Class<?> requestingClass,
        final String methodName,
        final String requestingUserId
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "documentation", 
            requestingClass.getSimpleName() + "." + methodName
        );
        
        return new DocumentationRequested(
            metadata,
            requestingClass,
            Optional.of(methodName),
            DocumentationType.METHOD,
            Optional.empty(),
            requestingUserId,
            Instant.now()
        );
    }

    /**
     * Factory method for creating a contextual documentation request with flow context.
     * @param requestingClass the class requesting documentation
     * @param flowContext the flow context for contextual documentation
     * @param requestingUserId ID of the user requesting documentation
     * @return new DocumentationRequested event
     */
    public static DocumentationRequested forContextualFlow(
        final Class<?> requestingClass,
        final Flow flowContext,
        final String requestingUserId
    ) {
        final EventMetadata metadata = createMetadataForNewAggregate(
            "documentation", 
            requestingClass.getSimpleName() + "@" + flowContext.getName()
        );
        
        return new DocumentationRequested(
            metadata,
            requestingClass,
            Optional.empty(),
            DocumentationType.CONTEXTUAL,
            Optional.of(flowContext),
            requestingUserId,
            Instant.now()
        );
    }
}