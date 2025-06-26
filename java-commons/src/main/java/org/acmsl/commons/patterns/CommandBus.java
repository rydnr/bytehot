/*
                        Commons

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
 * Filename: CommandBus.java
 *
 * Author: Claude Code
 *
 * Interface name: CommandBus
 *
 * Responsibilities:
 *   - Provide abstraction for command execution and dispatching
 *   - Enable decoupled command processing in CQRS architectures
 *   - Support both synchronous and asynchronous command execution
 *   - Integrate with existing Command and CommandHandler patterns
 *
 * Collaborators:
 *   - Command: Commands executed through the bus
 *   - CommandHandler: Handlers for specific command types
 *   - Application: Uses CommandBus for coordinating command execution
 */
package org.acmsl.commons.patterns;

import java.util.concurrent.CompletableFuture;

/**
 * Generic CommandBus abstraction for command execution and dispatching.
 * Provides decoupled command processing mechanism with support for both
 * synchronous and asynchronous execution patterns.
 * 
 * @author Claude Code
 * @since 2025-06-26
 */
public interface CommandBus {

    /**
     * Executes a command synchronously and returns the result.
     * 
     * @param command the command to execute
     * @param <R> the return type of the command execution
     * @return the result of command execution
     * @throws CommandBusException if command execution fails
     */
    <R> R execute(final Command<R> command) throws CommandBusException;

    /**
     * Executes a command asynchronously and returns a CompletableFuture.
     * 
     * @param command the command to execute
     * @param <R> the return type of the command execution
     * @return CompletableFuture containing the execution result
     */
    <R> CompletableFuture<R> executeAsync(final Command<R> command);

    /**
     * Executes a command without expecting a return value.
     * 
     * @param command the command to execute
     * @throws CommandBusException if command execution fails
     */
    void executeVoid(final Command<Void> command) throws CommandBusException;

    /**
     * Registers a command handler for a specific command type.
     * 
     * @param commandType the type of command to handle
     * @param handler the handler to register
     * @param <C> the specific Command type
     * @param <R> the return type of the command
     */
    <C extends Command<R>, R> void registerHandler(final Class<C> commandType, 
                                                   final CommandBusHandler<C, R> handler);

    /**
     * Unregisters a command handler for a specific command type.
     * 
     * @param commandType the type of command to stop handling
     * @param <C> the specific Command type
     */
    <C extends Command<?>> void unregisterHandler(final Class<C> commandType);

    /**
     * Checks if a handler is registered for the given command type.
     * 
     * @param commandType the command type to check
     * @return true if a handler is registered, false otherwise
     */
    boolean hasHandler(final Class<? extends Command<?>> commandType);

    /**
     * Gets the total number of commands executed through this bus.
     * 
     * @return the total command execution count
     */
    long getExecutedCommandCount();

    /**
     * Checks if the CommandBus is currently healthy and able to process commands.
     * 
     * @return true if healthy, false otherwise
     */
    boolean isHealthy();

    /**
     * Clears all registered command handlers.
     */
    void clear();

    /**
     * Exception thrown when CommandBus operations fail.
     */
    class CommandBusException extends RuntimeException {
        /**
         * Serial version UID for serialization compatibility.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance.
         * @param message the message.
         */
        public CommandBusException(final String message) {
            super(message);
        }

        /**
         * Creates a new instance.
         * @param message the message.
         * @param cause the underlying cause.
         */
        public CommandBusException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Enhanced Command interface with return type support.
     * 
     * @param <R> the return type of command execution
     */
    interface Command<R> extends org.acmsl.commons.patterns.Command {
        // Marker interface extending existing Command pattern
    }

    /**
     * CommandBus-specific handler interface that returns values.
     * 
     * @param <C> the command type
     * @param <R> the return type
     */
    interface CommandBusHandler<C extends Command<R>, R> {
        /**
         * Handles the given command and returns a result.
         * 
         * @param command the command to handle
         * @return the result of command execution
         * @throws Exception if command handling fails
         */
        R handle(final C command) throws Exception;
    }
}
