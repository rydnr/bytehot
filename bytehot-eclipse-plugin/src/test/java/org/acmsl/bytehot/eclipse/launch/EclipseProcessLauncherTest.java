package org.acmsl.bytehot.eclipse.launch;

import org.acmsl.bytehot.eclipse.analysis.ProjectConfiguration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

/**
 * Test suite for EclipseProcessLauncher.
 */
public class EclipseProcessLauncherTest {
    
    private EclipseProcessLauncher launcher;
    private ProjectConfiguration config;
    
    @Before
    public void setUp() {
        launcher = new EclipseProcessLauncher();
        config = createTestConfiguration();
    }
    
    @Test
    public void testBuildLaunchCommand() {
        // When: Build launch command
        String[] command = launcher.buildLaunchCommand(config);
        
        // Then: Should include all necessary components
        assertTrue("Should start with java", command[0].contains("java"));
        assertTrue("Should include agent argument", 
                  Arrays.stream(command).anyMatch(arg -> arg.startsWith("-javaagent:")));
        assertTrue("Should include classpath", 
                  Arrays.asList(command).contains("-cp"));
        assertTrue("Should include main class", 
                  Arrays.asList(command).contains("com.example.Application"));
    }
    
    @Test
    public void testBuildLaunchCommandWithJvmArgs() {
        // Given: Configuration with JVM arguments
        config.addJvmArg("-Xmx512m");
        config.addJvmArg("-Dspring.profiles.active=dev");
        
        // When: Build launch command
        String[] command = launcher.buildLaunchCommand(config);
        
        // Then: Should include JVM arguments
        assertTrue("Should include Xmx argument", 
                  Arrays.asList(command).contains("-Xmx512m"));
        assertTrue("Should include system property", 
                  Arrays.asList(command).contains("-Dspring.profiles.active=dev"));
    }
    
    @Test
    public void testBuildLaunchCommandWithProgramArgs() {
        // Given: Configuration with program arguments
        config.addProgramArg("--debug");
        config.addProgramArg("--port=8080");
        
        // When: Build launch command
        String[] command = launcher.buildLaunchCommand(config);
        
        // Then: Should include program arguments at the end
        List<String> commandList = Arrays.asList(command);
        assertTrue("Should include debug argument", commandList.contains("--debug"));
        assertTrue("Should include port argument", commandList.contains("--port=8080"));
        
        // Program args should come after main class
        int mainClassIndex = commandList.indexOf("com.example.Application");
        int debugIndex = commandList.indexOf("--debug");
        assertTrue("Program args should come after main class", debugIndex > mainClassIndex);
    }
    
    @Test
    public void testBuildLaunchCommandWithoutClasspath() {
        // Given: Configuration without classpath
        config.setClasspath(null);
        
        // When: Build launch command
        String[] command = launcher.buildLaunchCommand(config);
        
        // Then: Should not include classpath arguments
        assertFalse("Should not include -cp", Arrays.asList(command).contains("-cp"));
    }
    
    @Test
    public void testGetJavaExecutable() {
        // When: Get Java executable
        String javaExe = launcher.getJavaExecutable();
        
        // Then: Should return valid executable path
        assertNotNull("Java executable should not be null", javaExe);
        assertTrue("Should contain java", javaExe.contains("java"));
    }
    
    @Test
    public void testGetDryRunCommand() {
        // When: Get dry run command
        String[] command = launcher.getDryRunCommand(config);
        
        // Then: Should return same as build launch command
        String[] buildCommand = launcher.buildLaunchCommand(config);
        assertArrayEquals("Dry run command should match build command", buildCommand, command);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testLaunchWithInvalidConfiguration() throws Exception {
        // Given: Invalid configuration
        ProjectConfiguration invalidConfig = new ProjectConfiguration();
        // mainClass is null, making it invalid
        
        // When: Try to launch
        launcher.launchWithAgent(invalidConfig);
        
        // Then: Should throw exception
    }
    
    @Test
    public void testIsWindows() {
        // When: Check if running on Windows
        boolean isWindows = launcher.isWindows();
        
        // Then: Should match actual OS
        String osName = System.getProperty("os.name").toLowerCase();
        assertEquals("Should correctly detect Windows", 
                    osName.contains("windows"), isWindows);
    }
    
    @Test
    public void testCommandStructure() {
        // When: Build command
        String[] command = launcher.buildLaunchCommand(config);
        
        // Then: Should have correct structure
        assertTrue("Should have minimum number of arguments", command.length >= 3);
        
        // Find positions of key elements
        int javaIndex = 0; // Should be first
        int cpIndex = findIndex(command, "-cp");
        int mainClassIndex = findIndex(command, "com.example.Application");
        
        assertTrue("Java should be first", command[javaIndex].contains("java"));
        if (cpIndex >= 0) {
            assertTrue("Classpath should come before main class", cpIndex < mainClassIndex);
        }
        assertTrue("Main class should be present", mainClassIndex >= 0);
    }
    
    // Helper methods
    
    private ProjectConfiguration createTestConfiguration() {
        ProjectConfiguration config = new ProjectConfiguration();
        config.setMainClass("com.example.Application");
        config.setClasspath("/path/to/classes:/path/to/libs/library.jar");
        config.addJvmArg("-javaagent:/path/to/bytehot-agent.jar");
        config.addSourcePath("/path/to/src/main/java");
        return config;
    }
    
    private int findIndex(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (target.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }
}