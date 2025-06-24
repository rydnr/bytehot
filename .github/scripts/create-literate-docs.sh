#!/bin/bash
set -euo pipefail

# Literate Programming Documentation Creation Script
# This script creates a comprehensive literate programming documentation index

echo "📚 Creating comprehensive literate programming documentation..."

# Create bytehot directory if not exists
mkdir -p bytehot

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"
# Generate comprehensive literate programming documentation
cat >bytehot/literate-docs.html <<'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Literate Programming Documentation - ByteHot</title>
    <style>
${CSS}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
${NAV}
    </nav>

    <div class="container">
        <h1>📚 Literate Programming Documentation</h1>
        
        <div class="intro-section">
            <p><strong>Literate Programming</strong> is ByteHot's approach to documentation where code and explanation are interwoven. Each class is documented not just with what it does, but why it exists, how it collaborates, and what invariants it maintains.</p>
            <p>This comprehensive index provides direct access to detailed documentation for every class in the ByteHot system, organized by architectural layer and functional responsibility.</p>
        </div>

        <div class="revolutionary-banner">
            <div class="revolutionary-content">
                <h2 class="revolutionary-title">🎯 COMPREHENSIVE CLASS DOCUMENTATION 🎯</h2>
                <p style="font-size: 1.1rem; color: #ffffff;">
                    Every class documented with structure, behavior, and invariants
                </p>
            </div>
        </div>

        <div class="category-section">
            <h2>🏗️ Domain Layer</h2>
            <p>Core domain entities, aggregates, and value objects that encapsulate business logic.</p>
            <div class="docs-grid">
                <a href="ByteHot.html" class="class-link">ByteHot</a>
                <a href="AbstractVersionedDomainEvent.html" class="class-link">AbstractVersionedDomainEvent</a>
                <a href="AnalysisId.html" class="class-link">AnalysisId</a>
                <a href="BugReportGenerator.html" class="class-link">BugReportGenerator</a>
                <a href="BytecodeAnalyzer.html" class="class-link">BytecodeAnalyzer</a>
                <a href="BytecodeValidator.html" class="class-link">BytecodeValidator</a>
                <a href="BytecodeValidationException.html" class="class-link">BytecodeValidationException</a>
                <a href="CausalChain.html" class="class-link">CausalChain</a>
                <a href="ClassFileWatcher.html" class="class-link">ClassFileWatcher</a>
                <a href="ConflictResolutionStrategy.html" class="class-link">ConflictResolutionStrategy</a>
                <a href="ErrorHandler.html" class="class-link">ErrorHandler</a>
                <a href="EventSnapshot.html" class="class-link">EventSnapshot</a>
                <a href="EventSnapshotGenerator.html" class="class-link">EventSnapshotGenerator</a>
                <a href="Flow.html" class="class-link">Flow</a>
                <a href="FlowDetector.html" class="class-link">FlowDetector</a>
                <a href="HotSwapManager.html" class="class-link">HotSwapManager</a>
                <a href="InstanceTracker.html" class="class-link">InstanceTracker</a>
                <a href="RollbackManager.html" class="class-link">RollbackManager</a>
                <a href="User.html" class="class-link">User</a>
                <a href="WatchConfiguration.html" class="class-link">WatchConfiguration</a>
            </div>
        </div>

        <div class="category-section">
            <h2>🎯 Application Layer</h2>
            <p>Application services that coordinate domain objects and infrastructure.</p>
            <div class="docs-grid">
                <a href="ByteHotApplication.html" class="class-link">ByteHotApplication</a>
                <a href="Ports.html" class="class-link">Ports</a>
                <a href="DocProvider.html" class="class-link">DocProvider</a>
                <a href="DocLinkAvailable.html" class="class-link">DocLinkAvailable</a>
                <a href="JvmInstrumentationService.html" class="class-link">JvmInstrumentationService</a>
            </div>
        </div>

        <div class="category-section">
            <h2>🔧 Infrastructure Layer</h2>
            <p>Technical implementations that connect the domain to external systems.</p>
            <div class="docs-grid">
                <a href="ByteHotAgent.html" class="class-link">ByteHotAgent</a>
                <a href="ByteHotCLI.html" class="class-link">ByteHotCLI</a>
                <a href="ConfigurationAdapter.html" class="class-link">ConfigurationAdapter</a>
                <a href="EventEmitterAdapter.html" class="class-link">EventEmitterAdapter</a>
                <a href="FileWatcherAdapter.html" class="class-link">FileWatcherAdapter</a>
                <a href="FilesystemEventStoreAdapter.html" class="class-link">FilesystemEventStoreAdapter</a>
                <a href="InstrumentationAdapter.html" class="class-link">InstrumentationAdapter</a>
                <a href="UserContextResolver.html" class="class-link">UserContextResolver</a>
                <a href="FrameworkIntegration.html" class="class-link">FrameworkIntegration</a>
            </div>
        </div>

        <div class="category-section">
            <h2>📡 Domain Events</h2>
            <p>Events that capture significant domain occurrences and drive the system's behavior.</p>
            <div class="docs-grid">
                <a href="events/ClassFileChanged.html" class="class-link">ClassFileChanged</a>
                <a href="events/BytecodeValidated.html" class="class-link">BytecodeValidated</a>
                <a href="events/HotSwapRequested.html" class="class-link">HotSwapRequested</a>
                <a href="events/ClassRedefinitionSucceeded.html" class="class-link">ClassRedefinitionSucceeded</a>
                <a href="events/InstancesUpdated.html" class="class-link">InstancesUpdated</a>
                <a href="events/ByteHotAgentAttached.html" class="class-link">ByteHotAgentAttached</a>
                <a href="events/UserSessionStarted.html" class="class-link">UserSessionStarted</a>
                <a href="events/FlowDiscovered.html" class="class-link">FlowDiscovered</a>
                <a href="events/" class="class-link">📡 View All Events</a>
            </div>
        </div>

        <div class="category-section">
            <h2>🌊 Process Flows</h2>
            <p>Documented workflows that show how components collaborate to achieve business goals.</p>
            <div class="docs-grid">
                <a href="flows/complete-hot-swap-flow.html" class="class-link">Complete Hot-Swap Flow</a>
                <a href="flows/agent-startup-flow.html" class="class-link">Agent Startup Flow</a>
                <a href="flows/error-recovery-flow.html" class="class-link">Error Recovery Flow</a>
                <a href="flows/user-management-flow.html" class="class-link">User Management Flow</a>
                <a href="flows/bytecode-validation-flow.html" class="class-link">Bytecode Validation Flow</a>
                <a href="flows/framework-integration-flow.html" class="class-link">Framework Integration Flow</a>
                <a href="flows/" class="class-link">🌊 View All Flows</a>
            </div>
        </div>

        <div class="category-section">
            <h2>🚪 Port Interfaces</h2>
            <p>Boundary interfaces that define how the domain communicates with the outside world.</p>
            <div class="docs-grid">
                <a href="ports/InstrumentationPort.html" class="class-link">InstrumentationPort</a>
                <a href="ports/FileWatcherPort.html" class="class-link">FileWatcherPort</a>
                <a href="ConfigurationPort.html" class="class-link">ConfigurationPort</a>
                <a href="EventEmitterPort.html" class="class-link">EventEmitterPort</a>
                <a href="EventStorePort.html" class="class-link">EventStorePort</a>
                <a href="FlowDetectionPort.html" class="class-link">FlowDetectionPort</a>
                <a href="ports/" class="class-link">🚪 View All Ports</a>
            </div>
        </div>

        <div class="category-section">
            <h2>🛡️ Error Handling & Recovery</h2>
            <p>Comprehensive error management, classification, and recovery mechanisms.</p>
            <div class="docs-grid">
                <a href="ErrorHandler.html" class="class-link">ErrorHandler</a>
                <a href="ErrorRecoveryManager.html" class="class-link">ErrorRecoveryManager</a>
                <a href="RollbackManager.html" class="class-link">RollbackManager</a>
                <a href="ErrorClassifier.html" class="class-link">ErrorClassifier</a>
                <a href="RecoveryStrategy.html" class="class-link">RecoveryStrategy</a>
                <a href="CleanupResult.html" class="class-link">CleanupResult</a>
                <a href="CascadingRollbackResult.html" class="class-link">CascadingRollbackResult</a>
                <a href="ErrorSeverity.html" class="class-link">ErrorSeverity</a>
            </div>
        </div>

        <div class="category-section">
            <h2>🔄 Configuration & Utilities</h2>
            <p>Supporting classes for configuration, serialization, and cross-cutting concerns.</p>
            <div class="docs-grid">
                <a href="Defaults.html" class="class-link">Defaults</a>
                <a href="EventSerializationSupport.html" class="class-link">EventSerializationSupport</a>
                <a href="JsonClassFileChanged.html" class="class-link">JsonClassFileChanged</a>
                <a href="JsonFlow.html" class="class-link">JsonFlow</a>
                <a href="TimeWindow.html" class="class-link">TimeWindow</a>
                <a href="UserPreferences.html" class="class-link">UserPreferences</a>
                <a href="UserStatistics.html" class="class-link">UserStatistics</a>
            </div>
        </div>

        <div class="revolutionary-banner">
            <div class="revolutionary-content">
                <h2 class="revolutionary-title">📖 LIVING DOCUMENTATION 📖</h2>
                <p style="font-size: 1.1rem; color: #ffffff;">
                    Documentation that evolves with the code, tested as part of the build process
                </p>
            </div>
        </div>

${FOOTER}
    </div>

    <script>
${MATRIX}
    </script>
</body>
</html>
EOF

echo "✅ Comprehensive literate programming documentation created!"
