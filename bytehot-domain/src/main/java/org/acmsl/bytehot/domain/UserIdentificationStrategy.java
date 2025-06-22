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
 * Filename: UserIdentificationStrategy.java
 *
 * Author: Claude Code
 *
 * Class name: UserIdentificationStrategy
 *
 * Responsibilities:
 *   - Auto-discover user identities from multiple sources
 *   - Prioritize identification methods by reliability
 *   - Provide fallback mechanisms for user discovery
 *   - Cache discovered user identities for performance
 *
 * Collaborators:
 *   - UserId: User identifier value object
 *   - GitUserResolver: Git configuration reader
 *   - System: System properties and environment
 */
package org.acmsl.bytehot.domain;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Strategy for automatically identifying users from various system sources.
 * Provides intelligent user discovery with multiple fallback mechanisms.
 * @author Claude Code
 * @since 2025-06-19
 */
public final class UserIdentificationStrategy {

    private static final UserIdentificationStrategy INSTANCE = new UserIdentificationStrategy();
    
    private static final String BYTEHOT_USER_ID_PROPERTY = "bytehot.user.id";
    private static final String BYTEHOT_USER_ID_ENV = "BYTEHOT_USER_ID";
    private static final String USER_NAME_PROPERTY = "user.name";
    private static final String USER_EMAIL_ENV = "USER_EMAIL";
    private static final String GIT_AUTHOR_EMAIL_ENV = "GIT_AUTHOR_EMAIL";
    private static final String GIT_COMMITTER_EMAIL_ENV = "GIT_COMMITTER_EMAIL";

    private UserIdentificationStrategy() {
        // Singleton pattern
    }

    /**
     * Gets the singleton instance.
     * @return the singleton UserIdentificationStrategy instance
     */
    @NonNull
    public static UserIdentificationStrategy getInstance() {
        return INSTANCE;
    }

    /**
     * Identifies user from multiple sources in order of preference.
     * @return a UserId from the best available source
     */
    @NonNull
    public UserId identifyUser() {
        // 1. Explicit user ID (highest priority)
        UserId explicitUser = getExplicitUserId();
        if (explicitUser != null) {
            return explicitUser;
        }

        // 2. Environment email variables
        UserId envEmail = getEnvironmentEmail();
        if (envEmail != null) {
            return envEmail;
        }

        // 3. Git configuration
        UserId gitUser = getGitUser();
        if (gitUser != null) {
            return gitUser;
        }

        // 4. System user with hostname
        UserId systemUser = getSystemUser();
        if (systemUser != null) {
            return systemUser;
        }

        // 5. Generate anonymous user (last resort)
        return UserId.anonymous();
    }

    /**
     * Gets explicitly provided user ID from system properties or environment.
     * @return explicit user ID or null if not provided
     */
    @Nullable
    private UserId getExplicitUserId() {
        // Check system property first
        String explicitUserId = System.getProperty(BYTEHOT_USER_ID_PROPERTY);
        if (explicitUserId != null && !explicitUserId.trim().isEmpty()) {
            return UserId.of(explicitUserId.trim());
        }

        // Check environment variable
        explicitUserId = System.getenv(BYTEHOT_USER_ID_ENV);
        if (explicitUserId != null && !explicitUserId.trim().isEmpty()) {
            return UserId.of(explicitUserId.trim());
        }

        return null;
    }

    /**
     * Gets user email from environment variables.
     * @return user ID from environment email or null if not available
     */
    @Nullable
    private UserId getEnvironmentEmail() {
        // Check common email environment variables
        String[] emailEnvVars = {
            USER_EMAIL_ENV,
            GIT_AUTHOR_EMAIL_ENV,
            GIT_COMMITTER_EMAIL_ENV
        };

        for (String envVar : emailEnvVars) {
            String email = System.getenv(envVar);
            if (email != null && !email.trim().isEmpty() && isValidEmail(email)) {
                return UserId.fromEmail(email);
            }
        }

        return null;
    }

    /**
     * Gets user from Git configuration.
     * @return user ID from Git config or null if not available
     */
    @Nullable
    private UserId getGitUser() {
        try {
            GitUserResolver gitResolver = new GitUserResolver();
            String gitEmail = gitResolver.getGitEmail();
            String gitName = gitResolver.getGitName();

            if (gitEmail != null && !gitEmail.trim().isEmpty() && isValidEmail(gitEmail)) {
                return UserId.fromEmail(gitEmail);
            }

            if (gitName != null && !gitName.trim().isEmpty()) {
                return UserId.of(gitName.trim());
            }
        } catch (Exception e) {
            // Git configuration not available or readable
            // This is normal in many environments, so we silently continue
        }

        return null;
    }

    /**
     * Gets user from system information.
     * @return user ID from system or null if not available
     */
    @Nullable
    private UserId getSystemUser() {
        String systemUser = System.getProperty(USER_NAME_PROPERTY);
        if (systemUser != null && !systemUser.trim().isEmpty()) {
            String hostname = getHostname();
            return UserId.fromSystem(systemUser, hostname);
        }

        return null;
    }

    /**
     * Gets the hostname of the current machine.
     * @return hostname or null if not available
     */
    @Nullable
    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Hostname not available, this is okay
            return null;
        }
    }

    /**
     * Validates if a string looks like an email address.
     * @param email the string to validate
     * @return true if it appears to be a valid email
     */
    private boolean isValidEmail(@NonNull final String email) {
        return email.contains("@") && 
               email.indexOf("@") > 0 && 
               email.indexOf("@") < email.length() - 1 &&
               !email.startsWith("@") &&
               !email.endsWith("@");
    }

    /**
     * Helper class for resolving Git user information.
     */
    private static class GitUserResolver {

        /**
         * Gets the Git user email from configuration.
         * @return Git email or null if not available
         */
        @Nullable
        public String getGitEmail() {
            return getGitConfig("user.email");
        }

        /**
         * Gets the Git user name from configuration.
         * @return Git name or null if not available
         */
        @Nullable
        public String getGitName() {
            return getGitConfig("user.name");
        }

        /**
         * Gets a Git configuration value.
         * @param configKey the configuration key
         * @return the configuration value or null if not available
         */
        @Nullable
        private String getGitConfig(@NonNull final String configKey) {
            try {
                // Try git command first
                String value = getGitConfigFromCommand(configKey);
                if (value != null) {
                    return value;
                }

                // Fallback to reading .gitconfig file
                return getGitConfigFromFile(configKey);
            } catch (Exception e) {
                // Git not available or configuration not readable
                return null;
            }
        }

        /**
         * Gets Git config using the git command.
         * @param configKey the configuration key
         * @return the configuration value or null
         */
        @Nullable
        private String getGitConfigFromCommand(@NonNull final String configKey) {
            try {
                ProcessBuilder pb = new ProcessBuilder("git", "config", "--global", configKey);
                Process process = pb.start();
                
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String value = reader.readLine();
                    
                    int exitCode = process.waitFor();
                    if (exitCode == 0 && value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }
            } catch (IOException | InterruptedException e) {
                // Git command not available or failed
            }
            
            return null;
        }

        /**
         * Gets Git config by reading the .gitconfig file.
         * @param configKey the configuration key
         * @return the configuration value or null
         */
        @Nullable
        private String getGitConfigFromFile(@NonNull final String configKey) {
            try {
                Path gitConfigPath = Paths.get(System.getProperty("user.home"), ".gitconfig");
                if (Files.exists(gitConfigPath)) {
                    String content = Files.readString(gitConfigPath);
                    return parseGitConfigValue(content, configKey);
                }
            } catch (IOException e) {
                // File not readable
            }
            
            return null;
        }

        /**
         * Parses a Git configuration file content for a specific key.
         * @param content the file content
         * @param key the configuration key
         * @return the value or null if not found
         */
        @Nullable
        private String parseGitConfigValue(@NonNull final String content, @NonNull final String key) {
            String[] lines = content.split("\n");
            boolean inUserSection = false;
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.equals("[user]")) {
                    inUserSection = true;
                    continue;
                }
                
                if (line.startsWith("[") && !line.equals("[user]")) {
                    inUserSection = false;
                    continue;
                }
                
                if (inUserSection && line.startsWith(key.substring(key.indexOf('.') + 1))) {
                    int equalIndex = line.indexOf('=');
                    if (equalIndex > 0 && equalIndex < line.length() - 1) {
                        return line.substring(equalIndex + 1).trim();
                    }
                }
            }
            
            return null;
        }
    }
}