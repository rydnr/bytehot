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
 * Filename: SecurityManager.java
 *
 * Author: Claude Code
 *
 * Class name: SecurityManager
 *
 * Responsibilities:
 *   - Manage enterprise security features for ByteHot operations
 *   - Implement role-based access control (RBAC) and permissions
 *   - Handle authentication and authorization for hot-swap operations
 *   - Provide security policy enforcement and audit integration
 *
 * Collaborators:
 *   - UserContextResolver: Resolves current user context
 *   - ByteHotLogger: Logs security events and violations
 *   - AuditTrail: Records security-related audit events
 *   - PermissionManager: Manages user permissions and roles
 */
package org.acmsl.bytehot.infrastructure.security;

import org.acmsl.bytehot.domain.UserContextResolver;
import org.acmsl.bytehot.infrastructure.logging.ByteHotLogger;
import org.acmsl.bytehot.infrastructure.logging.AuditTrail;

import java.time.Instant;
import java.time.Duration;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enterprise security management system for ByteHot operations.
 * Provides comprehensive access control, authentication, and security enforcement.
 * @author Claude Code
 * @since 2025-07-06
 */
public class SecurityManager {

    private static final SecurityManager INSTANCE = new SecurityManager();
    private static final ByteHotLogger LOGGER = ByteHotLogger.getLogger(SecurityManager.class);
    
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, SecurityRole> roles = new ConcurrentHashMap<>();
    private final Map<String, SecurityPolicy> policies = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failedAttempts = new ConcurrentHashMap<>();
    private final Set<String> blockedUsers = Collections.synchronizedSet(new HashSet<>());
    
    private final ReentrantReadWriteLock securityLock = new ReentrantReadWriteLock();
    private final SecureRandom secureRandom = new SecureRandom();
    
    private final ScheduledExecutorService securityExecutor = 
        Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "ByteHot-Security-Manager");
            t.setDaemon(true);
            return t;
        });
    
    private volatile SecurityConfiguration configuration = SecurityConfiguration.defaultConfiguration();
    private volatile boolean securityEnabled = true;
    
    private SecurityManager() {
        initializeDefaultRolesAndPolicies();
        startSecurityMaintenance();
    }

    /**
     * Gets the singleton instance of SecurityManager.
     * @return The security manager instance
     */
    public static SecurityManager getInstance() {
        return INSTANCE;
    }

    /**
     * Authenticates a user and creates a secure session.
     * This method can be hot-swapped to change authentication behavior.
     * @param credentials User authentication credentials
     * @return Authentication result with session information
     */
    public AuthenticationResult authenticate(final UserCredentials credentials) {
        if (!securityEnabled) {
            return createUnauthenticatedResult("Security is disabled");
        }
        
        final String userId = credentials.getUserId();
        
        // Check if user is blocked
        if (blockedUsers.contains(userId)) {
            LOGGER.security(
                ByteHotLogger.SecurityEventType.AUTHENTICATION_FAILURE,
                ByteHotLogger.SecuritySeverity.HIGH,
                "Authentication attempt by blocked user: " + userId,
                Map.of("userId", userId, "reason", "user_blocked")
            );
            return createFailedResult("User is blocked due to security violations");
        }
        
        // Check rate limiting
        if (isRateLimited(userId)) {
            LOGGER.security(
                ByteHotLogger.SecurityEventType.AUTHENTICATION_FAILURE,
                ByteHotLogger.SecuritySeverity.MEDIUM,
                "Authentication rate limited for user: " + userId,
                Map.of("userId", userId, "reason", "rate_limited")
            );
            return createFailedResult("Too many authentication attempts");
        }
        
        try {
            // Validate credentials
            final boolean isValid = validateCredentials(credentials);
            
            if (isValid) {
                // Reset failed attempts on successful authentication
                failedAttempts.remove(userId);
                
                // Create secure session
                final UserSession session = createUserSession(userId, credentials.getAuthenticationMethod());
                activeSessions.put(session.getSessionId(), session);
                
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.AUTHENTICATION_SUCCESS,
                    ByteHotLogger.SecuritySeverity.LOW,
                    "User authenticated successfully: " + userId,
                    Map.of("userId", userId, "sessionId", session.getSessionId(), 
                          "method", credentials.getAuthenticationMethod())
                );
                
                return new AuthenticationResult(true, "Authentication successful", session);
                
            } else {
                // Track failed attempts
                final int attempts = failedAttempts.computeIfAbsent(userId, k -> new AtomicInteger(0))
                    .incrementAndGet();
                
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.AUTHENTICATION_FAILURE,
                    ByteHotLogger.SecuritySeverity.MEDIUM,
                    "Authentication failed for user: " + userId + " (attempt " + attempts + ")",
                    Map.of("userId", userId, "attempts", attempts)
                );
                
                // Block user if too many failed attempts
                if (attempts >= configuration.getMaxFailedAttempts()) {
                    blockedUsers.add(userId);
                    LOGGER.security(
                        ByteHotLogger.SecurityEventType.SUSPICIOUS_ACTIVITY,
                        ByteHotLogger.SecuritySeverity.HIGH,
                        "User blocked due to repeated failed authentication: " + userId,
                        Map.of("userId", userId, "attempts", attempts)
                    );
                }
                
                return createFailedResult("Invalid credentials");
            }
            
        } catch (final Exception e) {
            LOGGER.error("Authentication error for user: " + userId, e);
            return createFailedResult("Authentication system error");
        }
    }

    /**
     * Authorizes a user to perform a specific hot-swap operation.
     * This method can be hot-swapped to change authorization behavior.
     * @param sessionId User session ID
     * @param operation Hot-swap operation to authorize
     * @param resource Resource being accessed
     * @return Authorization result
     */
    public AuthorizationResult authorize(final String sessionId, final SecurityOperation operation, final String resource) {
        if (!securityEnabled) {
            return new AuthorizationResult(true, "Security is disabled", null);
        }
        
        final UserSession session = activeSessions.get(sessionId);
        if (session == null || session.isExpired()) {
            LOGGER.security(
                ByteHotLogger.SecurityEventType.AUTHORIZATION_FAILURE,
                ByteHotLogger.SecuritySeverity.MEDIUM,
                "Authorization failed: Invalid or expired session",
                Map.of("sessionId", sessionId, "operation", operation.name(), "resource", resource)
            );
            return new AuthorizationResult(false, "Invalid or expired session", null);
        }
        
        try {
            // Get user role and permissions
            final SecurityRole userRole = getUserRole(session.getUserId());
            if (userRole == null) {
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.AUTHORIZATION_FAILURE,
                    ByteHotLogger.SecuritySeverity.HIGH,
                    "Authorization failed: No role assigned to user",
                    Map.of("userId", session.getUserId(), "operation", operation.name())
                );
                return new AuthorizationResult(false, "No role assigned to user", null);
            }
            
            // Check if user has permission for this operation
            final boolean hasPermission = userRole.hasPermission(operation);
            if (!hasPermission) {
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.AUTHORIZATION_FAILURE,
                    ByteHotLogger.SecuritySeverity.MEDIUM,
                    "Authorization failed: Insufficient permissions",
                    Map.of("userId", session.getUserId(), "role", userRole.getRoleName(), 
                          "operation", operation.name(), "resource", resource)
                );
                return new AuthorizationResult(false, "Insufficient permissions for operation", userRole);
            }
            
            // Check resource-specific policies
            final boolean resourceAllowed = checkResourcePolicy(userRole, operation, resource);
            if (!resourceAllowed) {
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.AUTHORIZATION_FAILURE,
                    ByteHotLogger.SecuritySeverity.MEDIUM,
                    "Authorization failed: Resource access denied by policy",
                    Map.of("userId", session.getUserId(), "operation", operation.name(), "resource", resource)
                );
                return new AuthorizationResult(false, "Resource access denied by policy", userRole);
            }
            
            // Check time-based restrictions
            final boolean timeAllowed = checkTimeRestrictions(userRole);
            if (!timeAllowed) {
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.AUTHORIZATION_FAILURE,
                    ByteHotLogger.SecuritySeverity.LOW,
                    "Authorization failed: Operation not allowed at this time",
                    Map.of("userId", session.getUserId(), "operation", operation.name())
                );
                return new AuthorizationResult(false, "Operation not allowed at this time", userRole);
            }
            
            // Update session activity
            session.updateLastActivity();
            
            LOGGER.security(
                ByteHotLogger.SecurityEventType.DATA_ACCESS,
                ByteHotLogger.SecuritySeverity.LOW,
                "Authorization granted for operation",
                Map.of("userId", session.getUserId(), "operation", operation.name(), "resource", resource)
            );
            
            return new AuthorizationResult(true, "Authorization granted", userRole);
            
        } catch (final Exception e) {
            LOGGER.error("Authorization error for session: " + sessionId, e);
            return new AuthorizationResult(false, "Authorization system error", null);
        }
    }

    /**
     * Validates a security token for API access.
     * This method can be hot-swapped to change token validation behavior.
     * @param token Security token to validate
     * @return Token validation result
     */
    public TokenValidationResult validateToken(final String token) {
        if (!securityEnabled) {
            return new TokenValidationResult(true, "Security is disabled", null, null);
        }
        
        try {
            // Parse and validate token structure
            final SecurityToken parsedToken = parseSecurityToken(token);
            if (parsedToken == null) {
                return new TokenValidationResult(false, "Invalid token format", null, null);
            }
            
            // Check token expiration
            if (parsedToken.isExpired()) {
                return new TokenValidationResult(false, "Token has expired", null, null);
            }
            
            // Validate token signature
            if (!validateTokenSignature(parsedToken)) {
                LOGGER.security(
                    ByteHotLogger.SecurityEventType.SECURITY_VIOLATION,
                    ByteHotLogger.SecuritySeverity.HIGH,
                    "Invalid token signature detected",
                    Map.of("tokenId", parsedToken.getTokenId())
                );
                return new TokenValidationResult(false, "Invalid token signature", null, null);
            }
            
            // Check if token is revoked
            if (isTokenRevoked(parsedToken.getTokenId())) {
                return new TokenValidationResult(false, "Token has been revoked", null, null);
            }
            
            // Get user role for token
            final SecurityRole userRole = getUserRole(parsedToken.getUserId());
            
            return new TokenValidationResult(true, "Token is valid", parsedToken, userRole);
            
        } catch (final Exception e) {
            LOGGER.error("Token validation error", e);
            return new TokenValidationResult(false, "Token validation system error", null, null);
        }
    }

    /**
     * Creates a new security role with specified permissions.
     * This method can be hot-swapped to change role creation behavior.
     * @param roleName Name of the role
     * @param permissions Set of permissions for the role
     * @param description Role description
     * @return Created security role
     */
    public SecurityRole createRole(final String roleName, final Set<SecurityOperation> permissions, final String description) {
        securityLock.writeLock().lock();
        try {
            if (roles.containsKey(roleName)) {
                throw new IllegalArgumentException("Role already exists: " + roleName);
            }
            
            final SecurityRole role = new SecurityRole(roleName, permissions, description);
            roles.put(roleName, role);
            
            LOGGER.audit("CREATE_ROLE", roleName, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Created security role with " + permissions.size() + " permissions");
            
            return role;
            
        } finally {
            securityLock.writeLock().unlock();
        }
    }

    /**
     * Assigns a role to a user.
     * This method can be hot-swapped to change role assignment behavior.
     * @param userId User ID
     * @param roleName Role name to assign
     * @return true if assignment was successful
     */
    public boolean assignRole(final String userId, final String roleName) {
        securityLock.writeLock().lock();
        try {
            final SecurityRole role = roles.get(roleName);
            if (role == null) {
                LOGGER.audit("ASSIGN_ROLE", userId, ByteHotLogger.AuditOutcome.FAILURE, 
                    "Role not found: " + roleName);
                return false;
            }
            
            // Store user-role mapping (in real implementation would use persistent storage)
            final String userRoleKey = "user_role_" + userId;
            roles.put(userRoleKey, role);
            
            LOGGER.audit("ASSIGN_ROLE", userId, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Assigned role '" + roleName + "' to user");
            
            return true;
            
        } finally {
            securityLock.writeLock().unlock();
        }
    }

    /**
     * Creates a security policy for resource access control.
     * This method can be hot-swapped to change policy creation behavior.
     * @param policyName Name of the policy
     * @param rules Set of policy rules
     * @param description Policy description
     * @return Created security policy
     */
    public SecurityPolicy createPolicy(final String policyName, final Set<PolicyRule> rules, final String description) {
        securityLock.writeLock().lock();
        try {
            if (policies.containsKey(policyName)) {
                throw new IllegalArgumentException("Policy already exists: " + policyName);
            }
            
            final SecurityPolicy policy = new SecurityPolicy(policyName, rules, description);
            policies.put(policyName, policy);
            
            LOGGER.audit("CREATE_POLICY", policyName, ByteHotLogger.AuditOutcome.SUCCESS, 
                "Created security policy with " + rules.size() + " rules");
            
            return policy;
            
        } finally {
            securityLock.writeLock().unlock();
        }
    }

    /**
     * Invalidates a user session for logout or security purposes.
     * This method can be hot-swapped to change session invalidation behavior.
     * @param sessionId Session ID to invalidate
     * @return true if session was invalidated
     */
    public boolean invalidateSession(final String sessionId) {
        final UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            LOGGER.security(
                ByteHotLogger.SecurityEventType.DATA_ACCESS,
                ByteHotLogger.SecuritySeverity.LOW,
                "Session invalidated",
                Map.of("userId", session.getUserId(), "sessionId", sessionId)
            );
            return true;
        }
        return false;
    }

    /**
     * Gets current security statistics for monitoring.
     * @return Current security statistics
     */
    public SecurityStatistics getSecurityStatistics() {
        securityLock.readLock().lock();
        try {
            final int activeSessionCount = activeSessions.size();
            final int blockedUserCount = blockedUsers.size();
            final int roleCount = roles.size();
            final int policyCount = policies.size();
            
            final Map<String, Integer> failedAttemptStats = failedAttempts.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().get()
                ));
            
            return new SecurityStatistics(
                activeSessionCount,
                blockedUserCount,
                roleCount,
                policyCount,
                failedAttemptStats,
                configuration.getMaxFailedAttempts(),
                configuration.getSessionTimeout().toMinutes()
            );
            
        } finally {
            securityLock.readLock().unlock();
        }
    }

    /**
     * Configures security settings.
     * This method can be hot-swapped to change security configuration.
     * @param newConfiguration New security configuration
     */
    public void configure(final SecurityConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        this.securityEnabled = newConfiguration.isEnabled();
        
        LOGGER.audit("CONFIGURE_SECURITY", "system", ByteHotLogger.AuditOutcome.SUCCESS, 
            "Security configuration updated");
    }

    /**
     * Shuts down the security manager.
     */
    public void shutdown() {
        // Invalidate all active sessions
        activeSessions.clear();
        
        securityExecutor.shutdown();
        try {
            if (!securityExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                securityExecutor.shutdownNow();
            }
        } catch (final InterruptedException e) {
            securityExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Initializes default roles and policies.
     * This method can be hot-swapped to change default initialization.
     */
    protected void initializeDefaultRolesAndPolicies() {
        // Create default roles
        createDefaultRoles();
        
        // Create default policies
        createDefaultPolicies();
        
        LOGGER.info("Initialized default security roles and policies");
    }

    /**
     * Starts security maintenance tasks.
     * This method can be hot-swapped to change maintenance behavior.
     */
    protected void startSecurityMaintenance() {
        // Schedule session cleanup
        securityExecutor.scheduleAtFixedRate(
            this::cleanupExpiredSessions,
            1, 5, TimeUnit.MINUTES
        );
        
        // Schedule failed attempt cleanup
        securityExecutor.scheduleAtFixedRate(
            this::cleanupFailedAttempts,
            10, 30, TimeUnit.MINUTES
        );
        
        // Schedule security statistics reporting
        securityExecutor.scheduleAtFixedRate(
            this::reportSecurityStatistics,
            1, 15, TimeUnit.MINUTES
        );
    }

    // Helper methods
    
    protected void createDefaultRoles() {
        // Administrator role with full permissions
        final Set<SecurityOperation> adminPermissions = Set.of(SecurityOperation.values());
        createRole("ADMIN", adminPermissions, "Full system administrator access");
        
        // Developer role with hot-swap permissions
        final Set<SecurityOperation> devPermissions = Set.of(
            SecurityOperation.HOT_SWAP_CLASS,
            SecurityOperation.VIEW_CLASSES,
            SecurityOperation.VIEW_PERFORMANCE,
            SecurityOperation.EXECUTE_LOAD_TEST
        );
        createRole("DEVELOPER", devPermissions, "Developer access for hot-swap operations");
        
        // Operator role with monitoring permissions
        final Set<SecurityOperation> opPermissions = Set.of(
            SecurityOperation.VIEW_CLASSES,
            SecurityOperation.VIEW_PERFORMANCE,
            SecurityOperation.VIEW_AUDIT_LOGS,
            SecurityOperation.EXECUTE_LOAD_TEST
        );
        createRole("OPERATOR", opPermissions, "Operations monitoring and testing access");
        
        // Read-only role
        final Set<SecurityOperation> readOnlyPermissions = Set.of(
            SecurityOperation.VIEW_CLASSES,
            SecurityOperation.VIEW_PERFORMANCE
        );
        createRole("READ_ONLY", readOnlyPermissions, "Read-only access to system information");
    }
    
    protected void createDefaultPolicies() {
        // Time-based access policy
        final Set<PolicyRule> timeRules = Set.of(
            new PolicyRule("business_hours", "operation_time", "08:00-18:00", PolicyEffect.ALLOW),
            new PolicyRule("maintenance_window", "operation_time", "02:00-04:00", PolicyEffect.DENY)
        );
        createPolicy("TIME_BASED_ACCESS", timeRules, "Time-based operation restrictions");
        
        // Resource access policy
        final Set<PolicyRule> resourceRules = Set.of(
            new PolicyRule("critical_classes", "resource_pattern", "*.critical.*", PolicyEffect.RESTRICT),
            new PolicyRule("test_classes", "resource_pattern", "*.test.*", PolicyEffect.ALLOW)
        );
        createPolicy("RESOURCE_ACCESS", resourceRules, "Resource-based access control");
    }
    
    protected boolean validateCredentials(final UserCredentials credentials) {
        // Implement credential validation logic
        // In real implementation would integrate with LDAP, OAuth, etc.
        return credentials.getUserId() != null && credentials.getPassword() != null;
    }
    
    protected boolean isRateLimited(final String userId) {
        final AtomicInteger attempts = failedAttempts.get(userId);
        return attempts != null && attempts.get() >= configuration.getMaxFailedAttempts();
    }
    
    protected UserSession createUserSession(final String userId, final String authMethod) {
        final String sessionId = generateSecureSessionId();
        final Instant createdAt = Instant.now();
        final Instant expiresAt = createdAt.plus(configuration.getSessionTimeout());
        
        return new UserSession(sessionId, userId, authMethod, createdAt, expiresAt);
    }
    
    protected String generateSecureSessionId() {
        final byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    protected SecurityRole getUserRole(final String userId) {
        return roles.get("user_role_" + userId);
    }
    
    protected boolean checkResourcePolicy(final SecurityRole role, final SecurityOperation operation, final String resource) {
        // Check resource-specific policies
        for (final SecurityPolicy policy : policies.values()) {
            for (final PolicyRule rule : policy.getRules()) {
                if (rule.matches(resource) && rule.getEffect() == PolicyEffect.DENY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected boolean checkTimeRestrictions(final SecurityRole role) {
        // Check time-based restrictions
        // In real implementation would check business hours, maintenance windows, etc.
        return true;
    }
    
    protected SecurityToken parseSecurityToken(final String token) {
        // Parse JWT or custom token format
        // In real implementation would use proper JWT library
        try {
            final String[] parts = token.split("\\.");
            if (parts.length == 3) {
                // Simple token parsing simulation
                final String payload = new String(Base64.getDecoder().decode(parts[1]));
                return new SecurityToken("token_id", "user_id", Instant.now().plusSeconds(3600));
            }
        } catch (final Exception e) {
            LOGGER.debug("Token parsing failed", e);
        }
        return null;
    }
    
    protected boolean validateTokenSignature(final SecurityToken token) {
        // Validate token signature using cryptographic verification
        // In real implementation would use proper signature validation
        return true;
    }
    
    protected boolean isTokenRevoked(final String tokenId) {
        // Check if token is in revocation list
        // In real implementation would check persistent revocation store
        return false;
    }
    
    protected AuthenticationResult createUnauthenticatedResult(final String message) {
        return new AuthenticationResult(false, message, null);
    }
    
    protected AuthenticationResult createFailedResult(final String message) {
        return new AuthenticationResult(false, message, null);
    }
    
    protected void cleanupExpiredSessions() {
        final Instant now = Instant.now();
        final int sizeBefore = activeSessions.size();
        
        activeSessions.entrySet().removeIf(entry -> {
            final UserSession session = entry.getValue();
            if (session.isExpired(now)) {
                LOGGER.debug("Cleaned up expired session: " + entry.getKey());
                return true;
            }
            return false;
        });
        
        final int removed = sizeBefore - activeSessions.size();
        if (removed > 0) {
            LOGGER.debug("Cleaned up " + removed + " expired sessions");
        }
    }
    
    protected void cleanupFailedAttempts() {
        // Reset failed attempts after cooldown period
        final long cooldownMinutes = configuration.getFailedAttemptCooldown().toMinutes();
        if (cooldownMinutes > 0) {
            failedAttempts.clear();
            blockedUsers.clear();
            LOGGER.debug("Reset failed authentication attempts after cooldown");
        }
    }
    
    protected void reportSecurityStatistics() {
        if (configuration.isStatisticsReportingEnabled()) {
            final SecurityStatistics stats = getSecurityStatistics();
            LOGGER.info("Security Statistics: " + stats.getActiveSessionCount() + " active sessions, " +
                stats.getBlockedUserCount() + " blocked users, " + stats.getRoleCount() + " roles");
        }
    }

    // Enums and supporting classes
    
    public enum SecurityOperation {
        HOT_SWAP_CLASS,
        VIEW_CLASSES,
        CONFIGURE_SYSTEM,
        VIEW_AUDIT_LOGS,
        MANAGE_USERS,
        VIEW_PERFORMANCE,
        EXECUTE_LOAD_TEST,
        BACKUP_RESTORE,
        SECURITY_ADMIN
    }

    public enum PolicyEffect {
        ALLOW, DENY, RESTRICT
    }

    // Static inner classes for data structures
    
    public static class UserCredentials {
        private final String userId;
        private final String password;
        private final String authenticationMethod;
        private final Map<String, Object> additionalData;

        public UserCredentials(final String userId, final String password, final String authenticationMethod) {
            this(userId, password, authenticationMethod, Map.of());
        }

        public UserCredentials(final String userId, final String password, final String authenticationMethod,
                              final Map<String, Object> additionalData) {
            this.userId = userId;
            this.password = password;
            this.authenticationMethod = authenticationMethod;
            this.additionalData = additionalData;
        }

        public String getUserId() { return userId; }
        public String getPassword() { return password; }
        public String getAuthenticationMethod() { return authenticationMethod; }
        public Map<String, Object> getAdditionalData() { return additionalData; }
    }

    public static class UserSession {
        private final String sessionId;
        private final String userId;
        private final String authenticationMethod;
        private final Instant createdAt;
        private final Instant expiresAt;
        private volatile Instant lastActivity;

        public UserSession(final String sessionId, final String userId, final String authenticationMethod,
                          final Instant createdAt, final Instant expiresAt) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.authenticationMethod = authenticationMethod;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.lastActivity = createdAt;
        }

        public String getSessionId() { return sessionId; }
        public String getUserId() { return userId; }
        public String getAuthenticationMethod() { return authenticationMethod; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getExpiresAt() { return expiresAt; }
        public Instant getLastActivity() { return lastActivity; }

        public boolean isExpired() {
            return isExpired(Instant.now());
        }

        public boolean isExpired(final Instant now) {
            return now.isAfter(expiresAt);
        }

        public void updateLastActivity() {
            this.lastActivity = Instant.now();
        }
    }

    public static class SecurityRole {
        private final String roleName;
        private final Set<SecurityOperation> permissions;
        private final String description;
        private final Instant createdAt;

        public SecurityRole(final String roleName, final Set<SecurityOperation> permissions, final String description) {
            this.roleName = roleName;
            this.permissions = Set.copyOf(permissions);
            this.description = description;
            this.createdAt = Instant.now();
        }

        public String getRoleName() { return roleName; }
        public Set<SecurityOperation> getPermissions() { return permissions; }
        public String getDescription() { return description; }
        public Instant getCreatedAt() { return createdAt; }

        public boolean hasPermission(final SecurityOperation operation) {
            return permissions.contains(operation);
        }
    }

    public static class SecurityPolicy {
        private final String policyName;
        private final Set<PolicyRule> rules;
        private final String description;
        private final Instant createdAt;

        public SecurityPolicy(final String policyName, final Set<PolicyRule> rules, final String description) {
            this.policyName = policyName;
            this.rules = Set.copyOf(rules);
            this.description = description;
            this.createdAt = Instant.now();
        }

        public String getPolicyName() { return policyName; }
        public Set<PolicyRule> getRules() { return rules; }
        public String getDescription() { return description; }
        public Instant getCreatedAt() { return createdAt; }
    }

    public static class PolicyRule {
        private final String ruleName;
        private final String ruleType;
        private final String ruleValue;
        private final PolicyEffect effect;

        public PolicyRule(final String ruleName, final String ruleType, final String ruleValue, final PolicyEffect effect) {
            this.ruleName = ruleName;
            this.ruleType = ruleType;
            this.ruleValue = ruleValue;
            this.effect = effect;
        }

        public String getRuleName() { return ruleName; }
        public String getRuleType() { return ruleType; }
        public String getRuleValue() { return ruleValue; }
        public PolicyEffect getEffect() { return effect; }

        public boolean matches(final String resource) {
            return switch (ruleType) {
                case "resource_pattern" -> resource.matches(ruleValue.replace("*", ".*"));
                case "operation_time" -> matchesTimeRange(ruleValue);
                default -> false;
            };
        }

        private boolean matchesTimeRange(final String timeRange) {
            // Simple time range matching - in real implementation would be more sophisticated
            return true;
        }
    }

    public static class SecurityToken {
        private final String tokenId;
        private final String userId;
        private final Instant expiresAt;
        private final Map<String, Object> claims;

        public SecurityToken(final String tokenId, final String userId, final Instant expiresAt) {
            this(tokenId, userId, expiresAt, Map.of());
        }

        public SecurityToken(final String tokenId, final String userId, final Instant expiresAt,
                            final Map<String, Object> claims) {
            this.tokenId = tokenId;
            this.userId = userId;
            this.expiresAt = expiresAt;
            this.claims = claims;
        }

        public String getTokenId() { return tokenId; }
        public String getUserId() { return userId; }
        public Instant getExpiresAt() { return expiresAt; }
        public Map<String, Object> getClaims() { return claims; }

        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    public static class AuthenticationResult {
        private final boolean successful;
        private final String message;
        private final UserSession session;

        public AuthenticationResult(final boolean successful, final String message, final UserSession session) {
            this.successful = successful;
            this.message = message;
            this.session = session;
        }

        public boolean isSuccessful() { return successful; }
        public String getMessage() { return message; }
        public UserSession getSession() { return session; }
    }

    public static class AuthorizationResult {
        private final boolean authorized;
        private final String message;
        private final SecurityRole role;

        public AuthorizationResult(final boolean authorized, final String message, final SecurityRole role) {
            this.authorized = authorized;
            this.message = message;
            this.role = role;
        }

        public boolean isAuthorized() { return authorized; }
        public String getMessage() { return message; }
        public SecurityRole getRole() { return role; }
    }

    public static class TokenValidationResult {
        private final boolean valid;
        private final String message;
        private final SecurityToken token;
        private final SecurityRole role;

        public TokenValidationResult(final boolean valid, final String message, 
                                   final SecurityToken token, final SecurityRole role) {
            this.valid = valid;
            this.message = message;
            this.token = token;
            this.role = role;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public SecurityToken getToken() { return token; }
        public SecurityRole getRole() { return role; }
    }

    public static class SecurityStatistics {
        private final int activeSessionCount;
        private final int blockedUserCount;
        private final int roleCount;
        private final int policyCount;
        private final Map<String, Integer> failedAttemptStats;
        private final int maxFailedAttempts;
        private final long sessionTimeoutMinutes;

        public SecurityStatistics(final int activeSessionCount, final int blockedUserCount,
                                 final int roleCount, final int policyCount,
                                 final Map<String, Integer> failedAttemptStats,
                                 final int maxFailedAttempts, final long sessionTimeoutMinutes) {
            this.activeSessionCount = activeSessionCount;
            this.blockedUserCount = blockedUserCount;
            this.roleCount = roleCount;
            this.policyCount = policyCount;
            this.failedAttemptStats = failedAttemptStats;
            this.maxFailedAttempts = maxFailedAttempts;
            this.sessionTimeoutMinutes = sessionTimeoutMinutes;
        }

        public int getActiveSessionCount() { return activeSessionCount; }
        public int getBlockedUserCount() { return blockedUserCount; }
        public int getRoleCount() { return roleCount; }
        public int getPolicyCount() { return policyCount; }
        public Map<String, Integer> getFailedAttemptStats() { return failedAttemptStats; }
        public int getMaxFailedAttempts() { return maxFailedAttempts; }
        public long getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    }

    public static class SecurityConfiguration {
        private boolean enabled = true;
        private int maxFailedAttempts = 5;
        private Duration sessionTimeout = Duration.ofHours(8);
        private Duration failedAttemptCooldown = Duration.ofMinutes(30);
        private boolean statisticsReportingEnabled = true;
        private boolean tokenValidationEnabled = true;
        private String encryptionAlgorithm = "AES-256-GCM";
        private List<String> trustedHosts = new ArrayList<>();

        public static SecurityConfiguration defaultConfiguration() {
            return new SecurityConfiguration();
        }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(final boolean enabled) { this.enabled = enabled; }

        public int getMaxFailedAttempts() { return maxFailedAttempts; }
        public void setMaxFailedAttempts(final int maxFailedAttempts) { 
            this.maxFailedAttempts = maxFailedAttempts; 
        }

        public Duration getSessionTimeout() { return sessionTimeout; }
        public void setSessionTimeout(final Duration sessionTimeout) { this.sessionTimeout = sessionTimeout; }

        public Duration getFailedAttemptCooldown() { return failedAttemptCooldown; }
        public void setFailedAttemptCooldown(final Duration failedAttemptCooldown) { 
            this.failedAttemptCooldown = failedAttemptCooldown; 
        }

        public boolean isStatisticsReportingEnabled() { return statisticsReportingEnabled; }
        public void setStatisticsReportingEnabled(final boolean statisticsReportingEnabled) { 
            this.statisticsReportingEnabled = statisticsReportingEnabled; 
        }

        public boolean isTokenValidationEnabled() { return tokenValidationEnabled; }
        public void setTokenValidationEnabled(final boolean tokenValidationEnabled) { 
            this.tokenValidationEnabled = tokenValidationEnabled; 
        }

        public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
        public void setEncryptionAlgorithm(final String encryptionAlgorithm) { 
            this.encryptionAlgorithm = encryptionAlgorithm; 
        }

        public List<String> getTrustedHosts() { return Collections.unmodifiableList(trustedHosts); }
        public void setTrustedHosts(final List<String> trustedHosts) { 
            this.trustedHosts = new ArrayList<>(trustedHosts); 
        }
    }
}