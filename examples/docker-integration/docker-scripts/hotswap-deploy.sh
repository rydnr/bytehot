#!/bin/bash
# ByteHot Docker Integration - Hot-swap Deployment Script
# This script enables hot-swapping of classes and resources in containerized applications

set -e

# Configuration
HOTSWAP_DIR="${HOTSWAP_DIR:-/app/hotswap}"
BACKUP_DIR="${BACKUP_DIR:-$HOTSWAP_DIR/backup}"
LOG_FILE="${LOG_FILE:-/app/logs/hotswap.log}"

# Functions that can be hot-swapped
log_message() {
    local level="$1"
    local message="$2"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [$level] $message" | tee -a "$LOG_FILE"
}

validate_deployment_package() {
    local package_path="$1"
    
    log_message "INFO" "Validating deployment package: $package_path"
    
    if [ ! -f "$package_path" ]; then
        log_message "ERROR" "Package file not found: $package_path"
        return 1
    fi
    
    # Check file type and size
    local file_type=$(file "$package_path")
    local file_size=$(stat -c%s "$package_path")
    
    log_message "INFO" "Package type: $file_type"
    log_message "INFO" "Package size: ${file_size} bytes"
    
    # Validate based on file extension
    case "$package_path" in
        *.jar)
            if ! unzip -t "$package_path" >/dev/null 2>&1; then
                log_message "ERROR" "Invalid JAR file: $package_path"
                return 1
            fi
            ;;
        *.class)
            if ! file "$package_path" | grep -q "Java class"; then
                log_message "ERROR" "Invalid class file: $package_path"
                return 1
            fi
            ;;
        *.properties|*.yml|*.yaml|*.xml)
            # Configuration files are generally text-based
            if ! file "$package_path" | grep -q "text"; then
                log_message "WARN" "Non-text configuration file: $package_path"
            fi
            ;;
        *)
            log_message "WARN" "Unknown file type for hot-swapping: $package_path"
            ;;
    esac
    
    return 0
}

backup_existing_files() {
    local target_path="$1"
    
    if [ -f "$target_path" ]; then
        local backup_timestamp=$(date '+%Y%m%d_%H%M%S')
        local backup_file="$BACKUP_DIR/$(basename "$target_path").$backup_timestamp"
        
        mkdir -p "$BACKUP_DIR"
        cp "$target_path" "$backup_file"
        
        log_message "INFO" "Backed up existing file: $target_path -> $backup_file"
    fi
}

deploy_class_file() {
    local source_file="$1"
    local target_dir="$HOTSWAP_DIR/classes"
    local target_file="$target_dir/$(basename "$source_file")"
    
    log_message "INFO" "Deploying class file: $source_file"
    
    # Create target directory
    mkdir -p "$target_dir"
    
    # Backup existing file
    backup_existing_files "$target_file"
    
    # Copy new class file
    cp "$source_file" "$target_file"
    
    # Set permissions
    chmod 644 "$target_file"
    
    log_message "INFO" "Class file deployed successfully: $target_file"
}

deploy_jar_file() {
    local source_file="$1"
    local target_dir="$HOTSWAP_DIR/jars"
    local target_file="$target_dir/$(basename "$source_file")"
    
    log_message "INFO" "Deploying JAR file: $source_file"
    
    # Create target directory
    mkdir -p "$target_dir"
    
    # Backup existing file
    backup_existing_files "$target_file"
    
    # Copy new JAR file
    cp "$source_file" "$target_file"
    
    # Set permissions
    chmod 644 "$target_file"
    
    log_message "INFO" "JAR file deployed successfully: $target_file"
}

deploy_resource_file() {
    local source_file="$1"
    local target_dir="$HOTSWAP_DIR/resources"
    local target_file="$target_dir/$(basename "$source_file")"
    
    log_message "INFO" "Deploying resource file: $source_file"
    
    # Create target directory
    mkdir -p "$target_dir"
    
    # Backup existing file
    backup_existing_files "$target_file"
    
    # Copy new resource file
    cp "$source_file" "$target_file"
    
    # Set appropriate permissions based on file type
    case "$source_file" in
        *.sh)
            chmod 755 "$target_file"
            ;;
        *)
            chmod 644 "$target_file"
            ;;
    esac
    
    log_message "INFO" "Resource file deployed successfully: $target_file"
}

trigger_hotswap() {
    local deployment_type="$1"
    local target_file="$2"
    
    log_message "INFO" "Triggering hot-swap for $deployment_type: $target_file"
    
    # Send signal to ByteHot agent to perform hot-swap
    # This could be done via JMX, HTTP API, or file system events
    
    # Create trigger file for ByteHot agent
    local trigger_file="$HOTSWAP_DIR/.hotswap_trigger"
    echo "$(date '+%Y-%m-%d %H:%M:%S')|$deployment_type|$target_file" >> "$trigger_file"
    
    # Wait for acknowledgment (simplified implementation)
    local timeout=30
    local counter=0
    
    while [ $counter -lt $timeout ]; do
        if [ -f "$HOTSWAP_DIR/.hotswap_complete" ]; then
            log_message "INFO" "Hot-swap completed successfully"
            rm -f "$HOTSWAP_DIR/.hotswap_complete"
            return 0
        fi
        
        sleep 1
        counter=$((counter + 1))
    done
    
    log_message "WARN" "Hot-swap timeout after ${timeout} seconds"
    return 1
}

rollback_deployment() {
    local target_file="$1"
    local backup_pattern="$BACKUP_DIR/$(basename "$target_file").*"
    
    log_message "INFO" "Rolling back deployment: $target_file"
    
    # Find most recent backup
    local latest_backup=$(ls -t $backup_pattern 2>/dev/null | head -n1)
    
    if [ -n "$latest_backup" ] && [ -f "$latest_backup" ]; then
        cp "$latest_backup" "$target_file"
        log_message "INFO" "Rollback completed: $latest_backup -> $target_file"
        
        # Trigger hot-swap for rollback
        trigger_hotswap "rollback" "$target_file"
    else
        log_message "ERROR" "No backup found for rollback: $target_file"
        return 1
    fi
}

list_deployments() {
    log_message "INFO" "Listing current deployments:"
    
    echo "=== Classes ==="
    if [ -d "$HOTSWAP_DIR/classes" ]; then
        ls -la "$HOTSWAP_DIR/classes/"
    else
        echo "No classes deployed"
    fi
    
    echo ""
    echo "=== JARs ==="
    if [ -d "$HOTSWAP_DIR/jars" ]; then
        ls -la "$HOTSWAP_DIR/jars/"
    else
        echo "No JARs deployed"
    fi
    
    echo ""
    echo "=== Resources ==="
    if [ -d "$HOTSWAP_DIR/resources" ]; then
        ls -la "$HOTSWAP_DIR/resources/"
    else
        echo "No resources deployed"
    fi
    
    echo ""
    echo "=== Backups ==="
    if [ -d "$BACKUP_DIR" ]; then
        ls -la "$BACKUP_DIR/"
    else
        echo "No backups available"
    fi
}

show_help() {
    echo "ByteHot Hot-swap Deployment Script"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  deploy <file>     - Deploy a class, JAR, or resource file"
    echo "  rollback <file>   - Rollback a deployment to previous version"
    echo "  list              - List current deployments and backups"
    echo "  help              - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 deploy /tmp/MyClass.class"
    echo "  $0 deploy /tmp/library.jar"
    echo "  $0 deploy /tmp/application.properties"
    echo "  $0 rollback /app/hotswap/classes/MyClass.class"
    echo "  $0 list"
    echo ""
    echo "Environment Variables:"
    echo "  HOTSWAP_DIR  - Hot-swap directory (default: /app/hotswap)"
    echo "  BACKUP_DIR   - Backup directory (default: \$HOTSWAP_DIR/backup)"
    echo "  LOG_FILE     - Log file path (default: /app/logs/hotswap.log)"
}

# Main execution
main() {
    # Ensure directories exist
    mkdir -p "$HOTSWAP_DIR" "$BACKUP_DIR" "$(dirname "$LOG_FILE")"
    
    case "${1:-help}" in
        "deploy")
            if [ -z "$2" ]; then
                log_message "ERROR" "No file specified for deployment"
                show_help
                exit 1
            fi
            
            local source_file="$2"
            
            # Validate deployment package
            if ! validate_deployment_package "$source_file"; then
                exit 1
            fi
            
            # Deploy based on file type
            case "$source_file" in
                *.class)
                    deploy_class_file "$source_file"
                    trigger_hotswap "class" "$source_file"
                    ;;
                *.jar)
                    deploy_jar_file "$source_file"
                    trigger_hotswap "jar" "$source_file"
                    ;;
                *.properties|*.yml|*.yaml|*.xml)
                    deploy_resource_file "$source_file"
                    trigger_hotswap "resource" "$source_file"
                    ;;
                *)
                    deploy_resource_file "$source_file"
                    trigger_hotswap "resource" "$source_file"
                    ;;
            esac
            ;;
        "rollback")
            if [ -z "$2" ]; then
                log_message "ERROR" "No file specified for rollback"
                show_help
                exit 1
            fi
            
            rollback_deployment "$2"
            ;;
        "list")
            list_deployments
            ;;
        "help")
            show_help
            ;;
        *)
            log_message "ERROR" "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Execute main function
main "$@"