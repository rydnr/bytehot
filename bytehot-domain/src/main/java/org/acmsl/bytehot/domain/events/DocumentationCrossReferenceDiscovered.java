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
 * Filename: DocumentationCrossReferenceDiscovered.java
 *
 * Author: Claude Code
 *
 * Class name: DocumentationCrossReferenceDiscovered
 *
 * Responsibilities:
 *   - Represent when relationships between documentation artifacts are found
 *   - Track cross-reference discovery for documentation analytics
 *   - Enable enhanced contextual documentation generation
 *
 * Collaborators:
 *   - AbstractVersionedDomainEvent: Base class providing event metadata
 *   - DocumentationRequested: The request that triggered cross-reference analysis
 */
package org.acmsl.bytehot.domain.events;

import org.acmsl.bytehot.domain.EventMetadata;
import org.acmsl.commons.patterns.DomainResponseEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Event triggered when relationships between documentation artifacts are discovered.
 * This event enables building a rich knowledge graph of documentation relationships
 * and supports enhanced contextual documentation generation.
 * @author Claude Code
 * @since 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class DocumentationCrossReferenceDiscovered 
    extends AbstractVersionedDomainEvent
    implements DomainResponseEvent<DocumentationRequested> {

    /**
     * The original documentation request that triggered cross-reference analysis.
     */
    @Getter
    private final DocumentationRequested originalRequest;

    /**
     * The source documentation artifact.
     */
    @Getter
    private final String sourceArtifact;

    /**
     * Related documentation artifacts discovered.
     */
    @Getter
    private final List<String> relatedArtifacts;

    /**
     * Type of relationship (INHERITANCE, COMPOSITION, USAGE, REFERENCE, FLOW_SEQUENCE).
     */
    @Getter
    private final String relationshipType;

    /**
     * Strength of the relationship (0.0 to 1.0).
     */
    @Getter
    private final double relationshipStrength;

    /**
     * Context in which the relationship was discovered.
     */
    @Getter
    private final Optional<String> discoveryContext;

    /**
     * Method or approach used to discover the relationship.
     */
    @Getter
    private final String discoveryMethod;

    /**
     * Additional metadata about the relationship.
     */
    @Getter
    private final Optional<String> relationshipMetadata;

    /**
     * Timestamp when the cross-reference was discovered.
     */
    @Getter
    private final Instant discoveredAt;

    /**
     * Creates a new DocumentationCrossReferenceDiscovered event.
     * @param metadata event metadata
     * @param originalRequest the original request that triggered analysis
     * @param sourceArtifact the source documentation artifact
     * @param relatedArtifacts related documentation artifacts discovered
     * @param relationshipType type of relationship discovered
     * @param relationshipStrength strength of the relationship (0.0 to 1.0)
     * @param discoveryContext context in which the relationship was discovered
     * @param discoveryMethod method used to discover the relationship
     * @param relationshipMetadata additional metadata about the relationship
     * @param discoveredAt timestamp when the cross-reference was discovered
     */
    public DocumentationCrossReferenceDiscovered(
        final EventMetadata metadata,
        final DocumentationRequested originalRequest,
        final String sourceArtifact,
        final List<String> relatedArtifacts,
        final String relationshipType,
        final double relationshipStrength,
        final Optional<String> discoveryContext,
        final String discoveryMethod,
        final Optional<String> relationshipMetadata,
        final Instant discoveredAt
    ) {
        super(metadata);
        this.originalRequest = originalRequest;
        this.sourceArtifact = sourceArtifact;
        this.relatedArtifacts = List.copyOf(relatedArtifacts);
        this.relationshipType = relationshipType;
        this.relationshipStrength = relationshipStrength;
        this.discoveryContext = discoveryContext;
        this.discoveryMethod = discoveryMethod;
        this.relationshipMetadata = relationshipMetadata;
        this.discoveredAt = discoveredAt;
    }

    /**
     * Factory method for creating a basic cross-reference discovery event.
     * @param originalRequest the original documentation request
     * @param sourceArtifact the source documentation artifact
     * @param relatedArtifacts related documentation artifacts discovered
     * @param relationshipType type of relationship discovered
     * @param relationshipStrength strength of the relationship
     * @param discoveryMethod method used to discover the relationship
     * @return new DocumentationCrossReferenceDiscovered event
     */
    public static DocumentationCrossReferenceDiscovered forBasicDiscovery(
        final DocumentationRequested originalRequest,
        final String sourceArtifact,
        final List<String> relatedArtifacts,
        final String relationshipType,
        final double relationshipStrength,
        final String discoveryMethod
    ) {
        final EventMetadata metadata = createMetadataWithCorrelation(
            "documentation-cross-reference", 
            sourceArtifact + "-" + relationshipType,
            null,
            0L,
            "system",
            originalRequest.getEventId()
        );
        
        return new DocumentationCrossReferenceDiscovered(
            metadata,
            originalRequest,
            sourceArtifact,
            relatedArtifacts,
            relationshipType,
            relationshipStrength,
            Optional.empty(),
            discoveryMethod,
            Optional.empty(),
            Instant.now()
        );
    }

    /**
     * Factory method for creating a contextual cross-reference discovery event.
     * @param originalRequest the original documentation request
     * @param sourceArtifact the source documentation artifact
     * @param relatedArtifacts related documentation artifacts discovered
     * @param relationshipType type of relationship discovered
     * @param relationshipStrength strength of the relationship
     * @param discoveryContext context in which the relationship was discovered
     * @param discoveryMethod method used to discover the relationship
     * @param relationshipMetadata additional metadata about the relationship
     * @return new DocumentationCrossReferenceDiscovered event
     */
    public static DocumentationCrossReferenceDiscovered forContextualDiscovery(
        final DocumentationRequested originalRequest,
        final String sourceArtifact,
        final List<String> relatedArtifacts,
        final String relationshipType,
        final double relationshipStrength,
        final String discoveryContext,
        final String discoveryMethod,
        final String relationshipMetadata
    ) {
        final EventMetadata metadata = createMetadataWithCorrelation(
            "documentation-cross-reference", 
            sourceArtifact + "-" + relationshipType + "-contextual",
            null,
            0L,
            "system",
            originalRequest.getEventId()
        );
        
        return new DocumentationCrossReferenceDiscovered(
            metadata,
            originalRequest,
            sourceArtifact,
            relatedArtifacts,
            relationshipType,
            relationshipStrength,
            Optional.of(discoveryContext),
            discoveryMethod,
            Optional.of(relationshipMetadata),
            Instant.now()
        );
    }

    @Override
    public DocumentationRequested getPreceding() {
        return originalRequest;
    }

    /**
     * Checks if this represents a strong relationship.
     * @return true if relationship strength is >= 0.7
     */
    public boolean isStrongRelationship() {
        return relationshipStrength >= 0.7;
    }

    /**
     * Checks if multiple related artifacts were discovered.
     * @return true if more than one related artifact was found
     */
    public boolean hasMultipleRelations() {
        return relatedArtifacts.size() > 1;
    }

    /**
     * Gets the relationship quality category.
     * @return quality category (EXCELLENT, GOOD, FAIR, WEAK)
     */
    public String getRelationshipQuality() {
        if (relationshipStrength >= 0.9) {
            return "EXCELLENT";
        } else if (relationshipStrength >= 0.7) {
            return "GOOD";
        } else if (relationshipStrength >= 0.5) {
            return "FAIR";
        } else {
            return "WEAK";
        }
    }

    /**
     * Checks if this discovery enhances contextual documentation.
     * @return true if discovery has context and strong relationships
     */
    public boolean enhancesContextualDocumentation() {
        return discoveryContext.isPresent() && isStrongRelationship();
    }
}