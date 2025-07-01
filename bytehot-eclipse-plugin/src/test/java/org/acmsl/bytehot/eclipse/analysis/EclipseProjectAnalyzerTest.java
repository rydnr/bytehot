package org.acmsl.bytehot.eclipse.analysis;

import org.acmsl.bytehot.eclipse.ByteHotPlugin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Test suite for EclipseProjectAnalyzer.
 */
public class EclipseProjectAnalyzerTest {
    
    private Path tempDir;
    private EclipseProjectAnalyzer analyzer;
    private ByteHotPlugin plugin;
    
    @Before
    public void setUp() throws IOException {
        // Create and initialize plugin instance
        plugin = new ByteHotPlugin();
        plugin.initializePlugin();
        
        tempDir = Files.createTempDirectory("eclipse-test");
        analyzer = new EclipseProjectAnalyzer(tempDir.toString(), "test-project");
    }
    
    @Test
    public void testSpringBootMainClassDetection() throws IOException {
        // Given: Spring Boot application structure
        createSpringBootProject();
        
        // When: Detect main class
        Optional<String> mainClass = analyzer.findSpringBootMainClass();
        
        // Then: Should find Spring Boot main class
        assertTrue("Should find Spring Boot main class", mainClass.isPresent());
        assertEquals("Should find correct main class", "com.example.Application", mainClass.get());
    }
    
    @Test
    public void testMainMethodDetection() throws IOException {
        // Given: Java class with main method
        createJavaClassWithMain("com.example", "MainApp", 
            "public static void main(String[] args) { System.out.println(\"Hello\"); }");
        
        // When: Scan for main class
        Optional<String> mainClass = analyzer.scanForMainClass();
        
        // Then: Should find main class
        assertTrue("Should find main class", mainClass.isPresent());
        assertEquals("Should find correct main class", "com.example.MainApp", mainClass.get());
    }
    
    @Test
    public void testApplicationClassDetection() throws IOException {
        // Given: Class named Application
        createJavaClass("com.example", "Application", "public class Application {}");
        
        // When: Search for application class
        Optional<String> appClass = analyzer.findApplicationClass();
        
        // Then: Should find Application class
        assertTrue("Should find Application class", appClass.isPresent());
        assertEquals("Should find correct class", "com.example.Application", appClass.get());
    }
    
    @Test
    public void testSourcePathDetection() throws IOException {
        // Given: Standard Maven project structure
        createMavenProjectStructure();
        
        // When: Detect source paths
        List<String> sourcePaths = analyzer.detectSourcePaths();
        
        // Then: Should find source directories
        assertFalse("Should find source paths", sourcePaths.isEmpty());
        assertTrue("Should contain main java source", 
                  sourcePaths.stream().anyMatch(path -> path.contains("src/main/java")));
    }
    
    @Test
    public void testClasspathBuilding() throws IOException {
        // Given: Project with output directories
        createOutputDirectories();
        
        // When: Build classpath
        List<String> classpath = analyzer.buildClasspath();
        
        // Then: Should include output directories
        assertFalse("Should have classpath entries", classpath.isEmpty());
        assertTrue("Should contain target/classes", 
                  classpath.stream().anyMatch(path -> path.contains("target/classes")));
    }
    
    @Test
    public void testProjectAnalysis() throws IOException {
        // Given: Complete project structure
        createCompleteProject();
        
        // When: Analyze project
        ProjectConfiguration config = analyzer.analyzeProject();
        
        // Then: Should have valid configuration
        assertTrue("Configuration should be valid", config.isValid());
        assertNotNull("Should have main class", config.getMainClass());
        assertNotNull("Should have classpath", config.getClasspath());
        assertFalse("Should have source paths", config.getSourcePaths().isEmpty());
        assertFalse("Should have JVM args", config.getJvmArgs().isEmpty());
    }
    
    @Test
    public void testMainClassDetectionPriority() throws IOException {
        // Given: Project with both Spring Boot and regular main classes
        createSpringBootProject();
        createJavaClassWithMain("com.example", "RegularMain", 
            "public static void main(String[] args) {}");
        
        // When: Detect main class (should prefer Spring Boot)
        String mainClass = analyzer.detectMainClass();
        
        // Then: Should prefer Spring Boot main class
        assertEquals("Should prefer Spring Boot main class", "com.example.Application", mainClass);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testMainClassDetectionFailure() {
        // Given: Project with no main classes
        // When: Try to detect main class
        analyzer.detectMainClass();
        
        // Then: Should throw exception
    }
    
    @Test
    public void testLibDirectoryInClasspath() throws IOException {
        // Given: Project with lib directory containing JARs
        createLibDirectory();
        
        // When: Build classpath
        List<String> classpath = analyzer.buildClasspath();
        
        // Then: Should include JAR files from lib directory
        assertTrue("Should include library JAR", 
                  classpath.stream().anyMatch(path -> path.contains("library.jar")));
    }
    
    @Test
    public void testJvmArgsBuilding() throws IOException {
        // Given: Complete project with agent available
        createCompleteProject();
        // Mock agent availability
        System.setProperty("user.home", tempDir.toString());
        createAgentJar();
        
        // When: Build JVM args
        List<String> jvmArgs = analyzer.buildJvmArgs();
        
        // Then: Should include ByteHot agent and watch paths
        assertTrue("Should include agent argument", 
                  jvmArgs.stream().anyMatch(arg -> arg.startsWith("-javaagent:")));
        assertTrue("Should include watch paths argument", 
                  jvmArgs.stream().anyMatch(arg -> arg.startsWith("-Dbytehot.watch.paths=")));
    }
    
    // Helper methods for test setup
    
    private void createSpringBootProject() throws IOException {
        String springBootMainClass = """
            package com.example;
            
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            
            @SpringBootApplication
            public class Application {
                public static void main(String[] args) {
                    SpringApplication.run(Application.class, args);
                }
            }
            """;
        
        createJavaClass("com.example", "Application", springBootMainClass);
    }
    
    private void createJavaClassWithMain(String packageName, String className, String mainMethod) throws IOException {
        String classContent = String.format("""
            package %s;
            
            public class %s {
                %s
            }
            """, packageName, className, mainMethod);
        
        createJavaClass(packageName, className, classContent);
    }
    
    private void createJavaClass(String packageName, String className, String content) throws IOException {
        Path packageDir = tempDir.resolve("src/main/java").resolve(packageName.replace(".", "/"));
        Files.createDirectories(packageDir);
        Path classFile = packageDir.resolve(className + ".java");
        Files.writeString(classFile, content);
    }
    
    private void createMavenProjectStructure() throws IOException {
        Files.createDirectories(tempDir.resolve("src/main/java"));
        Files.createDirectories(tempDir.resolve("src/main/resources"));
        Files.createDirectories(tempDir.resolve("src/test/java"));
    }
    
    private void createOutputDirectories() throws IOException {
        Files.createDirectories(tempDir.resolve("target/classes"));
        Files.createDirectories(tempDir.resolve("build/classes/java/main"));
        Files.createDirectories(tempDir.resolve("bin"));
    }
    
    private void createLibDirectory() throws IOException {
        Path libDir = tempDir.resolve("lib");
        Files.createDirectories(libDir);
        Files.createFile(libDir.resolve("library.jar"));
        Files.createFile(libDir.resolve("another-lib.jar"));
    }
    
    private void createCompleteProject() throws IOException {
        createMavenProjectStructure();
        createOutputDirectories();
        createSpringBootProject();
    }
    
    private void createAgentJar() throws IOException {
        Path m2Dir = tempDir.resolve(".m2/repository/org/acmsl/bytehot-application/latest-SNAPSHOT");
        Files.createDirectories(m2Dir);
        Files.createFile(m2Dir.resolve("bytehot-application-latest-SNAPSHOT-agent.jar"));
    }
}