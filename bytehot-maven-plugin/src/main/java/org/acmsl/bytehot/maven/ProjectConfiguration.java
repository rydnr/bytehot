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

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<String> getWatchPaths() {
        return watchPaths;
    }

    public void setWatchPaths(List<String> watchPaths) {
        this.watchPaths = watchPaths != null ? watchPaths : new ArrayList<>();
    }

    public List<String> getClasspath() {
        return classpath;
    }

    public void setClasspath(List<String> classpath) {
        this.classpath = classpath != null ? classpath : new ArrayList<>();
    }

    public List<String> getJvmArgs() {
        return jvmArgs;
    }

    public void setJvmArgs(List<String> jvmArgs) {
        this.jvmArgs = jvmArgs != null ? jvmArgs : new ArrayList<>();
    }
}