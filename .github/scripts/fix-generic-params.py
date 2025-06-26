#!/usr/bin/env python3
"""
Automated fixer for missing @param annotations for generic types.
This is the safest type of javadoc fix to automate.
"""

import re
import subprocess
import os

def get_generic_param_issues():
    """Get all issues related to missing generic type parameters."""
    try:
        result = subprocess.run([
            'bash', '.github/scripts/validate-all-javadoc.sh'
        ], capture_output=True, text=True, cwd='.')
        
        issues = []
        for line in result.stderr.split('\n'):
            if 'no @param for <' in line and ':' in line:
                parts = line.split(':')
                if len(parts) >= 3:
                    file_path = parts[0].strip()
                    line_num = int(parts[1].strip())
                    message = ':'.join(parts[2:]).strip()
                    
                    # Extract the generic parameter name
                    match = re.search(r'no @param for <([^>]+)>', message)
                    if match:
                        param_name = match.group(1)
                        issues.append((file_path, line_num, param_name))
        
        return issues
    except Exception as e:
        print(f"Error getting issues: {e}")
        return []

def fix_generic_param(file_path, line_num, param_name):
    """Fix a single missing generic parameter."""
    try:
        with open(file_path, 'r') as f:
            lines = f.readlines()
        
        # Find the javadoc block above the method
        javadoc_end = None
        for i in range(line_num - 2, max(0, line_num - 20), -1):
            if i < len(lines) and '*/' in lines[i]:
                javadoc_end = i
                break
        
        if javadoc_end is not None:
            # Generate appropriate description for common generic types
            descriptions = {
                'T': 'the type parameter',
                'E': 'the element type parameter', 
                'K': 'the key type parameter',
                'V': 'the value type parameter',
                'C': 'the type parameter',
                'O': 'the type parameter',
                'R': 'the return type parameter'
            }
            
            description = descriptions.get(param_name, 'the type parameter')
            param_line = f"     * @param <{param_name}> {description}\n"
            
            # Insert the @param line before the closing */
            lines.insert(javadoc_end, param_line)
            
            # Write back to file
            with open(file_path, 'w') as f:
                f.writelines(lines)
            
            print(f"✅ Fixed <{param_name}> in {file_path}:{line_num}")
            return True
        else:
            print(f"❌ Could not find javadoc block for {file_path}:{line_num}")
            return False
            
    except Exception as e:
        print(f"❌ Error fixing {file_path}:{line_num} - {e}")
        return False

def main():
    print("🔧 Fixing missing @param annotations for generic types...")
    
    issues = get_generic_param_issues()
    print(f"📋 Found {len(issues)} generic parameter issues to fix")
    
    fixed_count = 0
    for file_path, line_num, param_name in issues:
        if os.path.exists(file_path):
            if fix_generic_param(file_path, line_num, param_name):
                fixed_count += 1
    
    print(f"\n✅ Fixed {fixed_count}/{len(issues)} generic parameter issues")
    
    if fixed_count > 0:
        print("\n📊 Checking remaining issues...")
        subprocess.run(['bash', '.github/scripts/validate-all-javadoc.sh'], 
                      capture_output=True)

if __name__ == "__main__":
    main()