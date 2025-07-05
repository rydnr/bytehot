# ByteHot Spring Boot E-Commerce System

This example demonstrates ByteHot's hot-swapping capabilities integrated with Spring Boot in a production-ready e-commerce system using proper hexagonal architecture.

## Architecture Overview

The system follows strict hexagonal architecture principles with clear separation between layers:

### Domain Layer (`ecommerce-domain`)
- **Product** - Aggregate root for product management with hot-swappable business logic
- **Money** - Value object for monetary amounts with currency support
- **ProductCategory** - Enumeration with hot-swappable category-specific behaviors
- **ProductStatus** - Enumeration with hot-swappable lifecycle management
- **Domain Events** - ProductCreationRequestedEvent, ProductCreatedEvent, ProductCreationRejectedEvent

### Application Layer (`ecommerce-application`)
- **ECommerceApplication** - Main application coordinator implementing JavaEDA patterns
- **ECommerceEventRouter** - Routes domain events to appropriate aggregates
- **UnknownEventResponse** - Handles unsupported event types gracefully

### Infrastructure Layer (`ecommerce-infrastructure`)
- **SpringBootECommerceApplication** - Spring Boot bootstrap with ByteHot integration
- **ProductController** - REST API endpoints with hot-swappable request handling
- **ECommerceConfiguration** - Spring configuration for hexagonal architecture beans

## Key Hot-Swapping Scenarios

### Product Business Logic
- `Product.accept()` - Main product creation workflow
- `Product.isValid()` - Product validation rules
- `Product.meetsBusinessRules()` - Business rule evaluation
- `Product.calculateDiscountedPrice()` - Pricing and discount logic
- `Product.shouldAutoActivate()` - Auto-activation criteria

### Category Behaviors
- `ProductCategory.requiresSpecialHandling()` - Special handling rules
- `ProductCategory.getShippingWeightMultiplier()` - Shipping calculations
- `ProductCategory.getTaxRate()` - Dynamic tax rate policies
- `ProductCategory.supportsExpeditedShipping()` - Shipping options

### REST API Behaviors
- `ProductController.transformToEvent()` - Request mapping logic
- `ProductController.getBasePriceForCategory()` - Category-based pricing
- `ProductController.getDiscountMultiplier()` - Quantity discount policies

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build
```bash
cd examples/spring-boot-ecommerce
mvn clean compile
```

### Run Spring Boot Application
```bash
mvn spring-boot:run -pl ecommerce-infrastructure
```

The application will start at: http://localhost:8080/ecommerce

### API Endpoints

#### Health Check
```bash
curl http://localhost:8080/ecommerce/api/products/health
```

#### Create Product
```bash
curl -X POST http://localhost:8080/ecommerce/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Computer",
    "description": "High-performance laptop for professionals",
    "price": "999.99",
    "currency": "USD",
    "stockQuantity": 50,
    "category": "ELECTRONICS",
    "sku": "LAPTOP001",
    "weightGrams": 2000
  }'
```

#### Get Pricing Information
```bash
curl "http://localhost:8080/ecommerce/api/products/pricing?category=ELECTRONICS&quantity=2"
```

## Hot-Swapping Examples

### Example 1: Change Category Tax Rates
```java
// Hot-swap ProductCategory.getTaxRate() to change electronics tax from 8% to 6%
case ELECTRONICS:
    return 0.06; // Changed from 0.08
```

### Example 2: Modify Discount Policies
```java
// Hot-swap ProductController.getDiscountMultiplier() for better electronics discounts
case ELECTRONICS:
    if (quantity >= 2) return 0.90; // Changed from 0.95 (10% vs 5% discount)
```

### Example 3: Update Business Rules
```java
// Hot-swap Product.shouldAutoActivate() to change activation criteria
return stockQuantity > 0 && price.isPositive(); // Removed special handling check
```

## Benefits

- **Production Ready** - Real Spring Boot application with proper configuration
- **Testable** - Each layer can be tested independently  
- **Flexible** - Hot-swappable business logic without downtime
- **Scalable** - Event-driven architecture supports horizontal scaling
- **Maintainable** - Clear separation of concerns and dependency injection