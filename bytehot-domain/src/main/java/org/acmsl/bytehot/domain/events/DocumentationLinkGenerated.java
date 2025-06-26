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
 * Filename: DocumentationLinkGenerated.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationLinkGenerated
 *
 * Responsibilities:
 *   - Represent when contextual documentation URL is created
 *   - Track documentation generation performance and strategy
 *   - Enable analysis of documentation system effectiveness
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - DocumentationRequested: The original request this responds to
 *   - DocumentationGenerationStrategy: Strategy used for generation
 *   - Flow: Optional flow context applied during generation
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.DocumentationGenerationStrategy;
import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.bytehot.domain.Flow;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Event triggered when contextual documentation URL is created.
 * This event captures the outcome of documentation generation including
 * the generated URL, applied strategy, and performance metrics.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class DocumentationLinkGenerated 
    extends AbstractVersionedDomainEvent
    implements DomainResponseEvent<DocumentationRequested> {

    /**
     * The original documentation request this responds to.
     */
    @Getter
    private final DocumentationRequested originalRequest;

    /**
     * The generated documentation URL.
     */
    @Getter
    private final String generatedUrl;

    /**
     * Optional flow context applied during generation.
     */
    @Getter
    private final Optional<Flow> appliedFlowContext;

    /**
     * Strategy used for documentation generation.
     */
    @Getter
    private final DocumentationGenerationStrategy strategy;

    /**
     * Whether the URL was retrieved from cache.
     */
    @Getter
    private final boolean wasCached;

    /**
     * Time taken to generate the documentation URL.
     */
    @Getter
    private final Duration generationTime;

    /**
     * Timestamp when the documentation link was generated.
     */
    @Getter
    private final Instant generatedAt;

    /**
     * Creates a new DocumentationLinkGenerated event.
     * @param metadata event metadata
     * @param originalRequest the original documentation request
     * @param generatedUrl the generated documentation URL
     * @param appliedFlowContext optional flow context applied during generation
     * @param strategy strategy used for documentation generation
     * @param wasCached whether the URL was retrieved from cache
     * @param generationTime time taken to generate the URL
     * @param generatedAt timestamp when the link was generated
     */
    public DocumentationLinkGenerated(
        final EventMetadata metadata,
        final DocumentationRequested originalRequest,
        final String generatedUrl,
        final Optional<Flow> appliedFlowContext,
        final DocumentationGenerationStrategy strategy,
        final boolean wasCached,
        final Duration generationTime,
        final Instant generatedAt
    ) {
        super(metadata);
        this.originalRequest = originalRequest;
        this.generatedUrl = generatedUrl;
        this.appliedFlowContext = appliedFlowContext;
        this.strategy = strategy;
        this.wasCached = wasCached;
        this.generationTime = generationTime;
        this.generatedAt = generatedAt;
    }

    /**
     * Factory method for creating a basic documentation link generation event.
     * @param originalRequest the original documentation request
     * @param generatedUrl the generated documentation URL
     * @param generationTime time taken to generate the URL
     * @return new DocumentationLinkGenerated event
     */
    public static DocumentationLinkGenerated forBasicGeneration(
        final DocumentationRequested originalRequest,
        final String generatedUrl,
        final Duration generationTime
    ) {
        final EventMetadata metadata = createMetadataWithFullContext(
            "documentation-generation", 
            originalRequest.getRequestingClass().getSimpleName(),
            null,
            0L,
            "system",
            originalRequest.getEventId(),
            null
        );
        
        return new DocumentationLinkGenerated(
            metadata,
            originalRequest,
            generatedUrl,
            Optional.empty(),
            DocumentationGenerationStrategy.BASIC,
            false,
            generationTime,
            Instant.now()
        );
    }

    /**
     * Factory method for creating a contextual documentation link generation event.
     * @param originalRequest the original documentation request
     * @param generatedUrl the generated documentation URL
     * @param appliedFlowContext the flow context applied during generation
     * @param generationTime time taken to generate the URL
     * @return new DocumentationLinkGenerated event
     */
    public static DocumentationLinkGenerated forContextualGeneration(
        final DocumentationRequested originalRequest,
        final String generatedUrl,
        final Flow appliedFlowContext,
        final Duration generationTime
    ) {
        final EventMetadata metadata = createMetadataWithFullContext(
            "documentation-generation", 
            originalRequest.getRequestingClass().getSimpleName() + "@" + appliedFlowContext.getName(),
            null,
            0L,
            "system",
            originalRequest.getEventId(),
            null
        );
        
        return new DocumentationLinkGenerated(
            metadata,
            originalRequest,
            generatedUrl,
            Optional.of(appliedFlowContext),
            DocumentationGenerationStrategy.CONTEXTUAL,
            false,
            generationTime,
            Instant.now()
        );
    }

    /**
     * Factory method for creating a cached documentation link event.
     * @param originalRequest the original documentation request
     * @param cachedUrl the cached documentation URL
     * @param cacheRetrievalTime time taken to retrieve from cache
     * @return new DocumentationLinkGenerated event
     */
    public static DocumentationLinkGenerated forCachedGeneration(
        final DocumentationRequested originalRequest,
        final String cachedUrl,
        final Duration cacheRetrievalTime
    ) {
        final EventMetadata metadata = createMetadataWithFullContext(
            "documentation-generation", 
            originalRequest.getRequestingClass().getSimpleName() + "-cached",
            null,
            0L,
            "system",
            originalRequest.getEventId(),
            null
        );
        
        return new DocumentationLinkGenerated(
            metadata,
            originalRequest,
            cachedUrl,
            Optional.empty(),
            DocumentationGenerationStrategy.CACHED,
            true,
            cacheRetrievalTime,
            Instant.now()
        );
    }

    @Override
    public DocumentationRequested getPreceding() {
        return originalRequest;
    }

    /**
     * Checks if this generation was performed quickly.
     * @return true if generation time was less than 10ms
     */
    public boolean isFastGeneration() {
        return generationTime.toMillis() < 10;
    }

    /**
     * Checks if flow context was successfully applied.
     * @return true if flow context was applied
     */
    public boolean hasFlowContext() {
        return appliedFlowContext.isPresent();
    }

    /**
     * Gets the generation performance category.
     * @return performance category (FAST, NORMAL, SLOW)
     */
    public String getPerformanceCategory() {
        final long millis = generationTime.toMillis();
        if (millis < 10) {
            return "FAST";
        } else if (millis < 50) {
            return "NORMAL";
        } else {
            return "SLOW";
        }
    }

    /**
     * Checks if this represents a contextual documentation generation.
     * @return true if strategy is contextual or flow-specific
     */
    public boolean isContextualGeneration() {
        return strategy == DocumentationGenerationStrategy.CONTEXTUAL ||
               strategy == DocumentationGenerationStrategy.FLOW_SPECIFIC;
    }
}