#!/bin/bash
set -euo pipefail

# Literate Programming Documentation Creation Script
# This script creates a comprehensive literate programming documentation index

echo "📚 Creating comprehensive literate programming documentation..."

# Create bytehot directory if not exists
mkdir -p bytehot

# Generate comprehensive literate programming documentation
cat > bytehot/literate-docs.html << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Literate Programming Documentation - ByteHot</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: linear-gradient(135deg, #0f0f23 0%, #1a1a3a 100%); color: #00ff00; line-height: 1.6; overflow-x: hidden; }
        .matrix-bg { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: -1; opacity: 0.1; }
        .nav-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(15, 15, 35, 0.95); backdrop-filter: blur(10px); padding: 1rem 2rem; z-index: 1000; border-bottom: 2px solid #00ff00; }
        .nav-links { display: flex; justify-content: center; gap: 2rem; flex-wrap: wrap; }
        .nav-link { color: #00ff00; text-decoration: none; padding: 0.5rem 1rem; border: 1px solid #00ff00; border-radius: 4px; transition: all 0.3s ease; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; }
        .nav-link:hover { background: #00ff00; color: #0f0f23; box-shadow: 0 0 20px #00ff00; transform: translateY(-2px); }
        .nav-link.getting-started { background: linear-gradient(45deg, #ff6b00, #ff0080); border-color: #ff6b00; color: white; animation: highlight 3s ease-in-out infinite; }
        @keyframes highlight { 0%, 100% { box-shadow: 0 0 10px #ff6b00; } 50% { box-shadow: 0 0 30px #ff6b00, 0 0 40px #ff0080; } }
        .nav-link.getting-started:hover { background: linear-gradient(45deg, #ff8b20, #ff20a0); transform: translateY(-3px) scale(1.05); }
        .container { max-width: 1400px; margin: 0 auto; background: rgba(26, 26, 58, 0.8); border: 1px solid #00ff00; border-radius: 8px; padding: 2rem; margin-top: 6rem; }
        h1, h2, h3 { color: #00cccc; text-shadow: 0 0 10px #00cccc; }
        h1 { font-size: 2.5rem; text-align: center; margin-bottom: 2rem; }
        h2 { font-size: 2rem; margin: 2rem 0 1rem 0; }
        h3 { font-size: 1.5rem; margin: 1.5rem 0 0.5rem 0; }
        p { color: #ffffff; margin: 1rem 0; line-height: 1.8; }
        a { color: #00cccc; text-decoration: none; }
        a:hover { text-shadow: 0 0 10px #00cccc; }
        .docs-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1rem; margin: 1.5rem 0; }
        .class-link { display: block; color: #00cccc; text-decoration: none; padding: 0.8rem; border: 1px solid #00cccc; border-radius: 6px; transition: all 0.3s ease; font-weight: bold; background: rgba(0, 204, 204, 0.05); }
        .class-link:hover { background: rgba(0, 204, 204, 0.15); transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0, 204, 204, 0.3); }
        .category-section { background: rgba(0, 0, 0, 0.3); border-radius: 12px; padding: 2rem; margin: 2rem 0; border: 2px solid #00cccc; }
        .footer { text-align: center; margin-top: 3rem; padding-top: 2rem; border-top: 1px solid #00ff00; color: #888; }
        .intro-section { background: rgba(0, 255, 0, 0.1); border-radius: 8px; padding: 1.5rem; margin: 2rem 0; border: 1px solid #00ff00; }
        .revolutionary-banner { background: linear-gradient(45deg, #ff0080, #00ff00, #0080ff); background-size: 400% 400%; animation: gradient 4s ease infinite; padding: 2rem; text-align: center; margin: 2rem 0; position: relative; overflow: hidden; border-radius: 8px; }
        @keyframes gradient { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }
        .revolutionary-banner::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(15, 15, 35, 0.8); }
        .revolutionary-content { position: relative; z-index: 1; }
        .revolutionary-title { font-size: 1.8rem; margin-bottom: 1rem; color: #ffffff; text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8); }
    </style>
</head>
<body>
    <div class="matrix-bg"></div>
    
    <nav class="nav-header">
        <div class="nav-links">
            <a href="index.html" class="nav-link">🏠 Home</a>
            <a href="story.html" class="nav-link">📖 Story</a>
            <a href="GETTING_STARTED.html" class="nav-link getting-started">🚀 Getting Started</a>
            <a href="implementation.html" class="nav-link">⚙️ Implementation</a>
            <a href="literate-docs.html" class="nav-link">📚 Literate Docs</a>
            <a href="events/" class="nav-link">📡 Events</a>
            <a href="flows/" class="nav-link">🌊 Flows</a>
            <a href="ports/" class="nav-link">🚪 Ports</a>
            <a href="javadocs/" class="nav-link">📖 JavaDocs</a>
            <a href="https://github.com/rydnr/bytehot" class="nav-link">🔗 GitHub</a>
        </div>
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

        <div class="footer">
            <p>&copy; 2025 ByteHot Project. Licensed under <a href="https://www.gnu.org/licenses/gpl-3.0.html">GPLv3</a></p>
            <p><a href="https://github.com/rydnr/bytehot">GitHub</a> • <a href="https://github.com/rydnr/bytehot/issues">Issues</a> • <a href="https://github.com/rydnr/bytehot/discussions">Discussions</a></p>
        </div>
    </div>

    <script>
        // Matrix rain effect
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        document.querySelector('.matrix-bg').appendChild(canvas);
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        const matrix = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789@#$%^&*()*&^%+-/~{[|`]}";
        const drops = [];
        for(let x = 0; x < canvas.width / 10; x++) { drops[x] = 1; }
        function draw() {
            ctx.fillStyle = 'rgba(15, 15, 35, 0.04)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = '#00ff00';
            ctx.font = '10px Courier New';
            for(let i = 0; i < drops.length; i++) {
                const text = matrix[Math.floor(Math.random() * matrix.length)];
                ctx.fillText(text, i * 10, drops[i] * 10);
                if(drops[i] * 10 > canvas.height && Math.random() > 0.975) { drops[i] = 0; }
                drops[i]++;
            }
        }
        setInterval(draw, 33);
        window.addEventListener('resize', () => { canvas.width = window.innerWidth; canvas.height = window.innerHeight; });
    </script>
</body>
</html>
EOF

echo "✅ Comprehensive literate programming documentation created!"