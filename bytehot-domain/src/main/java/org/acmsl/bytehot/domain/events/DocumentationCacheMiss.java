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
 * Filename: DocumentationCacheMiss.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationCacheMiss
 *
 * Responsibilities:
 *   - Represent when documentation URL is not found in cache
 *   - Track cache miss patterns for optimization
 *   - Trigger fallback documentation generation
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - DocumentationRequested: The original request that triggered the cache lookup
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.EventMetadata;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;

/**
 * Event triggered when documentation URL is not found in cache.
 * This event indicates the need for fresh documentation generation
 * and helps track cache effectiveness patterns.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class DocumentationCacheMiss 
    extends AbstractVersionedDomainEvent
    implements DomainResponseEvent<DocumentationRequested> {

    /**
     * The original documentation request.
     */
    @Getter
    private final DocumentationRequested originalRequest;

    /**
     * Cache key that was looked up but not found.
     */
    @Getter
    private final String missedCacheKey;

    /**
     * Time taken to perform the cache lookup.
     */
    @Getter
    private final Duration lookupTime;

    /**
     * Reason for the cache miss (NOT_FOUND, EXPIRED, INVALID).
     */
    @Getter
    private final String missReason;

    /**
     * Whether this is the first miss for this key.
     */
    @Getter
    private final boolean isFirstMiss;

    /**
     * Timestamp when the cache miss occurred.
     */
    @Getter
    private final Instant missedAt;

    /**
     * Creates a new DocumentationCacheMiss event.
     * @param metadata event metadata
     * @param originalRequest the original documentation request
     * @param missedCacheKey cache key that was looked up but not found
     * @param lookupTime time taken to perform the cache lookup
     * @param missReason reason for the cache miss
     * @param isFirstMiss whether this is the first miss for this key
     * @param missedAt timestamp when the cache miss occurred
     */
    public DocumentationCacheMiss(
        final EventMetadata metadata,
        final DocumentationRequested originalRequest,
        final String missedCacheKey,
        final Duration lookupTime,
        final String missReason,
        final boolean isFirstMiss,
        final Instant missedAt
    ) {
        super(metadata);
        this.originalRequest = originalRequest;
        this.missedCacheKey = missedCacheKey;
        this.lookupTime = lookupTime;
        this.missReason = missReason;
        this.isFirstMiss = isFirstMiss;
        this.missedAt = missedAt;
    }

    /**
     * Factory method for creating a cache miss event.
     * @param originalRequest the original documentation request
     * @param missedCacheKey cache key that was looked up but not found
     * @param lookupTime time taken to perform the cache lookup
     * @param missReason reason for the cache miss
     * @param isFirstMiss whether this is the first miss for this key
     * @return new DocumentationCacheMiss event
     */
    public static DocumentationCacheMiss forKeyNotFound(
        final DocumentationRequested originalRequest,
        final String missedCacheKey,
        final Duration lookupTime,
        final String missReason,
        final boolean isFirstMiss
    ) {
        final EventMetadata metadata = createMetadataWithCorrelation(
            "documentation-cache", 
            originalRequest.getRequestingClass().getSimpleName() + "-miss",
            null,
            0L,
            "system",
            originalRequest.getEventId()
        );
        
        return new DocumentationCacheMiss(
            metadata,
            originalRequest,
            missedCacheKey,
            lookupTime,
            missReason,
            isFirstMiss,
            Instant.now()
        );
    }

    @Override
    public DocumentationRequested getPreceding() {
        return originalRequest;
    }

    /**
     * Checks if this miss indicates a potential cache optimization opportunity.
     * @return true if this is a repeated miss for the same key
     */
    public boolean indicatesOptimizationOpportunity() {
        return !isFirstMiss;
    }

    /**
     * Checks if the cache lookup was performed quickly.
     * @return true if lookup time was less than 5ms
     */
    public boolean isFastLookup() {
        return lookupTime.toMillis() < 5;
    }

    /**
     * Gets the miss severity based on frequency and lookup performance.
     * @return severity level (LOW, MEDIUM, HIGH, CRITICAL)
     */
    public String getMissSeverity() {
        if (isFirstMiss && isFastLookup()) {
            return "LOW";
        } else if (isFirstMiss || isFastLookup()) {
            return "MEDIUM";
        } else if (!isFirstMiss && !isFastLookup()) {
            return "CRITICAL";
        } else {
            return "HIGH";
        }
    }
}