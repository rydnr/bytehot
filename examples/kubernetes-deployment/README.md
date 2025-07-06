# ByteHot Kubernetes Deployment Examples

This directory contains comprehensive Kubernetes deployment examples demonstrating ByteHot hot-swapping capabilities in container orchestration environments.

## Overview

The Kubernetes deployment examples showcase:

- **Production-ready manifests** for scalable ByteHot applications
- **Hot-swapping in pods** with persistent storage and rolling deployments
- **RBAC configuration** with appropriate permissions for ByteHot operations
- **Auto-scaling** with HPA and VPA support for ByteHot workloads
- **Service mesh integration** with Istio support for advanced traffic management
- **Monitoring and observability** with Prometheus, Grafana, and custom metrics
- **Security hardening** with pod security policies and network policies

## Quick Start

### Prerequisites

- Kubernetes cluster (v1.20+)
- kubectl configured for your cluster
- Docker images built and available
- Persistent volume provisioner
- (Optional) Ingress controller
- (Optional) Metrics server for autoscaling

### 1. Deploy Complete Stack

```bash
# Deploy everything
./scripts/deploy.sh deploy

# Check deployment status
./scripts/deploy.sh status

# View application logs
./scripts/deploy.sh logs
```

### 2. Development Environment

```bash
# Deploy minimal development setup
./scripts/deploy.sh deploy-dev

# Access development application
kubectl port-forward -n bytehot svc/bytehot-dev-service 8080:8080

# Access debug port
kubectl port-forward -n bytehot svc/bytehot-dev-service 5005:5005
```

### 3. Hot-swap Operations

```bash
# Deploy a class file to all pods
./scripts/hotswap-k8s.sh deploy-class MyUpdatedClass.class

# Deploy a JAR with rolling strategy
./scripts/hotswap-k8s.sh deploy-jar updated-library.jar --rolling

# Deploy to specific pod
./scripts/hotswap-k8s.sh deploy-to-pod bytehot-app-xyz MyClass.class class

# Monitor hot-swap operations
./scripts/hotswap-k8s.sh monitor
```

## Architecture

### Components

#### Core Application Components
- **Namespace**: Isolated environment for ByteHot applications
- **Deployment**: Scalable application with hot-swap capabilities
- **Service**: Internal and external service exposure
- **ConfigMap**: Application and ByteHot configuration
- **Secret**: Sensitive configuration (database, API keys)
- **ServiceAccount**: RBAC for ByteHot operations

#### Storage Components
- **PersistentVolume**: Shared storage for hot-swap files
- **StorageClass**: Optimized storage for hot-swap operations
- **Volume mounts**: Hot-swap directories, logs, configuration

#### Networking Components
- **Ingress**: External access with TLS termination
- **NetworkPolicy**: Security controls for pod communication
- **Service mesh**: Advanced traffic management (optional)

#### Scaling and Monitoring
- **HorizontalPodAutoscaler**: CPU/memory-based scaling
- **VerticalPodAutoscaler**: Resource optimization
- **PodDisruptionBudget**: Availability during updates
- **ServiceMonitor**: Prometheus metrics collection

## Configuration

### Environment-specific Configurations

#### Development
```yaml
# Development profile with fast hot-swapping
env:
  - name: SPRING_PROFILES_ACTIVE
    value: "kubernetes,development"
  - name: BYTEHOT_POLL_INTERVAL
    value: "1000"  # Fast polling
  - name: ENABLE_DEBUG
    value: "true"
```

#### Production
```yaml
# Production profile with security hardening
env:
  - name: SPRING_PROFILES_ACTIVE
    value: "kubernetes,production"
  - name: BYTEHOT_POLL_INTERVAL
    value: "5000"  # Slower polling
  - name: BYTEHOT_SECURITY_ENABLED
    value: "true"
```

### ByteHot-specific Configuration

#### ConfigMap Settings
```yaml
# ByteHot configuration
bytehot:
  enabled: true
  hotswap:
    enabled: true
    watch-directory: /app/hotswap
    poll-interval: 2000
    backup-enabled: true
  security:
    enabled: true
    whitelist-packages:
      - org.acmsl.bytehot.examples
  monitoring:
    enabled: true
    prometheus-enabled: true
```

#### JVM Configuration
```yaml
# JVM options for ByteHot
JAVA_OPTS: |
  -javaagent:/app/bytehot-agent.jar
  -Xms512m -Xmx1g
  -XX:+UseG1GC
  -Dbytehot.hotswap.enabled=true
  -Dbytehot.watch.directory=/app/hotswap
```

## Storage Configuration

### Persistent Volumes

#### Hot-swap Storage
```yaml
# Optimized for hot-swap operations
apiVersion: v1
kind: PersistentVolume
metadata:
  name: bytehot-hotswap-pv
spec:
  capacity:
    storage: 5Gi
  accessModes:
    - ReadWriteMany  # Shared across pods
  storageClassName: bytehot-fast-ssd
```

#### Storage Classes
```yaml
# Fast SSD storage for hot-swap
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: bytehot-fast-ssd
provisioner: kubernetes.io/aws-ebs  # Adjust for your cloud
parameters:
  type: gp3
  encrypted: "true"
```

### Volume Mounts

```yaml
volumeMounts:
  # Hot-swap directory (shared)
  - name: hotswap-volume
    mountPath: /app/hotswap
  # Application logs
  - name: logs-volume
    mountPath: /app/logs
  # Configuration (read-only)
  - name: config-volume
    mountPath: /app/config
    readOnly: true
```

## Security

### RBAC Configuration

#### Service Account
```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: bytehot-service-account
  namespace: bytehot
```

#### Role Permissions
```yaml
rules:
  # Pod operations for hot-swap coordination
  - apiGroups: [""]
    resources: ["pods", "pods/exec"]
    verbs: ["get", "list", "watch", "create"]
  # ConfigMap operations
  - apiGroups: [""]
    resources: ["configmaps"]
    verbs: ["get", "list", "watch"]
  # Event creation for hot-swap notifications
  - apiGroups: [""]
    resources: ["events"]
    verbs: ["create", "patch"]
```

### Security Hardening

#### Pod Security Context
```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
  runAsGroup: 1000
  fsGroup: 1000
  seccompProfile:
    type: RuntimeDefault
```

#### Container Security
```yaml
securityContext:
  allowPrivilegeEscalation: false
  readOnlyRootFilesystem: false  # ByteHot needs write access
  capabilities:
    drop: ["ALL"]
```

#### Network Policies
```yaml
# Restrict network access
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: bytehot-network-policy
spec:
  podSelector:
    matchLabels:
      app: bytehot-app
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              role: frontend
  egress:
    - to: []
      ports:
        - protocol: TCP
          port: 5432  # Database
```

## Scaling and Performance

### Horizontal Pod Autoscaler

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: bytehot-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: bytehot-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    # Custom metric: hot-swap operations rate
    - type: Pods
      pods:
        metric:
          name: bytehot_hotswap_operations_per_second
        target:
          type: AverageValue
          averageValue: "5"
```

### Resource Management

#### Resource Requests and Limits
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

#### Quality of Service
- **Guaranteed**: Request = Limit for critical workloads
- **Burstable**: Request < Limit for variable workloads
- **BestEffort**: No requests/limits for development

## Hot-swapping Operations

### Manual Hot-swap

```bash
# Deploy class file to all pods
kubectl cp MyClass.class bytehot/bytehot-app-xyz:/app/hotswap/classes/

# Trigger hot-swap
kubectl exec -n bytehot bytehot-app-xyz -- \
  /app/scripts/hotswap-deploy.sh deploy /app/hotswap/classes/MyClass.class
```

### Automated Hot-swap

```bash
# Using the hot-swap script
./scripts/hotswap-k8s.sh deploy-class MyClass.class

# Rolling deployment
./scripts/hotswap-k8s.sh rolling-deploy MyClass.class class

# Verify deployment
./scripts/hotswap-k8s.sh verify-health
```

### Hot-swap Strategies

#### All-at-once Deployment
- Deploy to all pods simultaneously
- Fastest deployment time
- Higher risk of service disruption

#### Rolling Deployment
- Deploy to pods one by one
- Slower but safer
- Maintains service availability

#### Canary Deployment
- Deploy to subset of pods first
- Monitor metrics and health
- Rollout to remaining pods if successful

## Monitoring and Observability

### Metrics Collection

#### Prometheus Configuration
```yaml
# Scrape ByteHot metrics
- job_name: 'bytehot-k8s'
  kubernetes_sd_configs:
    - role: pod
      namespaces:
        names: [bytehot]
  relabel_configs:
    - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
      action: keep
      regex: true
```

#### Custom Metrics
- `bytehot_hotswap_operations_total`: Hot-swap operation count
- `bytehot_hotswap_duration_seconds`: Hot-swap operation duration
- `bytehot_hotswap_success_rate`: Hot-swap success rate
- `bytehot_memory_usage_post_hotswap`: Memory usage after hot-swap
- `bytehot_gc_impact_seconds`: GC impact from hot-swapping

### Health Checks

#### Liveness Probe
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 30
```

#### Readiness Probe
```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
```

#### Custom Health Indicators
```java
@Component
public class ByteHotHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check ByteHot agent status
        // Verify hot-swap directory accessibility
        // Return health status
    }
}
```

### Logging

#### Structured Logging
```yaml
logging:
  pattern:
    console: |
      {
        "timestamp": "%d{ISO8601}",
        "level": "%level",
        "thread": "%thread",
        "logger": "%logger{36}",
        "message": "%msg",
        "traceId": "%X{traceId:-}",
        "spanId": "%X{spanId:-}",
        "bytehot": {
          "pod": "${HOSTNAME:-unknown}",
          "namespace": "${KUBERNETES_NAMESPACE:-unknown}"
        }
      }%n
```

#### Log Aggregation
- **Fluentd/Fluent Bit**: Log collection and forwarding
- **Elasticsearch**: Log storage and indexing
- **Kibana**: Log visualization and analysis

## Troubleshooting

### Common Issues

#### Hot-swap Not Working

1. **Check ByteHot Agent**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- jps -v | grep bytehot-agent
```

2. **Verify Permissions**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- ls -la /app/hotswap/
```

3. **Check Configuration**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- cat /app/config/bytehot.properties
```

#### Pod Startup Issues

1. **Check Resource Constraints**
```bash
kubectl describe pod -n bytehot bytehot-app-xyz
```

2. **Verify Volume Mounts**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- df -h
```

3. **Check Network Connectivity**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- nslookup postgres-service
```

#### Performance Issues

1. **Monitor Resource Usage**
```bash
kubectl top pods -n bytehot
```

2. **Check GC Performance**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- \
  jstat -gc $(jps | grep ByteHot | cut -d' ' -f1)
```

3. **Analyze Hot-swap Metrics**
```bash
kubectl exec -n bytehot bytehot-app-xyz -- \
  curl http://localhost:8080/actuator/prometheus | grep bytehot
```

### Debugging Commands

#### Pod Debugging
```bash
# Get pod logs
kubectl logs -n bytehot bytehot-app-xyz -f

# Execute commands in pod
kubectl exec -n bytehot bytehot-app-xyz -it -- /bin/bash

# Port forward for local debugging
kubectl port-forward -n bytehot bytehot-app-xyz 5005:5005
```

#### Event Monitoring
```bash
# Watch events
kubectl get events -n bytehot --watch

# Filter hot-swap events
kubectl get events -n bytehot \
  --field-selector reason=HotSwapSUCCESS,reason=HotSwapFAILED
```

#### Configuration Debugging
```bash
# Check ConfigMaps
kubectl get configmap -n bytehot bytehot-config -o yaml

# Check Secrets
kubectl get secret -n bytehot bytehot-secrets -o yaml

# Verify RBAC
kubectl auth can-i create events --as=system:serviceaccount:bytehot:bytehot-service-account
```

## Best Practices

### Deployment Best Practices

1. **Use resource requests and limits** for predictable scheduling
2. **Implement proper health checks** for reliable deployments
3. **Configure pod disruption budgets** for high availability
4. **Use rolling updates** for zero-downtime deployments
5. **Monitor resource usage** and adjust limits accordingly

### Hot-swapping Best Practices

1. **Test hot-swap scenarios** in development first
2. **Use rolling deployments** for production hot-swaps
3. **Monitor application health** after hot-swap operations
4. **Implement rollback procedures** for failed hot-swaps
5. **Log all hot-swap operations** for audit trails

### Security Best Practices

1. **Use least privilege RBAC** for service accounts
2. **Enable pod security policies** or security contexts
3. **Implement network policies** for traffic control
4. **Scan images** for vulnerabilities regularly
5. **Rotate secrets** and certificates periodically

### Monitoring Best Practices

1. **Implement comprehensive metrics** for hot-swap operations
2. **Set up alerting** for failed hot-swaps and performance degradation
3. **Use distributed tracing** for complex request flows
4. **Monitor resource usage** trends over time
5. **Create dashboards** for operational visibility

## Examples and Demos

The deployment includes several example scenarios:

### 1. Simple Class Hot-swap
```bash
# Compile updated class
javac -cp /path/to/classpath MyBusinessLogic.java

# Deploy to Kubernetes
./scripts/hotswap-k8s.sh deploy-class MyBusinessLogic.class --rolling
```

### 2. Configuration Update
```bash
# Update ConfigMap
kubectl patch configmap bytehot-config -n bytehot --patch='
data:
  application.yml: |
    # Updated configuration
    server:
      port: 8080
    bytehot:
      hotswap:
        poll-interval: 1000
'

# Restart pods to pick up new config
kubectl rollout restart deployment/bytehot-app -n bytehot
```

### 3. Library Update
```bash
# Deploy updated library
./scripts/hotswap-k8s.sh deploy-jar updated-library-1.2.0.jar --rolling

# Monitor the deployment
./scripts/hotswap-k8s.sh monitor
```

## Contributing

When adding new Kubernetes examples:

1. Follow Kubernetes best practices and conventions
2. Include comprehensive RBAC configuration
3. Add appropriate monitoring and health checks
4. Test in multiple Kubernetes environments
5. Update documentation with new features

## References

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [ByteHot Documentation](../../README.md)
- [Hot-swapping Best Practices](../README.md)
- [Container Security Guidelines](https://kubernetes.io/docs/concepts/security/)
- [Monitoring and Logging](https://kubernetes.io/docs/concepts/cluster-administration/logging/)