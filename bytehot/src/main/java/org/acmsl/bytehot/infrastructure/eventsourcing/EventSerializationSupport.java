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
 * Filename: EventSerializationSupport.java
 *
 * Author: Claude Code
 *
 * Class name: EventSerializationSupport
 *
 * Responsibilities:
 *   - Serialize VersionedDomainEvent instances to JSON
 *   - Deserialize JSON back to VersionedDomainEvent instances
 *   - Handle event type resolution and class loading
 *   - Provide consistent JSON format for event storage
 *
 * Collaborators:
 *   - VersionedDomainEvent: Events being serialized/deserialized
 *   - FilesystemEventStoreAdapter: Uses this for JSON operations
 */
package org.acmsl.bytehot.infrastructure.eventsourcing;

import org.acmsl.bytehot.domain.VersionedDomainEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for serializing and deserializing domain events
 * @author Claude Code
 * @since 2025-06-17
 */
public class EventSerializationSupport {

    /**
     * JSON property name for event type
     */
    private static final String EVENT_TYPE_PROPERTY = "eventType";

    /**
     * JSON property name for event data
     */
    private static final String EVENT_DATA_PROPERTY = "eventData";

    /**
     * JSON property name for event metadata
     */
    private static final String EVENT_METADATA_PROPERTY = "eventMetadata";

    /**
     * Configured object mapper for JSON operations
     */
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * Cache of event class mappings for performance
     */
    private static final Map<String, Class<? extends VersionedDomainEvent>> EVENT_TYPE_CACHE = new HashMap<>();

    /**
     * Creates and configures the JSON object mapper
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    /**
     * Serializes a domain event to JSON string
     * @param event the event to serialize
     * @return JSON representation of the event
     * @throws IOException if serialization fails
     */
    public static String toJson(VersionedDomainEvent event) throws IOException {
        ObjectNode rootNode = OBJECT_MAPPER.createObjectNode();
        
        // Add event type information
        rootNode.put(EVENT_TYPE_PROPERTY, event.getEventType());
        
        // Add event metadata
        ObjectNode metadataNode = createMetadataNode(event);
        rootNode.set(EVENT_METADATA_PROPERTY, metadataNode);
        
        // Add event data (the actual event object)
        JsonNode eventDataNode = OBJECT_MAPPER.valueToTree(event);
        rootNode.set(EVENT_DATA_PROPERTY, eventDataNode);
        
        return OBJECT_MAPPER.writeValueAsString(rootNode);
    }

    /**
     * Deserializes a JSON string to a domain event
     * @param json the JSON string
     * @return the deserialized event, or null if deserialization fails
     * @throws IOException if deserialization fails
     */
    public static VersionedDomainEvent fromJson(String json) throws IOException {
        JsonNode rootNode = OBJECT_MAPPER.readTree(json);
        
        // Extract event type
        JsonNode eventTypeNode = rootNode.get(EVENT_TYPE_PROPERTY);
        if (eventTypeNode == null) {
            throw new IOException("Missing event type in JSON");
        }
        
        String eventType = eventTypeNode.asText();
        
        // Extract event data
        JsonNode eventDataNode = rootNode.get(EVENT_DATA_PROPERTY);
        if (eventDataNode == null) {
            throw new IOException("Missing event data in JSON");
        }
        
        // Resolve event class and deserialize
        Class<? extends VersionedDomainEvent> eventClass = resolveEventClass(eventType);
        if (eventClass == null) {
            throw new IOException("Unknown event type: " + eventType);
        }
        
        return OBJECT_MAPPER.treeToValue(eventDataNode, eventClass);
    }

    /**
     * Deserializes a JSON string to a specific event type
     * @param json the JSON string
     * @param eventType the expected event type
     * @return the deserialized event
     * @throws IOException if deserialization fails
     */
    public static VersionedDomainEvent fromJson(String json, String eventType) throws IOException {
        Class<? extends VersionedDomainEvent> eventClass = resolveEventClass(eventType);
        if (eventClass == null) {
            throw new IOException("Unknown event type: " + eventType);
        }
        
        JsonNode rootNode = OBJECT_MAPPER.readTree(json);
        JsonNode eventDataNode = rootNode.get(EVENT_DATA_PROPERTY);
        
        if (eventDataNode == null) {
            // Fallback: try to deserialize the entire JSON as the event
            return OBJECT_MAPPER.readValue(json, eventClass);
        }
        
        return OBJECT_MAPPER.treeToValue(eventDataNode, eventClass);
    }

    /**
     * Creates a metadata node for an event
     */
    private static ObjectNode createMetadataNode(VersionedDomainEvent event) {
        ObjectNode metadataNode = OBJECT_MAPPER.createObjectNode();
        
        metadataNode.put("eventId", event.getEventId());
        metadataNode.put("aggregateType", event.getAggregateType());
        metadataNode.put("aggregateId", event.getAggregateId());
        metadataNode.put("aggregateVersion", event.getAggregateVersion());
        metadataNode.put("timestamp", event.getTimestamp().toString());
        metadataNode.put("schemaVersion", event.getSchemaVersion());
        
        if (event.getPreviousEventId() != null) {
            metadataNode.put("previousEventId", event.getPreviousEventId());
        }
        
        if (event.getUserId() != null) {
            metadataNode.put("userId", event.getUserId());
        }
        
        if (event.getCorrelationId() != null) {
            metadataNode.put("correlationId", event.getCorrelationId());
        }
        
        return metadataNode;
    }

    /**
     * Resolves an event class from its type name
     * @param eventType the simple class name of the event
     * @return the event class, or null if not found
     */
    private static Class<? extends VersionedDomainEvent> resolveEventClass(String eventType) {
        // Check cache first
        Class<? extends VersionedDomainEvent> cachedClass = EVENT_TYPE_CACHE.get(eventType);
        if (cachedClass != null) {
            return cachedClass;
        }
        
        // Try to find the class in the events package
        String[] packagePrefixes = {
            "org.acmsl.bytehot.domain.events.",
            "org.acmsl.bytehot.domain."
        };
        
        for (String packagePrefix : packagePrefixes) {
            try {
                String fullClassName = packagePrefix + eventType;
                Class<?> clazz = Class.forName(fullClassName);
                
                if (VersionedDomainEvent.class.isAssignableFrom(clazz)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends VersionedDomainEvent> eventClass = 
                        (Class<? extends VersionedDomainEvent>) clazz;
                    
                    // Cache for future use
                    EVENT_TYPE_CACHE.put(eventType, eventClass);
                    return eventClass;
                }
            } catch (ClassNotFoundException e) {
                // Continue trying other packages
            }
        }
        
        return null;
    }

    /**
     * Registers an event class for type resolution
     * @param eventType the simple class name
     * @param eventClass the event class
     */
    public static void registerEventType(String eventType, Class<? extends VersionedDomainEvent> eventClass) {
        EVENT_TYPE_CACHE.put(eventType, eventClass);
    }

    /**
     * Clears the event type cache (useful for testing)
     */
    public static void clearCache() {
        EVENT_TYPE_CACHE.clear();
    }

    /**
     * Gets all registered event types
     * @return map of event type names to classes
     */
    public static Map<String, Class<? extends VersionedDomainEvent>> getRegisteredEventTypes() {
        return new HashMap<>(EVENT_TYPE_CACHE);
    }

    /**
     * Checks if an event type is registered
     * @param eventType the event type name
     * @return true if the event type is known
     */
    public static boolean isEventTypeRegistered(String eventType) {
        return EVENT_TYPE_CACHE.containsKey(eventType) || resolveEventClass(eventType) != null;
    }

    /**
     * Extracts the event type from a JSON string without full deserialization
     * @param json the JSON string
     * @return the event type, or null if not found
     */
    public static String extractEventType(String json) {
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(json);
            JsonNode eventTypeNode = rootNode.get(EVENT_TYPE_PROPERTY);
            return eventTypeNode != null ? eventTypeNode.asText() : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Validates that a JSON string contains a valid event structure
     * @param json the JSON string to validate
     * @return true if the JSON has the expected event structure
     */
    public static boolean isValidEventJson(String json) {
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(json);
            
            // Check for required fields
            return rootNode.has(EVENT_TYPE_PROPERTY) && 
                   rootNode.has(EVENT_METADATA_PROPERTY) && 
                   rootNode.has(EVENT_DATA_PROPERTY);
                   
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates a minimal JSON representation for testing
     * @param eventType the event type
     * @param eventData the event data as JSON
     * @return minimal JSON string
     */
    public static String createMinimalEventJson(String eventType, String eventData) {
        try {
            ObjectNode rootNode = OBJECT_MAPPER.createObjectNode();
            rootNode.put(EVENT_TYPE_PROPERTY, eventType);
            rootNode.set(EVENT_DATA_PROPERTY, OBJECT_MAPPER.readTree(eventData));
            rootNode.set(EVENT_METADATA_PROPERTY, OBJECT_MAPPER.createObjectNode());
            
            return OBJECT_MAPPER.writeValueAsString(rootNode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create minimal event JSON", e);
        }
    }
}