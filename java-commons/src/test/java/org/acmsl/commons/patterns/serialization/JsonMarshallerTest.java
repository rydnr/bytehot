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
 * Filename: JsonMarshallerTest.java
 *
 * Author: Claude (Anthropic AI)
 *
 * Description: TDD tests for JsonMarshaller interface and implementations
 */
package org.acmsl.commons.patterns.serialization;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

/**
 * TDD tests for JsonMarshaller interface and implementations.
 * Tests define the expected behavior before implementation.
 * @author Claude (Anthropic AI)
 * @since 2025-06-26
 */
@DisplayName("JsonMarshaller TDD Tests")
class JsonMarshallerTest {

    private JsonMarshaller marshaller;

    @BeforeEach
    void setUp() {
        // This will be implemented after tests are written
        marshaller = JsonMarshallerFactory.createDefault();
    }

    @Nested
    @DisplayName("Basic Marshalling Tests")
    class BasicMarshallingTests {

        @Test
        @DisplayName("Should marshall simple object to JSON string")
        void shouldMarshallSimpleObjectToJson() throws SerializationException {
            // Given
            TestPerson person = new TestPerson("John Doe", 30);
            
            // When
            String json = marshaller.marshall(person);
            
            // Then
            assertNotNull(json);
            assertTrue(json.contains("John Doe"));
            assertTrue(json.contains("30"));
        }

        @Test
        @DisplayName("Should marshall null object to JSON")
        void shouldMarshallNullObjectToJson() throws SerializationException {
            // When
            String json = marshaller.marshall(null);
            
            // Then
            assertEquals("null", json);
        }

        @Test
        @DisplayName("Should marshall object to byte array")
        void shouldMarshallObjectToByteArray() throws SerializationException {
            // Given
            TestPerson person = new TestPerson("Jane Doe", 25);
            
            // When
            byte[] jsonBytes = marshaller.marshallToBytes(person);
            
            // Then
            assertNotNull(jsonBytes);
            assertTrue(jsonBytes.length > 0);
        }

        @Test
        @DisplayName("Should handle complex nested objects")
        void shouldHandleComplexNestedObjects() throws SerializationException {
            // Given
            TestAddress address = new TestAddress("123 Main St", "Anytown", "12345");
            TestPerson person = new TestPerson("John Doe", 30, address);
            
            // When
            String json = marshaller.marshall(person);
            
            // Then
            assertNotNull(json);
            assertTrue(json.contains("123 Main St"));
            assertTrue(json.contains("Anytown"));
        }
    }

    @Nested
    @DisplayName("Basic Unmarshalling Tests")
    class BasicUnmarshallingTests {

        @Test
        @DisplayName("Should unmarshall JSON string to object")
        void shouldUnmarshallJsonStringToObject() throws SerializationException {
            // Given
            String json = "{\"name\":\"John Doe\",\"age\":30}";
            
            // When
            TestPerson person = marshaller.unmarshall(json, TestPerson.class);
            
            // Then
            assertNotNull(person);
            assertEquals("John Doe", person.getName());
            assertEquals(30, person.getAge());
        }

        @Test
        @DisplayName("Should unmarshall null JSON to null object")
        void shouldUnmarshallNullJsonToNullObject() throws SerializationException {
            // When
            TestPerson person = marshaller.unmarshall("null", TestPerson.class);
            
            // Then
            assertNull(person);
        }

        @Test
        @DisplayName("Should unmarshall byte array to object")
        void shouldUnmarshallByteArrayToObject() throws SerializationException {
            // Given
            String json = "{\"name\":\"Jane Doe\",\"age\":25}";
            byte[] jsonBytes = json.getBytes();
            
            // When
            TestPerson person = marshaller.unmarshall(jsonBytes, TestPerson.class);
            
            // Then
            assertNotNull(person);
            assertEquals("Jane Doe", person.getName());
            assertEquals(25, person.getAge());
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON")
        void shouldThrowExceptionForInvalidJson() {
            // Given
            String invalidJson = "{\"name\":\"John\",\"age\":}";
            
            // When & Then
            SerializationException exception = assertThrows(
                SerializationException.class,
                () -> marshaller.unmarshall(invalidJson, TestPerson.class)
            );
            
            assertTrue(exception.getMessage().contains("unmarshalling"));
            assertEquals(TestPerson.class, exception.getTargetType());
        }
    }

    @Nested
    @DisplayName("Collection Marshalling Tests")
    class CollectionMarshallingTests {

        @Test
        @DisplayName("Should unmarshall JSON array to List")
        void shouldUnmarshallJsonArrayToList() throws SerializationException {
            // Given
            String json = "[{\"name\":\"John\",\"age\":30},{\"name\":\"Jane\",\"age\":25}]";
            
            // When
            List<TestPerson> people = marshaller.unmarshallList(json, TestPerson.class);
            
            // Then
            assertNotNull(people);
            assertEquals(2, people.size());
            assertEquals("John", people.get(0).getName());
            assertEquals("Jane", people.get(1).getName());
        }

        @Test
        @DisplayName("Should unmarshall JSON object to Map")
        void shouldUnmarshallJsonObjectToMap() throws SerializationException {
            // Given
            String json = "{\"person1\":{\"name\":\"John\",\"age\":30},\"person2\":{\"name\":\"Jane\",\"age\":25}}";
            
            // When
            Map<String, TestPerson> peopleMap = marshaller.unmarshallMap(json, TestPerson.class);
            
            // Then
            assertNotNull(peopleMap);
            assertEquals(2, peopleMap.size());
            assertEquals("John", peopleMap.get("person1").getName());
            assertEquals("Jane", peopleMap.get("person2").getName());
        }

        @Test
        @DisplayName("Should convert object to Map representation")
        void shouldConvertObjectToMapRepresentation() throws SerializationException {
            // Given
            TestPerson person = new TestPerson("John Doe", 30);
            
            // When
            Map<String, Object> map = marshaller.toMap(person);
            
            // Then
            assertNotNull(map);
            assertEquals("John Doe", map.get("name"));
            assertEquals(30, map.get("age"));
        }

        @Test
        @DisplayName("Should convert Map to object")
        void shouldConvertMapToObject() throws SerializationException {
            // Given
            Map<String, Object> map = new HashMap<>();
            map.put("name", "John Doe");
            map.put("age", 30);
            
            // When
            TestPerson person = marshaller.fromMap(map, TestPerson.class);
            
            // Then
            assertNotNull(person);
            assertEquals("John Doe", person.getName());
            assertEquals(30, person.getAge());
        }
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {

        @Test
        @DisplayName("Should use custom configuration for marshalling")
        void shouldUseCustomConfigurationForMarshalling() throws SerializationException {
            // Given
            JsonSerializationConfig config = JsonSerializationConfig.prettyPrintConfig();
            TestPerson person = new TestPerson("John Doe", 30);
            
            // When
            String json = marshaller.marshall(person, config);
            
            // Then
            assertNotNull(json);
            assertTrue(json.contains("\n")); // Pretty print should include newlines
        }

        @Test
        @DisplayName("Should create marshaller with new configuration")
        void shouldCreateMarshallerWithNewConfiguration() {
            // Given
            JsonSerializationConfig config = JsonSerializationConfig.compactConfig();
            
            // When
            JsonMarshaller newMarshaller = marshaller.withConfiguration(config);
            
            // Then
            assertNotNull(newMarshaller);
            assertNotSame(marshaller, newMarshaller);
            assertEquals(config, newMarshaller.getConfiguration());
        }

        @Test
        @DisplayName("Should support EventSourcing configuration")
        void shouldSupportEventSourcingConfiguration() throws SerializationException {
            // Given
            JsonSerializationConfig config = JsonSerializationConfig.eventSourcingConfig();
            TestEvent event = new TestEvent("test-id", Instant.now(), "test-data");
            
            // When
            String json = marshaller.marshall(event, config);
            
            // Then
            assertNotNull(json);
            // EventSourcing config should include type information
            assertTrue(config.isIncludeTypeInfo());
            assertTrue(config.isPreserveFieldOrder());
        }
    }

    @Nested
    @DisplayName("Type Handling Tests")
    class TypeHandlingTests {

        @Test
        @DisplayName("Should check if marshaller can handle type")
        void shouldCheckIfMarshallerCanHandleType() {
            // When & Then
            assertTrue(marshaller.canHandle(TestPerson.class));
            assertTrue(marshaller.canHandle(String.class));
            assertTrue(marshaller.canHandle(Integer.class));
            assertTrue(marshaller.canHandle(List.class));
            assertTrue(marshaller.canHandle(Map.class));
        }

        @Test
        @DisplayName("Should handle temporal types correctly")
        void shouldHandleTemporalTypesCorrectly() throws SerializationException {
            // Given
            TestEvent event = new TestEvent("test-id", Instant.now(), "test-data");
            
            // When
            String json = marshaller.marshall(event);
            TestEvent unmarshalled = marshaller.unmarshall(json, TestEvent.class);
            
            // Then
            assertNotNull(unmarshalled);
            assertEquals(event.getEventId(), unmarshalled.getEventId());
            assertEquals(event.getTimestamp(), unmarshalled.getTimestamp());
        }
    }

    @Nested
    @DisplayName("Round-trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should maintain object equality in round-trip")
        void shouldMaintainObjectEqualityInRoundTrip() throws SerializationException {
            // Given
            TestPerson original = new TestPerson("John Doe", 30);
            
            // When
            String json = marshaller.marshall(original);
            TestPerson restored = marshaller.unmarshall(json, TestPerson.class);
            
            // Then
            assertEquals(original, restored);
        }

        @Test
        @DisplayName("Should handle complex objects in round-trip")
        void shouldHandleComplexObjectsInRoundTrip() throws SerializationException {
            // Given
            TestAddress address = new TestAddress("123 Main St", "Anytown", "12345");
            TestPerson original = new TestPerson("John Doe", 30, address);
            
            // When
            String json = marshaller.marshall(original);
            TestPerson restored = marshaller.unmarshall(json, TestPerson.class);
            
            // Then
            assertEquals(original, restored);
            assertEquals(original.getAddress(), restored.getAddress());
        }

        @Test
        @DisplayName("Should handle collections in round-trip")
        void shouldHandleCollectionsInRoundTrip() throws SerializationException {
            // Given
            List<TestPerson> originalList = Arrays.asList(
                new TestPerson("John", 30),
                new TestPerson("Jane", 25)
            );
            
            // When
            String json = marshaller.marshall(originalList);
            List<TestPerson> restoredList = marshaller.unmarshallList(json, TestPerson.class);
            
            // Then
            assertEquals(originalList, restoredList);
        }
    }

    // Test data classes
    public static class TestPerson {
        private String name;
        private int age;
        private TestAddress address;

        public TestPerson() {}

        public TestPerson(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public TestPerson(String name, int age, TestAddress address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public TestAddress getAddress() { return address; }
        public void setAddress(TestAddress address) { this.address = address; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestPerson that = (TestPerson) obj;
            return age == that.age && 
                   java.util.Objects.equals(name, that.name) && 
                   java.util.Objects.equals(address, that.address);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, age, address);
        }
    }

    public static class TestAddress {
        private String street;
        private String city;
        private String zipCode;

        public TestAddress() {}

        public TestAddress(String street, String city, String zipCode) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
        }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestAddress that = (TestAddress) obj;
            return java.util.Objects.equals(street, that.street) && 
                   java.util.Objects.equals(city, that.city) && 
                   java.util.Objects.equals(zipCode, that.zipCode);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(street, city, zipCode);
        }
    }

    public static class TestEvent {
        private String eventId;
        private Instant timestamp;
        private String data;

        public TestEvent() {}

        public TestEvent(String eventId, Instant timestamp, String data) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.data = data;
        }

        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestEvent that = (TestEvent) obj;
            return java.util.Objects.equals(eventId, that.eventId) && 
                   java.util.Objects.equals(timestamp, that.timestamp) && 
                   java.util.Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(eventId, timestamp, data);
        }
    }
}