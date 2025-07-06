#!/bin/bash
# ByteHot Kubernetes Hot-swap Script
# Performs hot-swapping operations across Kubernetes pods

set -e

# Configuration
NAMESPACE="${NAMESPACE:-bytehot}"
DEPLOYMENT="${DEPLOYMENT:-bytehot-app}"
KUBECTL="${KUBECTL:-kubectl}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Functions that can be hot-swapped
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

get_pods() {
    local label_selector="$1"
    kubectl get pods -n "$NAMESPACE" -l "$label_selector" -o jsonpath='{.items[*].metadata.name}'
}

wait_for_file_stability() {
    local file_path="$1"
    local stable_count=0
    local required_stable=3
    local last_size=0
    
    if [ ! -f "$file_path" ]; then
        log_error "File not found: $file_path"
        return 1
    fi
    
    log_info "Waiting for file to stabilize: $file_path"
    
    while [ $stable_count -lt $required_stable ]; do
        local current_size=$(stat -c%s "$file_path" 2>/dev/null || echo 0)
        
        if [ "$current_size" -eq "$last_size" ]; then
            stable_count=$((stable_count + 1))
        else
            stable_count=0
            last_size=$current_size
        fi
        
        sleep 1
    done
    
    log_info "File stabilized: $file_path (size: $last_size bytes)"
}

deploy_to_single_pod() {
    local pod_name="$1"
    local source_file="$2"
    local target_subdir="$3"
    
    log_info "Deploying to pod: $pod_name"
    
    # Validate pod is running
    local pod_status=$(kubectl get pod "$pod_name" -n "$NAMESPACE" -o jsonpath='{.status.phase}')
    if [ "$pod_status" != "Running" ]; then
        log_error "Pod $pod_name is not running (status: $pod_status)"
        return 1
    fi
    
    # Copy file to pod
    local target_path="/app/hotswap/$target_subdir/$(basename "$source_file")"
    
    if kubectl cp "$source_file" "$NAMESPACE/$pod_name:$target_path"; then
        log_success "File copied to $pod_name:$target_path"
    else
        log_error "Failed to copy file to $pod_name"
        return 1
    fi
    
    # Trigger hot-swap in pod
    if kubectl exec -n "$NAMESPACE" "$pod_name" -- /app/scripts/hotswap-deploy.sh deploy "$target_path"; then
        log_success "Hot-swap completed in $pod_name"
        
        # Create hot-swap event
        create_hotswap_event "$pod_name" "$(basename "$source_file")" "SUCCESS"
        
        return 0
    else
        log_error "Hot-swap failed in $pod_name"
        
        # Create failure event
        create_hotswap_event "$pod_name" "$(basename "$source_file")" "FAILED"
        
        return 1
    fi
}

deploy_to_all_pods() {
    local source_file="$1"
    local target_subdir="$2"
    local deployment_name="${3:-$DEPLOYMENT}"
    
    log_info "Deploying $source_file to all pods in deployment $deployment_name"
    
    # Get all pods for the deployment
    local pods=$(get_pods "app=$deployment_name")
    
    if [ -z "$pods" ]; then
        log_error "No pods found for deployment $deployment_name"
        return 1
    fi
    
    local success_count=0
    local total_count=0
    local failed_pods=()
    
    for pod in $pods; do
        total_count=$((total_count + 1))
        
        if deploy_to_single_pod "$pod" "$source_file" "$target_subdir"; then
            success_count=$((success_count + 1))
        else
            failed_pods+=("$pod")
        fi
    done
    
    log_info "Deployment summary: $success_count/$total_count pods successful"
    
    if [ ${#failed_pods[@]} -gt 0 ]; then
        log_warning "Failed pods: ${failed_pods[*]}"
        return 1
    fi
    
    return 0
}

rolling_hotswap_deployment() {
    local source_file="$1"
    local target_subdir="$2"
    local deployment_name="${3:-$DEPLOYMENT}"
    
    log_info "Performing rolling hot-swap deployment..."
    
    local pods=$(get_pods "app=$deployment_name")
    local total_pods=$(echo $pods | wc -w)
    local current_pod=1
    
    for pod in $pods; do
        log_info "Processing pod $current_pod/$total_pods: $pod"
        
        # Deploy to single pod
        if deploy_to_single_pod "$pod" "$source_file" "$target_subdir"; then
            log_success "Pod $pod updated successfully"
            
            # Wait for pod to stabilize before moving to next
            log_info "Waiting for pod to stabilize..."
            sleep 10
            
            # Verify pod health
            if verify_pod_health "$pod"; then
                log_success "Pod $pod is healthy after hot-swap"
            else
                log_error "Pod $pod health check failed after hot-swap"
                
                # Optionally rollback this pod
                log_warning "Consider rolling back pod $pod"
            fi
        else
            log_error "Failed to update pod $pod"
            
            # Decide whether to continue or abort
            log_warning "Continuing with remaining pods..."
        fi
        
        current_pod=$((current_pod + 1))
    done
    
    log_success "Rolling hot-swap deployment completed"
}

verify_pod_health() {
    local pod_name="$1"
    local max_attempts=6
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        log_info "Health check attempt $attempt/$max_attempts for pod $pod_name"
        
        if kubectl exec -n "$NAMESPACE" "$pod_name" -- curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
            return 0
        fi
        
        sleep 10
        attempt=$((attempt + 1))
    done
    
    return 1
}

create_hotswap_event() {
    local pod_name="$1"
    local file_name="$2"
    local status="$3"
    
    local event_name="hotswap-$(date +%s)"
    local timestamp=$(date -Iseconds)
    
    kubectl create event "$event_name" \
        --namespace="$NAMESPACE" \
        --type=Normal \
        --reason="HotSwap$status" \
        --message="Hot-swap of $file_name in pod $pod_name: $status" \
        --source-host="$(hostname)" \
        --source-component="bytehot-hotswap-script" \
        --first-time="$timestamp" \
        --last-time="$timestamp" \
        --count=1 2>/dev/null || log_warning "Failed to create Kubernetes event"
}

list_hotswap_history() {
    log_info "Recent hot-swap events:"
    
    kubectl get events -n "$NAMESPACE" \
        --field-selector reason=HotSwapSUCCESS,reason=HotSwapFAILED \
        --sort-by='.firstTimestamp' \
        -o custom-columns=TIME:.firstTimestamp,TYPE:.type,REASON:.reason,MESSAGE:.message \
        2>/dev/null || log_warning "No hot-swap events found"
}

rollback_hotswap() {
    local pod_name="$1"
    local file_path="$2"
    
    log_info "Rolling back hot-swap in pod $pod_name"
    
    if kubectl exec -n "$NAMESPACE" "$pod_name" -- /app/scripts/hotswap-deploy.sh rollback "$file_path"; then
        log_success "Rollback completed in $pod_name"
        create_hotswap_event "$pod_name" "$(basename "$file_path")" "ROLLBACK_SUCCESS"
    else
        log_error "Rollback failed in $pod_name"
        create_hotswap_event "$pod_name" "$(basename "$file_path")" "ROLLBACK_FAILED"
        return 1
    fi
}

monitor_hotswap_status() {
    local deployment_name="${1:-$DEPLOYMENT}"
    
    log_info "Monitoring hot-swap status for deployment $deployment_name"
    
    while true; do
        clear
        echo "=== ByteHot Hot-swap Status Monitor ==="
        echo "Deployment: $deployment_name"
        echo "Namespace: $NAMESPACE"
        echo "Time: $(date)"
        echo ""
        
        # Pod status
        echo "=== Pod Status ==="
        kubectl get pods -n "$NAMESPACE" -l "app=$deployment_name" -o wide
        echo ""
        
        # Recent events
        echo "=== Recent Hot-swap Events ==="
        kubectl get events -n "$NAMESPACE" \
            --field-selector reason=HotSwapSUCCESS,reason=HotSwapFAILED \
            --sort-by='.firstTimestamp' \
            -o custom-columns=TIME:.firstTimestamp,REASON:.reason,MESSAGE:.message \
            | tail -5
        echo ""
        
        # Application health
        echo "=== Application Health ==="
        local pods=$(get_pods "app=$deployment_name")
        for pod in $pods; do
            if kubectl exec -n "$NAMESPACE" "$pod" -- curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
                echo "$pod: HEALTHY"
            else
                echo "$pod: UNHEALTHY"
            fi
        done
        
        echo ""
        echo "Press Ctrl+C to exit monitoring"
        sleep 10
    done
}

show_help() {
    echo "ByteHot Kubernetes Hot-swap Script"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  deploy-class <file>     - Deploy class file to all pods"
    echo "  deploy-jar <file>       - Deploy JAR file to all pods"
    echo "  deploy-resource <file>  - Deploy resource file to all pods"
    echo "  deploy-to-pod <pod> <file> <type> - Deploy to specific pod"
    echo "  rolling-deploy <file> <type> - Perform rolling hot-swap deployment"
    echo "  rollback <pod> <file>   - Rollback hot-swap in specific pod"
    echo "  verify-health [pod]     - Verify application health"
    echo "  list-history            - Show hot-swap event history"
    echo "  monitor                 - Monitor hot-swap status"
    echo "  help                    - Show this help message"
    echo ""
    echo "File Types:"
    echo "  class      - Java class files (.class)"
    echo "  jar        - JAR files (.jar)"
    echo "  resource   - Configuration and resource files"
    echo ""
    echo "Options:"
    echo "  --namespace  - Kubernetes namespace (default: bytehot)"
    echo "  --deployment - Deployment name (default: bytehot-app)"
    echo "  --rolling    - Use rolling deployment strategy"
    echo "  --verify     - Verify health after deployment"
    echo ""
    echo "Examples:"
    echo "  $0 deploy-class MyClass.class"
    echo "  $0 deploy-jar updated-library.jar --rolling"
    echo "  $0 deploy-resource application.properties --verify"
    echo "  $0 deploy-to-pod bytehot-app-xyz MyClass.class class"
    echo "  $0 rolling-deploy MyClass.class class"
    echo "  $0 monitor"
    echo ""
    echo "Environment Variables:"
    echo "  NAMESPACE    - Kubernetes namespace"
    echo "  DEPLOYMENT   - Deployment name"
    echo "  KUBECTL      - kubectl command path"
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --namespace)
                NAMESPACE="$2"
                shift 2
                ;;
            --deployment)
                DEPLOYMENT="$2"
                shift 2
                ;;
            --rolling)
                ROLLING_DEPLOY=true
                shift
                ;;
            --verify)
                VERIFY_HEALTH=true
                shift
                ;;
            *)
                break
                ;;
        esac
    done
}

# Main execution
main() {
    local command="${1:-help}"
    shift || true
    
    parse_args "$@"
    
    case "$command" in
        "deploy-class")
            if [ -z "$1" ]; then
                log_error "No class file specified"
                exit 1
            fi
            wait_for_file_stability "$1"
            if [ "$ROLLING_DEPLOY" = "true" ]; then
                rolling_hotswap_deployment "$1" "classes"
            else
                deploy_to_all_pods "$1" "classes"
            fi
            ;;
        "deploy-jar")
            if [ -z "$1" ]; then
                log_error "No JAR file specified"
                exit 1
            fi
            wait_for_file_stability "$1"
            if [ "$ROLLING_DEPLOY" = "true" ]; then
                rolling_hotswap_deployment "$1" "jars"
            else
                deploy_to_all_pods "$1" "jars"
            fi
            ;;
        "deploy-resource")
            if [ -z "$1" ]; then
                log_error "No resource file specified"
                exit 1
            fi
            wait_for_file_stability "$1"
            if [ "$ROLLING_DEPLOY" = "true" ]; then
                rolling_hotswap_deployment "$1" "resources"
            else
                deploy_to_all_pods "$1" "resources"
            fi
            ;;
        "deploy-to-pod")
            if [ $# -lt 3 ]; then
                log_error "Usage: deploy-to-pod <pod> <file> <type>"
                exit 1
            fi
            wait_for_file_stability "$2"
            deploy_to_single_pod "$1" "$2" "${3}s"  # Add 's' for directory name
            ;;
        "rolling-deploy")
            if [ $# -lt 2 ]; then
                log_error "Usage: rolling-deploy <file> <type>"
                exit 1
            fi
            wait_for_file_stability "$1"
            rolling_hotswap_deployment "$1" "${2}s"  # Add 's' for directory name
            ;;
        "rollback")
            if [ $# -lt 2 ]; then
                log_error "Usage: rollback <pod> <file>"
                exit 1
            fi
            rollback_hotswap "$1" "$2"
            ;;
        "verify-health")
            if [ -n "$1" ]; then
                verify_pod_health "$1"
            else
                local pods=$(get_pods "app=$DEPLOYMENT")
                for pod in $pods; do
                    verify_pod_health "$pod"
                done
            fi
            ;;
        "list-history")
            list_hotswap_history
            ;;
        "monitor")
            monitor_hotswap_status
            ;;
        "help")
            show_help
            ;;
        *)
            log_error "Unknown command: $command"
            show_help
            exit 1
            ;;
    esac
}

# Execute main function
main "$@"