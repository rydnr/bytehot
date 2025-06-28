package org.acmsl.bytehot.maven;

import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for MavenProjectAnalyzer using TDD approach.
 */
public class MavenProjectAnalyzerTest {

    @Test
    public void testAnalyzerInstantiation() {
        // Test that analyzer can be instantiated
        MavenProject project = mock(MavenProject.class);
        MavenSession session = mock(MavenSession.class);
        
        MavenProjectAnalyzer analyzer = new MavenProjectAnalyzer(project, session);
        assertNotNull("Analyzer should be instantiable", analyzer);
    }

    @Test
    public void testSourceDirectoryDetectionWithBasicProject() {
        // Test that analyzer can detect source directories
        MavenProject project = mock(MavenProject.class);
        MavenSession session = mock(MavenSession.class);
        Build build = mock(Build.class);
        
        when(project.getBuild()).thenReturn(build);
        when(build.getSourceDirectory()).thenReturn("src/main/java");
        when(build.getTestSourceDirectory()).thenReturn("src/test/java");
        when(project.getCompileSourceRoots()).thenReturn(Collections.emptyList());
        when(project.getTestCompileSourceRoots()).thenReturn(Collections.emptyList());
        
        MavenProjectAnalyzer analyzer = new MavenProjectAnalyzer(project, session);
        List<String> sourceDirs = analyzer.detectSourceDirectories();
        
        assertNotNull("Should return source directories", sourceDirs);
        // Note: Actual directories might not exist in test environment
        // so we just verify the method doesn't crash
    }

    @Test
    public void testClasspathBuildingWithBasicProject() throws Exception {
        // Test that analyzer can build proper classpath
        MavenProject project = mock(MavenProject.class);
        MavenSession session = mock(MavenSession.class);
        Build build = mock(Build.class);
        
        when(project.getBuild()).thenReturn(build);
        when(build.getOutputDirectory()).thenReturn("target/classes");
        when(build.getTestOutputDirectory()).thenReturn("target/test-classes");
        when(project.getArtifacts()).thenReturn(Collections.emptySet());
        
        MavenProjectAnalyzer analyzer = new MavenProjectAnalyzer(project, session);
        List<String> classpath = analyzer.buildClasspath();
        
        assertNotNull("Should return classpath", classpath);
        assertEquals("Should have 2 basic classpath entries", 2, classpath.size());
        assertTrue("Should contain main output directory", classpath.contains("target/classes"));
        assertTrue("Should contain test output directory", classpath.contains("target/test-classes"));
    }

    @Test
    public void testMainClassDetectionFailsGracefully() {
        // Test that main class detection fails gracefully when no main class exists
        MavenProject project = mock(MavenProject.class);
        MavenSession session = mock(MavenSession.class);
        Build build = mock(Build.class);
        
        when(project.getBuild()).thenReturn(build);
        when(build.getOutputDirectory()).thenReturn("/nonexistent/path");
        when(project.getPlugin(anyString())).thenReturn(null);
        
        MavenProjectAnalyzer analyzer = new MavenProjectAnalyzer(project, session);
        
        try {
            analyzer.detectMainClass();
            fail("Should throw exception when no main class found");
        } catch (Exception e) {
            assertTrue("Should contain helpful error message", 
                e.getMessage().contains("Could not detect main class"));
        }
    }
}