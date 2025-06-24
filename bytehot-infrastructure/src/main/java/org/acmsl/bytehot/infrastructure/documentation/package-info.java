/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/**
 * ByteHot Documentation Infrastructure - Code analysis and documentation generation.
 * 
 * <p>This package provides documentation generation infrastructure for ByteHot,
 * implementing intelligent code analysis, cross-reference generation, and
 * multiple output format support. It serves as a secondary adapter for
 * documentation-related domain operations.</p>
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Code Analysis</h3>
 * <ul>
 *   <li>{@code CodeAnalyzer} - AST parsing and code structure analysis</li>
 *   <li>{@code FlowDetector} - Control flow and data flow analysis</li>
 *   <li>{@code DependencyMapper} - Class and package dependency mapping</li>
 * </ul>
 * 
 * <h3>Documentation Generation</h3>
 * <ul>
 *   <li>{@code DocumentationGenerator} - Main documentation generation engine</li>
 *   <li>{@code TemplateProcessor} - Template-based document generation</li>
 *   <li>{@code CrossReferenceLinker} - Automatic cross-reference linking</li>
 * </ul>
 * 
 * <h3>Output Formats</h3>
 * <ul>
 *   <li>{@code HTMLGenerator} - HTML documentation with navigation</li>
 *   <li>{@code MarkdownGenerator} - Markdown format for wikis and README</li>
 *   <li>{@code PDFGenerator} - PDF reports for documentation packages</li>
 *   <li>{@code OrgModeGenerator} - Emacs Org-mode format</li>
 * </ul>
 * 
 * <h2>Documentation Strategies</h2>
 * 
 * <h3>Basic Documentation</h3>
 * <p>Standard class and method documentation:</p>
 * <pre>{@code
 * // Generate basic documentation
 * DocumentationRequested request = DocumentationRequested.forBasicDocumentation(
 *     MyClass.class
 * );
 * 
 * // Process through infrastructure
 * DocumentationGenerator generator = new DocumentationGenerator();
 * DocumentationResult result = generator.generateDocumentation(request);
 * 
 * // Output to file
 * HTMLGenerator htmlGen = new HTMLGenerator();
 * htmlGen.writeToFile(result, Paths.get("docs/MyClass.html"));
 * }</pre>
 * 
 * <h3>Flow-Based Documentation</h3>
 * <p>Advanced documentation with flow analysis:</p>
 * <pre>{@code
 * // Analyze and document code flows
 * DocumentationRequested request = DocumentationRequested.forContextualDocumentation(
 *     UserRegistrationFlow.class,
 *     "user-registration"
 * );
 * 
 * // Generate with flow context
 * DocumentationResult result = generator.generateWithStrategy(
 *     request,
 *     DocumentationGenerationStrategy.CONTEXTUAL_FLOW
 * );
 * }</pre>
 * 
 * <h2>Code Analysis Capabilities</h2>
 * 
 * <h3>AST Analysis</h3>
 * <p>Deep code structure analysis using AST parsing:</p>
 * <ul>
 *   <li><strong>Class Structure</strong> - Fields, methods, constructors</li>
 *   <li><strong>Inheritance Hierarchy</strong> - Superclasses and interfaces</li>
 *   <li><strong>Annotations</strong> - Annotation usage and values</li>
 *   <li><strong>Method Calls</strong> - Internal and external method calls</li>
 * </ul>
 * 
 * <h3>Flow Detection</h3>
 * <p>Control and data flow analysis:</p>
 * <pre>{@code
 * // Detect code flows
 * FlowDetector detector = new FlowDetector();
 * 
 * FlowContext context = detector.analyzeClass(UserService.class)
 *     .withEntryPoints("registerUser", "authenticateUser")
 *     .withDepthLimit(5)
 *     .includeDataFlow(true)
 *     .analyze();
 * 
 * // Generate flow documentation
 * FlowDocumentationGenerator flowGen = new FlowDocumentationGenerator();
 * String flowDoc = flowGen.generateFlowDiagram(context);
 * }</pre>
 * 
 * <h2>Cross-Reference Generation</h2>
 * 
 * <h3>Automatic Linking</h3>
 * <p>Intelligent cross-reference detection and linking:</p>
 * <ul>
 *   <li><strong>Type References</strong> - Links to class documentation</li>
 *   <li><strong>Method Calls</strong> - Links to method documentation</li>
 *   <li><strong>Field Access</strong> - Links to field documentation</li>
 *   <li><strong>Package References</strong> - Links to package overviews</li>
 * </ul>
 * 
 * <h3>Link Generation</h3>
 * <pre>{@code
 * // Configure cross-reference linking
 * CrossReferenceLinker linker = new CrossReferenceLinker()
 *     .withBaseUrl("https://docs.example.com/")
 *     .withLinkStrategy(LinkStrategy.RELATIVE)
 *     .withAnchorGeneration(true);
 * 
 * // Generate links
 * String linkedContent = linker.processContent(rawDocumentation);
 * }</pre>
 * 
 * <h2>Template System</h2>
 * 
 * <h3>Customizable Templates</h3>
 * <p>Flexible template system for consistent documentation:</p>
 * <ul>
 *   <li><strong>Class Templates</strong> - Standard class documentation layout</li>
 *   <li><strong>Method Templates</strong> - Method signature and description</li>
 *   <li><strong>Flow Templates</strong> - Code flow visualization</li>
 *   <li><strong>Custom Templates</strong> - Project-specific formatting</li>
 * </ul>
 * 
 * <h2>Integration Points</h2>
 * 
 * <h3>Hot-Swap Integration</h3>
 * <p>Documentation updates triggered by hot-swap events:</p>
 * <pre>{@code
 * // Documentation update on class change
 * @EventHandler
 * public void handleClassRedefinition(ClassRedefinitionSucceeded event) {
 *     // Trigger documentation regeneration
 *     DocumentationRequested docRequest = DocumentationRequested.forBasicDocumentation(
 *         Class.forName(event.getClassName())
 *     );
 *     
 *     // Emit documentation request
 *     eventEmitter.emit(List.of(docRequest));
 * }
 * }</pre>
 * 
 * <h3>Event Sourcing</h3>
 * <p>Documentation generation events for audit and replay:</p>
 * <ul>
 *   <li>{@code DocumentationRequested} - Documentation generation initiated</li>
 *   <li>{@code DocumentationLinkGenerated} - Cross-reference link created</li>
 *   <li>{@code FlowContextDetected} - Code flow analysis completed</li>
 *   <li>{@code DocumentationAnalyticsEvent} - Usage and performance metrics</li>
 * </ul>
 * 
 * <h2>Configuration</h2>
 * <p>Documentation generation is highly configurable:</p>
 * <pre>{@code
 * # bytehot.properties
 * bytehot.docs.enable=true
 * bytehot.docs.output.formats=html,markdown
 * bytehot.docs.output.directory=/docs
 * bytehot.docs.cross.references=true
 * bytehot.docs.flow.analysis=true
 * bytehot.docs.template.path=/templates
 * bytehot.docs.auto.update=true
 * }</pre>
 * 
 * @author rydnr
 * @since 2025-06-07
 * @version 1.0
 */
package org.acmsl.bytehot.infrastructure.documentation;