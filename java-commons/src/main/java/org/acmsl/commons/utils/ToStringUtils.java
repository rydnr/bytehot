/*
                        ACM-SL Java Commons

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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ToStringUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides logic to generate valid, complete toString()
 *              representations of objects.
 *
 * Date: 2013/11/22
 * Time: 07:00
 *
 */
package org.acmsl.commons.utils;

/*
 * Importing ACM-SL Java Commons classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.patterns.Utils;

/*
 * Importing Apache Commons Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */

/*
 * Importing StringTemplate classes.
 */
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/*
 * Importing JDK classes.
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides logic to generate valid, complete toString() representations of objects.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 0.7.1
 * Created: 2013/11/22 07:00
 */
public class ToStringUtils
    implements Utils
{
    /**
     * The path to the actual ST template.
     */
    public static final String TO_JSON = "org/acmsl/commons/utils/ToStringUtilsJSON.stg";

    /**
     * The ST template.
     */
    protected static final String ST_TEMPLATE = Literals.TO_STRING;

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class ToStringUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        public static final ToStringUtils SINGLETON = new ToStringUtils();
    }

    /**
     * Retrieves the singleton instance.
     * @return such instance.
     */
    
    public static ToStringUtils getInstance()
    {
        return ToStringUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Converts given list to JSON.
     * @param list the list.
     * @param <T> the type.
     * @return the JSON-formatted list.
     */
    
    public <T> String toJson(final List<T> list)
    {
        final String result;

        String aux = null;

        final StringBuilder builder = new StringBuilder("[ ");

        if (list != null)
        {
            for (final T item : list)
            {
                if (item != null)
                {
//                    builder.append(toJson(item, item.getClass(), new HashMap<String, Object>(0)));
                    builder.append(item);
                }
            }

            aux = builder.toString();

            if (aux.endsWith(","))
            {
                aux = builder.substring(0, builder.length() - 1);
            }

            aux = aux + " ]";
        }

        if (aux == null)
        {
            result = "";
        }
        else
        {
            result = aux;
        }

        return result;
    }

    /**
     * Generates a JSON-formatted string representation of given instance.
     * @param instance the instance.
     * @param fileClass the file class.
     * @param args the instance fields.
     * @param <T> the type.
     * @return the JSON representation.
     */
    
    public <T> String toJson(
        final T instance,
        final Class<?> fileClass,
        final Map<String, Object> args)
    {
        final STGroup stGroup = new STGroupFile(TO_JSON);

        final ST template = stGroup.getInstanceOf(ST_TEMPLATE);

        final Map<String, Object> context = new HashMap<>(3);

        context.put("obj", instance);
        context.put("fileClass", fileClass);
        context.put(Literals.ATTRS, decorate(args));

        template.add("C", context);

        final String result = template.render();

        return result;
    }

    /**
     * Decorates given arguments.
     * @param args the arguments.
     * @return the map of decorations.
     */
    protected Map<String, Object> decorate(final Map<String, Object> args)
    {
        final Map<String, Object> result = new HashMap<>(args.size());

        for (final Map.Entry<String, Object> arg : args.entrySet())
        {
            final Object value = arg.getValue();

            if (value != null)
            {
                result.put(arg.getKey(), decorateElement(arg.getKey(), value));
            }
        }

        return result;
    }

    /**
     * Decorates given element.
     * @param name the element name.
     * @param arg the element value.
     * @return the decorated instance.
     */
    @SuppressWarnings("unchecked")
    protected Decorator<?> decorateElement(final String name, final Object arg)
    {
        final Decorator<?> result;

        if (arg instanceof String)
        {
            result = new StringDecorator(name, (String) arg);
        }
        else if (arg instanceof Boolean)
        {
            result = new Decorator<>(name, arg);
        }
        else if (arg instanceof Integer)
        {
            result = new Decorator<>(name, arg);
        }
        else if (arg instanceof Long)
        {
            result = new Decorator<>(name, arg);
        }
        else if (arg instanceof Float)
        {
            result = new Decorator<>(name, arg);
        }
        else if (arg instanceof Double)
        {
            result = new Decorator<>(name, arg);
        }
        else if (arg instanceof Date)
        {
            result = new DateDecorator(name, (Date) arg);
        }
        else if (arg instanceof Collection<?>)
        {
            final List<Decorator<?>> aux =
                new ArrayList<>(((Collection<Object>) arg).size());

            for (final Object item: (Collection<Object>) arg)
            {
                aux.add(decorateElement("", item));
            }
            result = new CollectionDecorator<>(name, aux);
        }
        else if (arg instanceof Object[])
        {
            result = new CollectionDecorator<>(name, Arrays.asList(arg));
        }
        else
        {
            result = new Decorator<>(name, arg);
        }

        return result;
    }

    /**
     * Audits a call to toString(), to avoid infinite loops.
     * @param <T> the class type.
     * @param instance the instance to call toString() on.
     * @return the result of calling toString() on given instance,
     * unless an infinite loop is detected, in which an empty string
     * is returned.
     */
    
    public <T> String auditToString(final T instance)
    {
        final String result;

        if (stackTraceContainsRecursiveToStringCalls(instance.getClass()))
        {
            LogFactory.getLog("toString-monitor").error(
                "Detected recursive calls to " + instance.getClass() + "#toString()");

            new RuntimeException("dummy").printStackTrace(System.err);

            System.exit(1);

            result = "";
        }
        else
        {
            result = instance.toString();
        }

        return result;
    }

    /**
     * Checks whether given stack trace contains recursive calls to given class' toString() method.
     * @param <T> the class type.
     * @param clazz the class.
     * @return {@code true} in such case.
     */
    public <T> boolean stackTraceContainsRecursiveToStringCalls(final Class<T> clazz)
    {
        return stackTraceContainsRecursiveToStringCalls(new RuntimeException("").getStackTrace(), clazz);
    }

    /**
     * Checks whether given stack trace contains recursive calls to given class' toString() method.
     * @param <T> the class type.
     * @param stackTrace the stack trace.
     * @param clazz the class.
     * @return {@code true} in such case.
     */
    protected <T> boolean stackTraceContainsRecursiveToStringCalls(
        final StackTraceElement[] stackTrace, final Class<T> clazz)
    {
        return stackTraceContainsRecursiveCalls(stackTrace, clazz, "toString");
    }

    /**
     * Checks whether given stack trace contains recursive calls to given class' method.
     * @param <T> the class type.
     * @param stackTrace the stack trace.
     * @param clazz the class.
     * @param methodName the method name.
     * @return {@code true} in such case.
     */
    protected <T> boolean stackTraceContainsRecursiveCalls(
        final StackTraceElement[] stackTrace,
        final Class<T> clazz,
        final String methodName)
    {
        boolean result = false;

        int count = 0;

        for (final StackTraceElement stackEntry : stackTrace)
        {
            if (stackEntry != null)
            {
                final String className = stackEntry.getClassName();
                if (   (className != null)
                       && (className.equals(clazz.getName()))
                       && (methodName.equals(stackEntry.getMethodName())))
                {
                    count++;
                }

                if (count == 2)
                {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Decorates values to properly serialize them as JSON.
     * @param <T> the type of the wrapped value.
     */
    protected static class Decorator<T>
    {
        /**
         * The name.
         */
        private final String name;

        /**
         * The wrapped value.
         */
        private final T arg;

        /**
         * Creates a decorator.
         * @param name the name.
         * @param arg the value to wrap.
         */
        public Decorator(final String name, final T arg)
        {
            this.name = name;
            this.arg = arg;
        }

        /**
         * Indicates the value is decorated.
         * @return {@code true}.
         */
        @SuppressWarnings("unused")
        public boolean isRepresentedAsString()
        {
            return !isPrimitive();
        }

        /**
         * Retrieves the name.
         * @return such information.
         */
        
        public String getName()
        {
            return this.name;
        }

        /**
         * Retrieves the wrapped value.
         * @return such value.
         */
        
        protected T getArg()
        {
            return this.arg;
        }

        /**
         * Retrieves the wrapped value.
         * @return such value.
         */
        
        public String getValue()
        {
            return getArg().toString();
        }

        /**
         * Retrieves the file class.
         * @return such class.
         */
        @SuppressWarnings({"unused", "unchecked"})
        public Class<T> getFileClass()
        {
            return (Class<T>) this.arg.getClass();
        }

        /**
         * Checks whether the wrapped value is an array.
         * @return {@code true} in such case.
         */
        public boolean isArray()
        {
            return (this.arg instanceof Object[]);
        }

        /**
         * Checks whether the wrapped instance is compound.
         * @return {@code true} in such case.
         */
        @SuppressWarnings("unused")
        public boolean isCompound()
        {
            return
                (   !(isPrimitive())
                 && !(this.arg instanceof String));
        }

        /**
         * Checks whether the decorator wraps a primitive value.
         * @return {@code true} in such case.
         */
        public boolean isPrimitive()
        {
            final boolean result;

            result =
                    (this.arg instanceof Boolean)
                 || (this.arg instanceof Integer)
                 || (this.arg instanceof Long)
                 || (this.arg instanceof Float)
                 || (this.arg instanceof Double)
                 || (this.arg instanceof BigInteger)
                 || (this.arg instanceof BigDecimal);

            return result;
        }

        /**
         * Generates a JSON text representing the instance.
         * @return such text.
         */
        @SuppressWarnings("unused")
        protected String toStringSimple()
        {
            final STGroup stGroup = new STGroupFile(TO_JSON);

            final ST template = stGroup.getInstanceOf(Literals.TO_JSON);

            template.add("name", this.name);
            template.add("attr", this.arg);

            final String result = template.render();

            return result;
        }

        /**
         * Generates a JSON text representing the instance.
         * @return such text.
         */
        @SuppressWarnings("unused")
        protected String toStringCompound()
        {
            return getValue();
        }

        /**
         * Generates a JSON text representing the instance.
         *
         * @return such text.
         */
        @Override
        public String toString()
        {
            return
                  "{ \"class\": \"" + Decorator.class.getName() + '"'
                + ", \"name\": \"" + name + '"'
                + ", \"arg\": \"" + arg + "\" ";
        }
    }

    /**
     * A String decorator.
     */
    private static class StringDecorator
        extends Decorator<String>
    {
        /**
         * Creates a string decorator.
         * @param name the name.
         * @param arg the string value.
         */
        public StringDecorator(final String name, final String arg)
        {
            super(name, arg);
        }

        /**
         * Generates a JSON text representing the instance.
         * @return such text.
         */
        @Override
        
        public String toString()
        {
            final STGroup stGroup = new STGroupFile(TO_JSON);

            final ST template = stGroup.getInstanceOf("toStringImpl");

            template.add("obj", this);
            template.add("fClass", "");
            template.add(Literals.ATTRS, new HashMap<String, Object>(0));

            final String result = template.render();

            return result;
        }

        /**
         * Checks whether it's represented as string.
         * @return {@code true}.
         */
        @SuppressWarnings("unused")
        public boolean representedAsString()
        {
            return true;
        }
    }

    /**
     * A date decorator.
     */
    private static class DateDecorator
        extends Decorator<Date>
    {
        /**
         * Creates a date decorator.
         * @param name the name.
         * @param arg the date value.
         */
        public DateDecorator(final String name, final Date arg)
        {
            super(name, arg);
        }

        /**
         * Checks whether the wrapped instance is compound.
         * @return {@code true} in such case.
         */
        @Override
        @SuppressWarnings("unused")
        public boolean isCompound()
        {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRepresentedAsString()
        {
            return true;
        }

        /**
         * Retrieves the date, in String format.
         * @return the date.
         */
        
        @Override
        public String getValue()
        {
            return new SimpleDateFormat("YYYY-MM-ddHH:mm:ss.SSSZ").format(getArg());
        }
    }

    /**
     * A date decorator.
     * @param <T> the type.
     */
    static class CollectionDecorator<T>
        extends Decorator<Collection<T>>
    {
        /**
         * Creates a date decorator.
         * @param name the name.
         * @param arg the date value.
         */
        public CollectionDecorator(final String name, final Collection<T> arg)
        {
            super(name, arg);
        }

        /**
         * Checks whether the wrapped instance is compound.
         * @return {@code true} in such case.
         */
        @Override
        @SuppressWarnings("unused")
        public boolean isCompound()
        {
            return true;
        }

        /**
         * Checks whether the wrapped instance is a collection.
         * @return {@code true} in such case.
         */
        public boolean isCollection()
        {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRepresentedAsString()
        {
            return false;
        }

        /**
         * Generates a JSON text representing the instance.
         * @return such text.
         */
        @Override
        public String toString()
        {
            final STGroup stGroup = new STGroupFile(TO_JSON);

            final ST template = stGroup.getInstanceOf(Literals.TO_JSON);

            final String name = getName();

            if (!name.isEmpty())
            {
                template.add("name", name);
            }

            final Collection<T> value = getArg();

            final List<Decorator<?>> aux =
                new ArrayList<>((value).size());

            for (final Object item: value)
            {
                aux.add(ToStringUtils.getInstance().decorateElement("", item));
            }

            template.add("attr", aux.toString());

            final String result = template.render();

            return result;
        }
    }
}
