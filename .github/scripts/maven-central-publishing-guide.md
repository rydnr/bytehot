# Maven Central Publishing Guide for ByteHot

This guide walks you through publishing all ByteHot modules to Maven Central using Sonatype OSSRH.

## Prerequisites ✅

All modules are already configured with:
- ✅ **Release profiles** with GPG signing and Nexus staging
- ✅ **Proper metadata** (name, description, URL, licenses, developers)
- ✅ **Source and Javadoc generation**
- ✅ **Release versions** (1.0.0, no SNAPSHOTs)

## Required Setup

### 1. Sonatype OSSRH Account

1. **Create Jira account** at https://issues.sonatype.org/
2. **Create a ticket** to claim your group ID:
   ```
   Project: Community Support - Open Source Project Repository Hosting (OSSRH)
   Issue Type: New Project
   Summary: Request for org.acmsl.* group ID
   Group Id: org.acmsl
   Project URL: https://github.com/rydnr/bytehot
   SCM URL: https://github.com/rydnr/bytehot.git
   ```

### 2. GPG Key Setup

```bash
# Generate GPG key
gpg --gen-key
# Use: Real name: José San Leandro, Email: rydnr@acm-sl.org

# List keys and get key ID
gpg --list-secret-keys --keyid-format LONG

# Export public key to key server
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID
```

### 3. Maven Settings Configuration

Create/update `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR_JIRA_USERNAME</username>
      <password>YOUR_JIRA_PASSWORD</password>
    </server>
  </servers>
  
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

## Publishing Order 📦

**IMPORTANT**: Publish in dependency order to avoid resolution failures.

### Phase 1: Foundation (Independent)
```bash
# 1. Java Commons (no dependencies)
cd migration/java-commons
mvn clean deploy -P release

# 2. Java Commons Infrastructure (depends on java-commons)
cd ../java-commons-infrastructure  
mvn clean deploy -P release
```

### Phase 2: JavaEDA Framework
```bash
# 3. JavaEDA Domain (depends on java-commons)
cd ../javaeda-domain
mvn clean deploy -P release

# 4. JavaEDA Infrastructure (depends on domain + java-commons-infrastructure)
cd ../javaeda-infrastructure
mvn clean deploy -P release

# 5. JavaEDA Application (depends on domain + infrastructure)
cd ../javaeda-application
mvn clean deploy -P release
```

### Phase 3: ByteHot Core
```bash
# 6. ByteHot Domain (depends on javaeda-domain)
cd ../domain
mvn clean deploy -P release

# 7. ByteHot Infrastructure (depends on domain + javaeda-infrastructure)
cd ../infrastructure
mvn clean deploy -P release

# 8. ByteHot Application (depends on domain + infrastructure)
cd ../application
mvn clean deploy -P release
```

### Phase 4: ByteHot Plugins
```bash
# 9. Plugin Commons (depends on java-commons)
cd ../plugin-commons
mvn clean deploy -P release

# 10-15. All other plugins (depend on plugin-commons)
cd ../maven-plugin && mvn clean deploy -P release
cd ../gradle-plugin && mvn clean deploy -P release
cd ../eclipse-plugin && mvn clean deploy -P release
cd ../intellij-plugin && mvn clean deploy -P release
cd ../spring-plugin && mvn clean deploy -P release
cd ../vscode-plugin && mvn clean deploy -P release
```

## Automated Publishing Script

```bash
#!/usr/bin/env bash
# publish-to-maven-central.sh

set -e

MODULES=(
  "java-commons"
  "java-commons-infrastructure"
  "javaeda-domain"
  "javaeda-infrastructure"
  "javaeda-application"
  "domain"
  "infrastructure"
  "application"
  "plugin-commons"
  "maven-plugin"
  "gradle-plugin"
  "eclipse-plugin"
  "intellij-plugin"
  "spring-plugin"
  "vscode-plugin"
)

for module in "${MODULES[@]}"; do
  echo "Publishing $module to Maven Central..."
  cd "migration/$module"
  mvn clean deploy -P release
  cd ../..
  echo "✅ $module published successfully"
done

echo "🎉 All modules published to Maven Central!"
```

## Verification 

After publishing, verify at:
- **Staging**: https://s01.oss.sonatype.org/#stagingRepositories
- **Central**: https://search.maven.org/search?q=g:org.acmsl.bytehot
- **Central**: https://search.maven.org/search?q=g:org.acmsl.javaeda
- **Central**: https://search.maven.org/search?q=g:org.acmsl.commons

## Usage After Publishing

Once published, users can add dependencies:

```xml
<dependency>
  <groupId>org.acmsl.bytehot</groupId>
  <artifactId>bytehot-application</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>org.acmsl.javaeda</groupId>
  <artifactId>javaeda-domain</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>org.acmsl.commons</groupId>
  <artifactId>java-commons</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Troubleshooting

### Common Issues

1. **GPG Signing Failures**
   ```bash
   # Ensure GPG agent is running
   gpg-agent --daemon
   
   # Test signing
   echo "test" | gpg --clearsign
   ```

2. **Missing Metadata**
   - All required metadata is already configured in the POMs
   - Verify with: `mvn help:effective-pom -P release`

3. **Dependency Resolution**
   - Publish in the exact order specified above
   - Wait 10-15 minutes between phases for propagation

4. **Authentication Issues**
   - Verify Jira credentials in `~/.m2/settings.xml`
   - Ensure server ID matches `<serverId>ossrh</serverId>` in POMs

### Release Profile Test

Test the release profile locally:
```bash
mvn clean package -P release -DskipTests
```

This will:
- Generate sources JAR
- Generate Javadoc JAR  
- Sign all artifacts with GPG
- Verify everything is ready for upload

## Next Steps

After successful publication:
1. **Update documentation** with Maven Central coordinates
2. **Create GitHub releases** with artifacts
3. **Update README** files with installation instructions
4. **Announce** the release to the community

## Support

- **Sonatype Guide**: https://central.sonatype.org/publish/publish-guide/
- **OSSRH Jira**: https://issues.sonatype.org/browse/OSSRH
- **Maven Central Search**: https://search.maven.org/