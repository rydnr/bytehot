package org.acmsl.bytehot.maven;

import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Analyzes Maven projects to detect main classes, source directories, and build classpaths
 * for ByteHot agent integration.
 */
public class MavenProjectAnalyzer {
    
    protected final MavenProject project;
    protected final MavenSession session;
    
    /**
     * Creates a new Maven project analyzer.
     * @param project the Maven project to analyze
     * @param session the current Maven session
     */
    public MavenProjectAnalyzer(MavenProject project, MavenSession session) {
        this.project = project;
        this.session = session;
    }
    
    /**
     * Detects the main class using multiple strategies.
     * @return the detected main class name
     * @throws Exception if no main class can be found
     */
    public String detectMainClass() throws Exception {
        // Strategy 1: Check Maven exec plugin configuration
        String mainClass = findMainClassInExecPlugin();
        if (mainClass != null) return mainClass;
        
        // Strategy 2: Check Spring Boot plugin configuration
        mainClass = findMainClassInSpringBootPlugin();
        if (mainClass != null) return mainClass;
        
        // Strategy 3: Scan compiled classes for main methods
        mainClass = scanForMainClass();
        if (mainClass != null) return mainClass;
        
        throw new Exception("Could not detect main class. Please specify using -Dbytehot.mainClass=...");
    }
    
    /**
     * Detects source directories for file watching.
     * @return list of source directory paths
     */
    public List<String> detectSourceDirectories() {
        List<String> sourceDirs = new ArrayList<>();
        
        // Add main source directories
        if (project.getBuild().getSourceDirectory() != null) {
            sourceDirs.add(project.getBuild().getSourceDirectory());
        }
        
        // Add test source directories
        if (project.getBuild().getTestSourceDirectory() != null) {
            sourceDirs.add(project.getBuild().getTestSourceDirectory());
        }
        
        // Add additional source roots
        sourceDirs.addAll(project.getCompileSourceRoots());
        sourceDirs.addAll(project.getTestCompileSourceRoots());
        
        return sourceDirs.stream()
            .filter(Objects::nonNull)
            .filter(dir -> new File(dir).exists())
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Builds the complete classpath including project outputs and dependencies.
     * @return list of classpath elements
     * @throws Exception if classpath building fails
     */
    public List<String> buildClasspath() throws Exception {
        List<String> classpathElements = new ArrayList<>();
        
        // Add project's compiled classes
        if (project.getBuild().getOutputDirectory() != null) {
            classpathElements.add(project.getBuild().getOutputDirectory());
        }
        
        if (project.getBuild().getTestOutputDirectory() != null) {
            classpathElements.add(project.getBuild().getTestOutputDirectory());
        }
        
        // Add project dependencies
        for (Artifact artifact : project.getArtifacts()) {
            if (artifact.getFile() != null) {
                classpathElements.add(artifact.getFile().getAbsolutePath());
            }
        }
        
        return classpathElements;
    }
    
    protected String findMainClassInExecPlugin() {
        Plugin execPlugin = project.getPlugin("org.codehaus.mojo:exec-maven-plugin");
        if (execPlugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) execPlugin.getConfiguration();
            if (configuration != null) {
                Xpp3Dom mainClassNode = configuration.getChild("mainClass");
                if (mainClassNode != null) {
                    return mainClassNode.getValue();
                }
            }
        }
        return null;
    }
    
    protected String findMainClassInSpringBootPlugin() {
        Plugin springBootPlugin = project.getPlugin("org.springframework.boot:spring-boot-maven-plugin");
        if (springBootPlugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) springBootPlugin.getConfiguration();
            if (configuration != null) {
                Xpp3Dom mainClassNode = configuration.getChild("mainClass");
                if (mainClassNode != null) {
                    return mainClassNode.getValue();
                }
            }
        }
        return null;
    }
    
    protected String scanForMainClass() {
        try {
            String outputDirectory = project.getBuild().getOutputDirectory();
            if (outputDirectory == null) {
                return null;
            }
            
            Path classesDir = Paths.get(outputDirectory);
            if (!Files.exists(classesDir)) {
                return null;
            }
            
            return Files.walk(classesDir)
                .filter(path -> path.toString().endsWith(".class"))
                .map(this::loadClassAndCheckForMain)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
                
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Loads a class file and checks if it contains a main method.
     * @param classFile the path to the class file to analyze
     * @return the class name if it has a main method, null otherwise
     */
    protected String loadClassAndCheckForMain(Path classFile) {
        try {
            String className = classPathToClassName(classFile);
            Class<?> clazz = Class.forName(className);
            
            // Check for main method
            Method mainMethod = clazz.getMethod("main", String[].class);
            if (Modifier.isStatic(mainMethod.getModifiers()) && 
                Modifier.isPublic(mainMethod.getModifiers())) {
                return className;
            }
            
        } catch (Exception e) {
            // Ignore classes that can't be loaded or don't have main method
        }
        return null;
    }
    
    /**
     * Converts a class file path to a fully qualified class name.
     * @param classFile the path to the class file
     * @return the fully qualified class name
     */
    protected String classPathToClassName(Path classFile) {
        String outputDirectory = project.getBuild().getOutputDirectory();
        String relativePath = Paths.get(outputDirectory)
            .relativize(classFile).toString();
        return relativePath.substring(0, relativePath.length() - 6) // Remove .class
            .replace(File.separatorChar, '.');
    }
}