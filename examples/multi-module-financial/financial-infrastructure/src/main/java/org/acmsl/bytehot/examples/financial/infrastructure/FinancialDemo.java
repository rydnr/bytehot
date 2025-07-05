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
 * Filename: FinancialDemo.java
 *
 * Author: Claude Code
 *
 * Class name: FinancialDemo
 *
 * Responsibilities:
 *   - Demonstrate ByteHot hot-swapping capabilities in financial processing
 *   - Show hexagonal architecture with domain, application, and infrastructure layers
 *   - Provide examples of transaction processing with various scenarios
 *
 * Collaborators:
 *   - FinancialApplication: Core application for processing transactions
 *   - FinancialEventRouter: Routes events to appropriate handlers
 *   - TransactionRequestedEvent: Domain events for transaction processing
 */
package org.acmsl.bytehot.examples.financial.infrastructure;

import org.acmsl.bytehot.examples.financial.application.FinancialApplication;
import org.acmsl.bytehot.examples.financial.application.FinancialEventRouter;
import org.acmsl.bytehot.examples.financial.domain.Money;
import org.acmsl.bytehot.examples.financial.domain.TransactionRequestedEvent;
import org.acmsl.bytehot.examples.financial.domain.TransactionType;
import org.acmsl.commons.patterns.DomainResponseEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Demo application showcasing ByteHot hot-swapping in financial transaction processing.
 * @author Claude Code
 * @since 2025-07-04
 */
public class FinancialDemo {

    /**
     * Logger for demo operations.
     */
    private static final Logger LOGGER = Logger.getLogger(FinancialDemo.class.getName());

    /**
     * Main entry point for the demo.
     * @param args Command-line arguments
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting ByteHot Financial Processing Demo");
        
        // Initialize the hexagonal architecture
        FinancialEventRouter router = new FinancialEventRouter();
        FinancialApplication application = new FinancialApplication(router);
        
        FinancialDemo demo = new FinancialDemo(application);
        demo.runDemo();
    }

    /**
     * Financial application instance.
     */
    private final FinancialApplication application;

    /**
     * Creates a new FinancialDemo instance.
     * @param application The financial application
     */
    public FinancialDemo(final FinancialApplication application) {
        this.application = application;
    }

    /**
     * Runs the demo scenarios.
     */
    public void runDemo() {
        LOGGER.info("=== ByteHot Financial Processing Demo ===");
        
        // Scenario 1: Successful internal transfer
        demonstrateInternalTransfer();
        
        // Scenario 2: Wire transfer with fees
        demonstrateWireTransfer();
        
        // Scenario 3: High-risk international transfer
        demonstrateInternationalTransfer();
        
        // Scenario 4: Invalid transaction (validation failure)
        demonstrateInvalidTransaction();
        
        // Scenario 5: Business rules violation
        demonstrateBusinessRulesViolation();
        
        LOGGER.info("=== Demo Complete ===");
    }

    /**
     * Demonstrates a successful internal transfer.
     */
    protected void demonstrateInternalTransfer() {
        LOGGER.info("\n--- Scenario 1: Internal Transfer ---");
        
        TransactionRequestedEvent event = new TransactionRequestedEvent(
            "ACC001", "ACC002", 
            new Money(BigDecimal.valueOf(1000.00), "USD"),
            TransactionType.INTERNAL_TRANSFER,
            "Internal transfer demo"
        );
        
        processTransaction("Internal Transfer", event);
    }

    /**
     * Demonstrates a wire transfer with fees.
     */
    protected void demonstrateWireTransfer() {
        LOGGER.info("\n--- Scenario 2: Wire Transfer ---");
        
        TransactionRequestedEvent event = new TransactionRequestedEvent(
            "ACC003", "ACC004", 
            new Money(BigDecimal.valueOf(5000.00), "USD"),
            TransactionType.WIRE_TRANSFER,
            "WIRE-REF-12345"
        );
        
        processTransaction("Wire Transfer", event);
    }

    /**
     * Demonstrates a high-risk international transfer.
     */
    protected void demonstrateInternationalTransfer() {
        LOGGER.info("\n--- Scenario 3: International Transfer (High Risk) ---");
        
        TransactionRequestedEvent event = new TransactionRequestedEvent(
            "ACC005", "ACC006", 
            new Money(BigDecimal.valueOf(150000.00), "USD"),
            TransactionType.INTERNATIONAL_TRANSFER,
            "INTL-APPROVED-78901"
        );
        
        processTransaction("International Transfer", event);
    }

    /**
     * Demonstrates an invalid transaction.
     */
    protected void demonstrateInvalidTransaction() {
        LOGGER.info("\n--- Scenario 4: Invalid Transaction (Same Account) ---");
        
        TransactionRequestedEvent event = new TransactionRequestedEvent(
            "ACC007", "ACC007", 
            new Money(BigDecimal.valueOf(500.00), "USD"),
            TransactionType.PAYMENT,
            "Self-payment attempt"
        );
        
        processTransaction("Invalid Transaction", event);
    }

    /**
     * Demonstrates a business rules violation.
     */
    protected void demonstrateBusinessRulesViolation() {
        LOGGER.info("\n--- Scenario 5: Business Rules Violation (Daily Limit) ---");
        
        TransactionRequestedEvent event = new TransactionRequestedEvent(
            "ACC008", "ACC009", 
            new Money(BigDecimal.valueOf(300000.00), "USD"),
            TransactionType.WIRE_TRANSFER,
            "Large transfer"
        );
        
        processTransaction("Business Rules Violation", event);
    }

    /**
     * Processes a transaction and logs the results.
     * @param scenarioName The name of the scenario
     * @param event The transaction event
     */
    protected void processTransaction(final String scenarioName, final TransactionRequestedEvent event) {
        LOGGER.info("Processing: " + scenarioName);
        LOGGER.info("Event: " + event);
        
        try {
            List<DomainResponseEvent<?>> results = application.accept(event);
            
            LOGGER.info("Results (" + results.size() + "):");
            for (DomainResponseEvent<?> result : results) {
                LOGGER.info("  → " + result);
            }
            
        } catch (Exception e) {
            LOGGER.severe("Error processing transaction: " + e.getMessage());
        }
    }
}