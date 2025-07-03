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
 * Filename: DefaultPluginMessage.java
 *
 * Author: Claude Code
 *
 * Class name: DefaultPluginMessage
 *
 * Responsibilities:
 *   - Provide default implementation of PluginMessage
 *   - Handle basic message attributes and metadata
 *   - Serve as base for specialized message types
 *   - Support message creation and serialization
 *
 * Collaborators:
 *   - PluginMessage: Interface being implemented
 *   - Instant: Timestamp management
 *   - UUID: Request ID generation (implicit)
 */
package org.acmsl.bytehot.plugin.core;

import java.time.Instant;
import java.util.UUID;

/**
 * Default implementation of PluginMessage interface.
 * Provides basic message functionality that can be extended by specific message types.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class DefaultPluginMessage implements PluginMessage {

    /**
     * Message type identifier.
     */
    protected final String type;

    /**
     * Protocol version.
     */
    protected final String version;

    /**
     * Message timestamp.
     */
    protected final Instant timestamp;

    /**
     * Request identifier.
     */
    protected final String requestId;

    /**
     * Creates a new default plugin message with the specified attributes.
     * 
     * @param type the message type
     * @param version the protocol version
     * @param requestId the request identifier
     */
    public DefaultPluginMessage(final String type, final String version, final String requestId) {
        this.type = type;
        this.version = version;
        this.timestamp = Instant.now();
        this.requestId = requestId;
    }

    /**
     * Creates a new default plugin message with auto-generated request ID.
     * 
     * @param type the message type
     * @param version the protocol version
     */
    public DefaultPluginMessage(final String type, final String version) {
        this(type, version, UUID.randomUUID().toString());
    }

    /**
     * Creates a new default plugin message with default version and auto-generated request ID.
     * 
     * @param type the message type
     */
    public DefaultPluginMessage(final String type) {
        this(type, "1.0");
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return String.format("DefaultPluginMessage{type='%s', version='%s', timestamp=%s, requestId='%s'}", 
            type, version, timestamp, requestId);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final DefaultPluginMessage that = (DefaultPluginMessage) obj;

        if (!type.equals(that.type)) return false;
        if (!version.equals(that.version)) return false;
        if (!timestamp.equals(that.timestamp)) return false;
        return requestId.equals(that.requestId);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + requestId.hashCode();
        return result;
    }
}