package org.acmsl.bytehot.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Discovers ByteHot agent JAR file location using various strategies.
 */
public class AgentDiscovery {
    
    protected static final String AGENT_JAR_NAME = "bytehot-application-latest-SNAPSHOT-agent.jar";
    
    /**
     * Discovers the ByteHot agent JAR path.
     * @return optional path to agent JAR
     */
    public Optional<String> getAgentPath() {
        // Strategy 1: Check in local Maven repository
        String localRepoPath = findInLocalRepository();
        if (localRepoPath != null) {
            return Optional.of(localRepoPath);
        }
        
        // Strategy 2: Check in project target directory
        String projectPath = findInProjectTarget();
        if (projectPath != null) {
            return Optional.of(projectPath);
        }
        
        // Strategy 3: Check in current directory
        String currentDirPath = findInCurrentDirectory();
        if (currentDirPath != null) {
            return Optional.of(currentDirPath);
        }
        
        return Optional.empty();
    }
    
    protected String findInLocalRepository() {
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            Path agentPath = Paths.get(userHome, ".m2", "repository", "org", "acmsl", 
                "bytehot-application", "latest-SNAPSHOT", AGENT_JAR_NAME);
            if (Files.exists(agentPath)) {
                return agentPath.toString();
            }
        }
        return null;
    }
    
    protected String findInProjectTarget() {
        // Check if we're in a ByteHot project structure
        Path agentPath = Paths.get("bytehot-application", "target", AGENT_JAR_NAME);
        if (Files.exists(agentPath)) {
            return agentPath.toString();
        }
        
        // Check parent directory
        agentPath = Paths.get("..", "bytehot-application", "target", AGENT_JAR_NAME);
        if (Files.exists(agentPath)) {
            return agentPath.toString();
        }
        
        return null;
    }
    
    protected String findInCurrentDirectory() {
        Path agentPath = Paths.get(AGENT_JAR_NAME);
        if (Files.exists(agentPath)) {
            return agentPath.toString();
        }
        return null;
    }
}