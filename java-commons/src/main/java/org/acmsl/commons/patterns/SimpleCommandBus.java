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
 * Filename: SimpleCommandBus.java
 *
 * Author: Claude Code
 *
 * Class name: SimpleCommandBus
 *
 * Responsibilities:
 *   - Provide simple in-memory implementation of CommandBus
 *   - Support synchronous and asynchronous command execution
 *   - Manage command handler registration and invocation
 *   - Provide thread-safe operations for concurrent usage
 *
 * Collaborators:
 *   - CommandBus: Interface this class implements
 *   - CommandHandler: Registered handlers for command processing
 *   - Command: Commands executed through this bus
 */
package org.acmsl.commons.patterns;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple thread-safe implementation of CommandBus using in-memory storage.
 * Supports both synchronous and asynchronous command execution with
 * concurrent handler management.
 * 
 * @author Claude Code
 * @since 2025-06-26
 */
public class SimpleCommandBus implements CommandBus {

    /**
     * Map of command types to their registered handlers
     */
    protected final Map<Class<? extends Command<?>>, CommandBusHandler<?, ?>> handlers;

    /**
     * Counter for executed commands
     */
    protected final AtomicLong executedCommandCount;

    /**
     * Executor for asynchronous command processing
     */
    protected final Executor asyncExecutor;

    /**
     * Flag indicating if this bus is healthy
     */
    protected volatile boolean healthy;

    /**
     * Default constructor using ForkJoinPool for async execution.
     */
    public SimpleCommandBus() {
        this(ForkJoinPool.commonPool());
    }

    /**
     * Constructor with custom executor for async operations.
     * 
     * @param asyncExecutor the executor to use for async command processing
     */
    public SimpleCommandBus(final Executor asyncExecutor) {
        this.handlers = new ConcurrentHashMap<>();
        this.executedCommandCount = new AtomicLong(0);
        this.asyncExecutor = asyncExecutor;
        this.healthy = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R execute(final Command<R> command) throws CommandBusException {
        if (!healthy) {
            throw new CommandBusException("CommandBus is not healthy");
        }

        if (command == null) {
            throw new CommandBusException("Cannot execute null command");
        }

        final Class<? extends Command<?>> commandType = (Class<? extends Command<?>>) command.getClass();
        final CommandBusHandler<?, ?> handler = handlers.get(commandType);

        if (handler == null) {
            throw new CommandBusException("No handler registered for command type: " + commandType.getSimpleName());
        }

        try {
            @SuppressWarnings("unchecked")
            final CommandBusHandler<Command<R>, R> typedHandler = (CommandBusHandler<Command<R>, R>) handler;
            
            final R result = typedHandler.handle(command);
            executedCommandCount.incrementAndGet();
            return result;
        } catch (final Exception e) {
            throw new CommandBusException("Failed to execute command: " + commandType.getSimpleName(), e);
        }
    }

    @Override
    public <R> CompletableFuture<R> executeAsync(final Command<R> command) {
        if (!healthy) {
            return CompletableFuture.failedFuture(new CommandBusException("CommandBus is not healthy"));
        }

        if (command == null) {
            return CompletableFuture.failedFuture(new CommandBusException("Cannot execute null command"));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(command);
            } catch (final CommandBusException e) {
                throw new RuntimeException(e);
            }
        }, asyncExecutor);
    }

    @Override
    public void executeVoid(final Command<Void> command) throws CommandBusException {
        execute(command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Command<R>, R> void registerHandler(final Class<C> commandType, 
                                                          final CommandBusHandler<C, R> handler) {
        if (commandType == null) {
            throw new IllegalArgumentException("Command type cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        final CommandBusHandler<?, ?> existingHandler = handlers.put(
            (Class<? extends Command<?>>) commandType, 
            handler
        );

        if (existingHandler != null) {
            // Log warning about handler replacement
            System.err.println("Warning: Replaced existing handler for command type: " + commandType.getSimpleName());
        }
    }

    @Override
    public <C extends Command<?>> void unregisterHandler(final Class<C> commandType) {
        if (commandType == null) {
            return;
        }

        handlers.remove(commandType);
    }

    @Override
    public boolean hasHandler(final Class<? extends Command<?>> commandType) {
        if (commandType == null) {
            return false;
        }

        return handlers.containsKey(commandType);
    }

    @Override
    public long getExecutedCommandCount() {
        return executedCommandCount.get();
    }

    @Override
    public boolean isHealthy() {
        return healthy;
    }

    @Override
    public void clear() {
        handlers.clear();
        executedCommandCount.set(0);
    }

    /**
     * Sets the health status of this CommandBus.
     * 
     * @param healthy the new health status
     */
    public void setHealthy(final boolean healthy) {
        this.healthy = healthy;
    }

    /**
     * Gets the number of registered command handlers.
     * 
     * @return the number of registered handlers
     */
    public int getRegisteredHandlerCount() {
        return handlers.size();
    }

    /**
     * Gets a summary of registered handlers for debugging.
     * 
     * @return summary string of handler registrations
     */
    public String getHandlerSummary() {
        final StringBuilder summary = new StringBuilder();
        summary.append("SimpleCommandBus Handler Summary:\n");
        summary.append("Total command types: ").append(handlers.size()).append("\n");
        summary.append("Total executed commands: ").append(executedCommandCount.get()).append("\n");
        summary.append("Health status: ").append(healthy).append("\n");
        
        for (final Class<? extends Command<?>> commandType : handlers.keySet()) {
            final CommandBusHandler<?, ?> handler = handlers.get(commandType);
            summary.append("- ").append(commandType.getSimpleName())
                   .append(": ").append(handler.getClass().getSimpleName()).append("\n");
        }
        
        return summary.toString();
    }

}