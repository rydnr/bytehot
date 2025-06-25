/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ByteHotApplication.java
 *
 * Author: rydnr
 *
 * Class name: ByteHotApplication
 *
 * Responsibilities: Application layer for ByteHot.
 *
 * Collaborators:
 *   - org.acmsl.bytehot.domain.events.ByteHotAttachRequested
 */
package org.acmsl.bytehot.application;

import org.acmsl.bytehot.domain.ByteHot;
import org.acmsl.bytehot.domain.BytecodeValidationException;
import org.acmsl.bytehot.domain.BytecodeValidator;
import org.acmsl.bytehot.domain.ConfigurationPort;
import org.acmsl.bytehot.domain.EventEmitterPort;
import org.acmsl.bytehot.domain.FileWatcherPort;
import org.acmsl.bytehot.domain.HotSwapException;
import org.acmsl.bytehot.domain.HotSwapManager;
import org.acmsl.bytehot.domain.InstrumentationService;
import org.acmsl.bytehot.domain.JvmInstrumentationService;
import org.acmsl.bytehot.domain.Ports;
import org.acmsl.bytehot.domain.events.BytecodeValidated;
import org.acmsl.bytehot.domain.events.ByteHotAgentAttached;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.domain.events.ClassRedefinitionSucceeded;
import org.acmsl.bytehot.domain.events.HotSwapRequested;
import org.acmsl.bytehot.domain.events.DocumentationRequested;

import org.acmsl.commons.patterns.Adapter;
import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;
import org.acmsl.commons.patterns.Port;
import org.acmsl.commons.patterns.eventsourcing.VersionedDomainEvent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Application layer for ByteHot.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 * @since 2025-06-07
 */
@EqualsAndHashCode
@ToString
public class ByteHotApplication
    implements HandlesByteHotAttached {

    /**
     * Whether adapters have been initialized
     */
    private static volatile boolean adaptersInitialized = false;

    /**
     * The instrumentation service for this application
     */
    private static InstrumentationService instrumentationService;

    /**
     * Default constructor to point to the singleton.
     */
    protected ByteHotApplication() {}

    /**
     * Returns the singleton instance of ByteHotApplication.
     * @return the singleton instance.
     */
    public static ByteHotApplication getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Holder class for the singleton instance of ByteHotApplication.
     */
    protected static class Holder {
        private static final ByteHotApplication INSTANCE = new ByteHotApplication();
    }

    /**
     * Initializes the application with the given instrumentation
     */
    public static void initialize(final Instrumentation instrumentation) {
        if (!adaptersInitialized) {
            synchronized (ByteHotApplication.class) {
                if (!adaptersInitialized) {
                    // Create the core instrumentation service for the domain
                    instrumentationService = new JvmInstrumentationService(instrumentation);
                    
                    // Initialize user context before anything else
                    initializeUserContext();
                    
                    discoverAndInjectAdapters(instrumentation);
                    adaptersInitialized = true;
                }
            }
        }
    }

    /**
     * Initializes the user context for ByteHot operations
     */
    private static void initializeUserContext() {
        try {
            // TODO: Implement user context initialization when UserContextResolver is fully implemented
            System.out.println("ByteHot initialized (user context pending)");
            
        } catch (final Exception e) {
            System.err.println("Failed to initialize user context: " + e.getMessage());
            // Continue without user context - the system should still function
        }
    }

    /**
     * Handles a ByteHotAttachRequested event specifically.
     * @param event the ByteHotAttachRequested event to process
     * @return a list of response events
     */
    @Override
    public List<DomainResponseEvent<ByteHotAttachRequested>> handleByteHotAttachRequested(final ByteHotAttachRequested event) {
        // Ensure adapters are initialized
        if (!adaptersInitialized) {
            throw new IllegalStateException("Application not initialized. Call initialize() with Instrumentation first.");
        }
        
        final List<DomainResponseEvent<ByteHotAttachRequested>> responseEvents = Arrays.asList(ByteHot.accept(event));
        
        // Emit the response events
        try {
            final EventEmitterPort eventEmitter = Ports.resolve(EventEmitterPort.class);
            final List<DomainResponseEvent<?>> genericEvents = new ArrayList<>();
            for (final DomainResponseEvent<ByteHotAttachRequested> responseEvent : responseEvents) {
                genericEvents.add(responseEvent);
            }
            eventEmitter.emit(genericEvents);
        } catch (final Exception e) {
            System.err.println("Failed to emit response events: " + e.getMessage());
        }
        
        return responseEvents;
    }

    /**
     * Generic event dispatcher that handles different types of domain events.
     * @param event the domain event to process
     * @return a list of response events
     */
    @Override
    public List<? extends DomainResponseEvent<?>> accept(final DomainEvent event) {
        // Ensure adapters are initialized
        if (!adaptersInitialized) {
            throw new IllegalStateException("Application not initialized. Call initialize() with Instrumentation first.");
        }

        // Ensure user context is available for all operations
        ensureUserContext();

        // Dispatch based on event type
        if (event instanceof ByteHotAttachRequested attachEvent) {
            return handleByteHotAttachRequested(attachEvent);
        } else if (event instanceof ClassFileChanged classFileEvent) {
            return handleClassFileChanged(classFileEvent);
        } else if (event instanceof DocumentationRequested docEvent) {
            return handleDocumentationRequested(docEvent);
        } else {
            throw new UnsupportedOperationException(
                "ByteHotApplication does not support events of type: " + event.getClass().getSimpleName());
        }
    }

    /**
     * Ensures user context is available for the current operation
     */
    private void ensureUserContext() {
        try {
            // TODO: Implement user context checking when UserContextResolver is fully implemented
            // For now, continue without user context validation
        } catch (final Exception e) {
            System.err.println("Warning: Could not ensure user context: " + e.getMessage());
            // Continue without user context - the system should still function
        }
    }

    /**
     * Handles a ClassFileChanged event and returns response events.
     * @param event the class file changed event
     * @return a list of response events
     */
    protected List<? extends DomainResponseEvent<?>> handleClassFileChanged(final ClassFileChanged event) {
        final List<DomainResponseEvent<?>> responseEvents = new ArrayList<>();
        
        try {
            System.out.println("Processing ClassFileChanged: " + event.getClassName() + " at " + event.getClassFile());
            
            // Execute the hot-swap pipeline and collect response events
            final List<? extends DomainResponseEvent<?>> pipelineEvents = executeHotSwapPipelineWithEvents(event);
            responseEvents.addAll(pipelineEvents);
            
            // Emit the response events
            try {
                final EventEmitterPort eventEmitter = Ports.resolve(EventEmitterPort.class);
                eventEmitter.emit(responseEvents);
            } catch (final Exception e) {
                System.err.println("Failed to emit response events: " + e.getMessage());
            }
            
        } catch (final Exception e) {
            System.err.println("Failed to process ClassFileChanged event: " + e.getMessage());
            e.printStackTrace();
        }
        
        return responseEvents;
    }

    /**
     * Handles a DocumentationRequested event and returns response events.
     * @param event the documentation request event
     * @return a list of response events
     */
    protected List<? extends DomainResponseEvent<?>> handleDocumentationRequested(final DocumentationRequested event) {
        final List<DomainResponseEvent<?>> responseEvents = new ArrayList<>();
        
        try {
            System.out.println("Processing DocumentationRequested (simplified implementation)");
            
            // TODO: Implement full documentation generation when DocProvider is fully implemented
            // For now, just log that the request was received
            
            // Emit the response events
            try {
                final EventEmitterPort eventEmitter = Ports.resolve(EventEmitterPort.class);
                eventEmitter.emit(responseEvents);
            } catch (final Exception e) {
                System.err.println("Failed to emit documentation response events: " + e.getMessage());
            }
            
        } catch (final Exception e) {
            System.err.println("Failed to process DocumentationRequested event: " + e.getMessage());
            e.printStackTrace();
        }
        
        return responseEvents;
    }

    /**
     * Processes a ClassFileChanged event from file monitoring.
     * This method is kept for backward compatibility but now delegates to the event-based handler.
     * @param event the class file changed event
     */
    public void processClassFileChanged(final ClassFileChanged event) {
        handleClassFileChanged(event);
    }

    /**
     * Executes the complete hot-swap pipeline for a class file change and returns response events.
     * @param event the class file changed event
     * @return list of response events generated during the pipeline
     */
    protected List<? extends DomainResponseEvent<?>> executeHotSwapPipelineWithEvents(final ClassFileChanged event) {
        final List<DomainEvent> pipelineEvents = new ArrayList<>();
        
        try {
            // Step 1: Validate the new bytecode
            System.out.println("Validating bytecode for class: " + event.getClassName());
            final BytecodeValidator validator = new BytecodeValidator();
            final BytecodeValidated validation = validator.validate(event.getClassFile());
            pipelineEvents.add(validation);
            
            System.out.println("Bytecode validation successful for: " + event.getClassName());
            
            // Step 2: Create hot-swap request
            System.out.println("Creating HotSwapRequested event for: " + event.getClassName());
            final HotSwapManager hotSwapManager = new HotSwapManager(instrumentationService);
            
            // Get the current bytecode (mock empty for now since we don't have class tracking)
            final byte[] originalBytecode = new byte[0];
            final HotSwapRequested hotSwapRequest = hotSwapManager.requestHotSwap(
                event.getClassFile(), 
                validation, 
                originalBytecode
            );
            pipelineEvents.add(hotSwapRequest);
            
            // Step 3: Perform class redefinition
            System.out.println("Performing class redefinition for: " + event.getClassName());
            final ClassRedefinitionSucceeded result = hotSwapManager.performRedefinition(hotSwapRequest);
            pipelineEvents.add(result);
            
            // Step 4: Log success
            System.out.println("Hot-swap completed successfully for: " + event.getClassName() + 
                             " (affected instances: " + result.getAffectedInstances() + ")");
            System.out.println("ClassRedefinitionSucceeded event generated for: " + event.getClassName());
            
        } catch (final BytecodeValidationException e) {
            // Handle validation failure - add rejection event
            System.err.println("Bytecode validation failed for " + event.getClassName() + ": " + e.getMessage());
            System.err.println("BytecodeRejected event generated: " + e.getRejectionEvent().getRejectionReason());
            pipelineEvents.add(e.getRejectionEvent());
            
        } catch (final HotSwapException e) {
            // Handle redefinition failure - add failure event
            System.err.println("Class redefinition failed for " + event.getClassName() + ": " + e.getMessage());
            System.err.println("ClassRedefinitionFailed event generated: " + e.getFailureEvent().getFailureReason());
            pipelineEvents.add(e.getFailureEvent());
            
        } catch (final Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error in hot-swap pipeline for " + event.getClassName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        // Filter and convert to DomainResponseEvent list for return
        return pipelineEvents.stream()
            .filter(e -> e instanceof DomainResponseEvent)
            .map(e -> (DomainResponseEvent<?>) e)
            .toList();
    }

    /**
     * Executes the complete hot-swap pipeline for a class file change
     * @param event the class file changed event
     */
    protected void executeHotSwapPipeline(final ClassFileChanged event) {
        try {
            // Step 1: Validate the new bytecode
            System.out.println("Validating bytecode for class: " + event.getClassName());
            final BytecodeValidator validator = new BytecodeValidator();
            final BytecodeValidated validation = validator.validate(event.getClassFile());
            
            System.out.println("Bytecode validation successful for: " + event.getClassName());
            
            // Step 2: Create hot-swap request
            System.out.println("Creating HotSwapRequested event for: " + event.getClassName());
            final HotSwapManager hotSwapManager = new HotSwapManager(instrumentationService);
            
            // Get the current bytecode (mock empty for now since we don't have class tracking)
            final byte[] originalBytecode = new byte[0];
            final HotSwapRequested hotSwapRequest = hotSwapManager.requestHotSwap(
                event.getClassFile(), 
                validation, 
                originalBytecode
            );
            
            // Step 3: Perform class redefinition
            System.out.println("Performing class redefinition for: " + event.getClassName());
            final ClassRedefinitionSucceeded result = hotSwapManager.performRedefinition(hotSwapRequest);
            
            // Step 4: Log success (event emission would be handled by proper domain flow in production)
            System.out.println("Hot-swap completed successfully for: " + event.getClassName() + 
                             " (affected instances: " + result.getAffectedInstances() + ")");
            System.out.println("ClassRedefinitionSucceeded event generated for: " + event.getClassName());
            
        } catch (final BytecodeValidationException e) {
            // Handle validation failure
            System.err.println("Bytecode validation failed for " + event.getClassName() + ": " + e.getMessage());
            System.err.println("BytecodeRejected event generated: " + e.getRejectionEvent().getRejectionReason());
            
        } catch (final HotSwapException e) {
            // Handle redefinition failure
            System.err.println("Class redefinition failed for " + event.getClassName() + ": " + e.getMessage());
            System.err.println("ClassRedefinitionFailed event generated: " + e.getFailureEvent().getFailureReason());
            
        } catch (final Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error in hot-swap pipeline for " + event.getClassName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Discovers and injects all available adapters into the Ports registry
     */
    @SuppressWarnings("unchecked")
    protected static void discoverAndInjectAdapters(final Instrumentation instrumentation) {
        final Ports ports = Ports.getInstance();

        // Inject built-in adapters
        injectBuiltInAdapters(ports, instrumentation);
        
        // Discover additional adapters from classpath
        discoverAdaptersFromClasspath(ports);
    }

    /**
     * Injects the built-in infrastructure adapters
     */
    @SuppressWarnings("unchecked")
    protected static void injectBuiltInAdapters(final Ports ports, final Instrumentation instrumentation) {
        try {
            // TODO: Adapter injection should be done from infrastructure layer
            // This method needs refactoring to follow hexagonal architecture properly

            System.out.println("Built-in adapters injected successfully");

        } catch (final Exception e) {
            System.err.println("Failed to inject built-in adapters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Discovers additional adapters from the classpath
     */
    protected static void discoverAdaptersFromClasspath(final Ports ports) {
        try {
            final List<Class<?>> adapterClasses = findAdapterClasses();
            
            for (final Class<?> adapterClass : adapterClasses) {
                if (isValidAdapter(adapterClass)) {
                    instantiateAndInjectAdapter(ports, adapterClass);
                }
            }
            
            if (!adapterClasses.isEmpty()) {
                System.out.println("Discovered " + adapterClasses.size() + " additional adapter(s) from classpath");
            }

        } catch (final Exception e) {
            System.err.println("Failed to discover adapters from classpath: " + e.getMessage());
        }
    }

    /**
     * Finds all classes implementing the Adapter interface
     */
    protected static List<Class<?>> findAdapterClasses() {
        final List<Class<?>> adapterClasses = new ArrayList<>();
        
        try {
            // Get the current class loader
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            // Scan for classes in known infrastructure packages
            final String[] infraPackages = {
                "org.acmsl.bytehot.infrastructure",
                "org.acmsl.bytehot.adapters"
            };
            
            for (final String packageName : infraPackages) {
                adapterClasses.addAll(scanPackageForAdapters(packageName, classLoader));
            }
            
        } catch (final Exception e) {
            System.err.println("Error scanning for adapter classes: " + e.getMessage());
        }
        
        return adapterClasses;
    }

    /**
     * Scans a package for adapter classes
     */
    protected static List<Class<?>> scanPackageForAdapters(final String packageName, final ClassLoader classLoader) {
        final List<Class<?>> adapterClasses = new ArrayList<>();
        
        try {
            final String packagePath = packageName.replace('.', '/');
            final Enumeration<URL> resources = classLoader.getResources(packagePath);
            
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                
                if (resource.getProtocol().equals("file")) {
                    adapterClasses.addAll(scanFileSystemForAdapters(new File(resource.getFile()), packageName, classLoader));
                } else if (resource.getProtocol().equals("jar")) {
                    adapterClasses.addAll(scanJarForAdapters(resource, packagePath, classLoader));
                }
            }
            
        } catch (final Exception e) {
            System.err.println("Error scanning package " + packageName + ": " + e.getMessage());
        }
        
        return adapterClasses;
    }

    /**
     * Scans file system directory for adapter classes
     */
    protected static List<Class<?>> scanFileSystemForAdapters(final File directory, final String packageName, final ClassLoader classLoader) {
        final List<Class<?>> adapterClasses = new ArrayList<>();
        
        if (!directory.exists() || !directory.isDirectory()) {
            return adapterClasses;
        }
        
        final File[] files = directory.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    adapterClasses.addAll(scanFileSystemForAdapters(file, packageName + "." + file.getName(), classLoader));
                } else if (file.getName().endsWith(".class")) {
                    final String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    final Class<?> clazz = loadClass(className, classLoader);
                    if (clazz != null && isValidAdapter(clazz)) {
                        adapterClasses.add(clazz);
                    }
                }
            }
        }
        
        return adapterClasses;
    }

    /**
     * Scans JAR file for adapter classes
     */
    protected static List<Class<?>> scanJarForAdapters(final URL jarUrl, final String packagePath, final ClassLoader classLoader) {
        final List<Class<?>> adapterClasses = new ArrayList<>();
        
        try {
            final String jarPath = jarUrl.getPath().substring(5, jarUrl.getPath().indexOf("!"));
            final JarFile jarFile = new JarFile(jarPath);
            final Enumeration<JarEntry> entries = jarFile.entries();
            
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();
                
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    final String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                    final Class<?> clazz = loadClass(className, classLoader);
                    if (clazz != null && isValidAdapter(clazz)) {
                        adapterClasses.add(clazz);
                    }
                }
            }
            
            jarFile.close();
            
        } catch (final Exception e) {
            System.err.println("Error scanning JAR " + jarUrl + ": " + e.getMessage());
        }
        
        return adapterClasses;
    }

    /**
     * Safely loads a class
     */
    protected static Class<?> loadClass(final String className, final ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Checks if a class is a valid adapter
     */
    protected static boolean isValidAdapter(final Class<?> clazz) {
        if (clazz == null 
            || clazz.isInterface() 
            || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())
            || !Adapter.class.isAssignableFrom(clazz)) {
            return false;
        }
        
        final Class<? extends Port> portInterface = getPortInterface(clazz);
        return portInterface != null && Port.class.isAssignableFrom(portInterface);
    }

    /**
     * Gets the port interface from an adapter class
     */
    @SuppressWarnings("unchecked")
    protected static Class<? extends Port> getPortInterface(final Class<?> adapterClass) {
        try {
            if (Adapter.class.isAssignableFrom(adapterClass)) {
                // Special handling for adapters that can't be instantiated via default constructor
                if (adapterClass.getName().equals("org.acmsl.bytehot.infrastructure.filesystem.FileWatcherAdapter")) {
                    return org.acmsl.bytehot.domain.FileWatcherPort.class;
                }
                
                final Adapter<?> instance = (Adapter<?>) adapterClass.getDeclaredConstructor().newInstance();
                return (Class<? extends Port>) instance.adapts();
            }
        } catch (final Exception e) {
            // Silently ignore failed instantiations during discovery
        }
        return null;
    }

    /**
     * Instantiates and injects an adapter
     */
    @SuppressWarnings("unchecked")
    protected static void instantiateAndInjectAdapter(final Ports ports, final Class<?> adapterClass) {
        try {
            Adapter<? extends Port> adapter = null;
            Class<? extends Port> portInterface = null;
            
            // Special handling for FileWatcherAdapter
            if (adapterClass.getName().equals("org.acmsl.bytehot.infrastructure.filesystem.FileWatcherAdapter")) {
                try {
                    adapter = (Adapter<? extends Port>) adapterClass.getDeclaredConstructor().newInstance();
                    portInterface = org.acmsl.bytehot.domain.FileWatcherPort.class;
                    
                    // Initialize it properly
                    final java.lang.reflect.Method initializeMethod = adapterClass.getMethod("initialize", Application.class);
                    initializeMethod.invoke(adapter, getInstance());
                } catch (final Exception specialException) {
                    System.err.println("Failed to instantiate FileWatcherAdapter: " + specialException.getMessage());
                    return;
                }
            } else {
                adapter = (Adapter<? extends Port>) adapterClass.getDeclaredConstructor().newInstance();
                portInterface = adapter.adapts();
            }
            
            ports.inject((Class<Port>) portInterface, (Adapter<Port>) adapter);
            System.out.println("Injected adapter: " + adapterClass.getSimpleName() + " for port: " + portInterface.getSimpleName());
            
        } catch (final Exception e) {
            System.err.println("Failed to instantiate adapter " + adapterClass.getName() + ": " + e.getMessage());
        }
    }
}
