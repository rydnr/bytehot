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
 * Filename: FinancialCLI.java
 *
 * Author: Claude Code
 *
 * Class name: FinancialCLI
 *
 * Responsibilities:
 *   - Parse command-line arguments for financial transactions
 *   - Create and emit TransactionRequestedEvent from CLI input
 *   - Handle CLI-specific error conditions and responses
 *
 * Collaborators:
 *   - FinancialApplication: Processes domain events
 *   - TransactionRequestedEvent: Domain event created from CLI input
 *   - TransactionCLIAdapter: Handles transaction command parsing
 */
package org.acmsl.bytehot.examples.financial.infrastructure.cli;

import org.acmsl.bytehot.examples.financial.application.FinancialApplication;
import org.acmsl.bytehot.examples.financial.application.FinancialEventRouter;
import org.acmsl.bytehot.examples.financial.domain.Money;
import org.acmsl.bytehot.examples.financial.domain.TransactionRequestedEvent;
import org.acmsl.bytehot.examples.financial.domain.TransactionType;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Command-line interface for financial transaction processing.
 * @author Claude Code
 * @since 2025-07-04
 */
public class FinancialCLI {

    /**
     * Logger for CLI operations.
     */
    private static final Logger LOGGER = Logger.getLogger(FinancialCLI.class.getName());

    /**
     * Financial application instance.
     */
    @NotNull
    private final FinancialApplication application;

    /**
     * Creates a new FinancialCLI instance.
     * @param application The financial application
     */
    public FinancialCLI(@NotNull final FinancialApplication application) {
        this.application = application;
    }

    /**
     * Main entry point for the CLI.
     * @param args Command-line arguments
     */
    public static void main(final String[] args) {
        FinancialEventRouter router = new FinancialEventRouter();
        FinancialApplication app = new FinancialApplication(router);
        FinancialCLI cli = new FinancialCLI(app);
        
        cli.run(args);
    }

    /**
     * Runs the CLI with provided arguments.
     * @param args Command-line arguments
     */
    public void run(final String[] args) {
        try {
            if (args.length == 0) {
                showUsage();
                return;
            }

            String command = args[0].toLowerCase();
            
            switch (command) {
                case "transfer":
                    handleTransferCommand(args);
                    break;
                case "help":
                    showUsage();
                    break;
                default:
                    LOGGER.warning("Unknown command: " + command);
                    showUsage();
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing command: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Handles transfer command.
     * @param args Command arguments
     */
    protected void handleTransferCommand(final String[] args) {
        if (args.length < 6) {
            System.err.println("Usage: transfer <from-account> <to-account> <amount> <currency> <type> [reference]");
            return;
        }

        try {
            String fromAccount = args[1];
            String toAccount = args[2];
            BigDecimal amount = new BigDecimal(args[3]);
            String currency = args[4];
            TransactionType type = TransactionType.valueOf(args[5].toUpperCase());
            String reference = args.length > 6 ? args[6] : null;

            Money transactionAmount = new Money(amount, currency);
            
            TransactionRequestedEvent event = new TransactionRequestedEvent(
                fromAccount, toAccount, transactionAmount, type, reference
            );

            LOGGER.info("Processing transaction request: " + event);
            
            List<DomainResponseEvent<?>> results = application.accept(event);
            
            for (DomainResponseEvent<?> result : results) {
                System.out.println("Result: " + result);
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument: " + e.getMessage());
            showTransferUsage();
        } catch (Exception e) {
            System.err.println("Error processing transfer: " + e.getMessage());
        }
    }

    /**
     * Shows general usage information.
     */
    protected void showUsage() {
        System.out.println("ByteHot Financial Transaction Processing CLI");
        System.out.println();
        System.out.println("Usage: java -jar financial-cli.jar <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  transfer  - Process a financial transaction");
        System.out.println("  help      - Show this help message");
        System.out.println();
        System.out.println("For command-specific help, use: <command> --help");
    }

    /**
     * Shows transfer command usage.
     */
    protected void showTransferUsage() {
        System.out.println("Transfer Command Usage:");
        System.out.println("  transfer <from-account> <to-account> <amount> <currency> <type> [reference]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  from-account  - Source account identifier");
        System.out.println("  to-account    - Destination account identifier");
        System.out.println("  amount        - Transaction amount (decimal)");
        System.out.println("  currency      - Currency code (e.g., USD, EUR)");
        System.out.println("  type          - Transaction type:");
        System.out.println("                  WIRE_TRANSFER, INTERNATIONAL_TRANSFER,");
        System.out.println("                  INTERNAL_TRANSFER, PAYMENT");
        System.out.println("  reference     - Optional transaction reference");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  transfer ACC001 ACC002 1000.50 USD WIRE_TRANSFER REF123");
        System.out.println("  transfer ACC001 ACC002 500.00 EUR INTERNAL_TRANSFER");
    }
}