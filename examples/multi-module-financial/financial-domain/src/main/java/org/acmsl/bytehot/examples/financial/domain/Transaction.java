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
 * Filename: Transaction.java
 *
 * Author: Claude Code
 *
 * Class name: Transaction
 *
 * Responsibilities:
 *   - Act as aggregate root for financial transaction processing
 *   - Enforce business invariants and rules through domain events
 *   - Provide hot-swappable domain logic for transaction workflows
 *
 * Collaborators:
 *   - TransactionRequestedEvent: Incoming domain event
 *   - TransactionProcessedEvent: Outgoing domain event  
 *   - TransactionRejectedEvent: Outgoing domain event
 *   - Money: Value object for monetary amounts
 *   - TransactionType: Value object for transaction classification
 */
package org.acmsl.bytehot.examples.financial.domain;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction aggregate root following DDD and JavaEDA patterns.
 * Handles transaction processing through domain events with hot-swappable business logic.
 * @author Claude Code
 * @since 2025-07-04
 */
public class Transaction {

    /**
     * Unique transaction identifier.
     */
    @NotNull
    private final String transactionId;

    /**
     * Source account identifier.
     */
    @NotBlank
    private final String fromAccountId;

    /**
     * Destination account identifier.
     */
    @NotBlank
    private final String toAccountId;

    /**
     * Transaction amount.
     */
    @NotNull
    private final Money amount;

    /**
     * Transaction type.
     */
    @NotNull
    private final TransactionType type;

    /**
     * Transaction reference.
     */
    private final String reference;

    /**
     * Transaction timestamp.
     */
    @NotNull
    private final LocalDateTime createdAt;

    /**
     * Current transaction status.
     */
    @NotNull
    private volatile TransactionStatus status;

    /**
     * Risk assessment level.
     */
    private volatile RiskLevel riskLevel;

    /**
     * Calculated fees.
     */
    private volatile Money fees;

    /**
     * Creates a new Transaction aggregate.
     * @param fromAccountId Source account
     * @param toAccountId Destination account
     * @param amount Transaction amount
     * @param type Transaction type
     * @param reference Transaction reference
     */
    public Transaction(final String fromAccountId,
                      final String toAccountId,
                      final Money amount,
                      final TransactionType type,
                      final String reference) {
        this.transactionId = UUID.randomUUID().toString();
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.reference = reference;
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.riskLevel = RiskLevel.LOW;
        this.fees = Money.zero(amount.getCurrency());
    }

    /**
     * Primary port: accepts TransactionRequestedEvent and processes it.
     * This method can be hot-swapped to change transaction processing logic.
     * @param event The incoming transaction request event
     * @return List of resulting domain events
     */
    public static List<DomainResponseEvent<TransactionRequestedEvent>> accept(final TransactionRequestedEvent event) {
        List<DomainResponseEvent<TransactionRequestedEvent>> results = new ArrayList<>();
        
        try {
            // Create transaction from event (can be hot-swapped)
            Transaction transaction = createFromEvent(event);
            
            // Validate transaction (can be hot-swapped)
            if (!transaction.isValid()) {
                results.add(new TransactionRejectedEvent(event, "Transaction validation failed"));
                return results;
            }
            
            // Assess risk (can be hot-swapped)
            transaction.assessRisk();
            
            // Calculate fees (can be hot-swapped)
            transaction.calculateFees();
            
            // Apply business rules (can be hot-swapped)
            if (!transaction.meetsBusinessRules()) {
                results.add(new TransactionRejectedEvent(event, "Business rules violation"));
                return results;
            }
            
            // Process transaction (can be hot-swapped)
            transaction.process();
            
            results.add(new TransactionProcessedEvent(event, transaction));
            
        } catch (Exception e) {
            results.add(new TransactionRejectedEvent(event, "Processing error: " + e.getMessage()));
        }
        
        return results;
    }

    /**
     * Creates transaction from domain event.
     * This method can be hot-swapped to change event mapping.
     * @param event The transaction requested event
     * @return New transaction instance
     */
    protected static Transaction createFromEvent(final TransactionRequestedEvent event) {
        return new Transaction(
            event.getFromAccountId(),
            event.getToAccountId(),
            event.getAmount(),
            event.getType(),
            event.getReference()
        );
    }

    /**
     * Validates transaction according to domain rules.
     * This method can be hot-swapped to change validation logic.
     * @return true if valid, false otherwise
     */
    protected boolean isValid() {
        // Basic validation (can be hot-swapped)
        if (fromAccountId == null || fromAccountId.trim().isEmpty()) {
            return false;
        }
        
        if (toAccountId == null || toAccountId.trim().isEmpty()) {
            return false;
        }
        
        if (amount == null || !amount.isPositive()) {
            return false;
        }
        
        if (type == null) {
            return false;
        }
        
        // Self-transfer prevention (can be hot-swapped)
        if (fromAccountId.equals(toAccountId)) {
            return false;
        }
        
        return true;
    }

    /**
     * Assesses transaction risk level.
     * This method can be hot-swapped to change risk assessment algorithms.
     */
    protected void assessRisk() {
        int riskScore = 0;
        
        // Amount-based risk (can be hot-swapped)
        if (amount.getAmount().compareTo(BigDecimal.valueOf(100000)) > 0) {
            riskScore += 5;
        } else if (amount.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0) {
            riskScore += 2;
        }
        
        // Type-based risk (can be hot-swapped)
        switch (type) {
            case INTERNATIONAL_TRANSFER:
                riskScore += 4;
                break;
            case WIRE_TRANSFER:
                riskScore += 2;
                break;
            case INTERNAL_TRANSFER:
                riskScore += 0;
                break;
            case PAYMENT:
                riskScore += 1;
                break;
        }
        
        // Time-based risk (can be hot-swapped)
        int hour = createdAt.getHour();
        if (hour < 6 || hour > 22) {
            riskScore += 2;
        }
        
        // Determine risk level (can be hot-swapped)
        if (riskScore >= 8) {
            this.riskLevel = RiskLevel.HIGH;
        } else if (riskScore >= 4) {
            this.riskLevel = RiskLevel.MEDIUM;
        } else {
            this.riskLevel = RiskLevel.LOW;
        }
    }

    /**
     * Calculates transaction fees.
     * This method can be hot-swapped to change fee calculation logic.
     */
    protected void calculateFees() {
        BigDecimal feeAmount = BigDecimal.ZERO;
        
        // Type-based fees (can be hot-swapped)
        switch (type) {
            case WIRE_TRANSFER:
                feeAmount = BigDecimal.valueOf(25.00);
                break;
            case INTERNATIONAL_TRANSFER:
                feeAmount = amount.getAmount().multiply(BigDecimal.valueOf(0.003))
                    .max(BigDecimal.valueOf(50.00));
                break;
            case INTERNAL_TRANSFER:
                feeAmount = BigDecimal.ZERO;
                break;
            case PAYMENT:
                feeAmount = BigDecimal.valueOf(2.50);
                break;
        }
        
        // Risk-based fee adjustment (can be hot-swapped)
        if (riskLevel == RiskLevel.HIGH) {
            feeAmount = feeAmount.multiply(BigDecimal.valueOf(1.5));
        }
        
        this.fees = new Money(feeAmount, amount.getCurrency());
    }

    /**
     * Checks if transaction meets business rules.
     * This method can be hot-swapped to change business rule evaluation.
     * @return true if rules are met, false otherwise
     */
    protected boolean meetsBusinessRules() {
        // Daily limit check (can be hot-swapped)
        if (amount.getAmount().compareTo(BigDecimal.valueOf(250000)) > 0) {
            return false;
        }
        
        // High risk approval requirement (can be hot-swapped)
        if (riskLevel == RiskLevel.HIGH && !hasManualApproval()) {
            return false;
        }
        
        // Type-specific rules (can be hot-swapped)
        switch (type) {
            case WIRE_TRANSFER:
                return reference != null && !reference.trim().isEmpty();
            case INTERNATIONAL_TRANSFER:
                return amount.getAmount().compareTo(BigDecimal.valueOf(100)) >= 0;
            default:
                return true;
        }
    }

    /**
     * Processes the transaction.
     * This method can be hot-swapped to change processing logic.
     */
    protected void process() {
        // Mark as processing
        this.status = TransactionStatus.PROCESSING;
        
        // Simulate processing time based on type (can be hot-swapped)
        try {
            switch (type) {
                case INTERNAL_TRANSFER:
                    Thread.sleep(100);
                    break;
                case WIRE_TRANSFER:
                    Thread.sleep(500);
                    break;
                case INTERNATIONAL_TRANSFER:
                    Thread.sleep(1000);
                    break;
                default:
                    Thread.sleep(200);
                    break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mark as completed
        this.status = TransactionStatus.COMPLETED;
    }

    /**
     * Checks if transaction has manual approval.
     * This method can be hot-swapped to change approval detection.
     * @return true if manually approved, false otherwise
     */
    protected boolean hasManualApproval() {
        return reference != null && reference.contains("APPROVED");
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getFromAccountId() { return fromAccountId; }
    public String getToAccountId() { return toAccountId; }
    public Money getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getReference() { return reference; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TransactionStatus getStatus() { return status; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public Money getFees() { return fees; }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', from='%s', to='%s', amount=%s, type=%s, status=%s}",
                           transactionId, fromAccountId, toAccountId, amount, type, status);
    }
}