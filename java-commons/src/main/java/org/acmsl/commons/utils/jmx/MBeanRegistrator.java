//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA


    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: MBeanRegistrator.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Is able to register MBeans.
 *
 */
package org.acmsl.commons.utils.jmx;

/*
 * Importing some JMX classes.
 */
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

/*
 * Importing some JDK1.3 classes.
 */
import java.util.Iterator;
import java.util.List;

/*
 * Importing some Commons Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Is able to register MBeans.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
@SuppressWarnings("unused")
public class MBeanRegistrator
{
    /**
     * Singleton implementation to avoid double-locking check.
     */
    protected static final class MBeanRegistratorSingletonContainer
    {
        /**
         * The actual singleton.
         */
        public static final MBeanRegistrator SINGLETON = new MBeanRegistrator();
    }

    /**
     * Cached reference to the mbean server.
     */
    private MBeanServer m__Server;

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected MBeanRegistrator()  {}

    /**
     * Retrieves a <code>MBeanRegistrator</code> instance.
     * @return such instance.
     */
    @NotNull
    public static MBeanRegistrator getInstance()
    {
        return MBeanRegistratorSingletonContainer.SINGLETON;
    }

    /**
     * Registers a MBean from given its class name and an object name.
     * @param mbeanClassName the class name.
     * @param objectName the object name.
     * @return <code>true</code> if the MBean is registered successfully.
     */
    public boolean register(
        @NotNull final String mbeanClassName, @NotNull final String objectName)
    {
        boolean result = false;

        try
        {
            result =
                register(
                    Class.forName(mbeanClassName).getDeclaredConstructor().newInstance(),
                    new ObjectName(objectName));
        }
        catch  (final Throwable throwable)
        {
            LogFactory.getLog(MBeanRegistrator.class).warn(
                  "Specified object name and/or MBean class name are not "
                + "suitable of being registered in the MBean server.",
                throwable);
        }

        return result;
    }

    /**
     * Registers a MBean from given its class name and an object name.
     * @param mbean the class name.
     * @param objectName the object name.
     * @return <code>true</code> if the MBean is registered successfully.
     */
    public boolean register(@NotNull final Object mbean, @NotNull final ObjectName objectName)
    {
        return register(mbean, objectName, getMBeanServer());
    }

    /**
     * Registers a MBean from given its class name and an object name.
     * @param mbean the class name.
     * @param objectName the object name.
     * @param server the server.
     * @return <code>true</code> if the MBean is registered successfully.
     */
    protected boolean register(
        @NotNull final Object mbean, @NotNull final ObjectName objectName, @Nullable final MBeanServer server)
    {
        boolean result = false;

        try
        {
            if  (   (server != null)
                 && (!server.isRegistered(objectName)))
            {
                server.registerMBean(mbean, objectName);

                result = true;
            }
            else 
            {
                LogFactory.getLog(MBeanRegistrator.class).info(
                    objectName + " is already registered.");
            }
        }
        catch  (final Throwable throwable)
        {
            LogFactory.getLog(MBeanRegistrator.class).warn(
                  "Specified object name and/or MBean class name are not "
                + "suitable of being registered in the MBean server.",
                throwable);
        }

        return result;
    }

    /**
     * Unregisters a MBean from given object name.
     * @param objectName the object name.
     * @return <code>true</code> if the MBean is registered successfully.
     */
    @SuppressWarnings("unused")
    public boolean unregister(@NotNull final String objectName)
    {
        boolean result = false;

        try
        {
            result = unregister(new ObjectName(objectName));
        }
        catch  (final Throwable throwable)
        {
            LogFactory.getLog(MBeanRegistrator.class).warn(
                "Specified object name is not "
                + "suitable of being unregistered in the MBean server.",
                throwable);
        }

        return result;
    }

    /**
     * Unregisters a MBean from given an object name.
     * @param objectName the object name.
     * @return <code>true</code> if the MBean is unregistered successfully.
     */
    public boolean unregister(@NotNull final ObjectName objectName)
    {
        return unregister(objectName, getMBeanServer());
    }

    /**
     * Unregisters a MBean from given an object name.
     * @param objectName the object name.
     * @param server the MBean server.
     * @return <code>true</code> if the MBean is unregistered successfully.
     */
    protected boolean unregister(
        @NotNull final ObjectName objectName, @Nullable final MBeanServer server)
    {
        boolean result = false;

        try
        {
            if  (   (server != null)
                 && (server.isRegistered(objectName)))
            {
                server.unregisterMBean(objectName);

                result = true;
            }
            else 
            {
                LogFactory.getLog(MBeanRegistrator.class).info(
                    objectName + " is not registered.");
            }
        }
        catch  (final Throwable throwable)
        {
            LogFactory.getLog(MBeanRegistrator.class).warn(
                  "Specified object name is not "
                + "suitable of being unregistered in the MBean server.",
                throwable);
        }

        return result;
    }

    /**
     * Specifies the MBean server.
     * @param server such server.
     */
    protected final void immutableSetMBeanServer(@NotNull final MBeanServer server)
    {
        m__Server = server;
    }

    /**
     * Specifies the MBean server.
     * @param server such server.
     */
    protected void setMBeanServer(@NotNull final MBeanServer server)
    {
        immutableSetMBeanServer(server);
    }

    /**
     * Retrieves the MBean server.
     * @return such server.
     */
    @NotNull
    protected final MBeanServer immutableGetMBeanServer()
    {
        return m__Server;
    }
    
    /**
     * Obtains a reference to the MBean server. If at least one
     * MBean server already exists, then a reference to that MBean
     * server is returned. Otherwise a new instance is created.
     * @return such server.
     */
    @Nullable
    public MBeanServer getMBeanServer()
    {
        return getMBeanServer(immutableGetMBeanServer());
    }
    
    /**
     * Obtains a reference to the MBean server. If at least one
     * MBean server already exists, then a reference to that MBean
     * server is returned. Otherwise a new instance is created.
     * @param cachedInstance the cached instance, if any.
     * @return such server.
     */
    @Nullable
    protected MBeanServer getMBeanServer(@Nullable final MBeanServer cachedInstance)
    {
        MBeanServer result = cachedInstance;
        
        if  (result == null)
        {
            @NotNull final List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);

            @NotNull final Iterator<MBeanServer> serverIterator = mbeanServers.iterator();

            while  (serverIterator.hasNext())
            {
                @NotNull final MBeanServer mbeanServer = serverIterator.next();

                mbeanServer.queryMBeans(null, null);
            }

            result =
                (mbeanServers.size() > 0)
                ?  mbeanServers.get(0)
                :  MBeanServerFactory.createMBeanServer();

            setMBeanServer(result);
        }

        return result;
    }
}
