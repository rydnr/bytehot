package org.acmsl.bytehot.eclipse.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Configuration for ByteHot Eclipse Integration.
 * 
 * Encapsulates all the configuration needed to launch a Java application
 * with ByteHot agent from Eclipse workspace.
 */
public class ProjectConfiguration {
    
    private String mainClass;
    private List<String> sourcePaths;
    private String classpath;
    private List<String> jvmArgs;
    private List<String> programArgs;
    
    public ProjectConfiguration() {
        this.sourcePaths = new ArrayList<>();
        this.jvmArgs = new ArrayList<>();
        this.programArgs = new ArrayList<>();
    }
    
    /**
     * Gets the main class to launch.
     */
    public String getMainClass() {
        return mainClass;
    }
    
    /**
     * Sets the main class to launch.
     * @param mainClass the fully qualified main class name
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    /**
     * Gets the source paths to monitor.
     */
    public List<String> getSourcePaths() {
        return sourcePaths;
    }
    
    /**
     * Sets the source paths to monitor.
     * @param sourcePaths list of source directory paths, null values are converted to empty list
     */
    public void setSourcePaths(List<String> sourcePaths) {
        this.sourcePaths = sourcePaths != null ? sourcePaths : new ArrayList<>();
    }
    
    /**
     * Gets the classpath for the application.
     */
    public String getClasspath() {
        return classpath;
    }
    
    /**
     * Sets the classpath for the application.
     * @param classpath the classpath string for the application
     */
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }
    
    /**
     * Gets the JVM arguments.
     */
    public List<String> getJvmArgs() {
        return jvmArgs;
    }
    
    /**
     * Sets the JVM arguments.
     * @param jvmArgs list of JVM arguments, null values are converted to empty list
     */
    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs != null ? jvmArgs : new ArrayList<>();
    }
    
    /**
     * Gets the program arguments.
     */
    public List<String> getProgramArgs() {
        return programArgs;
    }
    
    /**
     * Sets the program arguments.
     * @param programArgs list of program arguments, null values are converted to empty list
     */
    public void setProgramArgs(List<String> programArgs) {
        this.programArgs = programArgs != null ? programArgs : new ArrayList<>();
    }
    
    /**
     * Adds a JVM argument.
     * @param arg the JVM argument to add, null or empty values are ignored
     */
    public void addJvmArg(String arg) {
        if (arg != null && !arg.trim().isEmpty()) {
            this.jvmArgs.add(arg);
        }
    }
    
    /**
     * Adds a program argument.
     * @param arg the program argument to add, null or empty values are ignored
     */
    public void addProgramArg(String arg) {
        if (arg != null && !arg.trim().isEmpty()) {
            this.programArgs.add(arg);
        }
    }
    
    /**
     * Adds a source path.
     * @param path the source path to add, null or empty values are ignored
     */
    public void addSourcePath(String path) {
        if (path != null && !path.trim().isEmpty()) {
            this.sourcePaths.add(path);
        }
    }
    
    /**
     * Validates the configuration.
     */
    public boolean isValid() {
        return mainClass != null && !mainClass.trim().isEmpty() &&
               classpath != null && !classpath.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "ProjectConfiguration{" +
                "mainClass='" + mainClass + '\'' +
                ", sourcePaths=" + sourcePaths +
                ", classpath='" + classpath + '\'' +
                ", jvmArgs=" + jvmArgs +
                ", programArgs=" + programArgs +
                '}';
    }
}