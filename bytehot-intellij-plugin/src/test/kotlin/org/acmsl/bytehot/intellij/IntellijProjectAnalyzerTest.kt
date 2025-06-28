package org.acmsl.bytehot.intellij

import org.acmsl.bytehot.intellij.analysis.IntellijProjectAnalyzer
import org.junit.Test
import kotlin.test.*
import java.io.File
import java.io.FileWriter

/**
 * Unit tests for IntellijProjectAnalyzer using TDD approach.
 */
class IntellijProjectAnalyzerTest {
    
    @Test
    fun testAnalyzerInstantiation() {
        val analyzer = IntellijProjectAnalyzer()
        assertNotNull(analyzer, "Analyzer should be instantiable")
    }
    
    @Test
    fun testAnalyzeCurrentProject() {
        val analyzer = IntellijProjectAnalyzer()
        val config = analyzer.analyzeCurrentProject()
        
        assertNotNull(config, "Should return configuration")
        assertNotNull(config.sourcePaths, "Should have source paths list")
        assertTrue(config.sourcePaths is List, "Source paths should be a list")
    }
    
    @Test
    fun testAnalyzeProjectWithNonexistentPath() {
        val analyzer = IntellijProjectAnalyzer()
        val config = analyzer.analyzeProject("/nonexistent/path")
        
        assertNotNull(config, "Should return configuration even for nonexistent path")
        assertNull(config.mainClass, "Should not find main class in nonexistent path")
        assertNull(config.classpath, "Should not find classpath in nonexistent path")
    }
    
    @Test
    fun testDetectSourcePathsWithStandardMavenLayout() {
        val tempDir = createTempDirectory()
        try {
            // Create standard Maven directory structure
            File(tempDir, "src/main/java").mkdirs()
            File(tempDir, "src/test/java").mkdirs()
            File(tempDir, "src/main/kotlin").mkdirs()
            
            val analyzer = TestableIntellijProjectAnalyzer()
            val sourcePaths = analyzer.detectSourcePaths(tempDir)
            
            assertTrue(sourcePaths.size >= 3, "Should find at least 3 source directories")
            assertTrue(sourcePaths.any { it.contains("src/main/java") }, "Should find main java sources")
            assertTrue(sourcePaths.any { it.contains("src/test/java") }, "Should find test java sources")
            assertTrue(sourcePaths.any { it.contains("src/main/kotlin") }, "Should find main kotlin sources")
            
        } finally {
            tempDir.deleteRecursively()
        }
    }
    
    @Test
    fun testBuildClasspathWithTargetDirectory() {
        val tempDir = createTempDirectory()
        try {
            // Create target directory structure
            val classesDir = File(tempDir, "target/classes")
            classesDir.mkdirs()
            File(classesDir, "dummy.class").createNewFile()
            
            val testClassesDir = File(tempDir, "target/test-classes")
            testClassesDir.mkdirs()
            File(testClassesDir, "test.class").createNewFile()
            
            val analyzer = TestableIntellijProjectAnalyzer()
            val classpath = analyzer.buildClasspath(tempDir)
            
            assertNotNull(classpath, "Should build classpath")
            assertTrue(classpath.contains("target/classes"), "Should include classes directory")
            assertTrue(classpath.contains("target/test-classes"), "Should include test classes directory")
            
        } finally {
            tempDir.deleteRecursively()
        }
    }
    
    @Test
    fun testFindMainClassInPomXmlWithExecPlugin() {
        val tempDir = createTempDirectory()
        try {
            val pomFile = File(tempDir, "pom.xml")
            FileWriter(pomFile).use { writer ->
                writer.write("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <project>
                        <build>
                            <plugins>
                                <plugin>
                                    <groupId>org.codehaus.mojo</groupId>
                                    <artifactId>exec-maven-plugin</artifactId>
                                    <configuration>
                                        <mainClass>com.example.MainApp</mainClass>
                                    </configuration>
                                </plugin>
                            </plugins>
                        </build>
                    </project>
                """.trimIndent())
            }
            
            val analyzer = TestableIntellijProjectAnalyzer()
            val mainClass = analyzer.findMainClassInPomXml(tempDir)
            
            assertEquals("com.example.MainApp", mainClass, "Should extract main class from exec plugin")
            
        } finally {
            tempDir.deleteRecursively()
        }
    }
    
    @Test
    fun testFindMainClassInPomXmlWithProperty() {
        val tempDir = createTempDirectory()
        try {
            val pomFile = File(tempDir, "pom.xml")
            FileWriter(pomFile).use { writer ->
                writer.write("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <project>
                        <properties>
                            <exec.mainClass>com.example.PropertyApp</exec.mainClass>
                        </properties>
                    </project>
                """.trimIndent())
            }
            
            val analyzer = TestableIntellijProjectAnalyzer()
            val mainClass = analyzer.findMainClassInPomXml(tempDir)
            
            assertEquals("com.example.PropertyApp", mainClass, "Should extract main class from property")
            
        } finally {
            tempDir.deleteRecursively()
        }
    }
    
    @Test
    fun testExtractClassNameFromJavaFile() {
        val tempDir = createTempDirectory()
        try {
            val sourceDir = File(tempDir, "src/main/java/com/example")
            sourceDir.mkdirs()
            
            val javaFile = File(sourceDir, "TestClass.java")
            FileWriter(javaFile).use { writer ->
                writer.write("""
                    package com.example;
                    
                    public class TestClass {
                        public static void main(String[] args) {
                            System.out.println("Hello World");
                        }
                    }
                """.trimIndent())
            }
            
            val analyzer = TestableIntellijProjectAnalyzer()
            val className = analyzer.extractClassNameFromFile(javaFile, File(tempDir, "src/main/java"))
            
            assertEquals("com.example.TestClass", className, "Should extract fully qualified class name")
            
        } finally {
            tempDir.deleteRecursively()
        }
    }
    
    private fun createTempDirectory(): File {
        val tempDir = kotlin.io.path.createTempDirectory("bytehot-test").toFile()
        tempDir.deleteOnExit()
        return tempDir
    }
}

/**
 * Testable version of IntellijProjectAnalyzer that exposes protected methods.
 */
class TestableIntellijProjectAnalyzer : IntellijProjectAnalyzer() {
    
    public override fun detectSourcePaths(projectDir: File): List<String> {
        return super.detectSourcePaths(projectDir)
    }
    
    public override fun buildClasspath(projectDir: File): String? {
        return super.buildClasspath(projectDir)
    }
    
    public override fun findMainClassInPomXml(projectDir: File): String? {
        return super.findMainClassInPomXml(projectDir)
    }
    
    public override fun extractClassNameFromFile(file: File, sourceRoot: File): String? {
        return super.extractClassNameFromFile(file, sourceRoot)
    }
}