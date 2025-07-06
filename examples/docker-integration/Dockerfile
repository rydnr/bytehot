# ByteHot Docker Integration Example
# Multi-stage build for optimized ByteHot application deployment

# Build stage
FROM openjdk:11-jdk-slim as builder

# Install build dependencies
RUN apt-get update && apt-get install -y \
    maven \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy parent POM and build tools
COPY pom.xml .
COPY examples/docker-integration/pom.xml examples/docker-integration/
COPY bytehot-domain/pom.xml bytehot-domain/
COPY bytehot-application/pom.xml bytehot-application/
COPY bytehot-infrastructure/pom.xml bytehot-infrastructure/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY . .

# Build the application with ByteHot agent
RUN mvn clean package -pl bytehot-application -am -DskipTests

# Production stage
FROM openjdk:11-jre-slim as runtime

# Install runtime dependencies
RUN apt-get update && apt-get install -y \
    curl \
    jq \
    && rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r bytehot && useradd -r -g bytehot bytehot

# Set working directory
WORKDIR /app

# Copy ByteHot agent and application
COPY --from=builder /app/bytehot-application/target/bytehot-application-*-agent.jar /app/bytehot-agent.jar
COPY --from=builder /app/examples/docker-integration/target/docker-integration-*.jar /app/application.jar

# Copy configuration files
COPY examples/docker-integration/src/main/resources/application.yml /app/config/
COPY examples/docker-integration/docker-scripts/ /app/scripts/

# Make scripts executable
RUN chmod +x /app/scripts/*.sh

# Create directories for hot-swapping
RUN mkdir -p /app/hotswap/classes /app/hotswap/jars /app/logs && \
    chown -R bytehot:bytehot /app

# Switch to application user
USER bytehot

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose ports
EXPOSE 8080 9090 5005

# Set JVM options for ByteHot
ENV JAVA_OPTS="-javaagent:/app/bytehot-agent.jar \
    -Xms512m -Xmx1g \
    -XX:+UseG1GC \
    -XX:+UnlockExperimentalVMOptions \
    -Dbytehot.hotswap.enabled=true \
    -Dbytehot.watch.directory=/app/hotswap \
    -Dbytehot.config.file=/app/config/bytehot.properties \
    -Dlogging.config=/app/config/logback-spring.xml"

# Development mode JVM options (can be overridden)
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Entry point script
ENTRYPOINT ["/app/scripts/entrypoint.sh"]
CMD ["application"]