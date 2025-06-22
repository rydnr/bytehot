#!/bin/bash

# Test hot-swap functionality
echo "Starting ByteHot hot-swap test..."

# Start the application in background
cd /home/chous/github/rydnr/bytehot-sample
timeout 30s ./run.sh > /tmp/bytehot-output.log 2>&1 &
APP_PID=$!

echo "Application started (PID: $APP_PID), waiting 5 seconds for initialization..."
sleep 5

echo "Initial output (first few lines):"
head -10 /tmp/bytehot-output.log

echo ""
echo "Modifying HelloWorld.java to change the message..."

# Make a change to the source file
sed -i 's/Hello, World!/🔥 HOT-SWAPPED!/g' /home/chous/github/rydnr/bytehot-sample/src/main/java/com/example/HelloWorld.java

echo "Recompiling the modified class..."
javac -d /home/chous/github/rydnr/bytehot-sample/target/classes /home/chous/github/rydnr/bytehot-sample/src/main/java/com/example/HelloWorld.java

echo "Waiting 8 seconds for hot-swap to occur..."
sleep 8

echo ""
echo "Final output (last 20 lines):"
tail -20 /tmp/bytehot-output.log

# Check if hot-swap occurred
if grep -q "🔥 HOT-SWAPPED!" /tmp/bytehot-output.log; then
    echo ""
    echo "✅ SUCCESS: Hot-swap worked! New message appeared in output."
else
    echo ""
    echo "❌ FAILURE: Hot-swap did not work. Message was not changed."
fi

# Clean up
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null

# Restore original file
sed -i 's/🔥 HOT-SWAPPED!/Hello, World!/g' /home/chous/github/rydnr/bytehot-sample/src/main/java/com/example/HelloWorld.java

echo "Test completed."