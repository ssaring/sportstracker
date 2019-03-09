package de.saring.util

import java.beans.ExceptionListener
import java.beans.XMLDecoder
import java.beans.XMLEncoder
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Helper class for reading and writing Java Bean objects from and to XML files (serialization). It is based on the
 * classes [java.beans.XMLDecoder] and [java.beans.XMLEncoder].
 *
 * @author Stefan Saring
 */
object XmlBeanStorage {

    /**
     * Loads the Java Bean object from the specified XML file.
     *
     * @param filename XML filename
     * @return the loaded object
     * @throws IOException when the filename does not exists or can't be read
     * @throws Exception when reading the bean has failed
     */
    @JvmStatic
    @Throws(Exception::class)
    fun loadBean(filename: String): Any {

        XMLDecoder(BufferedInputStream(FileInputStream(filename))).use { xmlDecoder ->

            // use custom event listener for proper error handling and logging
            val exceptionListener = XmlBeanExceptionListener()
            xmlDecoder.exceptionListener = exceptionListener

            val bean = xmlDecoder.readObject()
            exceptionListener.thrownException?.let { throw it }
            return bean
        }
    }

    /**
     * Saves the passed Java Bean object to the specified XML file.
     *
     * @param bean the object to store
     * @param filename XML filename
     * @throws IOException when the specified file can't be created or written
     * @throws Exception when writing the bean has failed
     */
    @JvmStatic
    @Throws(Exception::class)
    fun saveBean(bean: Any, filename: String) {

        XMLEncoder(BufferedOutputStream(FileOutputStream(filename))).use { xmlEncoder ->

            // use custom event listener for proper error handling and logging
            val exceptionListener = XmlBeanExceptionListener()
            xmlEncoder.exceptionListener = exceptionListener

            xmlEncoder.writeObject(bean)
            exceptionListener.thrownException?.let { throw it }
        }
    }

    /**
     * ExceptionListener implementation which stores the first thrown exception for later evaluation.
     */
    private class XmlBeanExceptionListener : ExceptionListener {

        /**
         * The first thrown exception (if there was one).
         */
        var thrownException: Exception? = null
            private set

        override fun exceptionThrown(e: Exception) {
            if (thrownException == null) {
                this.thrownException = e
            }
        }
    }
}
