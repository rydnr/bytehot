/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: StringProcessor.java
 *
 * Author: Claude Code
 *
 * Class name: StringProcessor
 *
 * Responsibilities:
 *   - Process and transform strings for ByteHot domain operations
 *   - Maintain processing history and patterns for optimization
 *   - Provide domain-specific string analysis capabilities
 *
 * Collaborators:
 *   - UserProfile: For name processing and display formatting
 *   - FlowId: For identifier normalization
 *   - Event names: For event type processing
 */
package org.acmsl.bytehot.domain;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.Instant;

/**
 * Domain-specific string processor that encapsulates string processing logic
 * with state tracking and performance optimization for ByteHot operations.
 * @author Claude Code
 * @since 2025-06-25
 */
public class StringProcessor {
    
    /**
     * Cache of compiled regex patterns for performance
     */
    protected final Map<String, Pattern> patternCache;
    
    /**
     * Processing statistics for optimization
     */
    protected final Map<String, AtomicLong> operationCounts;
    
    /**
     * Cache of processed string transformations
     */
    protected final Map<String, String> transformationCache;
    
    /**
     * Set of common domain terms for intelligent processing
     */
    protected final Set<String> domainTerms;
    
    /**
     * Maximum cache size to prevent memory issues
     */
    protected final int maxCacheSize;
    
    /**
     * Creation timestamp for age tracking
     */
    protected final Instant createdAt;
    
    /**
     * Processing preferences
     */
    protected boolean cacheEnabled;
    protected boolean statisticsEnabled;
    protected boolean caseInsensitive;

    /**
     * Creates a new StringProcessor with default configuration.
     */
    public StringProcessor() {
        this(1000, true, true, false);
    }

    /**
     * Creates a new StringProcessor with custom configuration.
     * @param maxCacheSize Maximum number of cached items
     * @param cacheEnabled Whether to enable result caching
     * @param statisticsEnabled Whether to track operation statistics
     * @param caseInsensitive Whether operations should be case insensitive
     */
    public StringProcessor(final int maxCacheSize, final boolean cacheEnabled, 
                          final boolean statisticsEnabled, final boolean caseInsensitive) {
        this.maxCacheSize = maxCacheSize;
        this.cacheEnabled = cacheEnabled;
        this.statisticsEnabled = statisticsEnabled;
        this.caseInsensitive = caseInsensitive;
        this.createdAt = Instant.now();
        
        this.patternCache = new ConcurrentHashMap<>();
        this.operationCounts = new ConcurrentHashMap<>();
        this.transformationCache = new ConcurrentHashMap<>();
        this.domainTerms = initializeDomainTerms();
    }

    /**
     * Processes a class name for ByteHot display and analysis.
     * @param className The class name to process
     * @return Processed class name suitable for display
     */
    public String processClassName(final String className) {
        if (className == null || className.trim().isEmpty()) {
            return "UnknownClass";
        }
        
        incrementOperationCount("processClassName");
        
        String cacheKey = "className:" + className;
        if (cacheEnabled && transformationCache.containsKey(cacheKey)) {
            return transformationCache.get(cacheKey);
        }
        
        String processed = className;
        
        // Remove package prefixes for common Java packages
        processed = removeCommonPackagePrefixes(processed);
        
        // Handle inner classes
        processed = processed.replace('$', '.');
        
        // Preserve domain-specific formatting
        processed = preserveDomainTerms(processed);
        
        // Cache result if enabled
        if (cacheEnabled && transformationCache.size() < maxCacheSize) {
            transformationCache.put(cacheKey, processed);
        }
        
        return processed;
    }

    /**
     * Generates a user-friendly event name from a class name.
     * @param eventClassName The event class name
     * @return User-friendly event name
     */
    public String generateEventDisplayName(final String eventClassName) {
        if (eventClassName == null || eventClassName.trim().isEmpty()) {
            return "Unknown Event";
        }
        
        incrementOperationCount("generateEventDisplayName");
        
        String cacheKey = "eventDisplay:" + eventClassName;
        if (cacheEnabled && transformationCache.containsKey(cacheKey)) {
            return transformationCache.get(cacheKey);
        }
        
        String processed = eventClassName;
        
        // Remove common suffixes
        processed = processed.replaceAll("(Event|Requested|Completed|Failed)$", "");
        
        // Convert camelCase to space-separated words
        processed = camelCaseToWords(processed);
        
        // Capitalize appropriately
        processed = capitalizeWords(processed);
        
        // Cache result
        if (cacheEnabled && transformationCache.size() < maxCacheSize) {
            transformationCache.put(cacheKey, processed);
        }
        
        return processed;
    }

    /**
     * Normalizes an identifier for consistent usage across the system.
     * @param identifier The identifier to normalize
     * @return Normalized identifier
     */
    public String normalizeIdentifier(final String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return "unknown";
        }
        
        incrementOperationCount("normalizeIdentifier");
        
        String cacheKey = "normalize:" + identifier;
        if (cacheEnabled && transformationCache.containsKey(cacheKey)) {
            return transformationCache.get(cacheKey);
        }
        
        String normalized = identifier.trim();
        
        // Remove non-alphanumeric characters except hyphens and underscores
        normalized = normalized.replaceAll("[^a-zA-Z0-9\\-_]", "");
        
        // Convert to lowercase if case insensitive
        if (caseInsensitive) {
            normalized = normalized.toLowerCase();
        }
        
        // Ensure it starts with a letter
        if (!normalized.isEmpty() && !Character.isLetter(normalized.charAt(0))) {
            normalized = "id_" + normalized;
        }
        
        // Cache result
        if (cacheEnabled && transformationCache.size() < maxCacheSize) {
            transformationCache.put(cacheKey, normalized);
        }
        
        return normalized;
    }

    /**
     * Extracts keywords from text for analysis and indexing.
     * @param text The text to analyze
     * @return List of significant keywords
     */
    public List<String> extractKeywords(final String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        
        incrementOperationCount("extractKeywords");
        
        List<String> keywords = new java.util.ArrayList<>();
        
        // Split by common delimiters
        String[] words = text.split("[\\s\\p{Punct}]+");
        
        for (String word : words) {
            String cleaned = word.trim().toLowerCase();
            
            // Skip short words and common stop words
            if (cleaned.length() >= 3 && !isStopWord(cleaned)) {
                // Prefer domain terms
                if (domainTerms.contains(cleaned) || isDomainRelevant(cleaned)) {
                    keywords.add(cleaned);
                } else if (cleaned.length() >= 5) {
                    keywords.add(cleaned);
                }
            }
        }
        
        return keywords.stream().distinct().sorted().limit(10).toList();
    }

    /**
     * Validates that a string meets ByteHot naming conventions.
     * @param name The name to validate
     * @param type The type of name (class, method, field, etc.)
     * @return Validation result with suggestions
     */
    public ValidationResult validateNaming(final String name, final String type) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.invalid("Name cannot be empty");
        }
        
        incrementOperationCount("validateNaming");
        
        List<String> issues = new java.util.ArrayList<>();
        List<String> suggestions = new java.util.ArrayList<>();
        
        // Check length
        if (name.length() < 3) {
            issues.add("Name is too short (minimum 3 characters)");
            suggestions.add("Use a more descriptive name");
        } else if (name.length() > 50) {
            issues.add("Name is too long (maximum 50 characters)");
            suggestions.add("Consider abbreviating or splitting the concept");
        }
        
        // Check naming conventions based on type
        switch (type.toLowerCase()) {
            case "class":
                if (!Character.isUpperCase(name.charAt(0))) {
                    issues.add("Class names should start with uppercase");
                    suggestions.add("Capitalize the first letter");
                }
                break;
            case "method":
                if (!Character.isLowerCase(name.charAt(0))) {
                    issues.add("Method names should start with lowercase");
                    suggestions.add("Use lowercase for the first letter");
                }
                break;
            case "field":
                if (name.matches(".*[A-Z].*") && name.contains("_")) {
                    issues.add("Mixed camelCase and underscore notation");
                    suggestions.add("Use either camelCase or snake_case consistently");
                }
                break;
        }
        
        // Check for domain relevance
        if (!containsDomainTerm(name) && !isDomainRelevant(name)) {
            suggestions.add("Consider including domain-specific terms for clarity");
        }
        
        return new ValidationResult(issues.isEmpty(), issues, suggestions);
    }

    /**
     * Gets processing statistics for this StringProcessor instance.
     * @return Map of operation names to counts
     */
    public Map<String, Long> getProcessingStatistics() {
        if (!statisticsEnabled) {
            return Map.of();
        }
        
        return operationCounts.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get()
            ));
    }

    /**
     * Gets cache efficiency statistics.
     * @return Cache hit ratio and other metrics
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("cacheSize", transformationCache.size());
        stats.put("maxCacheSize", maxCacheSize);
        stats.put("cacheEnabled", cacheEnabled);
        stats.put("patternCacheSize", patternCache.size());
        stats.put("createdAt", createdAt);
        
        // Calculate efficiency if statistics are enabled
        if (statisticsEnabled) {
            long totalOperations = operationCounts.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
            stats.put("totalOperations", totalOperations);
            
            if (totalOperations > 0) {
                double efficiency = Math.min(1.0, (double) transformationCache.size() / totalOperations);
                stats.put("cacheEfficiency", efficiency);
            }
        }
        
        return stats;
    }

    /**
     * Clears all caches and resets statistics.
     */
    public void reset() {
        transformationCache.clear();
        patternCache.clear();
        operationCounts.clear();
    }

    /**
     * Updates processing preferences.
     * @param cacheEnabled Whether to enable caching
     * @param statisticsEnabled Whether to track statistics
     * @param caseInsensitive Whether operations should be case insensitive
     */
    public void updatePreferences(final boolean cacheEnabled, final boolean statisticsEnabled, 
                                 final boolean caseInsensitive) {
        this.cacheEnabled = cacheEnabled;
        this.statisticsEnabled = statisticsEnabled;
        this.caseInsensitive = caseInsensitive;
        
        if (!cacheEnabled) {
            transformationCache.clear();
        }
        if (!statisticsEnabled) {
            operationCounts.clear();
        }
    }

    /**
     * Initializes the set of domain-specific terms.
     */
    protected Set<String> initializeDomainTerms() {
        return Set.of(
            "bytehot", "hotswap", "class", "method", "field", "event", "flow", "analysis",
            "detection", "monitoring", "agent", "jvm", "bytecode", "instrumentation",
            "rollback", "snapshot", "user", "profile", "session", "configuration",
            "statistics", "metadata", "audit", "recovery", "exception", "error"
        );
    }

    /**
     * Increments the operation count for statistics tracking.
     */
    protected void incrementOperationCount(final String operation) {
        if (statisticsEnabled) {
            operationCounts.computeIfAbsent(operation, k -> new AtomicLong(0)).incrementAndGet();
        }
    }

    /**
     * Removes common Java package prefixes to shorten class names.
     */
    protected String removeCommonPackagePrefixes(final String className) {
        String[] prefixesToRemove = {
            "java.lang.", "java.util.", "java.io.", "java.time.",
            "org.acmsl.bytehot.domain.", "org.acmsl.bytehot.application.",
            "org.acmsl.bytehot.infrastructure."
        };
        
        String result = className;
        for (String prefix : prefixesToRemove) {
            if (result.startsWith(prefix)) {
                result = result.substring(prefix.length());
                break;
            }
        }
        
        return result;
    }

    /**
     * Preserves important domain terms in their original casing.
     */
    protected String preserveDomainTerms(final String text) {
        String result = text;
        
        // Preserve specific domain term casing
        Map<String, String> preserveMap = Map.of(
            "bytehot", "ByteHot",
            "jvm", "JVM",
            "api", "API",
            "url", "URL",
            "http", "HTTP",
            "json", "JSON"
        );
        
        for (Map.Entry<String, String> entry : preserveMap.entrySet()) {
            Pattern pattern = getOrCreatePattern("(?i)" + entry.getKey());
            result = pattern.matcher(result).replaceAll(entry.getValue());
        }
        
        return result;
    }

    /**
     * Converts camelCase to space-separated words.
     */
    protected String camelCaseToWords(final String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    /**
     * Capitalizes the first letter of each word.
     */
    protected String capitalizeWords(final String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }

    /**
     * Checks if a word is a common stop word.
     */
    protected boolean isStopWord(final String word) {
        Set<String> stopWords = Set.of(
            "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with",
            "by", "from", "as", "is", "was", "are", "were", "be", "been", "have",
            "has", "had", "do", "does", "did", "will", "would", "could", "should"
        );
        return stopWords.contains(word.toLowerCase());
    }

    /**
     * Checks if a term is relevant to the ByteHot domain.
     */
    protected boolean isDomainRelevant(final String term) {
        return term.contains("byte") || term.contains("hot") || term.contains("swap")
            || term.contains("class") || term.contains("java") || term.contains("jvm")
            || term.contains("event") || term.contains("flow") || term.contains("agent");
    }

    /**
     * Checks if a name contains any domain terms.
     */
    protected boolean containsDomainTerm(final String name) {
        String lowerName = name.toLowerCase();
        return domainTerms.stream().anyMatch(lowerName::contains);
    }

    /**
     * Gets or creates a compiled pattern from cache.
     */
    protected Pattern getOrCreatePattern(final String regex) {
        return patternCache.computeIfAbsent(regex, Pattern::compile);
    }

    /**
     * Validation result for naming conventions.
     */
    public static class ValidationResult {
        protected final boolean valid;
        protected final List<String> issues;
        protected final List<String> suggestions;

        public ValidationResult(final boolean valid, final List<String> issues, final List<String> suggestions) {
            this.valid = valid;
            this.issues = List.copyOf(issues);
            this.suggestions = List.copyOf(suggestions);
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, List.of(), List.of());
        }

        public static ValidationResult invalid(final String issue) {
            return new ValidationResult(false, List.of(issue), List.of());
        }

        public boolean isValid() { return valid; }
        public List<String> getIssues() { return issues; }
        public List<String> getSuggestions() { return suggestions; }
    }
}