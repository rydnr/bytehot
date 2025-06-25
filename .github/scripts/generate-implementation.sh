#!/bin/bash
set -euo pipefail

# Generate implementation.html from implementation.org

echo "📋 Creating implementation index page..."

CSS="$(source ./.github/scripts/css.sh)"
NAV="$(source ./.github/scripts/nav.sh)"
FOOTER="$(source ./.github/scripts/footer.sh)"
MATRIX="$(source ./.github/scripts/matrix.sh)"

# Create Implementation index page (index files are allowed per CLAUDE.md)
cat >bytehot/implementation.html <<'HTML_EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot Implementation - Literate Programming Documentation</title>
    <style>
${CSS}
    </style>
</head>
<body>
    <div class="matrix-bg"></div>

    <nav class="nav-header">
${NAV}
    </nav>
    <div class="content">
        <h1>🔥 ByteHot Implementation - Literate Programming Documentation</h1>
        
        <p>This section contains comprehensive literate programming documentation for all ByteHot components, following Domain-Driven Design principles and hexagonal architecture patterns.</p>
        
        <h2>🏗️ Core Architecture</h2>
        
        <h3>Domain Layer</h3>
        <ul>
            <li><a href="docs/ByteHot.html">🎯 ByteHot Aggregate</a> - Central domain aggregate coordinating hot-swap operations and system state.</li>
            <li><a href="docs/HotSwapManager.html">🔄 Hot-Swap Manager</a> - Core domain service managing the complete hot-swap lifecycle.</li>
            <li><a href="docs/BytecodeValidator.html">✅ Bytecode Validator</a> - Domain service ensuring bytecode compatibility and validation.</li>
            <li><a href="docs/FrameworkIntegration.html">🔗 Framework Integration</a> - Integration patterns for external frameworks and libraries.</li>
        </ul>
        
        <h2>🔌 Domain Services & Adapters</h2>
        
        <ul>
            <li><a href="docs/InstrumentationService.html">🔧 Instrumentation Service</a> - Core domain service providing JVM instrumentation capabilities and class redefinition operations.</li>
            <li><a href="docs/ports/FileWatcherPort.html">👁️ File Watcher Port</a> - Domain interface for file system monitoring operations.</li>
            <li><a href="docs/FileWatcherAdapter.html">📁 File Watcher Adapter</a> - Infrastructure implementation of file system monitoring capabilities.</li>
            <li><a href="docs/ConfigurationPort.html">⚙️ Configuration Port</a> - Domain interface for system configuration management.</li>
            <li><a href="docs/ConfigurationAdapter.html">🔧 Configuration Adapter</a> - Infrastructure implementation of configuration management.</li>
            <li><a href="docs/EventEmitterPort.html">📡 Event Emitter Port</a> - Domain interface for event broadcasting and distribution.</li>
            <li><a href="docs/EventEmitterAdapter.html">📢 Event Emitter Adapter</a> - Infrastructure implementation of event emission capabilities.</li>
        </ul>
        
        <h2>🎭 Domain Events</h2>
        
        <ul>
            <li><a href="docs/events/ClassFileChanged.html">📝 Class File Changed</a> - Event triggered when a class file is modified on disk.</li>
            <li><a href="docs/events/HotSwapRequested.html">🔄 Hot-Swap Requested</a> - Event triggered when a hot-swap operation is initiated.</li>
            <li><a href="docs/events/UserSessionStarted.html">👤 User Session Started</a> - Event triggered when a user session is established.</li>
        </ul>
        
        <h2>📊 Advanced Components</h2>
        
        <ul>
            <li><a href="docs/RollbackManager.html">↩️ Rollback Management</a> - Sophisticated rollback and recovery coordination system.</li>
            <li><a href="docs/StatePreserver.html">💾 State Preservation</a> - Object state preservation and restoration during redefinitions.</li>
            <li><a href="docs/ErrorRecoveryManager.html">🚑 Error Recovery</a> - Automated error recovery and system resilience management.</li>
            <li><a href="docs/Flow.html">🌊 Flow Detection</a> - Dynamic process flow discovery and documentation system.</li>
        </ul>
        
        <h2>🎨 Revolutionary Architecture</h2>
        
        <p>ByteHot's literate programming approach combines Domain-Driven Design with hexagonal architecture, creating a self-documenting system where code and documentation evolve together in perfect harmony.</p>
        
        <h2>📖 Documentation Principles</h2>
        
        <p>All implementation documentation follows strict literate programming principles:</p>
        
        <ul>
            <li><strong>Invariant Documentation:</strong> Each class documents its assumptions and guarantees</li>
            <li><strong>Behavior Documentation:</strong> Complete explanation of class responsibilities and collaborations</li>
            <li><strong>Architecture Compliance:</strong> Strict adherence to hexagonal architecture boundaries</li>
            <li><strong>Code Tangling:</strong> Documentation fragments that generate actual implementation</li>
            <li><strong>Living Documentation:</strong> Documentation that evolves with the codebase</li>
        </ul>
    </div>
${FOOTER}
<script>
${MATRIX}
</script>
</body>
</html>
HTML_EOF

# Also convert implementation.org if it exists
if [ -f "implementation.org" ]; then
    echo "📄 Converting implementation.org to implementation-content.html..."
    pandoc -f org -t html5 --toc implementation.org -o bytehot/implementation-content.html || echo "implementation.org conversion failed"
fi

echo "✅ Implementation page created"
