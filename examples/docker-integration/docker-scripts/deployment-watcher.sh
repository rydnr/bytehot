#!/bin/bash
# ByteHot Deployment Watcher - Monitors for new deployments and triggers hot-swaps
# This script can be hot-swapped to change deployment automation behavior

set -e

# Configuration
WATCH_DIR="${WATCH_DIR:-/deployments}"
HOTSWAP_DIR="${HOTSWAP_DIR:-/app/hotswap}"
LOG_FILE="${LOG_FILE:-/app/logs/deployment-watcher.log}"
DEPLOY_SCRIPT="/app/hotswap-deploy.sh"

# Functions that can be hot-swapped
log_message() {
    local level="$1"
    local message="$2"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [$level] WATCHER: $message" | tee -a "$LOG_FILE"
}

setup_watcher() {
    log_message "INFO" "Setting up deployment watcher..."
    
    # Create directories
    mkdir -p "$WATCH_DIR" "$HOTSWAP_DIR" "$(dirname "$LOG_FILE")"
    
    # Set up watch subdirectories
    mkdir -p "$WATCH_DIR/classes" "$WATCH_DIR/jars" "$WATCH_DIR/resources"
    
    log_message "INFO" "Watching directory: $WATCH_DIR"
    log_message "INFO" "Hot-swap directory: $HOTSWAP_DIR"
    log_message "INFO" "Log file: $LOG_FILE"
}

process_deployment() {
    local file_path="$1"
    local event_type="$2"
    
    log_message "INFO" "Processing deployment: $file_path (event: $event_type)"
    
    # Skip if file doesn't exist (could be a delete event)
    if [ ! -f "$file_path" ]; then
        log_message "WARN" "File not found, skipping: $file_path"
        return 0
    fi
    
    # Skip temporary and hidden files
    local filename=$(basename "$file_path")
    if [[ "$filename" =~ ^\. ]] || [[ "$filename" =~ ~$ ]] || [[ "$filename" =~ \.tmp$ ]]; then
        log_message "DEBUG" "Skipping temporary/hidden file: $filename"
        return 0
    fi
    
    # Wait for file to be completely written
    wait_for_stable_file "$file_path"
    
    # Deploy the file
    if "$DEPLOY_SCRIPT" deploy "$file_path"; then
        log_message "INFO" "Deployment successful: $file_path"
        
        # Move to processed directory
        move_to_processed "$file_path"
        
        # Send notification
        send_deployment_notification "$file_path" "SUCCESS"
    else
        log_message "ERROR" "Deployment failed: $file_path"
        
        # Move to failed directory
        move_to_failed "$file_path"
        
        # Send notification
        send_deployment_notification "$file_path" "FAILED"
    fi
}

wait_for_stable_file() {
    local file_path="$1"
    local stable_count=0
    local required_stable=3
    local last_size=0
    
    log_message "DEBUG" "Waiting for file to stabilize: $file_path"
    
    while [ $stable_count -lt $required_stable ]; do
        if [ ! -f "$file_path" ]; then
            log_message "WARN" "File disappeared while waiting: $file_path"
            return 1
        fi
        
        local current_size=$(stat -c%s "$file_path" 2>/dev/null || echo 0)
        
        if [ "$current_size" -eq "$last_size" ]; then
            stable_count=$((stable_count + 1))
        else
            stable_count=0
            last_size=$current_size
        fi
        
        sleep 1
    done
    
    log_message "DEBUG" "File stabilized: $file_path (size: $last_size bytes)"
}

move_to_processed() {
    local file_path="$1"
    local processed_dir="$WATCH_DIR/processed/$(date '+%Y%m%d')"
    
    mkdir -p "$processed_dir"
    
    local processed_file="$processed_dir/$(basename "$file_path").$(date '+%H%M%S')"
    mv "$file_path" "$processed_file"
    
    log_message "INFO" "Moved to processed: $processed_file"
}

move_to_failed() {
    local file_path="$1"
    local failed_dir="$WATCH_DIR/failed/$(date '+%Y%m%d')"
    
    mkdir -p "$failed_dir"
    
    local failed_file="$failed_dir/$(basename "$file_path").$(date '+%H%M%S')"
    mv "$file_path" "$failed_file"
    
    log_message "INFO" "Moved to failed: $failed_file"
}

send_deployment_notification() {
    local file_path="$1"
    local status="$2"
    
    # Create notification payload
    local notification_file="$HOTSWAP_DIR/.notifications/$(date '+%Y%m%d_%H%M%S').json"
    mkdir -p "$(dirname "$notification_file")"
    
    cat > "$notification_file" << EOF
{
    "timestamp": "$(date -Iseconds)",
    "event": "deployment",
    "file": "$(basename "$file_path")",
    "status": "$status",
    "deployer": "deployment-watcher",
    "version": "1.0"
}
EOF
    
    log_message "INFO" "Notification sent: $status for $(basename "$file_path")"
}

handle_watch_event() {
    local event="$1"
    local file_path="$2"
    
    case "$event" in
        "CREATE"|"MOVED_TO"|"CLOSE_WRITE")
            # File was created or moved into watch directory
            process_deployment "$file_path" "$event"
            ;;
        "DELETE"|"MOVED_FROM")
            log_message "INFO" "File removed from watch directory: $file_path"
            ;;
        "MODIFY")
            # File was modified - might be still being written
            log_message "DEBUG" "File modified: $file_path"
            ;;
        *)
            log_message "DEBUG" "Unhandled event: $event for $file_path"
            ;;
    esac
}

start_watching() {
    log_message "INFO" "Starting deployment watcher..."
    log_message "INFO" "Monitoring: $WATCH_DIR"
    
    # Process any existing files
    for subdir in classes jars resources; do
        local watch_subdir="$WATCH_DIR/$subdir"
        if [ -d "$watch_subdir" ]; then
            for file in "$watch_subdir"/*; do
                if [ -f "$file" ]; then
                    log_message "INFO" "Processing existing file: $file"
                    process_deployment "$file" "EXISTING"
                fi
            done
        fi
    done
    
    # Start watching for new files
    inotifywait -m -r -e create,moved_to,close_write,delete,moved_from,modify \
        --format '%e %w%f' "$WATCH_DIR" 2>/dev/null | \
    while read event file_path; do
        handle_watch_event "$event" "$file_path"
    done
}

cleanup() {
    log_message "INFO" "Shutting down deployment watcher..."
    
    # Kill any background processes
    jobs -p | xargs -r kill
    
    log_message "INFO" "Deployment watcher stopped"
    exit 0
}

show_status() {
    echo "=== ByteHot Deployment Watcher Status ==="
    echo "Watch Directory: $WATCH_DIR"
    echo "Hot-swap Directory: $HOTSWAP_DIR"
    echo "Log File: $LOG_FILE"
    echo ""
    
    echo "=== Pending Deployments ==="
    for subdir in classes jars resources; do
        local count=$(find "$WATCH_DIR/$subdir" -type f 2>/dev/null | wc -l)
        echo "$subdir: $count files"
    done
    
    echo ""
    echo "=== Recent Activity ==="
    if [ -f "$LOG_FILE" ]; then
        tail -n 10 "$LOG_FILE"
    else
        echo "No activity logged yet"
    fi
}

show_help() {
    echo "ByteHot Deployment Watcher"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  start    - Start watching for deployments (default)"
    echo "  status   - Show current status"
    echo "  help     - Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  WATCH_DIR    - Directory to watch for deployments (default: /deployments)"
    echo "  HOTSWAP_DIR  - Hot-swap directory (default: /app/hotswap)"
    echo "  LOG_FILE     - Log file path (default: /app/logs/deployment-watcher.log)"
    echo ""
    echo "Deployment Structure:"
    echo "  $WATCH_DIR/classes/   - Java class files"
    echo "  $WATCH_DIR/jars/      - JAR files"
    echo "  $WATCH_DIR/resources/ - Configuration and resource files"
}

# Signal handlers
trap cleanup SIGTERM SIGINT

# Main execution
main() {
    case "${1:-start}" in
        "start")
            setup_watcher
            start_watching
            ;;
        "status")
            show_status
            ;;
        "help")
            show_help
            ;;
        *)
            echo "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Execute main function
main "$@"