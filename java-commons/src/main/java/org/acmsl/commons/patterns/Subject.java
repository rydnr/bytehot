//;-*- mode: java -*-
/*
                        ACM-SL Commons

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: Subject.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Generic implementation of the Observable pattern.
 *
 */
package org.acmsl.commons.patterns;

/*
 * Importing some JetBrains annotations.
 */

/*
 * Importing some JDK classes.
 */
import java.util.ArrayList;
import java.util.Collection;

/**
 * Generic implementation of the Observable pattern.
 * @param <O> the observer type parameter
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public class Subject<O extends Observer>
    implements  Observable<O>
{
    /**
     * Concrete reference to the observers' collection.
     */
    private Collection<O> m__cObservers;

    /**
     * Specifies the observer collection.
     * @param observers the observers.
     */
    protected void setObservers(final Collection<O> observers)
    {
        m__cObservers = observers;
    }

    /**
     * Retrieves the observer collection.
     * @return the observers.
     */
    
    protected final Collection<O> immutableGetObservers()
    {
        return m__cObservers;
    }

    /**
     * Retrieves the observer collection.
     * @return the observers.
     */
    @SuppressWarnings("unused")
    protected Collection<O> getObservers()
    {
        Collection<O> result = immutableGetObservers();

        if (result == null)
        {
            result = new ArrayList<O>(0);
        }
        else
        {
            result = new ArrayList<O>(result);
        }

        return result;
    }

    /**
     * Retrieves the observers. This includes creating the collection the first
     * time, so this method never returns a <code>null</code> value.
     * @return a not <code>null</code> collection.
     */
    
    protected final Collection<O> getObserverCollection()
    {
        Collection<O> result = immutableGetObservers();

        if  (result == null)
        {
            result = new ArrayList<O>();
            setObservers(result);
        }

        return result;
    }

    /**
     * Attaches given observer in order to be notified of state changes.
     * @param observer the new observer to attach.
     */
    @Override
    public void attach(final O observer)
    {
        if  (observer != null)
        {
            attach(observer, getObserverCollection());
        }
    }

    /**
     * Attaches given observer in order to be notified of state changes.
     * @param observer the new observer to attach.
     * @param observerCollection the observer collection.
     */
    protected void attach(
        final O observer, final Collection<O> observerCollection)
    {
        observerCollection.add(observer);
    }

    /**
     * Detaches given observer in order to stop being notified of state
     * changes.
     * @param observer the new observer to attach.
     */
    @Override
    public void detach(final O observer)
    {
        if  (observer != null)
        {
            detach(observer, getObserverCollection());
        }
    }

    /**
     * Detaches given observer in order to stop being notified of state
     * changes.
     * @param observer the new observer to attach.
     * @param observerCollection the observer collection.
     */
    protected void detach(
        final O observer, final Collection<O> observerCollection)
    {
        observerCollection.remove(observer);
    }

    /**
     * Notifies all attached observers of state changes.
     */
    public void inform()
    {
        for (final O t_Observer : getObserverCollection())
        {
            if (t_Observer != null)
            {
                t_Observer.update();
            }
        }
    }
}
