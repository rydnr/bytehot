/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/**
 * ByteHot Configuration Infrastructure - Configuration management and settings.
 * 
 * <p>This package provides configuration management infrastructure for ByteHot,
 * implementing flexible configuration loading, validation, and runtime updates.
 * It serves as a secondary adapter for configuration-related domain operations.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Configuration Loading</h3>
 * <ul>
 *   <li>{@code ConfigurationAdapter} - Main configuration management adapter</li>
 *   <li>{@code PropertiesConfigLoader} - Java properties file support</li>
 *   <li>{@code YamlConfigLoader} - YAML configuration file support</li>
 *   <li>{@code EnvironmentConfigLoader} - Environment variable support</li>
 * </ul>
 * 
 * <h3>Configuration Sources</h3>
 * <ul>
 *   <li>{@code FileConfigSource} - Configuration from files</li>
 *   <li>{@code SystemConfigSource} - System properties and environment</li>
 *   <li>{@code AgentConfigSource} - JVM agent arguments</li>
 *   <li>{@code DefaultConfigSource} - Built-in defaults</li>
 * </ul>
 * 
 * <h3>Configuration Management</h3>
 * <ul>
 *   <li>{@code ConfigValidator} - Configuration validation and constraints</li>
 *   <li>{@code ConfigMerger} - Multiple source configuration merging</li>
 *   <li>{@code ConfigWatcher} - Runtime configuration change detection</li>
 * </ul>
 * 
 * <h2>Configuration Hierarchy</h2>
 * 
 * <p>Configuration sources are prioritized in the following order (highest to lowest):</p>
 * <ol>
 *   <li><strong>JVM Agent Arguments</strong> - Highest priority</li>
 *   <li><strong>System Properties</strong> - JVM -D properties</li>
 *   <li><strong>Environment Variables</strong> - OS environment</li>
 *   <li><strong>Configuration Files</strong> - bytehot.properties, bytehot.yml</li>
 *   <li><strong>Built-in Defaults</strong> - Lowest priority</li>
 * </ol>
 * 
 * <h3>Configuration Loading</h3>
 * <pre>{@code
 * // Load configuration from multiple sources
 * ConfigurationAdapter configAdapter = new ConfigurationAdapter();
 * 
 * Configuration config = configAdapter.loadConfiguration()
 *     .fromFile("bytehot.properties")
 *     .fromEnvironment()
 *     .fromSystemProperties()
 *     .withDefaults()
 *     .build();
 * 
 * // Access configuration values
 * List<Path> watchPaths = config.getWatchPaths();
 * boolean strictValidation = config.isStrictValidation();
 * Duration debounceDelay = config.getDebounceDelay();
 * }</pre>
 * 
 * <h2>Configuration Categories</h2>
 * 
 * <h3>File Watching Configuration</h3>
 * <pre>{@code
 * # Watch path configuration
 * bytehot.watch.paths=/src/main/java,/src/test/java
 * bytehot.watch.recursive=true
 * bytehot.watch.debounce.ms=500
 * 
 * # File filtering
 * bytehot.watch.file.patterns=*.java,*.class
 * bytehot.watch.exclude.patterns=**/target/**,**/.git/**
 * }</pre>
 * 
 * <h3>Validation Configuration</h3>
 * <pre>{@code
 * # Bytecode validation settings
 * bytehot.validation.strict=false
 * bytehot.validation.allow.schema.changes=false
 * bytehot.validation.max.method.size=65535
 * 
 * # Class redefinition limits
 * bytehot.redefinition.max.instances=1000
 * bytehot.redefinition.timeout.ms=5000
 * }</pre>
 * 
 * <h3>Event Sourcing Configuration</h3>
 * <pre>{@code
 * # Event storage settings
 * bytehot.events.store.enabled=true
 * bytehot.events.store.path=/var/bytehot/events
 * bytehot.events.store.format=json
 * 
 * # Event emission settings
 * bytehot.events.emit.enabled=true
 * bytehot.events.batch.size=100
 * bytehot.events.flush.interval.ms=1000
 * }</pre>
 * 
 * <h3>Documentation Configuration</h3>
 * <pre>{@code
 * # Documentation generation
 * bytehot.docs.enabled=true
 * bytehot.docs.output.formats=html,markdown
 * bytehot.docs.output.directory=/docs
 * 
 * # Flow analysis settings
 * bytehot.docs.flow.analysis=true
 * bytehot.docs.flow.depth.limit=5
 * bytehot.docs.cross.references=true
 * }</pre>
 * 
 * <h2>Configuration Validation</h2>
 * 
 * <h3>Validation Rules</h3>
 * <p>Configuration values are validated for correctness and consistency:</p>
 * <ul>
 *   <li><strong>Type Validation</strong> - Ensure correct data types</li>
 *   <li><strong>Range Validation</strong> - Numeric values within acceptable ranges</li>
 *   <li><strong>Path Validation</strong> - File and directory paths exist and are accessible</li>
 *   <li><strong>Format Validation</strong> - String patterns and formats</li>
 * </ul>
 * 
 * <h3>Validation Example</h3>
 * <pre>{@code
 * // Configure validation rules
 * ConfigValidator validator = new ConfigValidator()
 *     .addRule("bytehot.watch.debounce.ms", 
 *              value -> value >= 0 && value <= 10000,
 *              "Debounce delay must be between 0 and 10000ms")
 *     .addRule("bytehot.watch.paths",
 *              paths -> paths.stream().allMatch(Files::isDirectory),
 *              "All watch paths must be existing directories");
 * 
 * // Validate configuration
 * ValidationResult result = validator.validate(config);
 * if (!result.isValid()) {
 *     throw new ConfigurationException(result.getErrors());
 * }
 * }</pre>
 * 
 * <h2>Runtime Configuration Updates</h2>
 * 
 * <h3>Dynamic Reconfiguration</h3>
 * <p>Support for runtime configuration changes without restart:</p>
 * <pre>{@code
 * // Watch for configuration file changes
 * ConfigWatcher watcher = new ConfigWatcher();
 * watcher.watchFile(Paths.get("bytehot.properties"))
 *        .onConfigChange(newConfig -> {
 *            // Validate new configuration
 *            ValidationResult validation = validator.validate(newConfig);
 *            if (validation.isValid()) {
 *                // Apply new configuration
 *                configAdapter.updateConfiguration(newConfig);
 *                
 *                // Emit configuration change event
 *                ConfigurationUpdated event = new ConfigurationUpdated(
 *                    newConfig, validation, Instant.now()
 *                );
 *                eventEmitter.emit(List.of(event));
 *            }
 *        });
 * }</pre>
 * 
 * <h2>Configuration Security</h2>
 * 
 * <h3>Sensitive Information</h3>
 * <p>Secure handling of sensitive configuration data:</p>
 * <ul>
 *   <li><strong>Password Encryption</strong> - Encrypt sensitive values</li>
 *   <li><strong>Environment Variables</strong> - Use env vars for secrets</li>
 *   <li><strong>File Permissions</strong> - Restrict config file access</li>
 *   <li><strong>Audit Logging</strong> - Log configuration access</li>
 * </ul>
 * 
 * <h2>Default Configuration</h2>
 * 
 * <p>ByteHot provides sensible defaults for all configuration options:</p>
 * <pre>{@code
 * // Built-in default configuration
 * public static Configuration getDefaults() {
 *     return Configuration.builder()
 *         .watchPaths(List.of("src/main/java"))
 *         .recursive(true)
 *         .debounceMs(500)
 *         .filePatterns(List.of("*.java"))
 *         .validationStrict(false)
 *         .eventsEnabled(true)
 *         .docsEnabled(false)
 *         .build();
 * }
 * }</pre>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.config;