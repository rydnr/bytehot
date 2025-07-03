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
 * Filename: PluginCommunicationHandler.java
 *
 * Author: Claude Code
 *
 * Class name: PluginCommunicationHandler
 *
 * Responsibilities:
 *   - Establish communication with ByteHot agent
 *   - Send and receive plugin messages
 *   - Handle communication errors and reconnection
 *   - Provide asynchronous message handling
 *
 * Collaborators:
 *   - PluginMessage: Message protocol interface
 *   - MessageHandler: Handles incoming messages
 *   - PluginBase: Uses communication during operations
 *   - CompletableFuture: Asynchronous message responses
 */
package org.acmsl.bytehot.plugin.core;

import java.util.concurrent.CompletableFuture;

/**
 * Handles communication between plugins and the ByteHot agent.
 * Provides asynchronous message passing with error handling and reconnection.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public interface PluginCommunicationHandler {

    /**
     * Sends a message to the ByteHot agent asynchronously.
     * 
     * @param message the message to send
     * @return CompletableFuture containing the response message
     */
    CompletableFuture<PluginMessage> sendMessage(final PluginMessage message);

    /**
     * Registers a handler for incoming messages of a specific type.
     * 
     * @param messageType the type of message to handle
     * @param handler the handler to register
     */
    void registerMessageHandler(final String messageType, final MessageHandler handler);

    /**
     * Starts listening for incoming messages from the ByteHot agent.
     * This method should be non-blocking and return immediately.
     */
    void startListening();

    /**
     * Stops listening for incoming messages and cleans up resources.
     */
    void stopListening();

    /**
     * Establishes connection with the ByteHot agent.
     * 
     * @return true if connection was established successfully, false otherwise
     */
    boolean connect();

    /**
     * Checks if the communication handler is currently connected.
     * 
     * @return true if connected, false otherwise
     */
    boolean isConnected();

    /**
     * Disconnects from the ByteHot agent and cleans up resources.
     */
    void disconnect();
}

/**
 * Handles incoming messages from the ByteHot agent.
 * Implementations process specific message types and generate appropriate responses.
 */
@FunctionalInterface
interface MessageHandler {

    /**
     * Handles an incoming message.
     * 
     * @param message the incoming message to handle
     * @return optional response message, or null if no response needed
     */
    PluginMessage handleMessage(final PluginMessage message);
}