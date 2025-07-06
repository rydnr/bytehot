# ByteHot Spring Boot E-Commerce Example

This example demonstrates ByteHot's hot-swapping capabilities in a realistic Spring Boot e-commerce microservice. You can modify business logic, pricing rules, and API behavior at runtime without restarting the application.

## Features Demonstrated

- **Hot-Swappable Business Logic**: Modify pricing rules, inventory management, and validation logic
- **Real-time API Changes**: Update REST endpoint behavior without downtime
- **Production-Ready Architecture**: Comprehensive error handling, monitoring, and optimization
- **Spring Boot Integration**: Seamless integration with Spring Boot ecosystem
- **Database Operations**: JPA entities with hot-swappable business methods
- **Caching**: Spring Cache integration with ByteHot compatibility

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    E-Commerce Microservice                  │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │    REST     │  │   Service   │  │ Repository  │        │
│  │ Controllers │  │   Layer     │  │    Layer    │        │
│  │             │  │             │  │             │        │
│  │ ProductCtrl │  │ ProductSvc  │  │ ProductRepo │        │
│  │ OrderCtrl   │  │ PricingSvc  │  │ OrderRepo   │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Domain    │  │   Config    │  │  ByteHot    │        │
│  │   Models    │  │   Layer     │  │ Integration │        │
│  │             │  │             │  │             │        │
│  │ Product     │  │ CacheConfig │  │ Hot-Swap    │        │
│  │ Category    │  │ DataConfig  │  │ Monitoring  │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- ByteHot agent JAR (built from parent project)

### Building and Running

1. **Build the application:**
   ```bash
   mvn clean package
   ```

2. **Run with ByteHot agent:**
   ```bash
   java -javaagent:target/bytehot-application-agent.jar \
        -Dbytehot.config.path=src/main/resources/bytehot.yml \
        -jar target/spring-boot-ecommerce-1.0.0-SNAPSHOT.jar
   ```

3. **Alternative: Use Spring Boot Maven plugin:**
   ```bash
   mvn spring-boot:run
   ```

### Accessing the Application

- **Application**: http://localhost:8080/ecommerce
- **H2 Console**: http://localhost:8080/ecommerce/h2-console
- **API Documentation**: http://localhost:8080/ecommerce/api/v1/products/info
- **Health Check**: http://localhost:8080/ecommerce/actuator/health

## Hot-Swapping Examples

### 1. Modify Pricing Logic

**File**: `src/main/java/org/acmsl/bytehot/examples/ecommerce/service/PricingService.java`

Try changing the discount percentages in the `applyBulkDiscounts` method:

```java
// Change this:
if (quantity >= 50) {
    return price.multiply(BigDecimal.valueOf(0.92)); // 8% discount
}

// To this:
if (quantity >= 50) {
    return price.multiply(BigDecimal.valueOf(0.85)); // 15% discount
}
```

Save the file and test the pricing endpoint immediately:
```bash
curl "http://localhost:8080/ecommerce/api/v1/products/1/price?quantity=50"
```

### 2. Modify Product Validation Rules

**File**: `src/main/java/org/acmsl/bytehot/examples/ecommerce/service/ProductService.java`

Change the validation logic in `validateByCategory`:

```java
case ELECTRONICS:
    // Change minimum price from $10 to $25
    if (product.getPrice().compareTo(BigDecimal.valueOf(25.0)) < 0) {
        throw new IllegalArgumentException("Electronics must have minimum price of $25");
    }
    break;
```

### 3. Add New API Response Fields

**File**: `src/main/java/org/acmsl/bytehot/examples/ecommerce/controller/ProductController.java`

Modify the `getApiInfo` method to add new fields:

```java
Map<String, Object> info = Map.of(
    "service", "ByteHot E-Commerce Product API",
    "version", "1.0.0",
    "hotSwapEnabled", true,
    "lastModified", Instant.now(),  // Add this
    "serverTime", LocalDateTime.now(),  // Add this
    "activeProducts", productService.countActiveProducts(),  // Add this
    // ... rest of the fields
);
```

### 4. Modify Time-Based Pricing

**File**: `src/main/java/org/acmsl/bytehot/examples/ecommerce/service/PricingService.java`

Change the happy hour timing in `applyTimeBasedPricing`:

```java
// Change from 2-4 PM to 10 AM-12 PM
if (now.isAfter(LocalTime.of(10, 0)) && now.isBefore(LocalTime.of(12, 0))) {
    // Weekend special: 10% discount
    return price.multiply(BigDecimal.valueOf(0.90));
}
```

## API Endpoints

### Products

- `GET /api/v1/products` - List all products (with pagination)
- `GET /api/v1/products/{id}` - Get product by ID
- `POST /api/v1/products` - Create new product
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Deactivate product
- `GET /api/v1/products/search?q={query}` - Search products
- `GET /api/v1/products/low-stock` - Get products with low stock

### Pricing & Inventory

- `GET /api/v1/products/{id}/price?quantity={qty}` - Calculate recommended price
- `POST /api/v1/products/{id}/reserve?quantity={qty}` - Reserve stock
- `POST /api/v1/products/{id}/release?quantity={qty}` - Release stock

### System

- `GET /api/v1/products/info` - API information
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Application metrics

## Sample Data

The application automatically creates sample products on startup:

1. **Laptop Pro** (Electronics) - $1,299.99
2. **Wireless Headphones** (Electronics) - $199.99
3. **Cotton T-Shirt** (Clothing) - $29.99
4. **Programming Book** (Books) - $49.99

## Testing Hot-Swap Scenarios

### Scenario 1: Dynamic Pricing Changes

1. Get current price: `GET /api/v1/products/1/price?quantity=10`
2. Modify pricing logic in `PricingService.java`
3. Get updated price immediately (no restart needed)
4. Compare the difference

### Scenario 2: Validation Rule Updates

1. Try creating a product that passes current validation
2. Modify validation rules in `ProductService.java`
3. Try creating the same product again
4. Observe different validation behavior

### Scenario 3: API Response Evolution

1. Call `GET /api/v1/products/info`
2. Modify the response structure in `ProductController.java`
3. Call the endpoint again
4. See the updated response format

## Configuration

### ByteHot Settings

Key configuration options in `bytehot.yml`:

- **Watch Paths**: Monitors service and controller packages
- **Hot-Swap Operations**: Method body changes, field additions
- **Production Features**: Error handling, monitoring, optimization
- **Performance Settings**: Auto-tuning and resource optimization

### Application Settings

Key settings in `application.yml`:

- **Database**: H2 in-memory for simplicity
- **Caching**: Simple cache with TTL settings
- **Monitoring**: Actuator endpoints enabled
- **Logging**: Detailed logging for ByteHot operations

## Monitoring and Observability

### Health Checks

Access health information at `/actuator/health`:

```json
{
  "status": "UP",
  "components": {
    "bytehot": {
      "status": "UP",
      "details": {
        "hotSwapEnabled": true,
        "watchedPaths": 3,
        "lastHotSwap": "2025-07-04T10:30:45Z"
      }
    }
  }
}
```

### Metrics

Access metrics at `/actuator/metrics`:

- `bytehot.hotswap.operations` - Number of hot-swap operations
- `bytehot.hotswap.duration` - Hot-swap operation duration
- `bytehot.errors.count` - Error count during hot-swapping

## Troubleshooting

### Common Issues

1. **Hot-swap not detected**: Check file paths in `bytehot.yml`
2. **Compilation errors**: Ensure Java syntax is correct
3. **Performance issues**: Monitor JVM metrics via actuator

### Debug Mode

Enable debug logging by modifying `application.yml`:

```yaml
logging:
  level:
    org.acmsl.bytehot: DEBUG
```

## Production Considerations

This example includes production-ready features:

- **Error Handling**: Circuit breaker and graceful degradation
- **Performance Monitoring**: Real-time JVM and application metrics
- **Resource Optimization**: Automatic memory and performance tuning
- **Health Checks**: Comprehensive system health monitoring

## Next Steps

1. **Explore Multi-Module Example**: See `../multi-module-financial/`
2. **Enterprise Integration**: Check `../enterprise-customer-platform/`
3. **Performance Analysis**: Use `../performance-benchmarking/`
4. **Container Deployment**: Try `../docker-integration/`

## Support

For issues and questions:

- Check the main ByteHot documentation
- Review the configuration files
- Enable debug logging for detailed information
- Use the H2 console to inspect database state