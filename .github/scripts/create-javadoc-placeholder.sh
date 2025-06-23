#!/bin/bash
set -euo pipefail

# Create Javadoc placeholder when generation fails

echo "📄 Creating Javadoc placeholder..."

mkdir -p target/site/apidocs

cat > target/site/apidocs/index.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>ByteHot API Documentation</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h1 { color: #2c3e50; border-bottom: 2px solid #3498db; }
        .notice { background: #f39c12; color: white; padding: 15px; border-radius: 5px; margin: 20px 0; }
        .module { background: #ecf0f1; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; }
    </style>
</head>
<body>
    <h1>ByteHot API Documentation</h1>
    <p><em>Revolutionary JVM Hot-Swapping Agent</em></p>
    
    <div class="notice">
        <strong>Notice:</strong> Javadoc generation is currently being improved. 
        Please check back soon or refer to the source code documentation.
    </div>
    
    <h2>Core Architecture</h2>
    <div class="module">
        <h3>Domain Layer: org.acmsl.bytehot.domain</h3>
        <p>Core business logic, aggregates, entities, and domain services</p>
    </div>
    
    <div class="module">
        <h3>Application Layer: org.acmsl.bytehot.application</h3>
        <p>Application coordination and port/adapter integration</p>
    </div>
    
    <div class="module">
        <h3>Infrastructure Layer: org.acmsl.bytehot.infrastructure</h3>
        <p>Technology-specific implementations and adapters</p>
    </div>
    
    <h2>Alternative Documentation</h2>
    <ul>
        <li><a href="../docs/">Comprehensive Documentation</a> - Detailed architectural documentation</li>
        <li><a href="../implementation.html">Implementation Guide</a> - Complete implementation reference</li>
        <li><a href="https://github.com/rydnr/bytehot">Source Code</a> - Browse the source code directly</li>
    </ul>
    
    <hr>
    <p><small>ByteHot - Revolutionary JVM Hot-Swapping Agent</small></p>
</body>
</html>
EOF

echo "✅ Javadoc placeholder created"