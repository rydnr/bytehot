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
import org.acmsl.bytehot.domain.ConfigurationPort;
import org.acmsl.bytehot.domain.EventEmitterPort;
import org.acmsl.bytehot.domain.FileWatcherPort;
import org.acmsl.bytehot.domain.InstrumentationPort;
import org.acmsl.bytehot.domain.Ports;
import org.acmsl.bytehot.domain.events.ByteHotAgentAttached;
import org.acmsl.bytehot.domain.events.ByteHotAttachRequested;
import org.acmsl.bytehot.domain.events.ClassFileChanged;
import org.acmsl.bytehot.infrastructure.config.ConfigurationAdapter;
import org.acmsl.bytehot.infrastructure.events.EventEmitterAdapter;
import org.acmsl.bytehot.infrastructure.filesystem.FileWatcherAdapter;
import org.acmsl.bytehot.infrastructure.instrumentation.InstrumentationAdapter;

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
                    discoverAndInjectAdapters(instrumentation);
                    adaptersInitialized = true;
                }
            }
        }
    }

    /**
     * Accepts a ByteHotAttachRequested event.
     * @param event such event.
     * @return A list of events in response.
     */
    @Override
    public List<DomainResponseEvent<ByteHotAttachRequested>> accept(final ByteHotAttachRequested event) {
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
     * Processes a ClassFileChanged event from file monitoring
     * @param event the class file changed event
     */
    public void processClassFileChanged(final ClassFileChanged event) {
        try {
            // Ensure adapters are initialized
            if (!adaptersInitialized) {
                System.err.println("Application not initialized - cannot process ClassFileChanged event");
                return;
            }
            
            // For now, just log the event and emit it
            System.out.println("Processing ClassFileChanged: " + event.getClassName() + " at " + event.getClassFile());
            
            // For now, just log the event since ClassFileChanged is not a DomainResponseEvent
            // In a full implementation, this would trigger the hot-swap pipeline
            System.out.println("ClassFileChanged event received: " + event.getClassName());
            
        } catch (final Exception e) {
            System.err.println("Failed to process ClassFileChanged event: " + e.getMessage());
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
            // Inject ConfigurationAdapter
            final ConfigurationAdapter configAdapter = new ConfigurationAdapter();
            ports.inject(ConfigurationPort.class, (Adapter<ConfigurationPort>) configAdapter);

            // Inject FileWatcherAdapter
            final FileWatcherAdapter fileWatcherAdapter = new FileWatcherAdapter();
            ports.inject(FileWatcherPort.class, (Adapter<FileWatcherPort>) fileWatcherAdapter);

            // Inject InstrumentationAdapter (only if instrumentation is available)
            if (instrumentation != null) {
                final InstrumentationAdapter instrumentationAdapter = new InstrumentationAdapter(instrumentation);
                ports.inject(InstrumentationPort.class, (Adapter<InstrumentationPort>) instrumentationAdapter);
            }

            // Inject EventEmitterAdapter
            final EventEmitterAdapter eventEmitterAdapter = new EventEmitterAdapter();
            ports.inject(EventEmitterPort.class, (Adapter<EventEmitterPort>) eventEmitterAdapter);

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
        return clazz != null 
            && !clazz.isInterface() 
            && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())
            && Adapter.class.isAssignableFrom(clazz)
            && Port.class.isAssignableFrom(getPortInterface(clazz));
    }

    /**
     * Gets the port interface from an adapter class
     */
    @SuppressWarnings("unchecked")
    protected static Class<? extends Port> getPortInterface(final Class<?> adapterClass) {
        try {
            if (Adapter.class.isAssignableFrom(adapterClass)) {
                final Adapter<?> instance = (Adapter<?>) adapterClass.getDeclaredConstructor().newInstance();
                return (Class<? extends Port>) instance.adapts();
            }
        } catch (final Exception e) {
            // Ignore
        }
        return null;
    }

    /**
     * Instantiates and injects an adapter
     */
    @SuppressWarnings("unchecked")
    protected static void instantiateAndInjectAdapter(final Ports ports, final Class<?> adapterClass) {
        try {
            final Adapter<? extends Port> adapter = (Adapter<? extends Port>) adapterClass.getDeclaredConstructor().newInstance();
            final Class<? extends Port> portInterface = adapter.adapts();
            
            ports.inject((Class<Port>) portInterface, (Adapter<Port>) adapter);
            System.out.println("Injected adapter: " + adapterClass.getSimpleName() + " for port: " + portInterface.getSimpleName());
            
        } catch (final Exception e) {
            System.err.println("Failed to instantiate adapter " + adapterClass.getName() + ": " + e.getMessage());
        }
    }
}
