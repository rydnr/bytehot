#!/usr/bin/env python3
"""
Mass javadoc fixer for ByteHot project.
Fixes the most common javadoc issues automatically.
"""

import re
import os
import subprocess
from pathlib import Path

class JavadocMassFixer:
    def __init__(self):
        self.fixes_applied = 0
        
    def get_all_issues(self):
        """Get all current javadoc issues."""
        try:
            result = subprocess.run([
                'bash', '.github/scripts/validate-all-javadoc.sh'
            ], capture_output=True, text=True, cwd='.')
            
            issues = []
            for line in result.stderr.split('\n'):
                if 'warning:' in line or 'error:' in line:
                    if ':' in line:
                        parts = line.split(':')
                        if len(parts) >= 3:
                            file_path = parts[0].strip()
                            line_num = parts[1].strip()
                            message = ':'.join(parts[2:]).strip()
                            if file_path and line_num.isdigit():
                                issues.append((file_path, int(line_num), message))
            
            return list(set(issues))  # Remove duplicates
        except Exception as e:
            print(f"Error getting issues: {e}")
            return []
    
    def fix_generic_params(self, issues):
        """Fix missing @param for generic types."""
        print("🔧 Fixing generic type parameters...")
        
        for file_path, line_num, message in issues:
            if 'no @param for <' in message:
                match = re.search(r'no @param for <([^>]+)>', message)
                if match and os.path.exists(file_path):
                    param_name = match.group(1)
                    self.add_generic_param(file_path, line_num, param_name)
    
    def add_generic_param(self, file_path, line_num, param_name):
        """Add a generic parameter to a class/interface."""
        try:
            with open(file_path, 'r') as f:
                lines = f.readlines()
            
            # Find the javadoc block above the class/interface
            javadoc_end = None
            for i in range(line_num - 2, max(0, line_num - 20), -1):
                if i < len(lines) and '*/' in lines[i]:
                    javadoc_end = i
                    break
            
            if javadoc_end is not None:
                # Generate description for common generic types
                descriptions = {
                    'T': 'the type parameter',
                    'E': 'the element type parameter',
                    'C': 'the command type parameter',
                    'CH': 'the command handler type parameter',
                    'V': 'the value type parameter',
                    'F': 'the field type parameter',
                    'VO': 'the value object type parameter',
                    'S': 'the state type parameter',
                    'S1': 'the source state type parameter',
                    'S2': 'the target state type parameter',
                    'P': 'the port type parameter',
                    'O': 'the observer type parameter'
                }
                
                description = descriptions.get(param_name, 'the type parameter')
                param_line = f"     * @param <{param_name}> {description}\n"
                
                # Insert before the closing */
                lines.insert(javadoc_end, param_line)
                
                with open(file_path, 'w') as f:
                    f.writelines(lines)
                
                print(f"  ✅ Fixed <{param_name}> in {file_path}")
                self.fixes_applied += 1
                return True
        except Exception as e:
            print(f"  ❌ Error fixing {file_path}: {e}")
        
        return False
    
    def fix_missing_returns(self, issues):
        """Fix missing @return annotations."""
        print("🔧 Fixing missing @return annotations...")
        
        for file_path, line_num, message in issues:
            if 'no @return' in message and os.path.exists(file_path):
                self.add_return_annotation(file_path, line_num)
    
    def add_return_annotation(self, file_path, line_num):
        """Add @return annotation to a method."""
        try:
            with open(file_path, 'r') as f:
                lines = f.readlines()
            
            if line_num <= len(lines):
                method_line = lines[line_num - 1]
                
                # Basic return type detection
                return_desc = "the result of the operation"
                if 'boolean' in method_line:
                    return_desc = "true if successful, false otherwise"
                elif 'String' in method_line:
                    return_desc = "the string representation"
                elif 'List' in method_line:
                    return_desc = "the list of items"
                elif 'Optional' in method_line:
                    return_desc = "an optional containing the result"
                
                # Find javadoc end
                javadoc_end = None
                for i in range(line_num - 2, max(0, line_num - 15), -1):
                    if i < len(lines) and '*/' in lines[i]:
                        javadoc_end = i
                        break
                
                if javadoc_end is not None:
                    return_line = f"     * @return {return_desc}\n"
                    lines.insert(javadoc_end, return_line)
                    
                    with open(file_path, 'w') as f:
                        f.writelines(lines)
                    
                    print(f"  ✅ Added @return to {file_path}:{line_num}")
                    self.fixes_applied += 1
                    return True
        except Exception as e:
            print(f"  ❌ Error fixing return in {file_path}: {e}")
        
        return False
    
    def fix_missing_method_comments(self, issues):
        """Add basic comments to methods without javadoc."""
        print("🔧 Adding missing method comments...")
        
        for file_path, line_num, message in issues:
            if 'no comment' in message and 'package' not in message:
                if os.path.exists(file_path):
                    self.add_method_comment(file_path, line_num)
    
    def add_method_comment(self, file_path, line_num):
        """Add basic javadoc comment to a method."""
        try:
            with open(file_path, 'r') as f:
                lines = f.readlines()
            
            if line_num <= len(lines):
                method_line = lines[line_num - 1].strip()
                
                # Extract method name for basic comment
                method_match = re.search(r'(\w+)\s*\(', method_line)
                if method_match:
                    method_name = method_match.group(1)
                    
                    # Generate basic comment
                    if method_name.startswith('get'):
                        comment = f"Gets the {method_name[3:].lower()}."
                    elif method_name.startswith('set'):
                        comment = f"Sets the {method_name[3:].lower()}."
                    elif method_name.startswith('is') or method_name.startswith('has'):
                        comment = f"Checks if {method_name.lower()}."
                    else:
                        comment = f"Performs {method_name} operation."
                    
                    # Add javadoc
                    indent = len(method_line) - len(method_line.lstrip())
                    javadoc_lines = [
                        ' ' * indent + "/**\n",
                        ' ' * indent + f" * {comment}\n",
                        ' ' * indent + " */\n"
                    ]
                    
                    # Insert before the method
                    for i, javadoc_line in enumerate(javadoc_lines):
                        lines.insert(line_num - 1 + i, javadoc_line)
                    
                    with open(file_path, 'w') as f:
                        f.writelines(lines)
                    
                    print(f"  ✅ Added comment to {method_name} in {file_path}")
                    self.fixes_applied += 1
                    return True
        except Exception as e:
            print(f"  ❌ Error adding comment to {file_path}: {e}")
        
        return False
    
    def run(self):
        """Run the mass fixer."""
        print("🚀 ByteHot Mass Javadoc Fixer")
        print("============================")
        
        issues = self.get_all_issues()
        print(f"📋 Found {len(issues)} issues to fix")
        
        if not issues:
            print("✅ No issues found!")
            return
        
        # Fix in order of safety
        self.fix_generic_params(issues)
        self.fix_missing_returns(issues)
        self.fix_missing_method_comments(issues)
        
        print(f"\n✅ Applied {self.fixes_applied} fixes")
        
        # Check remaining
        remaining_issues = self.get_all_issues()
        print(f"📊 Remaining issues: {len(remaining_issues)}")

if __name__ == "__main__":
    fixer = JavadocMassFixer()
    fixer.run()