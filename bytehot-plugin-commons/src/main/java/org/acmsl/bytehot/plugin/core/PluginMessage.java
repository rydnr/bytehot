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
 * Filename: PluginMessage.java
 *
 * Author: Claude Code
 *
 * Class name: PluginMessage
 *
 * Responsibilities:
 *   - Define plugin communication protocol structure
 *   - Provide message identification and versioning
 *   - Enable message serialization and tracking
 *   - Support request-response correlation
 *
 * Collaborators:
 *   - PluginCommunicationHandler: Uses messages for communication
 *   - MessageHandler: Processes incoming messages
 *   - Instant: Timestamp management
 *   - JSON serialization: Message serialization (implicit)
 */
package org.acmsl.bytehot.plugin.core;

import java.time.Instant;

/**
 * Represents a message in the ByteHot plugin communication protocol.
 * All plugin messages implement this interface for consistent communication.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public interface PluginMessage {

    /**
     * Gets the type of this message.
     * Used for message routing and handler registration.
     * 
     * @return the message type identifier
     */
    String getType();

    /**
     * Gets the protocol version of this message.
     * Used for backward compatibility and protocol evolution.
     * 
     * @return the protocol version string
     */
    String getVersion();

    /**
     * Gets the timestamp when this message was created.
     * Used for message ordering and timeout handling.
     * 
     * @return the message creation timestamp
     */
    Instant getTimestamp();

    /**
     * Gets the unique request identifier for this message.
     * Used for correlating request and response messages.
     * 
     * @return the request identifier
     */
    String getRequestId();
}