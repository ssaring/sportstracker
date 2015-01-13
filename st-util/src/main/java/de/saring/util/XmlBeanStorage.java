package de.saring.util;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper class for reading and writing Java Bean objects from and to XML files (serialization),
 * it is based on the classes {@link java.beans.XMLDecoder} and {@link java.beans.XMLEncoder}.
 *
 * @author Stefan Saring
 */
public final class XmlBeanStorage {

    private XmlBeanStorage() {
    }

    /**
     * Loads the Java Bean object from the specified XML file.
     *
     * @param filename XML filename
     * @return the loaded object
     * @throws IOException when the filename does not exists or can't be read
     * @throws Exception when reading the bean has failed
     */
    public static Object loadBean(final String filename) throws Exception {

        try (final XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)))) {

            // use custom event listener for proper error handling and logging
            final XmlBeanExceptionListener exceptionListener = new XmlBeanExceptionListener();
            xmlDecoder.setExceptionListener(exceptionListener);

            final Object bean = xmlDecoder.readObject();
            if (exceptionListener.getThrownException() != null) {
                throw exceptionListener.getThrownException();
            }
            return bean;
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
    public static void saveBean(final Object bean, final String filename) throws Exception {

        try (final XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)))) {

            // use custom event listener for proper error handling and logging
            final XmlBeanExceptionListener exceptionListener = new XmlBeanExceptionListener();
            xmlEncoder.setExceptionListener(exceptionListener);

            xmlEncoder.writeObject(bean);
            if (exceptionListener.getThrownException() != null) {
                throw exceptionListener.getThrownException();
            }
        }
    }

    /**
     * ExceptionListener implementation which stores the first thrown exception for later evaluation.
     */
    private static class XmlBeanExceptionListener implements ExceptionListener {

        private Exception thrownException;

        @Override
        public void exceptionThrown(final Exception e) {
            if (thrownException == null) {
                this.thrownException = e;
            }
        }

        /**
         * Returns the first thrown exception if there was one.
         *
         * @return exception or null
         */
        public Exception getThrownException() {
            return thrownException;
        }
    }
}
