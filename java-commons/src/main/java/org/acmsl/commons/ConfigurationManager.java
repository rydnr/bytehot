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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: ConfigurationManager.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides basic methods to allow specialized classes to
 *              manage the configuration of applications.
 */
package org.acmsl.commons;

/*
 * Importing project classes.
 */
import org.acmsl.commons.patterns.Manager;

/*
 * Importing JetBrains annotations..
 */

/*
 * Importing some JDK classes.
 */
import java.io.InputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/*
 * Importing Apache Commons Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/**
 * Provides basic methods to allow specialized classes to manage th
 * configuration of applications.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 */
public abstract class ConfigurationManager
    implements  Manager
{
    /**
     * In-memory configuration settings.
     */
    private Properties m__Properties = null;

    /**
     * Default protected constructor to avoid accidental instantiation.
     */
    protected ConfigurationManager() {}

    /**
     * Retrieves the properties file name.
     * @return such name.
     */
    
    protected abstract String getPropertiesFileName();

    /**
     * Specifies the configuration settings.
     * @param properties the properties.
     */
    protected final void immutableSetProperties(final Properties properties)
    {
        m__Properties = properties;
    }
    
    /**
     * Retrieves the configuration settings.
     * @return the properties.
     */
    
    protected final Properties immutableGetProperties()
    {
        return m__Properties;
    }
    
    /**
     * Retrieves the configuration settings.
     * @return the properties.
     */
    
    public Properties getProperties()
    {
        return getProperties(getPropertiesFileName());
    }

    /**
     * Retrieves the configuration settings.
     * @param propertiesFileName the properties' file name.
     * @return the properties.
     */
    
    public synchronized final Properties getProperties(
        final String propertiesFileName)
    {
        return
            getProperties(
                propertiesFileName,
                immutableGetProperties());
    }
    
    /**
     * Retrieves the configuration settings.
     * @param propertiesFileName the properties' file name.
     * @param properties the properties.
     * @return the properties.
     */
    
    protected final Properties getProperties(
        final String propertiesFileName,
        final Properties properties)
    {
        final Properties result;
        
        if  (properties == null)
        {
            result = new Properties();
            immutableSetProperties(result);
        }
        else
        {
            result = properties;
        }

        if  (result.size() == 0)
        {
            loadProperties(result, propertiesFileName);
        }

        return result;
    }

    /**
     * Retrieves the property.
     * @param key the setting name.
     * @return the configuration value associated to such setting.
     */
    
    public String getProperty(final String key)
    {
        return getProperty(key, getProperties());
    }

    /**
     * Retrieves the property.
     * @param key the setting name.
     * @param properties the property collection.
     * @return the configuration value associated to such setting.
     */
    
    protected String getProperty(final String key, final Properties properties)
    {
        return properties.getProperty(key);
    }

    /**
     * Retrieves the array property.
     * @param name the name.
     * @return the array of values.
     */
    
    @SuppressWarnings("unused")
    public String[] getStringArray(final String name)
    {
        final String[] result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getStringArrayValue(t_strValue);
        }
        else
        {
            result = new String[0];
        }

        return result;
    }

    /**
     * Retrieves the array property.
     * @param value the value.
     * @return the array of values.
     */
    
    protected String[] getStringArrayValue(final String value)
    {
        return value.split(",");
    }

    /**
     * Specifies an array property.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setStringArray(@SuppressWarnings("unused") final String name, final String[] value)
    {
        final StringBuilder propertyValue = new StringBuilder();

        for  (int index = 0; index < value.length; index++)
        {
            propertyValue.append(value[index]);

            if  (index < value.length - 1)
            {
                propertyValue.append(",");
            }
        }
    }

    /**
     * Retrieves the property.
     * @param key the setting name.
     * @param value the value.
     */
    public void setProperty(final String key, final String value)
    {
        setProperty(key, value, getProperties());
    }

    /**
     * Specifies the property.
     * @param key the setting name.
     * @param value the value.
     * @param properties the property collection.
     */
    protected void setProperty(
        final String key, final String value, final Properties properties)
    {
        properties.setProperty(key, value);
    }

    /**
     * Retrieves a concrete integer setting.
     * @param name the name.
     * @return such parameter.
     */
    @SuppressWarnings("unused")
    public int getIntProperty(final String name)
    {
        final int result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getIntProperty(name, t_strValue);
        }
        else
        {
            result = -1;
        }

        return result;
    }

    /**
     * Retrieves a concrete integer setting.
     * @param name the name.
     * @param value the value.
     * @return such parameter.
     */
    protected int getIntProperty(
        final String name, final String value)
    {
        int result = -1;

        try
        {
            result = Integer.parseInt(value);
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).fatal(
                "Invalid integer setting (" + name + "): " + value,
                numberFormatException);
        }

        return result;
    }

    /**
     * Specifies a concrete integer setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setIntProperty(final String name, final int value)
    {
        setProperty(name, String.valueOf(value));
    }

    /**
     * Retrieves an array of integers associated to given name.
     * @param name the name.
     * @return such value.
     */
    @SuppressWarnings("unused")
    
    public int[] getIntArray(final String name)
    {
        final int[] result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getIntArray(name, t_strValue);
        }
        else
        {
            result = new int[0];
        }

        return result;
    }

    /**
     * Retrieves an array of integers associated to given name.
     * @param name the name.
     * @param value the property value.
     * @return such parsed value.
     */
    
    protected int[] getIntArray(final String name, final String value)
    {
        final int[] result;

        final String[] splitValue = value.split(",");

        result = new int[splitValue.length];

        try
        {
            for  (int t_iIndex = 0; t_iIndex < splitValue.length; t_iIndex++)
            {
                result[t_iIndex] = Integer.parseInt(splitValue[t_iIndex]);
            }
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).fatal(
                "Invalid integer array setting (" + name + "): " + value,
                numberFormatException);
        }

        return result;
    }

    /**
     * Specifies an integer setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setIntArray(final String name, final int[] value)
    {
        final StringBuilder propertyValue = new StringBuilder();

        for  (int index = 0; index < value.length; index++)
        {
            propertyValue.append(value[index]);

            if  (index < value.length - 1)
            {
                propertyValue.append(",");
            }
        }

        setProperty(name, propertyValue.toString());
    }

    /**
     * Retrieves a concrete long setting.
     * @param name the name.
     * @return such parameter.
     */
    @SuppressWarnings("unused")
    public long getLongProperty(final String name)
    {
        final long result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getLongProperty(name, t_strValue);
        }
        else
        {
            result = -1;
        }

        return result;
    }

    /**
     * Retrieves a concrete long setting.
     * @param name the name.
     * @param value the value.
     * @return such parameter.
     */
    protected long getLongProperty(
        final String name, final String value)
    {
        long result = -1;

        try
        {
            result = Long.parseLong(value);
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).fatal(
                "Invalid long setting (" + name + "): " + value,
                numberFormatException);
        }

        return result;
    }

    /**
     * Specifies a concrete long setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setLongProperty(final String name, final long value)
    {
        setProperty(name, String.valueOf(value));
    }

    /**
     * Retrieves an array of longs associated to given name.
     * @param name the name.
     * @return such value.
     */
    
    @SuppressWarnings("unused")
    public long[] getLongArray(final String name)
    {
        final long[] result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getLongArray(name, t_strValue);
        }
        else
        {
            result = new long[0];
        }

        return result;
    }

    /**
     * Retrieves an array of longs associated to given name.
     * @param name the name.
     * @param value the property value.
     * @return such parsed value.
     */
    
    protected long[] getLongArray(final String name, final String value)
    {
        final long[] result;

        final String[] splitValue = value.split(",");

        result = new long[splitValue.length];

        try
        {
            for  (int index = 0; index < result.length; index++)
            {
                result[index] = Long.parseLong(splitValue[index]);
            }
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).fatal(
                "Invalid long array setting (" + name + "): " + value,
                numberFormatException);
        }

        return result;
    }

    /**
     * Specifies an long setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setLongArray(final String name, final long[] value)
    {
        final StringBuilder propertyValue = new StringBuilder();

        for  (int index = 0; index < value.length; index++)
        {
            propertyValue.append("" + value[index]);

            if  (index < value.length - 1)
            {
                propertyValue.append(",");
            }
        }

        setProperty(name, propertyValue.toString());
    }

    /**
     * Retrieves a concrete double setting.
     * @param name the name.
     * @return such parameter.
     */
    @SuppressWarnings("unused")
    public double getDoubleProperty(final String name)
    {
        final double result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getDoubleProperty(name, t_strValue);
        }
        else
        {
            result = -1.0d;
        }

        return result;
    }

    /**
     * Retrieves a concrete double setting.
     * @param name the name.
     * @param value the value.
     * @return such parameter.
     */
    protected double getDoubleProperty(
        final String name, final String value)
    {
        double result = -1.0d;

        try
        {
            result = Double.parseDouble(value);
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).fatal(
                "Invalid double setting (" + name + "): " + value,
                numberFormatException);
        }

        return result;
    }

    /**
     * Specifies a concrete double setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setDoubleProperty(final String name, final double value)
    {
        setProperty(name, String.valueOf(value));
    }

    /**
     * Retrieves an array of doubles associated to given name.
     * @param name the name.
     * @return such value.
     */
    @SuppressWarnings("unused")
    
    public double[] getDoubleArray(final String name)
    {
        final double[] result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getDoubleArray(name, t_strValue);
        }
        else
        {
            result = new double[0];
        }

        return result;
    }

    /**
     * Retrieves an array of doubles associated to given name.
     * @param name the name.
     * @param value the property value.
     * @return such parsed value.
     */
    
    protected double[] getDoubleArray(final String name, final String value)
    {
        final double[] result;

        final String[] splitValue = value.split(",");

        result = new double[splitValue.length];

        try
        {
            for  (int index = 0; index < result.length; index++)
            {
                result[index] = Double.parseDouble(splitValue[index]);
            }
        }
        catch  (final NumberFormatException numberFormatException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).fatal(
                "Invalid double array setting (" + name + "): " + value,
                numberFormatException);
        }

        return result;
    }

    /**
     * Specifies an double setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setDoubleArray(final String name, final double[] value)
    {
        final StringBuilder propertyValue = new StringBuilder();

        for  (int index = 0; index < value.length; index++)
        {
            propertyValue.append("" + value[index]);

            if  (index < value.length - 1)
            {
                propertyValue.append(",");
            }
        }

        setProperty(name, propertyValue.toString());
    }

    /**
     * Retrieves a concrete boolean setting.
     * @param name the name.
     * @return such parameter.
     */
    @SuppressWarnings("unused")
    public boolean getBooleanProperty(final String name)
    {
        final boolean result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getBooleanPropertyValue(t_strValue);
        }
        else
        {
            result = false;
        }

        return result;
    }

    /**
     * Retrieves a concrete boolean setting.
     * @param value the value.
     * @return such parameter.
     */
    protected boolean getBooleanPropertyValue(final String value)
    {
        return Boolean.valueOf(value).booleanValue();
    }

    /**
     * Specifies a concrete boolean setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setBooleanProperty(final String name, final boolean value)
    {
        setProperty(name, String.valueOf(value));
    }

    /**
     * Retrieves an array of boolean values associated to given name.
     * @param name the name.
     * @return such value.
     */
    @SuppressWarnings("unused")
    
    public boolean[] getBooleanArray(final String name)
    {
        final boolean[] result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getBooleanArrayValue(t_strValue);
        }
        else
        {
            result = new boolean[0];
        }

        return result;
    }

    /**
     * Retrieves an array of boolean values associated to given name.
     * @param value the property value.
     * @return such parsed value.
     */
    
    protected boolean[] getBooleanArrayValue(final String value)
    {
        final boolean[] result;

        final String[] splitValue = value.split(",");

        result = new boolean[splitValue.length];

        for  (int index = 0; index < result.length; index++)
        {
            result[index] = Boolean.valueOf(splitValue[index]);
        }

        return result;
    }

    /**
     * Specifies an boolean setting.
     * @param name the name.
     * @param value the value.
     */
    @SuppressWarnings("unused")
    public void setBooleanArray(final String name, final boolean[] value)
    {
        final StringBuilder propertyValue = new StringBuilder();

        for  (int index = 0; index < value.length; index++)
        {
            propertyValue.append(value[index]);

            if  (index < value.length - 1)
            {
                propertyValue.append(",");
            }
        }

        setProperty(name, propertyValue.toString());
    }

    /**
     * Retrieves a date property.
     * @param name the name.
     * @param format the date format.
     * @return the property.
     */
    @SuppressWarnings("unused")
    
    public Date getDateProperty(final String name, final String format)
    {
        final Date result;

        final String t_strValue = getProperty(name);

        if (t_strValue != null)
        {
            result = getDateProperty(name, format, t_strValue);
        }
        else
        {
            result = null;
        }

        return result;
    }

    /**
     * Retrieves a date property.
     * @param name the name.
     * @param format the date format.
     * @param value the value.
     * @return the property.
     */
    
    protected Date getDateProperty(
        final String name, final String format, final String value)
    {
        Date result = null;

        try
        {
            result = new SimpleDateFormat(format).parse(value);
        }
        catch  (final ParseException parseException)
        {
            LogFactory.getLog(ConfigurationManager.class + "." + getClass()).error(
                "Invalid date property (" + name + "):" + value,
                parseException);
        }

        return result;
    }

    /**
     * Specifies a date property.
     * @param name the name.
     * @param value the value.
     * @param format the format.
     */
    @SuppressWarnings("unused")
    public void setDateProperty(
        final String name, final Date value, final String format)
    {
        setProperty(name, new SimpleDateFormat(format).format(value));
        setProperty(name + ".format", format);
    }

    /**
     * Loads the configuration from a property file.
     * @param properties where to store the settings.
     * @param propertiesFileName the properties file.
     */
    public final void loadProperties(
        final Properties properties, final String propertiesFileName)
    {
        loadProperties(properties, propertiesFileName, true);
    }

    /**
     * Loads the configuration from a property file.
     * @param properties where to store the settings.
     * @param propertiesFileName the properties file.
     * @param retry whether to retry to load the properties appending an slash
     * at the beginning of the file name or not.
     */
    public synchronized void loadProperties(
        final Properties properties,
        final String propertiesFileName,
        final boolean retry)
    {
        InputStream propertiesFile = null;

        final boolean startsWithSlash = propertiesFileName.startsWith("/");

        final String alternatePropertiesFileName;

        if  (startsWithSlash)
        {
            alternatePropertiesFileName = propertiesFileName.substring(1);
        }
        else
        {
            alternatePropertiesFileName = "/" + propertiesFileName;
        }

        // Loading properties
        try
        {
            propertiesFile =
                getClass().getResourceAsStream(propertiesFileName);

            if  (propertiesFile != null)
            {
                properties.load(propertiesFile);
            }
            else if (retry)
            {
                LogFactory.getLog(ConfigurationManager.class + "." + getClass()).debug(
                      "Properties file: "
                    + propertiesFileName
                    + " not found. Trying " + alternatePropertiesFileName);

                loadProperties(
                    properties,
                    alternatePropertiesFileName,
                    false);
            }
            else
            {
                LogFactory.getLog(ConfigurationManager.class + "." + getClass()).warn(
                      "Could not load configuration properties from file: "
                    + propertiesFileName);
            }
        }
        catch  (final IOException ioException)
        {
            if  (retry)
            {
                LogFactory.getLog(ConfigurationManager.class + "." + getClass()).debug(
                      "Properties file: "
                    + propertiesFileName
                    + " not found. Trying " + alternatePropertiesFileName);

                loadProperties(
                    properties,
                    alternatePropertiesFileName,
                    false);
            }
            else
            {
                LogFactory.getLog(ConfigurationManager.class + "." + getClass()).warn(
                      "Could not load configuration properties from file: "
                    + propertiesFileName);
            }
        }
        finally
        {
            if (propertiesFile != null)
            {
                try
                {
                    propertiesFile.close();
                }
                catch (final IOException cannotClose)
                {
                    LogFactory.getLog(ConfigurationManager.class + "." + getClass()).warn(
                        "Could not close file: "
                        + propertiesFileName);
                }
            }
        }
    }

    @Override
    
    public String toString()
    {
        return "ConfigurationManager{ properties=" + m__Properties + " }";
    }
}
