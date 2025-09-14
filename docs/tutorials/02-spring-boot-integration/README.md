# Tutorial 02: Spring Boot Integration

**Duration:** 45 minutes  
**Goal:** Integrate ByteHot with Spring Boot applications  
**Prerequisites:** Tutorial 01 completed, Spring Boot experience  

## What You'll Learn

In this tutorial, you'll:
- Configure ByteHot for Spring Boot applications
- Hot-swap Spring components and services
- Handle dependency injection changes
- Understand Spring's proxy mechanisms with hot-swapping
- Apply best practices for Spring integration

## Overview

Spring Boot is one of the most popular Java frameworks, and ByteHot provides excellent integration. You'll learn how to hot-swap Spring beans, controllers, services, and configurations without losing Spring's dependency injection benefits.

## Prerequisites

- **Tutorial 01 completed**: Basic ByteHot understanding
- **Spring Boot knowledge**: Familiarity with annotations, dependency injection
- **Java 11+**: Required for both Spring Boot and ByteHot
- **Maven 3.6+**: For building the Spring Boot application

## Step 1: Create Spring Boot Application

Let's create a RESTful web service that we can hot-swap.

### Initialize Spring Boot Project

```bash
mkdir spring-boot-hotswap-demo
cd spring-boot-hotswap-demo
```

### Create Maven Configuration

Create `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>spring-boot-hotswap-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>11</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <jvmArguments>
                        -javaagent:${user.home}/bytehot-agent.jar
                        -Dbytehot.config=bytehot.yml
                    </jvmArguments>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Step 2: Create Hot-Swappable Spring Components

### Main Application Class

Create `src/main/java/com/example/demo/SpringBootHotswapDemoApplication.java`:

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootHotswapDemoApplication {
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Spring Boot with ByteHot Integration");
        System.out.println("📖 Follow Tutorial 02: Spring Boot Integration");
        System.out.println("🔥 Ready for hot-swapping Spring components!");
        System.out.println();
        
        SpringApplication.run(SpringBootHotswapDemoApplication.class, args);
    }
}
```

### Hot-Swappable Service

Create `src/main/java/com/example/demo/service/GreetingService.java`:

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A Spring service that can be hot-swapped.
 * 
 * This service demonstrates how ByteHot works with Spring's 
 * dependency injection and proxy mechanisms.
 */
@Service
public class GreetingService {
    
    private int requestCount = 0;
    
    /**
     * Generate a greeting message.
     * This method will be hot-swapped during the tutorial.
     * 
     * Try modifying:
     * - The message format
     * - Adding personalization
     * - Including additional information
     * - Changing the business logic
     */
    public String generateGreeting(String name) {
        requestCount++;
        
        return String.format("Hello, %s! This is request #%d", 
                           name, requestCount);
    }
    
    /**
     * Get service statistics.
     * Another method that can be hot-swapped.
     */
    public String getStatistics() {
        return String.format("Service has handled %d requests", requestCount);
    }
    
    /**
     * Business logic method for hot-swapping demonstration.
     * Try adding new functionality here.
     */
    public String getServiceInfo() {
        return "Greeting Service v1.0 - Ready for hot-swapping!";
    }
}
```

### Hot-Swappable REST Controller

Create `src/main/java/com/example/demo/controller/GreetingController.java`:

```java
package com.example.demo.controller;

import com.example.demo.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

/**
 * REST controller demonstrating ByteHot with Spring Boot.
 * 
 * This controller shows how to hot-swap Spring MVC components
 * while maintaining dependency injection and request handling.
 */
@RestController
@RequestMapping("/api")
public class GreetingController {
    
    @Autowired
    private GreetingService greetingService;
    
    /**
     * Simple greeting endpoint.
     * Perfect for demonstrating hot-swap changes.
     */
    @GetMapping("/greet/{name}")
    public Map<String, Object> greet(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", greetingService.generateGreeting(name));
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "success");
        
        return response;
    }
    
    /**
     * Status endpoint showing service statistics.
     * Try hot-swapping this to add more information.
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", greetingService.getServiceInfo());
        response.put("statistics", greetingService.getStatistics());
        response.put("hotswap_enabled", true);
        
        return response;
    }
    
    /**
     * Health check endpoint.
     * Demonstrates hot-swapping endpoint behavior.
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Spring Boot + ByteHot Integration Working!");
        
        return response;
    }
}
```

### Configuration Class

Create `src/main/java/com/example/demo/config/AppConfig.java`:

```java
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

/**
 * Application configuration that can be hot-swapped.
 * 
 * Demonstrates how ByteHot works with Spring configuration classes.
 */
@Configuration
public class AppConfig {
    
    /**
     * Example bean that can be hot-swapped.
     * Try modifying the returned value and see the change immediately.
     */
    @Bean
    public String applicationVersion() {
        return "1.0.0-SNAPSHOT";
    }
    
    /**
     * Another configurable bean for hot-swap demonstration.
     */
    @Bean
    public Map<String, Object> applicationSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("feature.enhanced_greeting", false);
        settings.put("feature.detailed_stats", false);
        settings.put("debug.verbose_logging", false);
        
        return settings;
    }
}
```

## Step 3: ByteHot Configuration for Spring Boot

Create `bytehot.yml`:

```yaml
bytehot:
  # Spring Boot specific configuration
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
      recursive: true
      pollInterval: 1000
  
  # Spring integration settings
  spring:
    # Enable Spring-aware hot-swapping
    enabled: true
    
    # Handle Spring proxies appropriately
    proxy_aware: true
    
    # Refresh application context after hot-swap
    refresh_context: false  # Usually not needed for method body changes
    
    # Monitor specific Spring components
    monitor_components: true
  
  # Logging configuration
  logging:
    level: INFO
    includeTimestamps: true
    showHotSwapEvents: true
    showSpringEvents: true
    
  # User configuration
  user:
    name: "Spring Developer"
    email: "spring.dev@example.com"
```

Create `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: "ByteHot Spring Boot Demo"
  
  # Enable Spring Boot DevTools (works alongside ByteHot)
  devtools:
    restart:
      enabled: false  # Let ByteHot handle the hot-swapping
    livereload:
      enabled: true

# Actuator endpoints for monitoring
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

# Logging configuration
logging:
  level:
    com.example.demo: DEBUG
    org.springframework: INFO
```

## Step 4: Build and Run

### Build the Application

```bash
mvn clean compile
```

### Run with ByteHot

```bash
mvn spring-boot:run
```

Or manually:
```bash
java -javaagent:~/bytehot-agent.jar \
     -Dbytehot.config=bytehot.yml \
     -jar target/spring-boot-hotswap-demo-1.0.0.jar
```

You should see output like:
```
🚀 Starting Spring Boot with ByteHot Integration
📖 Follow Tutorial 02: Spring Boot Integration
🔥 Ready for hot-swapping Spring components!

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v3.2.0)

... Spring Boot startup logs ...
```

## Step 5: Test the Application

### Basic API Testing

Open a new terminal and test the endpoints:

```bash
# Test greeting endpoint
curl http://localhost:8080/api/greet/World
# Response: {"message":"Hello, World! This is request #1","timestamp":1672531200000,"status":"success"}

# Test status endpoint  
curl http://localhost:8080/api/status
# Response: {"service":"Greeting Service v1.0 - Ready for hot-swapping!","statistics":"Service has handled 1 requests","hotswap_enabled":true}

# Test health endpoint
curl http://localhost:8080/api/health
# Response: {"status":"UP","message":"Spring Boot + ByteHot Integration Working!"}
```

## Step 6: Hot-Swap Spring Components

### Hot-Swap 1: Modify Service Logic

Keep the application running and modify `GreetingService.java`:

```java
public String generateGreeting(String name) {
    requestCount++;
    
    String timeOfDay = LocalDateTime.now().getHour() < 12 ? "Good morning" : 
                      LocalDateTime.now().getHour() < 18 ? "Good afternoon" : "Good evening";
    
    return String.format("%s, %s! 🔥 This is request #%d (Hot-swapped!)", 
                       timeOfDay, name, requestCount);
}
```

Compile the changes:
```bash
mvn compile
```

Test the change:
```bash
curl http://localhost:8080/api/greet/SpringDeveloper
# Response: {"message":"Good afternoon, SpringDeveloper! 🔥 This is request #2 (Hot-swapped!)","timestamp":1672531200000,"status":"success"}
```

### Hot-Swap 2: Update Controller Response

Modify the `GreetingController.java`:

```java
@GetMapping("/greet/{name}")
public Map<String, Object> greet(@PathVariable String name) {
    Map<String, Object> response = new HashMap<>();
    response.put("message", greetingService.generateGreeting(name));
    response.put("timestamp", System.currentTimeMillis());
    response.put("status", "success");
    response.put("hot_swapped", true);
    response.put("framework", "Spring Boot + ByteHot");
    
    return response;
}
```

Compile and test:
```bash
mvn compile
curl http://localhost:8080/api/greet/Developer
```

### Hot-Swap 3: Add New Endpoint

Add a new method to the controller:

```java
/**
 * New endpoint added via hot-swap!
 */
@GetMapping("/info")
public Map<String, Object> getInfo() {
    Map<String, Object> response = new HashMap<>();
    response.put("application", "Spring Boot Hot-Swap Demo");
    response.put("version", "1.0.0");
    response.put("hot_swap_capability", "ACTIVE");
    response.put("endpoints", Arrays.asList("/greet/{name}", "/status", "/health", "/info"));
    
    return response;
}
```

Don't forget the import:
```java
import java.util.Arrays;
```

Compile and test:
```bash
mvn compile
curl http://localhost:8080/api/info
```

## Step 7: Understanding Spring Integration

### How ByteHot Works with Spring

1. **Proxy Handling**: ByteHot understands Spring's CGLIB and JDK proxies
2. **Bean Lifecycle**: Existing bean instances are updated with new behavior
3. **Dependency Injection**: Autowired dependencies remain intact
4. **AOP Integration**: Aspect-oriented programming continues to work
5. **Configuration Changes**: Some configuration changes may require context refresh

### Spring-Specific Considerations

#### What Works Well
- ✅ **Service method implementations**
- ✅ **Controller endpoint logic**
- ✅ **Business logic in @Component classes**
- ✅ **Most @Bean method implementations**

#### What Requires Caution
- ⚠️ **Adding new @Autowired fields** (may need context refresh)
- ⚠️ **Changing @RequestMapping values** (Spring needs to re-register)
- ⚠️ **Modifying @Configuration classes** (context-dependent)

#### Advanced Integration

For more complex scenarios, you can programmatically refresh Spring context:

```java
@Autowired
private ApplicationContext applicationContext;

// In rare cases where context refresh is needed
if (applicationContext instanceof ConfigurableApplicationContext) {
    ((ConfigurableApplicationContext) applicationContext).refresh();
}
```

## Step 8: Production Considerations

### Spring Boot Actuator Integration

ByteHot integrates well with Spring Boot Actuator:

```bash
# Check application health (includes ByteHot status)
curl http://localhost:8080/actuator/health

# View application metrics
curl http://localhost:8080/actuator/metrics

# Get application info
curl http://localhost:8080/actuator/info
```

### DevTools vs ByteHot

Spring Boot DevTools and ByteHot complement each other:
- **DevTools**: Handles classpath changes, resource updates
- **ByteHot**: Handles runtime class redefinition, method updates

For best results:
```yaml
spring:
  devtools:
    restart:
      enabled: false  # Let ByteHot handle class changes
    livereload:
      enabled: true   # Keep for resource updates
```

## Troubleshooting

### Common Spring Integration Issues

#### 1. Autowiring Not Working After Hot-Swap

**Solution**: Ensure you're only changing method implementations, not field declarations.

#### 2. Request Mapping Changes Not Taking Effect

**Solution**: Spring caches request mappings. For mapping changes, consider restarting or using programmatic registration.

#### 3. Configuration Changes Ignored

**Solution**: Some configuration changes require context refresh:
```java
@EventListener
public void handleHotSwapEvent(HotSwapEvent event) {
    // Custom logic for configuration refresh
}
```

### Debugging Hot-Swap Issues

Enable debug logging:
```yaml
logging:
  level:
    org.acmsl.bytehot: DEBUG
    org.springframework: DEBUG
```

Monitor Spring context:
```bash
# Check which beans are registered
curl http://localhost:8080/actuator/beans
```

## Best Practices

### 1. Design for Hot-Swapping

```java
@Service
public class BusinessService {
    
    // ✅ Good: Private fields with getters/setters
    private String configValue = "default";
    
    // ✅ Good: Method implementations that can change
    public String processData(String input) {
        return enhanceData(input);
    }
    
    // ✅ Good: Private helper methods
    private String enhanceData(String input) {
        // This logic can be hot-swapped easily
        return input.toUpperCase();
    }
}
```

### 2. Use Configuration Properties

```java
@ConfigurationProperties(prefix = "app.business")
@Component
public class BusinessConfig {
    private boolean featureEnabled = false;
    private String processingMode = "standard";
    
    // Getters and setters...
}
```

### 3. Leverage Spring Profiles

```yaml
# application-development.yml
bytehot:
  enabled: true
  watch:
    pollInterval: 500  # Faster polling for development

# application-production.yml  
bytehot:
  enabled: true
  watch:
    pollInterval: 5000  # Slower polling for production
```

## Next Steps

🎯 **Congratulations!** You've successfully:
- ✅ Integrated ByteHot with Spring Boot
- ✅ Hot-swapped Spring services and controllers
- ✅ Understood Spring proxy mechanisms
- ✅ Applied hot-swapping best practices
- ✅ Explored production considerations

### Continue Your Learning Journey

- **[Tutorial 03: Event-Driven Development](../03-event-driven-development/)** - Master ByteHot's event-driven architecture
- **[Tutorial 04: Testing with ByteHot](../04-testing-with-bytehot/)** - Revolutionary testing patterns
- **[Examples](../../../examples/spring-boot-ecommerce/)** - Real-world Spring Boot integration

### Advanced Spring Topics

- Complex dependency injection scenarios
- AOP and aspect hot-swapping  
- Spring Security integration
- Microservices hot-swapping patterns

## Summary

In this tutorial, you learned:

1. **Spring Boot Setup**: Configuring ByteHot with Spring Boot applications
2. **Component Integration**: Hot-swapping services, controllers, and configurations
3. **Dependency Injection**: How ByteHot preserves Spring's DI mechanisms
4. **Best Practices**: Designing Spring applications for effective hot-swapping
5. **Production Considerations**: Actuator integration and DevTools coordination

ByteHot and Spring Boot form a powerful combination for modern Java development, enabling rapid iteration while maintaining enterprise-grade dependency injection and configuration management.

**Ready for more advanced topics?** Continue with [Tutorial 03: Event-Driven Development](../03-event-driven-development/) to explore ByteHot's revolutionary event architecture!

---

## Quick Reference

### Starting Spring Boot with ByteHot
```bash
mvn spring-boot:run
# or
java -javaagent:bytehot-agent.jar -jar app.jar
```

### Hot-Swap Workflow
```bash
# 1. Modify Spring component
# 2. Compile changes
mvn compile
# 3. Test endpoint
curl http://localhost:8080/api/test
```

### Configuration Template
```yaml
bytehot:
  spring:
    enabled: true
    proxy_aware: true
  watch:
    - path: "target/classes"
      patterns: ["*.class"]
```

**🔥 Happy Spring Boot Hot-Swapping!**