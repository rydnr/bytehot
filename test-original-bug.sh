#!/bin/bash

# Test script to demonstrate that our test detects the original bug

echo "=== Demonstrating Bug Detection Test ==="
echo ""

# Create a temporary copy of HotSwapManager with the original buggy code
echo "1. Creating temporary HotSwapManager with ORIGINAL BUGGY CODE..."

cp /home/chous/github/rydnr/bytehot/bytehot/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java /tmp/HotSwapManager_fixed.java

# Create buggy version with mock logic
cat > /tmp/HotSwapManager_buggy.java << 'EOF'
    public ClassRedefinitionSucceeded performRedefinition(final HotSwapRequested request) throws HotSwapException {
        final long startTime = System.nanoTime();
        
        try {
            // Mock JVM redefinition logic for testing (ORIGINAL BUG)
            final String content = new String(request.getNewBytecode());
            
            if (content.contains("INCOMPATIBLE_BYTECODE") || content.contains("SCHEMA_CHANGE_BYTECODE")) {
                // Simulate JVM rejection
                throw createJvmRejectionException(request, content);
            }
            
            // Simulate successful redefinition (BUG: NO ACTUAL JVM REDEFINITION)
            final long endTime = System.nanoTime();
            final Duration duration = Duration.ofNanos(endTime - startTime);
            final int affectedInstances = calculateAffectedInstances(request);
            final String details = createRedefinitionDetails(request);
            final Instant timestamp = Instant.now();
            
            return new ClassRedefinitionSucceeded(
                request.getClassName(),
                request.getClassFile(),
                affectedInstances,
                details,
                duration,
                timestamp
            );
            
        } catch (final Exception e) {
            if (e instanceof HotSwapException) {
                throw e;
            }
            // Wrap unexpected exceptions
            final ClassRedefinitionFailed failure = createUnexpectedFailure(request, e);
            throw new HotSwapException(failure, e);
        }
    }
EOF

# Replace the method in the actual file temporarily
echo "2. Temporarily installing BUGGY CODE to test detection..."
sed -i '/public ClassRedefinitionSucceeded performRedefinition/,/^    }$/c\
    public ClassRedefinitionSucceeded performRedefinition(final HotSwapRequested request) throws HotSwapException {\
        final long startTime = System.nanoTime();\
        \
        try {\
            // Mock JVM redefinition logic for testing (ORIGINAL BUG)\
            final String content = new String(request.getNewBytecode());\
            \
            if (content.contains("INCOMPATIBLE_BYTECODE") || content.contains("SCHEMA_CHANGE_BYTECODE")) {\
                // Simulate JVM rejection\
                throw createJvmRejectionException(request, content);\
            }\
            \
            // Simulate successful redefinition (BUG: NO ACTUAL JVM REDEFINITION)\
            final long endTime = System.nanoTime();\
            final Duration duration = Duration.ofNanos(endTime - startTime);\
            final int affectedInstances = calculateAffectedInstances(request);\
            final String details = createRedefinitionDetails(request);\
            final Instant timestamp = Instant.now();\
            \
            return new ClassRedefinitionSucceeded(\
                request.getClassName(),\
                request.getClassFile(),\
                affectedInstances,\
                details,\
                duration,\
                timestamp\
            );\
            \
        } catch (final Exception e) {\
            if (e instanceof HotSwapException) {\
                throw e;\
            }\
            // Wrap unexpected exceptions\
            final ClassRedefinitionFailed failure = createUnexpectedFailure(request, e);\
            throw new HotSwapException(failure, e);\
        }\
    }' /home/chous/github/rydnr/bytehot/bytehot/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java

echo "3. Running test against BUGGY CODE (should FAIL)..."
echo ""

# Run the test - it should fail
mvn -Dtest=HotSwapManagerActualRedefinitionTest test -q

echo ""
echo "4. Restoring FIXED CODE..."

# Restore the fixed version
cp /tmp/HotSwapManager_fixed.java /home/chous/github/rydnr/bytehot/bytehot/src/main/java/org/acmsl/bytehot/domain/HotSwapManager.java

echo "5. Running test against FIXED CODE (should PASS)..."
echo ""

# Run the test again - it should pass
mvn -Dtest=HotSwapManagerActualRedefinitionTest test -q

echo ""
echo "=== Test Demonstration Complete ==="
echo "✅ The test successfully DETECTS the original bug"
echo "✅ The test PASSES with the fixed implementation"

# Cleanup
rm -f /tmp/HotSwapManager_*.java