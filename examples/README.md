# ByteHot Real-World Integration Examples

This directory contains comprehensive, production-ready examples demonstrating ByteHot's hot-swapping capabilities across various real-world scenarios, architectures, and deployment environments.

## Overview

The examples showcase ByteHot integration with:

- **Enterprise Applications**: Complex business logic with sophisticated hot-swappable components
- **Spring Boot**: Popular Java framework with ByteHot integration
- **Hexagonal Architecture**: Domain-driven design with clean separation of concerns
- **Multi-module Projects**: Maven-based modular applications with inter-module hot-swapping
- **Docker Containers**: Containerized applications with hot-swap deployment workflows
- **Kubernetes**: Container orchestration with scalable hot-swap operations
- **Performance Benchmarking**: Comprehensive performance analysis and optimization

## Examples Directory Structure

```
examples/
├── README.md                           # This file - overview and getting started
├── spring-boot-ecommerce/              # Spring Boot e-commerce platform
│   ├── ecommerce-domain/               # Pure domain logic
│   ├── ecommerce-application/          # Application coordination layer
│   ├── ecommerce-infrastructure/       # Spring Boot REST API integration
│   └── README.md                       # E-commerce specific documentation
├── multi-module-financial/             # Financial services platform
│   ├── financial-domain/               # Financial business rules
│   ├── financial-application/          # Transaction processing
│   ├── financial-infrastructure/       # External integrations
│   └── README.md                       # Financial platform documentation
├── enterprise-customer-platform/       # Enterprise customer management
│   ├── customer-domain/                # Customer lifecycle management
│   ├── customer-application/           # Event-driven coordination
│   ├── customer-infrastructure/        # Enterprise integrations
│   └── README.md                       # Customer platform documentation
├── performance-benchmarking/           # JMH-based performance testing
│   ├── src/main/java/.../benchmarks/   # Benchmark implementations
│   ├── benchmark-results/              # Generated reports
│   └── README.md                       # Benchmarking guide
├── docker-integration/                 # Docker containerization
│   ├── Dockerfile                      # Multi-stage build
│   ├── docker-compose.yml              # Complete stack
│   ├── scripts/                        # Deployment automation
│   └── README.md                       # Docker integration guide
└── kubernetes-deployment/              # Kubernetes orchestration
    ├── manifests/                      # K8s resource definitions
    ├── helm-chart/                     # Helm deployment
    ├── scripts/                        # Deployment automation
    └── README.md                       # Kubernetes deployment guide
```

## Quick Start Guide

### Prerequisites

- **Java 11+**: Required for all examples
- **Maven 3.6+**: Build tool for all projects
- **Docker**: For containerization examples
- **Kubernetes**: For orchestration examples
- **Git**: For cloning and version control

### 1. Build ByteHot Framework

```bash
# From the root of ByteHot repository
mvn clean install -DskipTests
```

### 2. Choose Your Example

#### For Spring Boot Integration
```bash
cd examples/spring-boot-ecommerce
mvn clean package
java -javaagent:../../bytehot-application/target/bytehot-application-*-agent.jar \
     -jar ecommerce-infrastructure/target/ecommerce-infrastructure-*.jar
```

#### For Enterprise Applications
```bash
cd examples/enterprise-customer-platform
mvn clean package
java -javaagent:../../bytehot-application/target/bytehot-application-*-agent.jar \
     -jar customer-infrastructure/target/customer-infrastructure-*.jar
```

#### For Performance Testing
```bash
cd examples/performance-benchmarking
mvn clean package
java -jar target/benchmarks.jar
```

#### For Docker Integration
```bash
cd examples/docker-integration
docker-compose up -d
```

#### For Kubernetes Deployment
```bash
cd examples/kubernetes-deployment
./scripts/deploy.sh deploy
```

### 3. Test Hot-swapping

Once your chosen example is running:

1. **Identify hot-swappable components** in the example's documentation
2. **Modify the business logic** in the relevant source files
3. **Compile the changes** using Maven or your IDE
4. **Deploy the hot-swap** using the example-specific deployment method
5. **Verify the changes** through the application's API or UI

## Example Scenarios by Use Case

### 1. Business Logic Updates

**Scenario**: Update pricing algorithms without downtime

**Best Example**: `spring-boot-ecommerce/ecommerce-domain/Product.java`

```java
// Original pricing logic
public BigDecimal calculateDiscountedPrice() {
    return basePrice.multiply(BigDecimal.valueOf(0.9)); // 10% discount
}

// Hot-swapped logic
public BigDecimal calculateDiscountedPrice() {
    return basePrice.multiply(BigDecimal.valueOf(0.85)); // 15% discount
}
```

**Hot-swap Process**:
1. Modify the method in your IDE
2. Compile: `mvn compile -pl ecommerce-domain`
3. Deploy: Copy class to hot-swap directory
4. Verify: Test API endpoint for new pricing

### 2. Configuration Changes

**Scenario**: Update feature flags and business rules

**Best Example**: `enterprise-customer-platform/customer-domain/CustomerSegmentation.java`

```java
// Hot-swappable segmentation criteria
public CustomerSegmentation upgrade() {
    // Business rules can be modified at runtime
    switch (this) {
        case STANDARD:
            return HIGH_VALUE; // Changed criteria
        // ... other cases
    }
}
```

### 3. Integration Updates

**Scenario**: Update external service integrations

**Best Example**: `multi-module-financial/financial-infrastructure/PaymentGatewayAdapter.java`

```java
// Hot-swappable integration logic
protected PaymentResponse processPayment(PaymentRequest request) {
    // Switch payment providers without downtime
    return newPaymentProvider.process(request);
}
```

### 4. Performance Optimizations

**Scenario**: Optimize algorithms under load

**Best Example**: `performance-benchmarking/.../HotSwapPerformanceBenchmark.java`

- Benchmark original implementation
- Hot-swap optimized algorithm
- Compare performance metrics
- Rollback if performance degrades

## Architecture Patterns

### 1. Hexagonal Architecture (Domain-Driven Design)

All examples follow hexagonal architecture principles:

- **Domain Layer**: Pure business logic, framework-agnostic
- **Application Layer**: Orchestration and event routing
- **Infrastructure Layer**: External integrations and adapters

**Benefits for Hot-swapping**:
- Clear separation of concerns
- Domain logic isolated from infrastructure
- Easy to identify hot-swappable components
- Minimal coupling between layers

### 2. Event-Driven Architecture

Examples use JavaEDA framework for event-driven patterns:

- **Domain Events**: Business events triggering hot-swaps
- **Event Sourcing**: Complete audit trail of changes
- **CQRS**: Separate read/write models for complex scenarios

**Hot-swap Integration**:
- Events can trigger automatic hot-swaps
- Event handlers can be hot-swapped
- Business rules expressed as event processing

### 3. Microservices Architecture

Container and Kubernetes examples demonstrate:

- **Service Isolation**: Each service can be hot-swapped independently
- **Circuit Breakers**: Graceful degradation during hot-swaps
- **Load Balancing**: Traffic routing during rolling hot-swaps

## Development Workflows

### 1. Local Development

#### Setup IDE for Hot-swapping
1. **Configure JVM**: Add ByteHot agent to run configurations
2. **Enable Auto-compile**: Configure IDE for automatic compilation
3. **Setup Watch Directories**: Configure ByteHot to watch target/classes
4. **Hot Reload**: Modify-compile-deploy cycle in seconds

#### Example IDE Configuration (IntelliJ)
```
VM Options: -javaagent:/path/to/bytehot-agent.jar
           -Dbytehot.watch.directory=target/classes
           -Dbytehot.poll.interval=1000
Program Arguments: --spring.profiles.active=development
```

### 2. Testing Hot-swaps

#### Unit Testing Hot-swappable Components
```java
@Test
public void testPricingAlgorithmHotSwap() {
    // Test original algorithm
    Product product = new Product("Test", BigDecimal.valueOf(100));
    assertEquals(BigDecimal.valueOf(90), product.calculateDiscountedPrice());
    
    // Simulate hot-swap (in real scenario, this would be done externally)
    // Test updated algorithm
    assertEquals(BigDecimal.valueOf(85), product.calculateDiscountedPrice());
}
```

#### Integration Testing
```java
@SpringBootTest
@TestPropertySource(properties = "bytehot.enabled=true")
public class HotSwapIntegrationTest {
    
    @Test
    public void testBusinessLogicHotSwap() {
        // Test API before hot-swap
        // Perform hot-swap operation
        // Test API after hot-swap
        // Verify behavior change
    }
}
```

### 3. Production Deployment

#### Blue-Green Deployment with Hot-swapping
1. **Deploy to Green Environment**: New version with ByteHot
2. **Warm Up**: Initialize application and JVM
3. **Traffic Switch**: Route subset of traffic to green
4. **Hot-swap Validation**: Test hot-swap capabilities
5. **Full Cutover**: Route all traffic to green
6. **Backup**: Keep blue environment for rollback

#### Canary Deployment with Hot-swapping
1. **Deploy Canary**: Small percentage of pods with new version
2. **Monitor Metrics**: Performance, error rates, user feedback
3. **Hot-swap Testing**: Validate hot-swap works in production
4. **Gradual Rollout**: Increase canary percentage
5. **Full Deployment**: Complete rollout if successful

## Performance Considerations

### JVM Optimization for Hot-swapping

#### Recommended JVM Settings
```bash
# Memory management
-Xms1g -Xmx2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200

# Hot-swap optimization
-XX:+UnlockExperimentalVMOptions
-XX:+EnableJVMCI
-XX:+UseJVMCICompiler

# Monitoring
-Dcom.sun.management.jmxremote
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
```

#### Performance Monitoring
- **Response Time**: Monitor API response times during hot-swaps
- **Memory Usage**: Track heap usage and GC behavior
- **CPU Utilization**: Monitor compilation overhead
- **Hot-swap Metrics**: Track hot-swap frequency and success rate

### Scaling Considerations

#### Horizontal Scaling
- **Load Balancer**: Route traffic away from pods during hot-swap
- **Session Affinity**: Maintain user sessions during rolling hot-swaps
- **Health Checks**: Verify application health after hot-swaps

#### Vertical Scaling
- **Memory Allocation**: Account for hot-swap memory overhead
- **CPU Resources**: Ensure sufficient CPU for compilation
- **Storage**: Allocate space for backup and staging

## Security Considerations

### Hot-swap Security Policies

#### Code Signing
```java
// Example security configuration
bytehot.security.enabled=true
bytehot.security.require-signature=true
bytehot.security.trusted-certificates=/path/to/certs
```

#### Package Whitelisting
```yaml
# Only allow hot-swapping of specific packages
bytehot:
  security:
    whitelist-packages:
      - com.example.business
      - org.acmsl.bytehot.examples
    blacklist-packages:
      - java.security
      - java.lang
```

#### Audit Logging
```java
// All hot-swap operations are logged
[INFO] ByteHot: Hot-swap initiated for MyBusinessLogic.class
[INFO] ByteHot: Signature verified for MyBusinessLogic.class  
[INFO] ByteHot: Hot-swap completed successfully
[AUDIT] ByteHot: User 'admin' deployed MyBusinessLogic.class at 2025-01-15T10:30:00Z
```

### Production Security Hardening

1. **Network Segmentation**: Isolate hot-swap operations
2. **Access Control**: Restrict who can perform hot-swaps
3. **Change Management**: Integrate with existing change processes
4. **Monitoring**: Alert on unauthorized hot-swap attempts
5. **Backup**: Automatic backup before any hot-swap operation

## Monitoring and Observability

### Key Metrics to Monitor

#### Application Metrics
- **Response Time**: p50, p95, p99 latencies
- **Error Rate**: 4xx and 5xx error percentages
- **Throughput**: Requests per second
- **Availability**: Uptime and SLA compliance

#### Hot-swap Specific Metrics
- **Hot-swap Frequency**: Operations per hour/day
- **Hot-swap Success Rate**: Percentage of successful operations
- **Hot-swap Duration**: Time taken for hot-swap operations
- **Rollback Rate**: Percentage of hot-swaps that were rolled back

#### JVM Metrics
- **Heap Usage**: Before and after hot-swaps
- **GC Performance**: Collection frequency and duration
- **Class Loading**: Number of classes loaded/unloaded
- **Compilation**: JIT compilation activity

### Alerting Strategies

#### Hot-swap Alerts
```yaml
# Example Prometheus alerts
groups:
  - name: bytehot.rules
    rules:
      - alert: HotSwapFailureRate
        expr: rate(bytehot_hotswap_failures_total[5m]) > 0.1
        for: 2m
        annotations:
          summary: "High hot-swap failure rate"
      
      - alert: HotSwapDurationHigh
        expr: bytehot_hotswap_duration_seconds > 30
        for: 1m
        annotations:
          summary: "Hot-swap taking too long"
```

### Dashboards and Visualization

#### Grafana Dashboard Panels
1. **Application Health**: Status indicators and health checks
2. **Hot-swap Operations**: Timeline and success/failure rates
3. **Performance Impact**: Before/after performance comparison
4. **Resource Usage**: CPU, memory, and storage utilization
5. **Error Tracking**: Error rates and types over time

## Troubleshooting Guide

### Common Issues and Solutions

#### 1. Hot-swap Not Taking Effect

**Symptoms**: Changes not visible after hot-swap
**Possible Causes**:
- Class not in classpath
- Package name mismatch
- Security restrictions

**Solutions**:
```bash
# Check ByteHot logs
tail -f /app/logs/bytehot.log

# Verify class loading
jps -v | grep bytehot-agent

# Check security settings
grep "security" /app/config/bytehot.properties
```

#### 2. Performance Degradation

**Symptoms**: Increased response times after hot-swap
**Possible Causes**:
- JIT compiler warming up
- Memory pressure
- Inefficient new code

**Solutions**:
```bash
# Monitor JVM performance
jstat -gc <pid> 1s

# Check memory usage
jmap -histo <pid>

# Profile application
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=hotswap.jfr
```

#### 3. Container/Pod Issues

**Symptoms**: Hot-swap failing in containerized environments
**Possible Causes**:
- File permissions
- Volume mount issues
- Network connectivity

**Solutions**:
```bash
# Check file permissions
kubectl exec -it pod-name -- ls -la /app/hotswap/

# Verify volume mounts
kubectl describe pod pod-name

# Test network connectivity
kubectl exec -it pod-name -- curl http://localhost:8080/actuator/health
```

### Diagnostic Commands

#### ByteHot Status
```bash
# Check agent status
jps -v | grep bytehot

# View configuration
curl http://localhost:8080/actuator/bytehot/config

# List hot-swappable classes
curl http://localhost:8080/actuator/bytehot/classes
```

#### Application Health
```bash
# Health endpoints
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Application info
curl http://localhost:8080/actuator/info
```

## Best Practices Summary

### Development Best Practices

1. **Design for Hot-swapping**: Structure code with hot-swap in mind
2. **Test Extensively**: Validate hot-swap scenarios in development
3. **Monitor Performance**: Track impact of hot-swap operations
4. **Version Control**: Tag and version hot-swappable components
5. **Documentation**: Document hot-swappable components and procedures

### Operational Best Practices

1. **Gradual Rollout**: Use canary and blue-green deployment strategies
2. **Automated Testing**: Include hot-swap validation in CI/CD pipelines
3. **Monitoring**: Implement comprehensive monitoring and alerting
4. **Rollback Procedures**: Have automated rollback mechanisms
5. **Change Management**: Integrate with existing change control processes

### Security Best Practices

1. **Access Control**: Restrict hot-swap operations to authorized users
2. **Code Signing**: Verify authenticity of hot-swapped code
3. **Audit Trail**: Log all hot-swap operations for compliance
4. **Network Security**: Secure hot-swap deployment channels
5. **Regular Reviews**: Periodically review hot-swap security policies

## Contributing to Examples

### Adding New Examples

When contributing new examples:

1. **Follow Architecture**: Use hexagonal architecture patterns
2. **Include Tests**: Comprehensive unit and integration tests
3. **Document Thoroughly**: Clear README with setup instructions
4. **Add Hot-swap Scenarios**: Demonstrate realistic hot-swap use cases
5. **Performance Testing**: Include benchmarks for hot-swap impact

### Example Template Structure

```
new-example/
├── README.md                    # Comprehensive documentation
├── pom.xml                      # Maven configuration
├── domain/                      # Pure business logic
│   ├── src/main/java/           # Domain classes
│   └── src/test/java/           # Domain tests
├── application/                 # Application layer
│   ├── src/main/java/           # Application services
│   └── src/test/java/           # Application tests
├── infrastructure/              # Infrastructure adapters
│   ├── src/main/java/           # Adapters and REST APIs
│   └── src/test/java/           # Integration tests
├── docker/                      # Container configuration
│   ├── Dockerfile               # Multi-stage build
│   └── docker-compose.yml       # Local development stack
└── k8s/                        # Kubernetes manifests
    ├── deployment.yaml          # Application deployment
    └── service.yaml             # Service configuration
```

## Support and Resources

### Documentation Links

- [ByteHot Main Documentation](../README.md)
- [Spring Boot Integration Guide](spring-boot-ecommerce/README.md)
- [Docker Integration Guide](docker-integration/README.md)
- [Kubernetes Deployment Guide](kubernetes-deployment/README.md)
- [Performance Benchmarking Guide](performance-benchmarking/README.md)

### Community Resources

- **Issue Tracking**: [GitHub Issues](https://github.com/rydnr/bytehot/issues)
- **Discussions**: [GitHub Discussions](https://github.com/rydnr/bytehot/discussions)
- **Wiki**: [Project Wiki](https://github.com/rydnr/bytehot/wiki)

### Getting Help

1. **Check Documentation**: Review example-specific README files
2. **Search Issues**: Look for similar problems in GitHub issues
3. **Create Issue**: Provide detailed information and steps to reproduce
4. **Join Discussions**: Participate in community discussions

---

**Note**: These examples demonstrate ByteHot capabilities in realistic scenarios. For production use, ensure proper testing, security review, and operational procedures are in place.