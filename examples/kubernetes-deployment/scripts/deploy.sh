#!/bin/bash
# ByteHot Kubernetes Deployment Script
# Comprehensive deployment automation for ByteHot applications

set -e

# Configuration
NAMESPACE="${NAMESPACE:-bytehot}"
MANIFESTS_DIR="$(dirname "$0")/../manifests"
HELM_CHART_DIR="$(dirname "$0")/../helm-chart"
KUBECTL="${KUBECTL:-kubectl}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl not found. Please install kubectl first."
        exit 1
    fi
    
    # Check cluster connectivity
    if ! kubectl cluster-info &> /dev/null; then
        log_error "Cannot connect to Kubernetes cluster. Please check your kubeconfig."
        exit 1
    fi
    
    # Check if namespace exists
    if kubectl get namespace "$NAMESPACE" &> /dev/null; then
        log_warning "Namespace '$NAMESPACE' already exists"
    fi
    
    # Check for required images
    log_info "Prerequisites check completed"
}

create_namespace() {
    log_info "Creating namespace '$NAMESPACE'..."
    
    if kubectl apply -f "$MANIFESTS_DIR/namespace.yaml"; then
        log_success "Namespace created successfully"
    else
        log_error "Failed to create namespace"
        exit 1
    fi
}

deploy_storage() {
    log_info "Deploying storage components..."
    
    # Apply storage manifests
    if kubectl apply -f "$MANIFESTS_DIR/storage.yaml"; then
        log_success "Storage components deployed"
    else
        log_error "Failed to deploy storage components"
        exit 1
    fi
    
    # Wait for PVCs to be bound
    log_info "Waiting for PVCs to be bound..."
    kubectl wait --for=condition=Bound pvc/bytehot-hotswap-pvc -n "$NAMESPACE" --timeout=300s
    kubectl wait --for=condition=Bound pvc/bytehot-logs-pvc -n "$NAMESPACE" --timeout=300s
    
    log_success "Storage components ready"
}

deploy_rbac() {
    log_info "Deploying RBAC components..."
    
    if kubectl apply -f "$MANIFESTS_DIR/service-account.yaml"; then
        log_success "RBAC components deployed"
    else
        log_error "Failed to deploy RBAC components"
        exit 1
    fi
}

deploy_config() {
    log_info "Deploying configuration..."
    
    # Apply secrets first
    if kubectl apply -f "$MANIFESTS_DIR/secret.yaml"; then
        log_success "Secrets deployed"
    else
        log_warning "Failed to deploy secrets (may already exist)"
    fi
    
    # Apply ConfigMaps
    if kubectl apply -f "$MANIFESTS_DIR/configmap.yaml"; then
        log_success "ConfigMaps deployed"
    else
        log_error "Failed to deploy ConfigMaps"
        exit 1
    fi
}

deploy_application() {
    log_info "Deploying ByteHot application..."
    
    # Deploy the main application
    if kubectl apply -f "$MANIFESTS_DIR/deployment.yaml"; then
        log_success "Application deployment created"
    else
        log_error "Failed to create application deployment"
        exit 1
    fi
    
    # Deploy services
    if kubectl apply -f "$MANIFESTS_DIR/service.yaml"; then
        log_success "Services deployed"
    else
        log_error "Failed to deploy services"
        exit 1
    fi
    
    # Wait for deployment to be ready
    log_info "Waiting for application to be ready..."
    kubectl wait --for=condition=Available deployment/bytehot-app -n "$NAMESPACE" --timeout=600s
    
    log_success "Application is ready"
}

deploy_autoscaling() {
    log_info "Deploying autoscaling components..."
    
    if kubectl apply -f "$MANIFESTS_DIR/hpa.yaml"; then
        log_success "Autoscaling components deployed"
    else
        log_warning "Failed to deploy autoscaling (may require metrics server)"
    fi
}

deploy_ingress() {
    log_info "Deploying ingress..."
    
    if kubectl apply -f "$MANIFESTS_DIR/ingress.yaml"; then
        log_success "Ingress deployed"
    else
        log_warning "Failed to deploy ingress (may require ingress controller)"
    fi
}

verify_deployment() {
    log_info "Verifying deployment..."
    
    # Check pod status
    log_info "Pod status:"
    kubectl get pods -n "$NAMESPACE" -l app=bytehot-app
    
    # Check service status
    log_info "Service status:"
    kubectl get services -n "$NAMESPACE"
    
    # Check ingress status
    log_info "Ingress status:"
    kubectl get ingress -n "$NAMESPACE" || log_warning "No ingress found"
    
    # Test application health
    log_info "Testing application health..."
    local pod_name=$(kubectl get pods -n "$NAMESPACE" -l app=bytehot-app -o jsonpath='{.items[0].metadata.name}')
    
    if kubectl exec -n "$NAMESPACE" "$pod_name" -- curl -f http://localhost:8080/actuator/health; then
        log_success "Application health check passed"
    else
        log_warning "Application health check failed"
    fi
    
    log_success "Deployment verification completed"
}

cleanup_deployment() {
    log_info "Cleaning up deployment..."
    
    # Delete in reverse order
    kubectl delete -f "$MANIFESTS_DIR/ingress.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/hpa.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/service.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/deployment.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/configmap.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/secret.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/service-account.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/storage.yaml" --ignore-not-found=true
    kubectl delete -f "$MANIFESTS_DIR/namespace.yaml" --ignore-not-found=true
    
    log_success "Cleanup completed"
}

deploy_development() {
    log_info "Deploying development environment..."
    
    # Deploy minimal development setup
    create_namespace
    deploy_rbac
    deploy_config
    
    # Deploy only development application
    kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bytehot-dev
  namespace: $NAMESPACE
  labels:
    app: bytehot-dev
spec:
  replicas: 1
  selector:
    matchLabels:
      app: bytehot-dev
  template:
    metadata:
      labels:
        app: bytehot-dev
    spec:
      containers:
      - name: bytehot-dev
        image: bytehot/docker-integration:latest
        command: ["development"]
        ports:
        - containerPort: 8080
        - containerPort: 5005
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes,development"
        - name: ENABLE_DEBUG
          value: "true"
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
        - name: hotswap-volume
          mountPath: /app/hotswap
      volumes:
      - name: config-volume
        configMap:
          name: bytehot-config
      - name: hotswap-volume
        emptyDir: {}
EOF
    
    # Create development service
    kubectl expose deployment bytehot-dev --type=NodePort --port=8080 --target-port=8080 -n "$NAMESPACE"
    
    log_success "Development environment deployed"
}

show_help() {
    echo "ByteHot Kubernetes Deployment Script"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  deploy       - Deploy complete ByteHot application stack"
    echo "  deploy-dev   - Deploy development environment only"
    echo "  cleanup      - Remove all deployed resources"
    echo "  verify       - Verify deployment status"
    echo "  logs         - Show application logs"
    echo "  status       - Show deployment status"
    echo "  help         - Show this help message"
    echo ""
    echo "Options:"
    echo "  --namespace  - Kubernetes namespace (default: bytehot)"
    echo "  --skip-wait  - Skip waiting for resources to be ready"
    echo "  --dry-run    - Show what would be deployed without applying"
    echo ""
    echo "Examples:"
    echo "  $0 deploy --namespace my-bytehot"
    echo "  $0 deploy-dev"
    echo "  $0 cleanup"
    echo "  $0 status"
    echo ""
    echo "Environment Variables:"
    echo "  NAMESPACE    - Kubernetes namespace"
    echo "  KUBECTL      - kubectl command path"
}

show_status() {
    log_info "ByteHot Kubernetes Deployment Status"
    echo ""
    
    # Namespace status
    echo "=== Namespace ==="
    kubectl get namespace "$NAMESPACE" 2>/dev/null || echo "Namespace not found"
    echo ""
    
    # Pod status
    echo "=== Pods ==="
    kubectl get pods -n "$NAMESPACE" 2>/dev/null || echo "No pods found"
    echo ""
    
    # Service status
    echo "=== Services ==="
    kubectl get services -n "$NAMESPACE" 2>/dev/null || echo "No services found"
    echo ""
    
    # Deployment status
    echo "=== Deployments ==="
    kubectl get deployments -n "$NAMESPACE" 2>/dev/null || echo "No deployments found"
    echo ""
    
    # PVC status
    echo "=== Persistent Volume Claims ==="
    kubectl get pvc -n "$NAMESPACE" 2>/dev/null || echo "No PVCs found"
    echo ""
    
    # Ingress status
    echo "=== Ingress ==="
    kubectl get ingress -n "$NAMESPACE" 2>/dev/null || echo "No ingress found"
}

show_logs() {
    log_info "Showing ByteHot application logs..."
    
    local pod_name=$(kubectl get pods -n "$NAMESPACE" -l app=bytehot-app -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)
    
    if [ -n "$pod_name" ]; then
        kubectl logs -n "$NAMESPACE" "$pod_name" -f
    else
        log_error "No ByteHot application pods found"
        exit 1
    fi
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --namespace)
                NAMESPACE="$2"
                shift 2
                ;;
            --skip-wait)
                SKIP_WAIT=true
                shift
                ;;
            --dry-run)
                DRY_RUN=true
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
        "deploy")
            log_info "Starting ByteHot Kubernetes deployment..."
            check_prerequisites
            create_namespace
            deploy_storage
            deploy_rbac
            deploy_config
            deploy_application
            deploy_autoscaling
            deploy_ingress
            verify_deployment
            log_success "ByteHot deployment completed successfully!"
            ;;
        "deploy-dev")
            log_info "Starting ByteHot development deployment..."
            check_prerequisites
            deploy_development
            log_success "ByteHot development environment ready!"
            ;;
        "cleanup")
            log_info "Starting cleanup..."
            cleanup_deployment
            ;;
        "verify")
            verify_deployment
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs
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