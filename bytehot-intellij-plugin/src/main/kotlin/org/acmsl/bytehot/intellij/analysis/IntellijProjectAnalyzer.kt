package org.acmsl.bytehot.intellij.analysis

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * IntelliJ Project Analyzer for ByteHot plugin.
 * 
 * Analyzes IntelliJ IDEA projects to extract configuration needed
 * for ByteHot live mode activation. Provides automatic detection
 * of main classes, classpath building, and project structure analysis.
 * 
 * Detection strategies:
 * 1. Scan for classes with main method in source directories
 * 2. Check for Spring Boot applications
 * 3. Look for common application patterns
 * 4. Build classpath from project outputs and dependencies
 */
open class IntellijProjectAnalyzer {
    
    /**
     * Analyzes the current IntelliJ project.
     * @return ProjectConfiguration with detected settings
     */
    fun analyzeCurrentProject(): ProjectConfiguration {
        val currentDir = System.getProperty("user.dir")
        return analyzeProject(currentDir)
    }
    
    /**
     * Analyzes a specific project directory.
     * @param projectPath path to the project root
     * @return ProjectConfiguration with detected settings
     */
    fun analyzeProject(projectPath: String): ProjectConfiguration {
        val projectDir = File(projectPath)
        
        val mainClass = detectMainClass(projectDir)
        val classpath = buildClasspath(projectDir)
        val sourcePaths = detectSourcePaths(projectDir)
        
        return ProjectConfiguration(
            mainClass = mainClass,
            classpath = classpath,
            sourcePaths = sourcePaths,
            jvmArgs = emptyList(),
            programArgs = emptyList()
        )
    }
    
    /**
     * Detects the main class for the project.
     * Uses multiple strategies for comprehensive detection.
     */
    protected fun detectMainClass(projectDir: File): String? {
        // Strategy 1: Look for pom.xml with exec plugin configuration
        val pomXmlMainClass = findMainClassInPomXml(projectDir)
        if (pomXmlMainClass != null) {
            return pomXmlMainClass
        }
        
        // Strategy 2: Look for Spring Boot application
        val springBootMainClass = findSpringBootMainClass(projectDir)
        if (springBootMainClass != null) {
            return springBootMainClass
        }
        
        // Strategy 3: Scan source directories for main methods
        return scanForMainClass(projectDir)
    }
    
    /**
     * Searches for main class configuration in pom.xml.
     */
    open protected fun findMainClassInPomXml(projectDir: File): String? {
        val pomFile = File(projectDir, "pom.xml")
        if (!pomFile.exists()) return null
        
        try {
            val content = pomFile.readText()
            
            // Look for exec-maven-plugin mainClass configuration
            val execPluginRegex = """<groupId>org\.codehaus\.mojo</groupId>\s*<artifactId>exec-maven-plugin</artifactId>.*?<mainClass>([^<]+)</mainClass>""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val execMatch = execPluginRegex.find(content)
            if (execMatch != null) {
                return execMatch.groupValues[1].trim()
            }
            
            // Look for properties-based main class
            val propertyRegex = """<exec\.mainClass>([^<]+)</exec\.mainClass>""".toRegex()
            val propertyMatch = propertyRegex.find(content)
            if (propertyMatch != null) {
                return propertyMatch.groupValues[1].trim()
            }
            
        } catch (e: Exception) {
            // Ignore file reading errors
        }
        
        return null
    }
    
    /**
     * Searches for Spring Boot main class.
     */
    protected fun findSpringBootMainClass(projectDir: File): String? {
        val sourceDirs = listOf(
            File(projectDir, "src/main/java"),
            File(projectDir, "src/main/kotlin")
        )
        
        for (sourceDir in sourceDirs) {
            if (!sourceDir.exists()) continue
            
            val springBootClass = findSpringBootClassInDirectory(sourceDir)
            if (springBootClass != null) {
                return springBootClass
            }
        }
        
        return null
    }
    
    /**
     * Recursively searches for Spring Boot application class.
     */
    protected fun findSpringBootClassInDirectory(dir: File): String? {
        if (!dir.isDirectory) return null
        
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                val result = findSpringBootClassInDirectory(file)
                if (result != null) return result
            } else if (file.name.endsWith(".java") || file.name.endsWith(".kt")) {
                try {
                    val content = file.readText()
                    if (content.contains("@SpringBootApplication") && content.contains("public static void main")) {
                        return extractClassNameFromFile(file, dir.parentFile.parentFile.parentFile)
                    }
                } catch (e: Exception) {
                    // Ignore file reading errors
                }
            }
        }
        
        return null
    }
    
    /**
     * Scans source directories for classes with main method.
     */
    protected fun scanForMainClass(projectDir: File): String? {
        val sourceDirs = listOf(
            File(projectDir, "src/main/java"),
            File(projectDir, "src/main/kotlin")
        )
        
        for (sourceDir in sourceDirs) {
            if (!sourceDir.exists()) continue
            
            val mainClass = scanDirectoryForMainClass(sourceDir)
            if (mainClass != null) {
                return mainClass
            }
        }
        
        return null
    }
    
    /**
     * Recursively scans directory for main method.
     */
    protected fun scanDirectoryForMainClass(dir: File): String? {
        if (!dir.isDirectory) return null
        
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                val result = scanDirectoryForMainClass(file)
                if (result != null) return result
            } else if (file.name.endsWith(".java") || file.name.endsWith(".kt")) {
                try {
                    val content = file.readText()
                    if (content.contains("public static void main") || content.contains("fun main")) {
                        return extractClassNameFromFile(file, dir.parentFile.parentFile.parentFile)
                    }
                } catch (e: Exception) {
                    // Ignore file reading errors
                }
            }
        }
        
        return null
    }
    
    /**
     * Extracts fully qualified class name from source file.
     */
    open protected fun extractClassNameFromFile(file: File, sourceRoot: File): String? {
        try {
            val content = file.readText()
            
            // Extract package name
            val packageRegex = """package\s+([^;]+);?""".toRegex()
            val packageMatch = packageRegex.find(content)
            val packageName = packageMatch?.groupValues?.get(1)?.trim()
            
            // Extract class name from filename
            val className = file.nameWithoutExtension
            
            return if (packageName != null) {
                "$packageName.$className"
            } else {
                className
            }
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Builds classpath from project structure.
     */
    open protected fun buildClasspath(projectDir: File): String? {
        val classpathEntries = mutableListOf<String>()
        
        // Add compiled classes directory
        val classesDir = File(projectDir, "target/classes")
        if (classesDir.exists()) {
            classpathEntries.add(classesDir.absolutePath)
        }
        
        // Add test classes if available
        val testClassesDir = File(projectDir, "target/test-classes")
        if (testClassesDir.exists()) {
            classpathEntries.add(testClassesDir.absolutePath)
        }
        
        // Add Maven dependencies from target/dependency if available
        val dependencyDir = File(projectDir, "target/dependency")
        if (dependencyDir.exists()) {
            dependencyDir.listFiles()?.forEach { jar ->
                if (jar.name.endsWith(".jar")) {
                    classpathEntries.add(jar.absolutePath)
                }
            }
        }
        
        return if (classpathEntries.isNotEmpty()) {
            classpathEntries.joinToString(File.pathSeparator)
        } else {
            null
        }
    }
    
    /**
     * Detects source paths in the project.
     */
    open protected fun detectSourcePaths(projectDir: File): List<String> {
        val sourcePaths = mutableListOf<String>()
        
        val commonSourceDirs = listOf(
            "src/main/java",
            "src/main/kotlin",
            "src/main/scala",
            "src/test/java",
            "src/test/kotlin",
            "src/test/scala"
        )
        
        commonSourceDirs.forEach { path ->
            val sourceDir = File(projectDir, path)
            if (sourceDir.exists() && sourceDir.isDirectory) {
                sourcePaths.add(sourceDir.absolutePath)
            }
        }
        
        return sourcePaths
    }
}