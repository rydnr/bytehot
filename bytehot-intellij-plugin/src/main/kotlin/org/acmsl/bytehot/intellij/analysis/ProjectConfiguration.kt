package org.acmsl.bytehot.intellij.analysis

/**
 * Configuration holder for project analysis results.
 * 
 * Contains the essential configuration needed to launch a Java application
 * with the ByteHot agent for live code hot-swapping.
 */
data class ProjectConfiguration(
    val mainClass: String?,
    val classpath: String?,
    val sourcePaths: List<String>?,
    val jvmArgs: List<String>?,
    val programArgs: List<String>?
)