package de.saring.sportstracker.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper class for common functions when reading and writing XML files.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class XMLUtils {

    private XMLUtils () {
    }

    /**
     * Parses the specified XML file and creates the JDOM document. The XML will
     * be verified against the specified XSD schema (will be read from classpath).
     *
     * @param xmlFile the XML file to parse
     * @param xsdFilename the name of the XSD (just the filename)
     * @return the created JDOM Document
     * @throws java.io.IOException
     * @throws org.jdom.JDOMException
     */
    public static Document getJDOMDocument (final File xmlFile, final String xsdFilename) throws IOException, JDOMException {

        // create a SAX parser with XSD validation
        SAXBuilder builder = new SAXBuilder(true);
        builder.setFeature ("http://apache.org/xml/features/validation/schema", true);
        builder.setProperty ("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "file://" + xsdFilename);

        // define the EntityResolver for loading the XSD schema as a resource
        // from the classpath instead from filesystem
        builder.setEntityResolver (new EntityResolver () {
            public InputSource resolveEntity (String publicId, String systemId) throws SAXException, IOException {
                if (systemId.toLowerCase ().endsWith (xsdFilename)) {
                    return new InputSource (this.getClass ().getResourceAsStream ("/xml/" + xsdFilename));
                }
                else {
                    return null;
                }
            }
        });

        // create JDOM Document
        return builder.build (xmlFile);
    }

    /**
     * Creates the element with the specified name and text and adds it to the parent.
     * @param eParent the parent element
     * @param name name of the new element
     * @param text text of the new element
     */
    public static void addElement (Element eParent, String name, String text) {
        Element element = new Element (name);
        element.setText (text);
        eParent.addContent (element);
    }

    /**
     * Writes the specified JDOM element to the XML file specified by filename.
     * The file will use UTF-8 encoding and has 4-space indentation.
     * @param eRoot root element of the XML document
     * @param filename filename of the XML file to create
     * @throws IOException
     */
    public static void writeXMLFile (Element eRoot, String filename) throws IOException {

        Document document = new Document (eRoot);
        XMLOutputter outputter = new XMLOutputter ();
        Format format = Format.getPrettyFormat ();
        format.setLineSeparator (System.getProperty ("line.separator"));
        format.setIndent ("    ");
        outputter.setFormat (format);
        
        // FileWriter can't be used here, because default encoding on Win32 isn't UTF-8
        try (OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")) {
            outputter.output (document, osWriter);
            osWriter.flush ();
        }
    }
}
