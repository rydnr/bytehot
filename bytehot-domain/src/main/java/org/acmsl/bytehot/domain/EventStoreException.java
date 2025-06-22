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
 * Filename: EventStoreException.java
 *
 * Author: Claude Code
 *
 * Class name: EventStoreException
 *
 * Responsibilities:
 *   - Handle errors related to event store operations
 *   - Provide specific error types for different failure scenarios
 *   - Enable proper error handling and recovery strategies
 *
 * Collaborators:
 *   - EventStorePort: Throws this exception for operation failures
 *   - FilesystemEventStoreAdapter: Infrastructure implementation
 */
package org.acmsl.bytehot.domain;

/**
 * Exception thrown when event store operations fail
 * @author Claude Code
 * @since 2025-06-17
 */
public class EventStoreException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * The type of event store operation that failed
     */
    public enum OperationType {
        SAVE,
        RETRIEVE,
        COUNT,
        HEALTH_CHECK,
        INITIALIZATION
    }

    /**
     * The operation that failed
     */
    private final OperationType operationType;

    /**
     * The aggregate type involved (if applicable)
     */
    private final String aggregateType;

    /**
     * The aggregate ID involved (if applicable)
     */
    private final String aggregateId;

    /**
     * Constructor for general event store exceptions
     * @param message the error message
     * @param operationType the type of operation that failed
     */
    public EventStoreException(String message, OperationType operationType) {
        super(message);
        this.operationType = operationType;
        this.aggregateType = null;
        this.aggregateId = null;
    }

    /**
     * Constructor for event store exceptions with cause
     * @param message the error message
     * @param cause the underlying cause
     * @param operationType the type of operation that failed
     */
    public EventStoreException(String message, Throwable cause, OperationType operationType) {
        super(message, cause);
        this.operationType = operationType;
        this.aggregateType = null;
        this.aggregateId = null;
    }

    /**
     * Constructor for aggregate-specific exceptions
     * @param message the error message
     * @param operationType the type of operation that failed
     * @param aggregateType the aggregate type involved
     * @param aggregateId the aggregate ID involved
     */
    public EventStoreException(
        String message,
        OperationType operationType,
        String aggregateType,
        String aggregateId
    ) {
        super(message);
        this.operationType = operationType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }

    /**
     * Constructor for aggregate-specific exceptions with cause
     * @param message the error message
     * @param cause the underlying cause
     * @param operationType the type of operation that failed
     * @param aggregateType the aggregate type involved
     * @param aggregateId the aggregate ID involved
     */
    public EventStoreException(
        String message,
        Throwable cause,
        OperationType operationType,
        String aggregateType,
        String aggregateId
    ) {
        super(message, cause);
        this.operationType = operationType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }

    /**
     * Gets the operation type that failed
     * @return the operation type
     */
    public OperationType getOperationType() {
        return operationType;
    }

    /**
     * Gets the aggregate type involved
     * @return the aggregate type, or null if not applicable
     */
    public String getAggregateType() {
        return aggregateType;
    }

    /**
     * Gets the aggregate ID involved
     * @return the aggregate ID, or null if not applicable
     */
    public String getAggregateId() {
        return aggregateId;
    }

    /**
     * Checks if this exception is related to a specific aggregate
     * @return true if aggregate type and ID are specified
     */
    public boolean isAggregateSpecific() {
        return aggregateType != null && aggregateId != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EventStoreException{");
        sb.append("operationType=").append(operationType);
        
        if (aggregateType != null) {
            sb.append(", aggregateType='").append(aggregateType).append("'");
        }
        
        if (aggregateId != null) {
            sb.append(", aggregateId='").append(aggregateId).append("'");
        }
        
        sb.append(", message='").append(getMessage()).append("'");
        sb.append("}");
        
        return sb.toString();
    }
}