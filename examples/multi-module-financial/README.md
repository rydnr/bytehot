# ByteHot Financial Processing System

This example demonstrates ByteHot's hot-swapping capabilities in a properly structured financial transaction processing system using Domain-Driven Design (DDD) and Hexagonal Architecture.

## Architecture Overview

The system follows strict hexagonal architecture principles with clear separation of concerns:

### Domain Layer (`financial-domain`)
- **Transaction** - Aggregate root handling transaction processing logic
- **Money** - Value object for monetary amounts with currency
- **TransactionType** - Enumeration of transaction types (WIRE_TRANSFER, INTERNATIONAL_TRANSFER, etc.)
- **TransactionStatus** - Enumeration of transaction lifecycle states
- **RiskLevel** - Enumeration of risk assessment levels
- **Domain Events** - TransactionRequestedEvent, TransactionProcessedEvent, TransactionRejectedEvent

### Application Layer (`financial-application`)
- **FinancialApplication** - Main application coordinator implementing JavaEDA patterns
- **FinancialEventRouter** - Routes domain events to appropriate aggregates
- **UnknownEventResponse** - Handles unsupported event types

### Infrastructure Layer (`financial-infrastructure`)
- **FinancialCLI** - Command-line interface for transaction processing
- **FinancialDemo** - Demo application showcasing various scenarios
- **REST APIs** - HTTP endpoints for transaction processing (planned)
- **Persistence** - Event sourcing repositories (planned)
- **Messaging** - Event publishing adapters (planned)

## Key Hot-Swapping Scenarios

This example demonstrates several methods that can be hot-swapped at runtime:

### Transaction Processing Logic
- `Transaction.accept()` - Main transaction processing workflow
- `Transaction.isValid()` - Validation rules
- `Transaction.assessRisk()` - Risk assessment algorithms
- `Transaction.calculateFees()` - Fee calculation logic
- `Transaction.meetsBusinessRules()` - Business rule evaluation

### Domain Behaviors
- `TransactionStatus.isFinal()` - Final status determination
- `TransactionType.requiresReference()` - Reference requirements
- `RiskLevel.requiresManualApproval()` - Approval requirements

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build
```bash
cd examples/multi-module-financial
mvn clean compile
```

### Run Demo
```bash
mvn exec:java -Dexec.mainClass="org.acmsl.bytehot.examples.financial.infrastructure.FinancialDemo" -pl financial-infrastructure
```

### Run CLI
```bash
mvn exec:java -Dexec.mainClass="org.acmsl.bytehot.examples.financial.infrastructure.cli.FinancialCLI" -pl financial-infrastructure -Dexec.args="transfer ACC001 ACC002 1000.00 USD WIRE_TRANSFER REF123"
```

## Demo Scenarios

The demo application runs several scenarios:

1. **Internal Transfer** - Low-risk, no fees
2. **Wire Transfer** - Medium-risk, with fees
3. **International Transfer** - High-risk, requires approval
4. **Invalid Transaction** - Validation failure (same account)
5. **Business Rules Violation** - Daily limit exceeded

## Hot-Swapping Examples

### Example 1: Change Fee Calculation
```java
// Original: Wire transfer fee is $25
// Hot-swap to: Wire transfer fee is $15

// In Transaction.calculateFees():
case WIRE_TRANSFER:
    feeAmount = BigDecimal.valueOf(15.00); // Changed from 25.00
    break;
```

### Example 2: Modify Risk Assessment
```java
// Original: High amounts (>$100K) add 5 risk points
// Hot-swap to: High amounts (>$50K) add 3 risk points

// In Transaction.assessRisk():
if (amount.getAmount().compareTo(BigDecimal.valueOf(50000)) > 0) { // Changed from 100000
    riskScore += 3; // Changed from 5
}
```

### Example 3: Update Business Rules
```java
// Original: Daily limit is $250K
// Hot-swap to: Daily limit is $500K

// In Transaction.meetsBusinessRules():
if (amount.getAmount().compareTo(BigDecimal.valueOf(500000)) > 0) { // Changed from 250000
    return false;
}
```

## Event Flow

1. **CLI/REST** creates `TransactionRequestedEvent`
2. **FinancialApplication** routes event to `FinancialEventRouter`
3. **FinancialEventRouter** forwards to `Transaction.accept()`
4. **Transaction** processes through validation, risk assessment, fee calculation
5. **Transaction** returns `TransactionProcessedEvent` or `TransactionRejectedEvent`
6. **Application** emits response events to interested adapters

## Testing Hot-Swapping

1. Start the demo application
2. Note the original behavior (fees, risk levels, etc.)
3. Use ByteHot to hot-swap methods at runtime
4. Observe the changed behavior without restarting the application

## Integration Points

This example is designed to integrate with:
- **ByteHot Agent** - For runtime hot-swapping
- **Spring Boot** - For REST API and dependency injection
- **Event Sourcing** - For transaction history and audit trails
- **Message Brokers** - For event publishing and integration

## Architecture Benefits

- **Testability** - Each layer can be tested independently
- **Flexibility** - Hot-swappable business logic
- **Maintainability** - Clear separation of concerns
- **Scalability** - Event-driven architecture supports scaling
- **Compliance** - Event sourcing provides audit trails