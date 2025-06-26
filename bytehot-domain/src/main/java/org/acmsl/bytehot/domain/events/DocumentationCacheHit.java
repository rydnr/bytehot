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
 * Filename: DocumentationCacheHit.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationCacheHit
 *
 * Responsibilities:
 *   - Represent when documentation URL is retrieved from cache
 *   - Track cache performance and effectiveness
 *   - Enable cache optimization and analytics
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - DocumentationRequested: The original request that triggered the cache lookup
 *   - DocumentationGenerationStrategy: Strategy used for the cached entry
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.DocumentationGenerationStrategy;
import org.acmsl.commons.patterns.eventsourcing.AbstractVersionedDomainEvent;
import org.acmsl.commons.patterns.eventsourcing.EventMetadata;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

/**
 * Event triggered when documentation URL is successfully retrieved from cache.
 * This event tracks cache effectiveness and enables optimization of the
 * documentation caching system.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class DocumentationCacheHit 
    extends AbstractVersionedDomainEvent
    implements DomainResponseEvent<DocumentationRequested> {

    /**
     * The original documentation request.
     */
    @Getter
    private final DocumentationRequested originalRequest;

    /**
     * The cached URL that was retrieved.
     */
    @Getter
    private final String cachedUrl;

    /**
     * Strategy that was used to generate the originally cached entry.
     */
    @Getter
    private final DocumentationGenerationStrategy originalStrategy;

    /**
     * Age of the cached entry.
     */
    @Getter
    private final Duration cacheAge;

    /**
     * Time taken to retrieve from cache.
     */
    @Getter
    private final Duration retrievalTime;

    /**
     * Cache key used for the lookup.
     */
    @Getter
    private final String cacheKey;

    /**
     * Timestamp when the cache hit occurred.
     */
    @Getter
    private final Instant hitAt;

    /**
     * Creates a new DocumentationCacheHit event.
     * @param metadata event metadata
     * @param originalRequest the original documentation request
     * @param cachedUrl the cached URL that was retrieved
     * @param originalStrategy strategy used to generate the originally cached entry
     * @param cacheAge age of the cached entry
     * @param retrievalTime time taken to retrieve from cache
     * @param cacheKey cache key used for the lookup
     * @param hitAt timestamp when the cache hit occurred
     */
    public DocumentationCacheHit(
        final EventMetadata metadata,
        final DocumentationRequested originalRequest,
        final String cachedUrl,
        final DocumentationGenerationStrategy originalStrategy,
        final Duration cacheAge,
        final Duration retrievalTime,
        final String cacheKey,
        final Instant hitAt
    ) {
        super(metadata);
        this.originalRequest = originalRequest;
        this.cachedUrl = cachedUrl;
        this.originalStrategy = originalStrategy;
        this.cacheAge = cacheAge;
        this.retrievalTime = retrievalTime;
        this.cacheKey = cacheKey;
        this.hitAt = hitAt;
    }

    /**
     * Factory method for creating a cache hit event.
     * @param originalRequest the original documentation request
     * @param cachedUrl the cached URL that was retrieved
     * @param originalStrategy strategy used to generate the originally cached entry
     * @param cacheAge age of the cached entry
     * @param retrievalTime time taken to retrieve from cache
     * @param cacheKey cache key used for the lookup
     * @return new DocumentationCacheHit event
     */
    public static DocumentationCacheHit forSuccessfulRetrieval(
        final DocumentationRequested originalRequest,
        final String cachedUrl,
        final DocumentationGenerationStrategy originalStrategy,
        final Duration cacheAge,
        final Duration retrievalTime,
        final String cacheKey
    ) {
        final EventMetadata metadata = createMetadataWithFullContext(
            "documentation-cache", 
            originalRequest.getRequestingClass().getSimpleName() + "-hit",
            null,
            0L,
            "system",
            originalRequest.getEventId(),
            null
        );
        
        return new DocumentationCacheHit(
            metadata,
            originalRequest,
            cachedUrl,
            originalStrategy,
            cacheAge,
            retrievalTime,
            cacheKey,
            Instant.now()
        );
    }

    @Override
    public DocumentationRequested getPreceding() {
        return originalRequest;
    }

    /**
     * Checks if this represents a fresh cache entry.
     * @return true if cache age is less than 1 hour
     */
    public boolean isFreshCacheEntry() {
        return cacheAge.toHours() < 1;
    }

    /**
     * Checks if the cache retrieval was fast.
     * @return true if retrieval time was less than 1ms
     */
    public boolean isFastRetrieval() {
        return retrievalTime.toNanos() < 1_000_000; // 1ms in nanoseconds
    }

    /**
     * Gets the cache effectiveness category.
     * @return effectiveness category (EXCELLENT, GOOD, FAIR, POOR)
     */
    public String getCacheEffectiveness() {
        if (isFastRetrieval() && isFreshCacheEntry()) {
            return "EXCELLENT";
        } else if (isFastRetrieval() || isFreshCacheEntry()) {
            return "GOOD";
        } else if (cacheAge.toDays() < 1) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }
}