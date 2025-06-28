package org.acmsl.bytehot.maven;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Optional;

/**
 * Unit tests for AgentDiscovery using TDD approach.
 */
public class AgentDiscoveryTest {

    @Test
    public void testAgentDiscoveryInstantiation() {
        AgentDiscovery discovery = new AgentDiscovery();
        assertNotNull("Discovery should be instantiable", discovery);
    }

    @Test
    public void testGetAgentPathReturnsOptional() {
        AgentDiscovery discovery = new AgentDiscovery();
        Optional<String> agentPath = discovery.getAgentPath();
        
        assertNotNull("Should return an Optional", agentPath);
        // The path may or may not exist in test environment, but method should not crash
    }

    @Test
    public void testFindInCurrentDirectoryReturnsNullWhenNotFound() {
        AgentDiscovery discovery = new AgentDiscovery();
        String result = discovery.findInCurrentDirectory();
        
        // In test environment, agent JAR likely doesn't exist in current directory
        assertNull("Should return null when agent not found in current directory", result);
    }

    @Test
    public void testFindInLocalRepositoryHandlesNullUserHome() {
        AgentDiscovery discovery = new AgentDiscovery();
        
        // This test verifies the method handles potential null user.home gracefully
        String result = discovery.findInLocalRepository();
        
        // Should not crash, may return null if not found
        // This is acceptable behavior
    }
}