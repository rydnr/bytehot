package org.acmsl.bytehot.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import java.io.File

/**
 * Gradle Project Analyzer for ByteHot plugin.
 * 
 * Analyzes Gradle projects to extract configuration needed
 * for ByteHot live mode activation. Provides automatic detection
 * of main classes, classpath building, and project structure analysis.
 * 
 * Detection strategies:
 * 1. Check application plugin for mainClassName
 * 2. Parse build.gradle for application configuration
 * 3. Scan for classes with main method in source directories
 * 4. Build classpath from project outputs and dependencies
 */
open class GradleProjectAnalyzer(private val project: Project) {
    
    /**
     * Analyzes the Gradle project.
     * @return ProjectConfiguration with detected settings
     */
    fun analyzeProject(): ProjectConfiguration {
        val mainClass = detectMainClass()
        val classpath = buildClasspath()
        val sourcePaths = detectSourcePaths()
        
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
    protected open fun detectMainClass(): String? {
        // Strategy 1: Check application plugin configuration
        val applicationMainClass = findMainClassInApplicationPlugin()
        if (applicationMainClass != null) {
            return applicationMainClass
        }
        
        // Strategy 2: Parse build.gradle file
        val buildFileMainClass = findMainClassInBuildFile()
        if (buildFileMainClass != null) {
            return buildFileMainClass
        }
        
        // Strategy 3: Scan source directories for main methods
        return scanForMainClass()
    }
    
    /**
     * Searches for main class in application plugin configuration.
     */
    protected fun findMainClassInApplicationPlugin(): String? {
        return try {
            // Try to get application extension if plugin is applied
            if (project.plugins.hasPlugin("application")) {
                project.extensions.findByName("application")?.let { ext ->
                    // Use reflection to get mainClass or mainClassName
                    try {
                        val mainClassProperty = ext.javaClass.getMethod("getMainClass")
                        val mainClassProvider = mainClassProperty.invoke(ext)
                        if (mainClassProvider != null) {
                            val getMethod = mainClassProvider.javaClass.getMethod("getOrNull")
                            return getMethod.invoke(mainClassProvider) as? String
                        }
                    } catch (e: Exception) {
                        // Fall back to mainClassName property for older Gradle versions
                        try {
                            val mainClassNameProperty = ext.javaClass.getMethod("getMainClassName")
                            return mainClassNameProperty.invoke(ext) as? String
                        } catch (e2: Exception) {
                            // Ignore
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Searches for main class configuration in build.gradle.
     */
    protected open fun findMainClassInBuildFile(): String? {
        val buildFile = File(project.projectDir, "build.gradle")
        if (!buildFile.exists()) {
            val buildFileKts = File(project.projectDir, "build.gradle.kts")
            if (!buildFileKts.exists()) return null
            return findMainClassInKtsBuildFile(buildFileKts)
        }
        
        try {
            val content = buildFile.readText()
            
            // Look for application block with mainClassName
            val applicationRegex = """application\s*\{[^}]*mainClassName\s*=\s*['"]([^'"]+)['"][^}]*\}""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val applicationMatch = applicationRegex.find(content)
            if (applicationMatch != null) {
                return applicationMatch.groupValues[1].trim()
            }
            
            // Look for mainClassName property
            val propertyRegex = """mainClassName\s*=\s*['"]([^'"]+)['"]""".toRegex()
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
     * Searches for main class configuration in build.gradle.kts.
     */
    protected fun findMainClassInKtsBuildFile(buildFile: File): String? {
        try {
            val content = buildFile.readText()
            
            // Look for application block with mainClass
            val applicationRegex = """application\s*\{[^}]*mainClass\.set\("([^"]+)"\)[^}]*\}""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val applicationMatch = applicationRegex.find(content)
            if (applicationMatch != null) {
                return applicationMatch.groupValues[1].trim()
            }
            
            // Look for mainClass property
            val propertyRegex = """mainClass\.set\("([^"]+)"\)""".toRegex()
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
     * Scans source directories for classes with main method.
     */
    protected open fun scanForMainClass(): String? {
        val sourcePaths = detectSourcePaths()
        
        for (sourcePath in sourcePaths) {
            val sourceDir = File(sourcePath)
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
                        return extractClassNameFromFile(file, findSourceRoot(file))
                    }
                } catch (e: Exception) {
                    // Ignore file reading errors
                }
            }
        }
        
        return null
    }
    
    /**
     * Finds the source root directory for a given file.
     */
    protected fun findSourceRoot(file: File): File? {
        var current = file.parentFile
        while (current != null) {
            if (current.name == "java" || current.name == "kotlin") {
                val parent = current.parentFile
                if (parent != null && parent.name == "main") {
                    return current
                }
            }
            current = current.parentFile
        }
        return null
    }
    
    /**
     * Extracts fully qualified class name from source file.
     */
    protected open fun extractClassNameFromFile(file: File, sourceRoot: File?): String? {
        if (sourceRoot == null) return null
        
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
    protected open fun buildClasspath(): String? {
        val classpathEntries = mutableListOf<String>()
        
        // Add compiled classes from Java plugin
        if (project.plugins.hasPlugin("java")) {
            try {
                val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
                val mainSourceSet = javaExtension.sourceSets.getByName("main")
                
                mainSourceSet.output.classesDirs.forEach { dir ->
                    if (dir.exists()) {
                        classpathEntries.add(dir.absolutePath)
                    }
                }
                
                mainSourceSet.output.resourcesDir?.let { resourcesDir ->
                    if (resourcesDir.exists()) {
                        classpathEntries.add(resourcesDir.absolutePath)
                    }
                }
            } catch (e: Exception) {
                // Fall back to standard build directory
                val buildDir = File(project.layout.buildDirectory.asFile.get(), "classes/java/main")
                if (buildDir.exists()) {
                    classpathEntries.add(buildDir.absolutePath)
                }
            }
        }
        
        // Add runtime dependencies
        try {
            val runtimeClasspath = project.configurations.getByName("runtimeClasspath")
            runtimeClasspath.forEach { file ->
                classpathEntries.add(file.absolutePath)
            }
        } catch (e: Exception) {
            // Fallback - add build directory if it exists
            val buildDir = File(project.layout.buildDirectory.asFile.get(), "classes/java/main")
            if (buildDir.exists() && !classpathEntries.contains(buildDir.absolutePath)) {
                classpathEntries.add(buildDir.absolutePath)
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
    protected open fun detectSourcePaths(): List<String> {
        val sourcePaths = mutableListOf<String>()
        
        // Get source paths from Java plugin
        if (project.plugins.hasPlugin("java")) {
            try {
                val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
                val mainSourceSet = javaExtension.sourceSets.getByName("main")
                
                mainSourceSet.allSource.srcDirs.forEach { dir ->
                    if (dir.exists()) {
                        sourcePaths.add(dir.absolutePath)
                    }
                }
            } catch (e: Exception) {
                // Ignore and fall back to standard layout
            }
        }
        
        // Fall back to standard Gradle directory layout
        if (sourcePaths.isEmpty()) {
            val standardDirs = listOf(
                "src/main/java",
                "src/main/kotlin",
                "src/main/scala",
                "src/test/java",
                "src/test/kotlin",
                "src/test/scala"
            )
            
            standardDirs.forEach { path ->
                val sourceDir = File(project.projectDir, path)
                if (sourceDir.exists() && sourceDir.isDirectory) {
                    sourcePaths.add(sourceDir.absolutePath)
                }
            }
        }
        
        return sourcePaths
    }
}

/**
 * Configuration holder for project analysis results.
 */
data class ProjectConfiguration(
    val mainClass: String?,
    val classpath: String?,
    val sourcePaths: List<String>?,
    val jvmArgs: List<String>?,
    val programArgs: List<String>?
)