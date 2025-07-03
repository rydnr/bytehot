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
 * Filename: DefaultPluginCommunicationHandler.java
 *
 * Author: Claude Code
 *
 * Class name: DefaultPluginCommunicationHandler
 *
 * Responsibilities:
 *   - Provide default implementation of plugin communication
 *   - Handle basic message passing patterns
 *   - Implement connection lifecycle management
 *   - Serve as base for specialized communication handlers
 *
 * Collaborators:
 *   - PluginMessage: Messages being communicated
 *   - MessageHandler: Handles incoming messages
 *   - CompletableFuture: Asynchronous message responses
 *   - ConcurrentHashMap: Thread-safe handler registration
 */
package org.acmsl.bytehot.plugin.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default implementation of PluginCommunicationHandler.
 * Provides basic communication functionality that can be extended by specific implementations.
 * 
 * @author Claude Code
 * @since 2025-07-03
 */
public class DefaultPluginCommunicationHandler implements PluginCommunicationHandler {

    /**
     * Map of message type to handler.
     */
    protected final ConcurrentMap<String, MessageHandler> messageHandlers = new ConcurrentHashMap<>();

    /**
     * Connection status flag.
     */
    protected volatile boolean connected = false;

    /**
     * Listening status flag.
     */
    protected volatile boolean listening = false;

    @Override
    public CompletableFuture<PluginMessage> sendMessage(final PluginMessage message) {
        if (!connected) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("Communication handler not connected"));
        }

        if (message == null) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("Message cannot be null"));
        }

        // Default implementation - subclasses should override with actual communication
        return CompletableFuture.completedFuture(createDefaultResponse(message));
    }

    @Override
    public void registerMessageHandler(final String messageType, final MessageHandler handler) {
        if (messageType == null || messageType.trim().isEmpty()) {
            throw new IllegalArgumentException("Message type cannot be null or empty");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Message handler cannot be null");
        }

        messageHandlers.put(messageType, handler);
    }

    @Override
    public void startListening() {
        if (!connected) {
            throw new IllegalStateException("Must be connected before starting to listen");
        }
        listening = true;
    }

    @Override
    public void stopListening() {
        listening = false;
    }

    @Override
    public boolean connect() {
        // Default implementation - always succeeds
        // Subclasses should override with actual connection logic
        connected = true;
        return true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void disconnect() {
        stopListening();
        connected = false;
    }

    /**
     * Creates a default response for a given message.
     * Subclasses can override to provide more sophisticated responses.
     * 
     * @param originalMessage the message to respond to
     * @return a default response message
     */
    protected PluginMessage createDefaultResponse(final PluginMessage originalMessage) {
        return new DefaultPluginMessage(
            "response", 
            originalMessage.getVersion(),
            originalMessage.getRequestId()
        );
    }

    /**
     * Handles an incoming message using registered handlers.
     * 
     * @param message the incoming message
     * @return the response message, or null if no handler or no response
     */
    protected PluginMessage handleIncomingMessage(final PluginMessage message) {
        if (message == null || !listening) {
            return null;
        }

        final MessageHandler handler = messageHandlers.get(message.getType());
        if (handler != null) {
            return handler.handleMessage(message);
        }

        return null;
    }
}