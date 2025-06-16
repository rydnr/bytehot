#!/usr/bin/env groovy

import groovy.xml.*

// Load and parse the pom.xml
def pomFile = new File("pom.xml")
def parser = new XmlParser()
parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
def pom = parser.parse(pomFile)

def cloneNode(Node source, Node parent) {
    def newNode = new Node(parent, source.name(), source.attributes())
    source.children().each {
        if (it instanceof Node) {
            cloneNode(it, newNode)
        } else {
            newNode.value += it
        }
    }
    return newNode
}

// --- 1. Sort <dependencyManagement><dependencies><dependency> ---
def dmDependenciesBlocks = pom.'dependencyManagement'.'dependencies'
dmDependenciesBlocks.each { dependencies ->
    def sortedDeps = dependencies.'dependency'.sort { a, b ->
        def groupA = a.groupId?.text() ?: ''
        def groupB = b.groupId?.text() ?: ''
        def artifactA = a.artifactId?.text() ?: ''
        def artifactB = b.artifactId?.text() ?: ''
        groupA <=> groupB ?: artifactA <=> artifactB
    }

    dependencies.children().clear()
    sortedDeps.each { dep -> cloneNode(dep, dependencies) }
}

// --- 2. Sort <build><pluginManagement><plugins><plugin> ---
def pluginManagementPlugins = pom.build?.pluginManagement?.plugins
pluginManagementPlugins?.each { plugins ->
    def sortedPlugins = plugins.'plugin'.sort { a, b ->
        def groupA = a.groupId?.text() ?: ''
        def groupB = b.groupId?.text() ?: ''
        def artifactA = a.artifactId?.text() ?: ''
        def artifactB = b.artifactId?.text() ?: ''
        groupA <=> groupB ?: artifactA <=> artifactB
    }

    plugins.children().clear()
    sortedPlugins.each { plugin -> cloneNode(plugin, plugins) }
}

// --- Write output ---
def printer = new XmlNodePrinter(new PrintWriter(new FileWriter("pom-sorted.xml")))
printer.preserveWhitespace = true
printer.print(pom)

println "✅ Sorted <dependencyManagement> and <pluginManagement> written to pom-sorted.xml"
