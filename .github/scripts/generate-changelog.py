#!/usr/bin/env python3
"""
ByteHot Changelog Generation Script
Generates smart changelogs based on commit patterns and ByteHot conventions
"""

import subprocess
import re
import sys
from datetime import datetime
import os

def get_commits(commit_range):
    """Get commits using git log with proper formatting"""
    if commit_range:
        cmd = f"git log {commit_range} --pretty=format:'%h|%s|%an|%ae|%ad' --date=short --no-merges"
    else:
        cmd = "git log --pretty=format:'%h|%s|%an|%ae|%ad' --date=short --no-merges"
    
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return result.stdout.strip().split('\n') if result.stdout.strip() else []

def categorize_commit(subject):
    """Categorize commits based on ByteHot emoji conventions"""
    categories = {
        '🧪': ('Tests & Validation', ['test', 'spec', 'validation']),
        '✅': ('Features & Implementation', ['implement', 'add', 'feature']),
        '🔥': ('Major Features', ['major', 'milestone', 'revolutionary']),
        '📚': ('Documentation', ['docs', 'documentation', 'readme']),
        '🔒': ('Security & Dependencies', ['security', 'upgrade', 'vulnerability']),
        '🏗️': ('Infrastructure', ['infrastructure', 'build', 'ci']),
        '🐛': ('Bug Fixes', ['fix', 'bug', 'issue']),
        '🚀': ('Performance & Optimization', ['performance', 'optimize', 'improve']),
        '📝': ('Content Updates', ['update', 'modify', 'change']),
    }
    
    for emoji, (category, keywords) in categories.items():
        if emoji in subject or any(keyword in subject.lower() for keyword in keywords):
            return emoji, category
    
    return '📋', 'Other Changes'

def extract_issue_refs(subject):
    """Extract issue references from commit subjects"""
    patterns = [
        r'\[#(\d+)\]',
        r'#(\d+)',
        r'\[([^\]]+)\]'
    ]
    
    refs = []
    for pattern in patterns:
        matches = re.findall(pattern, subject)
        refs.extend(matches)
    
    return refs

def generate_changelog(tag, commit_range, range_desc):
    """Generate comprehensive changelog"""
    # Get environment variables
    github_repo = os.environ.get('GITHUB_REPOSITORY', 'rydnr/bytehot')
    github_sha = os.environ.get('GITHUB_SHA', 'unknown')
    commit_short = github_sha[:8]
    build_date = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S UTC')
    
    # Determine release type
    if re.match(r'^[0-9]+\.[0-9]+\.[0-9]+$', tag):
        release_type = 'stable'
    elif 'milestone' in tag:
        release_type = 'milestone'
    elif tag.startswith('v'):
        release_type = 'stable'
    else:
        release_type = 'development'
    
    commits = get_commits(commit_range)
    
    # Categorize commits
    categories = {}
    commit_details = []
    
    for commit_line in commits:
        if not commit_line.strip():
            continue
            
        parts = commit_line.split('|')
        if len(parts) < 5:
            continue
            
        hash_short, subject, author, email, date = parts
        
        emoji, category = categorize_commit(subject)
        
        if category not in categories:
            categories[category] = []
        
        # Create commit entry with metadata
        issue_refs = extract_issue_refs(subject)
        commit_entry = {
            'hash': hash_short,
            'subject': subject,
            'author': author,
            'email': email,
            'date': date,
            'emoji': emoji,
            'issue_refs': issue_refs
        }
        
        categories[category].append(commit_entry)
        commit_details.append(commit_entry)
    
    # Generate changelog markdown
    changelog = f"""# ByteHot {tag}

> **Release Type:** {release_type}  
> **Build Date:** {build_date}  
> **Commit:** [{commit_short}](https://github.com/{github_repo}/commit/{github_sha})  
> **Changes:** {len(commit_details)} commits {range_desc}

## 🚀 Quick Start

```bash
# Download the agent JAR from this release
java -javaagent:bytehot-{tag}-agent.jar \\
     -Dbytehot.watch.paths=target/classes \\
     -cp target/classes \\
     com.example.YourApplication
```

## What's Changed

"""
    
    # Add categorized changes
    priority_order = [
        '🔥 Major Features',
        '✅ Features & Implementation', 
        '🧪 Tests & Validation',
        '🐛 Bug Fixes',
        '📚 Documentation',
        '🔒 Security & Dependencies',
        '🚀 Performance & Optimization',
        '🏗️ Infrastructure',
        '📝 Content Updates',
        '📋 Other Changes'
    ]
    
    for category_name in priority_order:
        category_key = category_name.split(' ', 1)[1]  # Remove emoji prefix
        if category_key in categories:
            commits_in_category = categories[category_key]
            changelog += f"### {category_name}\n\n"
            
            for commit in commits_in_category:
                # Format commit with metadata
                commit_link = f"[{commit['hash']}](https://github.com/{github_repo}/commit/{commit['hash']})"
                
                # Add issue references if present
                issue_info = ""
                if commit['issue_refs']:
                    issue_links = []
                    for ref in commit['issue_refs']:
                        if ref.isdigit():
                            issue_links.append(f"[#{ref}](https://github.com/{github_repo}/issues/{ref})")
                        else:
                            issue_links.append(f"[{ref}]")
                    if issue_links:
                        issue_info = f" ({', '.join(issue_links)})"
                
                changelog += f"- {commit['subject']} ({commit_link}){issue_info}\n"
            
            changelog += "\n"
    
    # Add technical details
    changelog += f"""## 🛠️ Technical Details

**System Requirements:**
- Java 17 or later
- Maven 3.6+ (for building from source)

**Architecture:**
- Domain-Driven Design (DDD) + Hexagonal Architecture
- Event-driven with comprehensive event sourcing
- Test-Driven Development methodology
- User session tracking and flow detection

**Build Information:**
- **Java Version:** 17
- **Build Tool:** Maven with shade plugin
- **Test Coverage:** Comprehensive unit and integration tests
- **Artifact Type:** Shaded JAR with all dependencies

## 📦 Release Artifacts

| Artifact | Description | Size | Checksum |
|----------|-------------|------|----------|
| `bytehot-{tag}-agent.jar` | Main ByteHot agent (recommended) | ~14MB | See SHA256SUMS |
| `bytehot-{tag}.jar` | Slim version without dependencies | ~300KB | See SHA256SUMS |
| `bytehot-latest-SNAPSHOT-agent.jar` | Compatibility alias | ~14MB | For CI/CD |

## 📖 Documentation & Resources

- **[Getting Started Guide](https://github.com/{github_repo}/blob/main/GETTING_STARTED.md)** - Complete setup instructions
- **[Architecture Documentation](https://github.com/{github_repo}/tree/main/docs)** - Technical deep-dive  
- **[Feature Specifications](https://github.com/{github_repo}/tree/main/specs)** - Detailed feature specs
- **[Development Journal](https://github.com/{github_repo}/blob/main/journal.org)** - Development process log

## 🧪 Quality Assurance

This release includes comprehensive testing:
- **Unit Tests:** Core functionality validation
- **Integration Tests:** End-to-end scenario verification  
- **Configuration Tests:** All configuration loading approaches verified
- **TDD Methodology:** Test-driven development throughout
"""
    
    # Add milestone-specific info if applicable
    if release_type == "milestone":
        changelog += f"""

## 🎯 Milestone Information

This is a milestone release of ByteHot development.
        
**Migration Guide:**
1. **Update JAR file:** Replace your existing ByteHot agent JAR
2. **Review configuration:** Check updated GETTING_STARTED.md for any configuration changes
3. **Test thoroughly:** Milestone releases may include experimental features
4. **Update documentation:** Review any new specifications or documentation
"""
    
    changelog += f"""

---

🤖 **Automated Release**  
📋 **Workflow:** [GitHub Actions](https://github.com/{github_repo}/actions)  
🔗 **Repository:** [ByteHot](https://github.com/{github_repo})

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
"""
    
    return changelog

def main():
    if len(sys.argv) < 4:
        print("Usage: generate-changelog.py <tag> <commit_range> <range_description>")
        sys.exit(1)
    
    tag = sys.argv[1]
    commit_range = sys.argv[2]
    range_desc = sys.argv[3]
    
    changelog = generate_changelog(tag, commit_range, range_desc)
    print(changelog)

if __name__ == "__main__":
    main()