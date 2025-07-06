#!/bin/bash
# ByteHot Docker Integration - Application Entry Point
# This script can be hot-swapped to change application startup behavior

set -e

# Configuration
APP_JAR="/app/application.jar"
BYTEHOT_AGENT="/app/bytehot-agent.jar"
CONFIG_DIR="/app/config"
HOTSWAP_DIR="/app/hotswap"
LOGS_DIR="/app/logs"

# Functions that can be hot-swapped
setup_environment() {
    echo "Setting up ByteHot environment..."
    
    # Create necessary directories
    mkdir -p "$HOTSWAP_DIR/classes" "$HOTSWAP_DIR/jars" "$LOGS_DIR"
    
    # Set permissions
    chmod 755 "$HOTSWAP_DIR" "$LOGS_DIR"
    
    # Initialize ByteHot configuration
    if [ ! -f "$CONFIG_DIR/bytehot.properties" ]; then
        generate_bytehot_config
    fi
}

generate_bytehot_config() {
    echo "Generating ByteHot configuration..."
    cat > "$CONFIG_DIR/bytehot.properties" << EOF
# ByteHot Docker Configuration
bytehot.enabled=true
bytehot.watch.enabled=true
bytehot.watch.directory=$HOTSWAP_DIR
bytehot.watch.recursive=true
bytehot.watch.poll.interval=1000

# Hot-swap policies (can be modified at runtime)
bytehot.hotswap.classes.enabled=true
bytehot.hotswap.methods.enabled=true
bytehot.hotswap.resources.enabled=true

# Monitoring and metrics
bytehot.metrics.enabled=true
bytehot.metrics.jmx.enabled=true
bytehot.metrics.prometheus.enabled=true
bytehot.metrics.port=9090

# Security settings
bytehot.security.enabled=true
bytehot.security.whitelist.packages=org.acmsl.bytehot.examples
EOF
}

wait_for_dependencies() {
    echo "Checking dependencies..."
    
    # Wait for external services if needed
    if [ -n "$DATABASE_URL" ]; then
        echo "Waiting for database connection..."
        timeout 30 bash -c 'until curl -s $DATABASE_URL; do sleep 1; done'
    fi
    
    if [ -n "$REDIS_URL" ]; then
        echo "Waiting for Redis connection..."
        timeout 30 bash -c 'until curl -s $REDIS_URL; do sleep 1; done'
    fi
}

start_application() {
    echo "Starting ByteHot application..."
    
    # Build JVM arguments
    JVM_ARGS="$JAVA_OPTS"
    
    # Add debug options if enabled
    if [ "$ENABLE_DEBUG" = "true" ]; then
        JVM_ARGS="$JVM_ARGS $DEBUG_OPTS"
        echo "Debug mode enabled on port 5005"
    fi
    
    # Add memory monitoring
    if [ "$ENABLE_MEMORY_MONITORING" = "true" ]; then
        JVM_ARGS="$JVM_ARGS -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:$LOGS_DIR/gc.log"
    fi
    
    # Start application with ByteHot agent
    exec java $JVM_ARGS -jar "$APP_JAR" "$@"
}

start_benchmark() {
    echo "Starting ByteHot benchmarking mode..."
    
    # Special configuration for benchmarking
    BENCHMARK_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xms2g -Xmx2g"
    
    exec java $BENCHMARK_OPTS -javaagent:"$BYTEHOT_AGENT" \
        -cp "$APP_JAR" org.acmsl.bytehot.examples.benchmarks.BenchmarkRunner "$@"
}

start_development() {
    echo "Starting ByteHot in development mode..."
    
    # Development-specific settings
    DEV_OPTS="-Dspring.profiles.active=development -Dbytehot.watch.poll.interval=500"
    
    exec java $JAVA_OPTS $DEBUG_OPTS $DEV_OPTS -jar "$APP_JAR" "$@"
}

show_help() {
    echo "ByteHot Docker Integration - Usage:"
    echo ""
    echo "Commands:"
    echo "  application   - Start the main application (default)"
    echo "  benchmark     - Run performance benchmarks"
    echo "  development   - Start in development mode with fast hot-swapping"
    echo "  help          - Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  ENABLE_DEBUG              - Enable remote debugging (default: false)"
    echo "  ENABLE_MEMORY_MONITORING  - Enable GC logging (default: false)"
    echo "  DATABASE_URL              - Database connection URL"
    echo "  REDIS_URL                 - Redis connection URL"
    echo "  JAVA_OPTS                 - Additional JVM options"
    echo ""
    echo "Hot-swap Directory: $HOTSWAP_DIR"
    echo "Configuration Directory: $CONFIG_DIR"
    echo "Logs Directory: $LOGS_DIR"
}

# Main execution
main() {
    echo "=== ByteHot Docker Integration ==="
    echo "Container started at: $(date)"
    echo "Command: $1"
    echo ""
    
    # Setup environment
    setup_environment
    
    # Wait for dependencies
    wait_for_dependencies
    
    # Execute command
    case "${1:-application}" in
        "application")
            start_application "${@:2}"
            ;;
        "benchmark")
            start_benchmark "${@:2}"
            ;;
        "development")
            start_development "${@:2}"
            ;;
        "help")
            show_help
            ;;
        *)
            echo "Unknown command: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Execute main function
main "$@"