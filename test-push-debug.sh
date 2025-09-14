#!/usr/bin/env bash

set -e

echo "=== Debug Push Script ==="

MIGRATION_DIR="/home/chous/github/rydnr/bytehot/migration"
cd "$MIGRATION_DIR"

available_dirs=($(find . -maxdepth 1 -type d -name "[!.]*" | sed 's|./||' | sort))

echo "Available directories:"
for dir in "${available_dirs[@]}"; do
    echo "  $dir"
done

echo
echo "Processing modules:"

for dir in "${available_dirs[@]}"; do
    # Skip temp directories
    if [[ "$dir" == *"temp"* ]]; then
        echo "SKIP: $dir (temp directory)"
        continue
    fi
    
    github_repo=""
    
    case "$dir" in
        "application")
            github_repo="bytehot/application"
            ;;
        "domain")
            github_repo="bytehot/domain"
            ;;
        "infrastructure")
            github_repo="bytehot/infrastructure"
            ;;
        "plugin-commons")
            github_repo="bytehot/plugin-commons"
            ;;
        "maven-plugin")
            github_repo="bytehot/maven-plugin"
            ;;
        "spring-plugin")
            github_repo="bytehot/spring-plugin"
            ;;
        "java-commons")
            github_repo="rydnr/java-commons"
            ;;
        *)
            echo "SKIP: $dir (no mapping)"
            continue
            ;;
    esac
    
    echo "PROCESS: $dir → $github_repo"
done

echo "Debug script completed"