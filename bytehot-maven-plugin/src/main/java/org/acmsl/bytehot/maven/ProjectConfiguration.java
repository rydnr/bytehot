package org.acmsl.bytehot.maven;

import java.util.List;
import java.util.ArrayList;

/**
 * Configuration object holding project analysis results for ByteHot plugin.
 */
public class ProjectConfiguration {
    
    protected String mainClass;
    protected List<String> watchPaths;
    protected List<String> classpath;
    protected List<String> jvmArgs;

    public ProjectConfiguration() {
        this.watchPaths = new ArrayList<>();
        this.classpath = new ArrayList<>();
        this.jvmArgs = new ArrayList<>();
    }

    public String getMainClass() {
        return mainClass;
    }

    /**
     * Sets the main class for the project.
     * @param mainClass the fully qualified main class name
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<String> getWatchPaths() {
        return watchPaths;
    }

    /**
     * Sets the list of paths to watch for changes.
     * @param watchPaths list of directory paths to monitor, null values are converted to empty list
     */
    public void setWatchPaths(List<String> watchPaths) {
        this.watchPaths = watchPaths != null ? watchPaths : new ArrayList<>();
    }

    public List<String> getClasspath() {
        return classpath;
    }

    /**
     * Sets the classpath entries for the project.
     * @param classpath list of classpath entries, null values are converted to empty list
     */
    public void setClasspath(List<String> classpath) {
        this.classpath = classpath != null ? classpath : new ArrayList<>();
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    /**
     * Sets the JVM arguments for the project.
     * @param jvmArgs list of JVM arguments, null values are converted to empty list
     */
    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs != null ? jvmArgs : new ArrayList<>();
    }
}