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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-307  USA

    Thanks to ACM S.L. for distributing this library under the LGPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: FileUtils.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Provides some useful methods when working with files.
 *
 */
package org.acmsl.commons.utils.io;

/*
 * Importing some ACM-SL classes.
 */
import org.acmsl.commons.Literals;
import org.acmsl.commons.patterns.Singleton;
import org.acmsl.commons.patterns.Utils;
import org.acmsl.commons.regexpplugin.Helper;
import org.acmsl.commons.regexpplugin.MalformedPatternException;
import org.acmsl.commons.regexpplugin.RegexpEngine;
import org.acmsl.commons.regexpplugin.RegexpEngineNotFoundException;
import org.acmsl.commons.regexpplugin.RegexpManager;
import org.acmsl.commons.regexpplugin.RegexpPluginMisconfiguredException;

/*
 * Importing some JDK1.3 classes.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.channels.Channel;
import java.nio.charset.Charset;

/*
 * Importing Apache Commons-Logging classes.
 */
import org.apache.commons.logging.LogFactory;

/*
 * Importing JetBrains annotations.
 */

/**
 * Provides some useful methods when working with files.
 * @author <a href="mailto:chous@acm-sl.org"
           >Jose San Leandro Armendariz</a>
 */
public class FileUtils
    implements  Utils,
                Singleton
{
    /**
     * Retrieves the {@link File} associated to given {@link FileInputStream}, if possible.
     * @param inputStream the input stream.
     * @return the file.
     */
    
    public File retrieveFile(final FileInputStream inputStream)
    {
        final File result;

        String t_strPath = null;

        try
        {
            final Field field = inputStream.getClass().getDeclaredField("path");
            field.setAccessible(true);
            t_strPath = (String) field.get(inputStream);
        }
        catch (final Throwable noField)
        {
            System.err.println(noField.getMessage());
        }

        if (t_strPath == null)
        {
            result = null;
        }
        else
        {
            result = new File(t_strPath);
        }

        return result;
    }

    /**
     * Retrieves the file name from an absolute path.
     * @param filePath the path.
     * @return just the file name (no folder information).
     */
    
    public String getFileName(final String filePath)
    {
        final String result;

        final int lastSeparatorPosition = filePath.lastIndexOf(File.separator);

        if (lastSeparatorPosition > -1)
        {
            if (lastSeparatorPosition < filePath.length() - 1)
            {
                result = filePath.substring(lastSeparatorPosition + 1);
            }
            else
            {
                result = "";
            }
        }
        else
        {
            result = filePath;
        }

        return result;
    }

    /**
     * Strips the last extension.
     * @param filePath the file path.
     * @return the file path, after removing the last extension.
     */
    
    public String stripExtension(final String filePath)
    {
        final String result;

        final int lastDotPosition = filePath.lastIndexOf(".");

        if (lastDotPosition > -1)
        {
            result = filePath.substring(0, lastDotPosition);
        }
        else
        {
            result = filePath;
        }

        return result;
    }

    /**
     * Removes all extensions from a given file name.
     * @param filePath the path.
     * @return the file path, with no extensions.
     */
    
    public String stripExtensions(final String filePath)
    {
        final String result;

        String aux = filePath;
        String aux2;

        do
        {
            aux2 = aux;
            aux = stripExtension(aux);
        }
        while (!aux.equals(aux2));

        result = aux;

        return result;
    }

    /**
     * Singleton implemented to avoid the double-checked locking.
     */
    private static class FileUtilsSingletonContainer
    {
        /**
         * The actual singleton.
         */
        
        public static final FileUtils SINGLETON = new FileUtils();
    }

    /**
     * Retrieves a FileUtils instance.
     * @return such instance.
     */
    
    public static FileUtils getInstance()
    {
        return FileUtilsSingletonContainer.SINGLETON;
    }

    /**
     * Protected constructor to avoid accidental instantiation.
     */
    protected FileUtils() {}

    /**
     * Reads a file pointed by given path, and returns its contents.
     * @param filePath the path to the file to be read.
     * @param charset the {@link Charset}.
     * @return the contents of the file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the operation is forbidden because of
     * security manager settings.
     * @throws IOException if some I/O exception occurs.
     */
    
    public String readFile(final String filePath, final Charset charset)
        throws  SecurityException,
                IOException
    {
        return readFile(new File(filePath), charset);
    }

    /**
     * Reads a file and returns its contents.
     * @param file the file to be read.
     * @param charset the {@link Charset}.
     * @return the contents of the file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the operation is forbidden because of
     * security manager settings.
     * @throws IOException if some I/O exception occurs.
     */
    
    public char[] readFileContents(final File file, final Charset charset)
        throws  SecurityException,
                IOException
    {
        final char[] result;

        FileInputStream t_fisFileStream = null;

        InputStreamReader t_isFileReader = null;

            /*
             * To read file's contents it's better to use BufferedReader class.
             */
        BufferedReader t_frPageBufferedReader = null;

        try
        {
            /*
             * Instantiate a FileReader object to read file's contents.
             */
            t_fisFileStream = new FileInputStream(file);

            t_isFileReader = new InputStreamReader(t_fisFileStream, charset);

            /*
             * To read file's contents it's better to use BufferedReader class.
             */
            t_frPageBufferedReader = new BufferedReader(t_isFileReader);

            if  (file.length() > Integer.MAX_VALUE)
            {
                throw
                    new IOException(
                        "File too large (" + file.length() + " bytes)");
            }

            /*
             * Next, I find out the necessary size of the array where file's
             * contents will be copied into.
             */
            result = new char[(int) file.length()];

            /*
             * Now I actually read the file, and fill the array.
             */
            t_frPageBufferedReader.read(
                result,
                0,
                result.length);
        }
        finally
        {
            if (t_frPageBufferedReader != null)
            {
                try
                {
                    t_frPageBufferedReader.close();
                }
                catch (final IOException cannotCloseStream)
                {
                    LogFactory.getLog(FileUtils.class).warn(
                        "Cannot close file", cannotCloseStream);
                }
            }
            if (t_isFileReader != null)
            {
                try
                {
                    t_isFileReader.close();
                }
                catch (final IOException cannotCloseStream)
                {
                    LogFactory.getLog(FileUtils.class).warn(
                        "Cannot close file", cannotCloseStream);
                }
            }
            if (t_fisFileStream != null)
            {
                try
                {
                    t_fisFileStream.close();
                }
                catch (final IOException cannotCloseStream)
                {
                    LogFactory.getLog(FileUtils.class).warn(
                        "Cannot close file", cannotCloseStream);
                }
            }
        }
        return result;
    }

    /**
     * Reads a file and returns its contents.
     * @param file the file to be read.
     * @param charset the {@link Charset}.
     * @return the contents of the file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the operation is forbidden because of
     * security manager settings.
     * @throws IOException if some I/O exception occurs.
     */
    
    public String readFile(final File file, final Charset charset)
        throws  SecurityException,
                IOException
    {
        /*
         * we can use a String constructor that exactly
         * fits our needs.
         */
        return new String(readFileContents(file, charset));
    }

    /**
     * Reads a file by its path and returns its contents, if possible. If some
     * exception occurs, it's ignored, and returns an empty String. This method
     * is used to avoid declaring try/catch blocks in client code.
     * @param filePath the path to the file to be read.
     * @param charset the {@link Charset}.
     * @return the contents of the file, or empty if reading cannot be
     * accomplished.
     */
    @SuppressWarnings("unused")
    
    public String readFileIfPossible(final String filePath, final Charset charset)
    {
        String result = null;

        try
        {
            result = readFile(filePath, charset);
        }
        catch  (final FileNotFoundException fileNotFoundException)
        {
            /*
            * We have chosen not to notify of exceptions, so this
            * block of code is only descriptive.
            */
            LogFactory.getLog(FileUtils.class).trace(
                "Cannot read file " + filePath,
                fileNotFoundException);
        }
        catch  (final SecurityException securityException)
        {
            /*
            * We have chosen not to notify of exceptions, so this
            * block of code is only descriptive.
            */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot read file " + filePath,
                securityException);
        }
        catch  (final IOException ioException)
        {
            /*
            * We have chosen not to notify of exceptions, so this
            * block of code is only descriptive.
            */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot read file " + filePath,
                ioException);
        }

        if (result == null)
        {
            result = "";
        }

        return result;
    }

    /**
     * Reads a file and returns its contents, if possible. If some exception
     * occurs, it's ignored, and returns an empty String. This method is used
     * to avoid declaring try/catch blocks in client code.
     * @param file the file to be read.
     * @param charset the {@link Charset}.
     * @return the contents of the file, or empty if reading cannot be
     * accomplished.
     */
    @SuppressWarnings("unused")
    
    public String readFileIfPossible(final File file, final Charset charset)
    {
        String result = null;

        try
        {
            result = readFile(file, charset);
        }
        catch  (final FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).trace(
                "Cannot read file " + file,
                fileNotFoundException);
        }
        catch  (final SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot read file " + file,
                securityException);
        }
        catch  (final IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot read file " + file,
                ioException);
        }

        return result;
    }

    /**
     * Tries to read from given stream.
     * @param inputStream the stream to read from.
     * @return the contents read.
     * @throws IOException if the input stream could not be read or closed.
     */
    
    public String read(final InputStream inputStream)
        throws  IOException
    {
        final BufferedInputStream bufferedInputStream;

        if  (inputStream instanceof BufferedInputStream)
        {
            bufferedInputStream = (BufferedInputStream) inputStream;
        }
        else
        {
            bufferedInputStream = new BufferedInputStream(inputStream);
        }

        return read(bufferedInputStream);
    }

    /**
     * Tries to read from given stream.
     * @param bufferedInputStream the stream to read from.
     * @return the contents read.
     * @throws IOException if the input stream could not be read or closed.
     */
    
    public String read(final BufferedInputStream bufferedInputStream)
        throws  IOException
    {
        final StringWriter t_swResult = new StringWriter();

        final PrintWriter t_Writer = new PrintWriter(t_swResult);

        int t_iReadChar = bufferedInputStream.read();

        while  (t_iReadChar != -1)
        {
            t_Writer.print((char) t_iReadChar);

            t_iReadChar = bufferedInputStream.read();
        }

        bufferedInputStream.close();

        t_Writer.close();

        return t_swResult.toString();
    }

    /**
     * Saves the contents to a file.
     * @param filePath the path of the file.
     * @param contents the text to save.
     * @param charset the file charset to use.
     * @return <code>true</code> if the process is successfully accomplished.
     */
    @SuppressWarnings("unused")
    public boolean writeFileIfPossible(
        final String filePath, final String contents, final Charset charset)
    {
        return writeFileIfPossible(new File(filePath), contents, charset);
    }

    /**
     * Saves the contents to a file.
     * @param file the file to be overwritten.
     * @param contents the text to save.
     * @param charset the file charset to use.
     * @return <code>true</code> if the process is successfully accomplished.
     */
    public boolean writeFileIfPossible(
        final File file, final String contents, final Charset charset)
    {
        boolean result = false;

        try
        {
            writeFile(file, contents, charset);
            result = true;
        }
        catch  (final FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot write file " + file,
                fileNotFoundException);
        }
        catch  (final SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot write file " + file,
                securityException);
        }
        catch  (final IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot write file " + file,
                ioException);
        }

        return result;
    }

    /**
     * Writes a file referred by given path, with given contents.
     * @param filePath the path of the file.
     * @param contents the text to write.
     * @param charset the file charset to use.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
     * operation.
     * @throws IOException if any other I/O error occurs.
     */
    @SuppressWarnings("unused")
    public void writeFile(
        final String filePath, final String contents, final Charset charset)
        throws  SecurityException,
                IOException
    {
        writeFile(new File(filePath), contents, charset);
    }

    /**
     * Writes a file with given contents.
     * @param file the file to write.
     * @param contents the text to write.
     * @param charset the charset to use.
     * @throws SecurityException if the security manager forbids this
     * operation.
     * @throws IOException if any other I/O error occurs.
     */
    public void writeFile(
        final File file, final String contents, final Charset charset)
        throws  SecurityException,
                IOException
    {
        FileOutputStream t_fosFileStream = null;

        OutputStreamWriter t_osFileWriter = null;

        final PrintWriter t_pwWriter;

        try
        {
            t_fosFileStream = new FileOutputStream(file);

            t_osFileWriter = new OutputStreamWriter(t_fosFileStream, charset);

            t_pwWriter = new PrintWriter(t_osFileWriter);

            t_pwWriter.println(contents);

            t_pwWriter.close();
        }
        finally
        {
            if (t_fosFileStream != null)
            {
                try
                {
                    t_fosFileStream.close();
                }
                catch (final IOException cannotClose)
                {
                    LogFactory.getLog(FileUtils.class).error(
                        "Cannot close file", cannotClose);
                }
            }
            if (t_osFileWriter != null)
            {
                try
                {
                    t_osFileWriter.close();
                }
                catch (final IOException cannotClose)
                {
                    LogFactory.getLog(FileUtils.class).error(
                        "Cannot close file", cannotClose);
                }
            }
        }
    }

    /**
     * Copies the contents of a file to another.
     * @param original the content to copy.
     * @param destination the file to be overwritten.
     * @param charset the {@link Charset}.
     * @throws SecurityException if the security manager forbids this
     * operation.
     * @throws IOException if any other I/O error occurs.
     */
    public void copy(final File original, final File destination, final Charset charset)
        throws  SecurityException,
                IOException
    {
        writeFile(destination, readFile(original, charset), charset);
    }

    /**
     * Copies the contents of a file (referred by given path) to another.
     * @param originalPath the path of the file to copy.
     * @param destinationPath the path of the file to be overwritten.
     * @param charset the {@link Charset}.
     * @return <code>true</code> if the operation ends up successfully.
     */
    @SuppressWarnings("unused")
    public boolean copyIfPossible(
        final String originalPath, final String destinationPath, final Charset charset)
    {
        return
            copyIfPossible(new File(originalPath), new File(destinationPath), charset);
    }

    /**
     * Copies the contents of a file to another.
     * @param original the content to copy.
     * @param destination the file to be overwritten.
     * @param charset the {@link Charset}.
     * @return <code>true</code> if the operation ends up successfully.
     */
    public boolean copyIfPossible(
        final File original, final File destination, final Charset charset)
    {
        boolean result = false;

        try
        {
            copy(original, destination, charset);

            result = true;
        }
        catch  (final FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot copy file " + original + " to " + destination,
                fileNotFoundException);
        }
        catch  (final SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot copy file " + original + " to " + destination,
                securityException);
        }
        catch  (final IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot copy file " + original + " to " + destination,
                ioException);
        }

        return result;
    }

    /**
     * Moves a file.
     * @param originalFile the file to move.
     * @param destinationFile the new file.
     * @param charset the {@link Charset}.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
     * operation.
     * @throws IOException if any other I/O error occurs.
     */
    public void move(
        final File originalFile, final File destinationFile, final Charset charset)
        throws  SecurityException,
                IOException
    {
        copy(originalFile, destinationFile, charset);

        if (!originalFile.delete())
        {
            throw new IOException("Cannot delete " + originalFile.getAbsolutePath());
        }
    }

    /**
     * Moves a file, if possible.
     * @param originalFile the file to move.
     * @param destinationFile the new file.
     * @param charset the {@link Charset}.
     * @return <code>true</code> if the operation ends up successfully.
     */
    public boolean moveIfPossible(
        final File originalFile, final File destinationFile, final Charset charset)
    {
        boolean result = false;

        try
        {
            move(originalFile, destinationFile, charset);
            result = true;
        }
        catch  (final FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot move file " + originalFile + " to " + destinationFile,
                fileNotFoundException);
        }
        catch  (final SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot move file " + originalFile + " to " + destinationFile,
                securityException);
        }
        catch  (final IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
            LogFactory.getLog(FileUtils.class).info(
                "Cannot move file " + originalFile + " to " + destinationFile,
                ioException);
        }

        return result;
    }

    /**
     * Moves a file from one path to another, if possible.
     * @param filePath the path of the file to move.
     * @param destinationPath the new file's path.
     * @param charset the {@link Charset}.
     * @return <code>true</code> if the operation ends up successfully.
     */
    @SuppressWarnings("unused")
    public boolean moveIfPossible(
        final String filePath, final String destinationPath, final Charset charset)
    {
        return
            moveIfPossible(
                new File(filePath),
                new File(destinationPath),
                charset);
    }

    /**
     * Translates given package name to a relative path.
     * @param packageName the package name.
     * @return the path.
     */
    
    public String packageToPath(final String packageName)
    {
        String result = null;

        try
        {
            final Helper t_Helper = createHelper(RegexpManager.getInstance());

            if (t_Helper != null)
            {
                result =
                    t_Helper.replaceAll(packageName, "\\.", File.separator);
            }
        }
        catch  (final MalformedPatternException malformedPatternException)
        {
            /*
             * This exception is about pattern mismatch. In my opinion,
             * it's an error that should be detected at compile time,
             * but regexp API design cannot provide this functionality,
             * since all patterns are defined with strings, and therefore
             * they escape all compiler checks.
             */
            LogFactory.getLog(FileUtils.class).warn(
                Literals.MALFORMED_STATIC_PATTERNS_ARE_FATAL_CODING_ERRORS,
                malformedPatternException);
        }
        catch  (final RegexpEngineNotFoundException regengNotFoundException)
        {
            /*
             * This exception is thrown only if no regexp library is available
             * at runtime. Not only this one, but any method provided by this
             * class that use regexps will not work.
             */
            LogFactory.getLog(FileUtils.class).fatal(
                Literals.CANNOT_FIND_ANY_REGEXP_ENGINE,
                regengNotFoundException);
        }
        catch  (final RegexpPluginMisconfiguredException
                      regexpPluginMisconfiguredException)
        {
            /*
             * This exception is thrown if RegexpPlugin cannot be configured
             * properly at runtime. Not only this one, but any method provided
             * by thisclass that use regexps will not work.
             */
            LogFactory.getLog(FileUtils.class).fatal(
                "Cannot configure RegexpPlugin.",
                regexpPluginMisconfiguredException);
        }

        return result;
    }

    /**
     * Creates the helper.
     * @return the regexp helper.
     */
    
    protected static synchronized Helper createHelper()
    {
        return createHelper(RegexpManager.getInstance());
    }

    /**
     * Creates the helper.
     * @param regexpManager the RegexpManager instance.
     * @return the regexp helper.
     */
    
    protected static synchronized Helper createHelper(
        final RegexpManager regexpManager)
    {
        return createHelper(regexpManager.getEngine());
    }

    /**
     * Creates the helper.
     * @param regexpEngine the RegexpEngine instance.
     * @return the regexp helper.
     */
    
    protected static synchronized Helper createHelper(
        final RegexpEngine regexpEngine)
    {
        return regexpEngine.createHelper();
    }
}
