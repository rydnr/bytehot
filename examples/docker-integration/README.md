# ByteHot Docker Integration Examples

This directory contains comprehensive examples demonstrating ByteHot hot-swapping capabilities in containerized environments using Docker and Docker Compose.

## Overview

The Docker integration examples showcase:

- **Multi-stage Docker builds** optimized for ByteHot applications
- **Hot-swapping in containers** with persistent volumes and deployment automation
- **Development vs Production** configurations with different hot-swap policies
- **Performance monitoring** with Prometheus and Grafana integration
- **Automated deployment** workflows for hot-swapping updates
- **Health checks and observability** for containerized ByteHot applications

## Quick Start

### 1. Build and Start the Complete Stack

```bash
# Start all services (application, database, monitoring)
docker-compose up -d

# Check service status
docker-compose ps

# View application logs
docker-compose logs -f bytehot-app
```

### 2. Development Mode with Fast Hot-swapping

```bash
# Start development environment with fast hot-swapping
docker-compose --profile development up -d bytehot-dev

# Mount local classes for rapid development
mkdir -p ./dev-classes
docker-compose up -d bytehot-dev
```

### 3. Performance Benchmarking

```bash
# Run performance benchmarks
docker-compose --profile benchmark run --rm bytehot-benchmark

# View benchmark results
docker-compose exec bytehot-benchmark ls -la /app/benchmark-results/
```

## Architecture

### Services

- **bytehot-app**: Main application with ByteHot agent
- **bytehot-dev**: Development instance with fast hot-swapping
- **bytehot-benchmark**: Performance benchmarking container
- **postgres**: PostgreSQL database
- **redis**: Redis for caching and sessions
- **prometheus**: Metrics collection (optional with `--profile monitoring`)
- **grafana**: Metrics visualization (optional with `--profile monitoring`)
- **hotswap-deployer**: Automated deployment service (optional with `--profile deployment`)

### Volumes

- `hotswap-data`: Persistent storage for hot-swap classes and resources
- `logs-data`: Application and ByteHot logs
- `benchmark-results`: Performance benchmark outputs

## Hot-swapping in Containers

### Manual Hot-swap Deployment

```bash
# Copy new class file to hot-swap directory
docker cp MyUpdatedClass.class bytehot-application:/app/hotswap/classes/

# Deploy using the deployment script
docker exec bytehot-application /app/scripts/hotswap-deploy.sh deploy /app/hotswap/classes/MyUpdatedClass.class

# Check deployment status
docker exec bytehot-application /app/scripts/hotswap-deploy.sh list
```

### Automated Hot-swap Deployment

```bash
# Start the deployment automation service
docker-compose --profile deployment up -d hotswap-deployer

# Copy files to the watched deployment directory
mkdir -p ./deployments/classes
cp MyUpdatedClass.class ./deployments/classes/

# The deployer will automatically detect and deploy the file
docker-compose logs -f hotswap-deployer
```

### Hot-swap from Host System

```bash
# Mount local development directory
docker run -v $(pwd)/hot-classes:/app/hotswap/classes bytehot-app

# Compile and copy classes directly
javac -cp app-classpath MyClass.java
cp MyClass.class ./hot-classes/
```

## Configuration

### Environment Variables

#### Application Configuration
- `SPRING_PROFILES_ACTIVE`: Active Spring profiles (docker, development, production)
- `ENABLE_DEBUG`: Enable remote debugging (default: false)
- `ENABLE_MEMORY_MONITORING`: Enable GC logging (default: false)
- `DATABASE_URL`: Database connection URL
- `REDIS_URL`: Redis connection URL

#### ByteHot Configuration
- `BYTEHOT_WATCH_DIR`: Directory to watch for hot-swap files
- `BYTEHOT_POLL_INTERVAL`: File watching poll interval in milliseconds
- `BYTEHOT_FAST_RELOAD`: Enable fast reload for development
- `BYTEHOT_DEBUG`: Enable ByteHot debug logging
- `BYTEHOT_VERBOSE`: Enable verbose logging

#### JVM Configuration
- `JAVA_OPTS`: Additional JVM options
- `DEBUG_OPTS`: Remote debugging options

### Configuration Files

#### Application Configuration (`config/application.yml`)
- Spring Boot configuration with profile-specific settings
- ByteHot configuration parameters
- Database and Redis connection settings
- Logging configuration

#### Monitoring Configuration (`monitoring/prometheus.yml`)
- Prometheus scrape configurations for ByteHot metrics
- Custom recording rules for hot-swap operations
- Alerting rules for performance degradation

## Monitoring and Observability

### Metrics

The Docker integration provides comprehensive metrics for:

- **Hot-swap Operations**: Success rate, duration, frequency
- **Application Performance**: Response times, error rates, throughput
- **JVM Metrics**: Memory usage, GC performance, thread counts
- **System Metrics**: CPU, memory, disk usage

### Health Checks

All containers include health checks that monitor:

- Application responsiveness
- ByteHot agent status
- Database connectivity
- Hot-swap directory accessibility

### Accessing Monitoring

```bash
# Start monitoring stack
docker-compose --profile monitoring up -d

# Access Grafana (admin/admin123)
open http://localhost:3000

# Access Prometheus
open http://localhost:9191

# View application metrics directly
curl http://localhost:8080/actuator/prometheus
```

## Development Workflow

### 1. Local Development with Hot-swapping

```bash
# Start development environment
docker-compose --profile development up -d

# Compile your changes
mvn compile -pl your-module

# Copy compiled classes to hot-swap directory
docker cp target/classes/. bytehot-development:/app/hotswap/classes/

# Verify hot-swap occurred
docker logs bytehot-development | grep "Hot-swap"
```

### 2. Testing Hot-swap Scenarios

```bash
# Test class hot-swapping
./test-hotswap-class.sh

# Test resource hot-swapping
./test-hotswap-resource.sh

# Test JAR hot-swapping
./test-hotswap-jar.sh
```

### 3. Performance Testing

```bash
# Run benchmarks
docker-compose --profile benchmark run --rm bytehot-benchmark

# Generate load while hot-swapping
docker-compose exec bytehot-app ab -n 10000 -c 10 http://localhost:8080/api/test
```

## Production Deployment

### Production Configuration

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  bytehot-app:
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - ENABLE_DEBUG=false
      - BYTEHOT_POLL_INTERVAL=5000
      - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '2'
          memory: 2G
```

### Security Considerations

- Disable debug mode in production
- Restrict hot-swap to specific packages
- Use secure volume mounts for hot-swap directory
- Enable ByteHot security features
- Monitor hot-swap operations for unauthorized changes

### Rolling Updates with Hot-swapping

```bash
# Update without downtime using hot-swapping
./deploy-hotswap-update.sh new-version.jar

# Monitor the update
docker-compose logs -f bytehot-app | grep -E "(Hot-swap|Deploy)"

# Rollback if necessary
./rollback-hotswap.sh previous-version.jar
```

## Troubleshooting

### Common Issues

#### Hot-swap Not Working
```bash
# Check ByteHot agent is loaded
docker exec bytehot-app jps -v | grep bytehot-agent

# Verify hot-swap directory permissions
docker exec bytehot-app ls -la /app/hotswap/

# Check ByteHot logs
docker exec bytehot-app tail -f /app/logs/bytehot.log
```

#### Container Performance Issues
```bash
# Monitor resource usage
docker stats

# Check GC performance
docker exec bytehot-app jstat -gc $(docker exec bytehot-app jps | grep ByteHot | cut -d' ' -f1)

# View memory usage
docker exec bytehot-app jcmd $(docker exec bytehot-app jps | grep ByteHot | cut -d' ' -f1) VM.summary
```

#### Database Connectivity
```bash
# Test database connection
docker exec bytehot-app curl -f postgres:5432

# Check database logs
docker-compose logs postgres
```

### Debugging

#### Enable Debug Mode
```bash
# Start with debugging enabled
docker-compose up -d -e ENABLE_DEBUG=true

# Connect with IDE debugger to localhost:5005
```

#### Verbose Logging
```bash
# Enable verbose ByteHot logging
docker-compose exec bytehot-app \
  java -Dlogging.level.org.acmsl.bytehot=DEBUG \
  -jar /app/application.jar
```

## Scripts

### Utility Scripts

- `entrypoint.sh`: Container startup and configuration
- `hotswap-deploy.sh`: Manual hot-swap deployment
- `deployment-watcher.sh`: Automated deployment monitoring

### Testing Scripts

- `test-hotswap-class.sh`: Test class hot-swapping
- `test-hotswap-resource.sh`: Test resource hot-swapping
- `test-performance-impact.sh`: Measure hot-swap performance impact

## Best Practices

### Container Configuration

1. **Use multi-stage builds** to minimize image size
2. **Set appropriate resource limits** for ByteHot operations
3. **Configure health checks** that verify ByteHot functionality
4. **Use named volumes** for persistent hot-swap storage
5. **Enable metrics collection** for monitoring hot-swap operations

### Hot-swapping in Containers

1. **Test hot-swap scenarios** in development before production
2. **Monitor performance impact** of hot-swapping operations
3. **Implement rollback mechanisms** for failed hot-swaps
4. **Use deployment automation** for consistent hot-swap workflows
5. **Secure hot-swap directories** with appropriate permissions

### Development Workflow

1. **Use development profiles** with fast hot-swapping
2. **Mount source directories** for rapid iteration
3. **Enable debug mode** for troubleshooting
4. **Monitor logs** for hot-swap events and errors
5. **Test with realistic data** and load patterns

## Examples and Demos

The `examples/` directory contains:

- **Simple Class Hot-swap**: Basic method modification demo
- **Business Logic Update**: Complex business rule changes
- **Configuration Hot-swap**: Runtime configuration updates
- **Performance Benchmarks**: Before/after hot-swap performance comparison
- **Error Handling**: Hot-swap failure scenarios and recovery

## Contributing

When adding new Docker integration examples:

1. Follow the existing directory structure
2. Include comprehensive documentation
3. Add appropriate health checks and monitoring
4. Test in both development and production scenarios
5. Update this README with new features and examples