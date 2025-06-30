package org.acmsl.bytehot.maven;

import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

/**
 * Integration tests for ByteHotMojo using TDD approach.
 */
public class ByteHotMojoIntegrationTest {

    @Test
    public void testMojoExecutionWithDryRun() throws Exception {
        // Test complete mojo execution in dry run mode
        ByteHotMojo mojo = new ByteHotMojo();
        
        // Set up minimal project structure
        MavenProject project = mock(MavenProject.class);
        MavenSession session = mock(MavenSession.class);
        Build build = mock(Build.class);
        
        when(project.getBuild()).thenReturn(build);
        when(build.getSourceDirectory()).thenReturn("src/main/java");
        when(build.getTestSourceDirectory()).thenReturn("src/test/java");
        when(build.getOutputDirectory()).thenReturn("/nonexistent/target/classes");
        when(build.getTestOutputDirectory()).thenReturn("/nonexistent/target/test-classes");
        when(project.getCompileSourceRoots()).thenReturn(Collections.emptyList());
        when(project.getTestCompileSourceRoots()).thenReturn(Collections.emptyList());
        when(project.getArtifacts()).thenReturn(Collections.emptySet());
        when(project.getPlugin(anyString())).thenReturn(null);
        
        // Set mojo fields using reflection to avoid complex setup
        setField(mojo, "project", project);
        setField(mojo, "session", session);
        setField(mojo, "enabled", true);
        setField(mojo, "dryRun", true);
        setField(mojo, "mainClass", "com.example.TestApp");
        setField(mojo, "agentPath", "/mock/agent.jar");
        setField(mojo, "verbose", false);
        
        // Execute mojo - should not throw exception in dry run mode
        try {
            mojo.execute();
            // Test passes if no exception is thrown
        } catch (Exception e) {
            fail("Dry run execution should not fail: " + e.getMessage());
        }
    }

    @Test
    public void testMojoValidationWithInvalidConfiguration() throws Exception {
        ByteHotMojo mojo = new ByteHotMojo();
        
        // Set invalid configuration - null project should fail validation
        setField(mojo, "enabled", true);
        setField(mojo, "project", null);
        
        try {
            mojo.execute();
            fail("Should throw exception for null project");
        } catch (Exception e) {
            // The validation error gets wrapped in a MojoExecutionException
            // Check both the main message and the cause message
            String mainMessage = e.getMessage() != null ? e.getMessage() : "";
            String causeMessage = e.getCause() != null && e.getCause().getMessage() != null ? 
                e.getCause().getMessage() : "";
            
            assertTrue("Should contain validation error message. Got: '" + mainMessage + 
                      "' with cause: '" + causeMessage + "'", 
                mainMessage.contains("Failed to start application") ||
                causeMessage.contains("Maven project is required") ||
                causeMessage.contains("project") ||
                causeMessage.contains("null"));
        }
    }

    @Test
    public void testMojoExecutionWhenDisabled() throws Exception {
        ByteHotMojo mojo = new ByteHotMojo();
        
        setField(mojo, "enabled", false);
        
        // Should execute without error when disabled
        mojo.execute();
        // Test passes if no exception is thrown
    }

    // Helper method to set private fields using reflection
    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}