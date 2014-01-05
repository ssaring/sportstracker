package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.data.WeightList;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * This class is for reading or writing a WeightList object from or to a XML file.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class XMLWeightList {

    /**
     * The XSD filename with the structure of the weight list.
     */
    private static final String XSD_WEIGHTS = "weights.xsd";

    /**
     * The date and time parser instance.
     */
    private static final SimpleDateFormat SD_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Reads the weight list from the specified XML file.
     * Returns an empty list when the file doesn't exists yet.
     *
     * @param source name of the XML file to read from
     * @return the created WeightList
     * @throws STException thrown on read problems
     */
    public WeightList readWeightList(String source) throws STException {

        try {
            // return an empty list if the file doesn't exists yet
            WeightList weightList = new WeightList();
            File fSource = new File(source);
            if (!fSource.exists()) {
                return weightList;
            }

            // create JDOM Document from XML with XSD validation
            Document document = XMLUtils.getJDOMDocument(fSource, XSD_WEIGHTS);

            // get root element and read all the contained weights
            Element eWeightList = document.getRootElement();
            eWeightList.getChildren("weight").forEach(eWeight ->
                weightList.set(readWeight(eWeight)));

            return weightList;
        } catch (Exception e) {
            throw new STException(STExceptionID.XMLSTORAGE_READ_WEIGHT_LIST,
                    "Failed to read weight list from XML file '" + source + "' ...", e);
        }
    }

    /**
     * Reads the data from the specified weight element and returns the created
     * Weight object.
     *
     * @param eWeight weight JDOM element
     * @return the created Weight object
     */
    private Weight readWeight(Element eWeight) {
        Weight weight = new Weight(Integer.parseInt(eWeight.getChildText("id")));
        weight.setValue(Float.parseFloat(eWeight.getChildText("value")));
        weight.setComment(eWeight.getChildText("comment"));

        // get and convert date (format allready checked by XSD schema)
        String strDate = eWeight.getChildText("date");
        try {
            weight.setDate(SD_FORMAT.parse(strDate));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse weight with ID '" + weight.getId() +
                    "', the date format '" + strDate + "' is not valid!");
        }
        return weight;
    }

    /**
     * Writes the WeightList to the specified XML file.
     *
     * @param weightList the WeightList to store
     * @param destination name of xml file to write to
     * @throws STException thrown on store problems
     */
    public void storeWeightList(WeightList weightList, String destination) throws STException {

        // create JDOM element with all weights
        Element eWeightList = createWeightListElement(weightList);

        // write the element to XML file
        try {
            XMLUtils.writeXMLFile(eWeightList, destination);
        } catch (IOException e) {
            throw new STException(STExceptionID.XMLSTORAGE_STORE_WEIGHT_LIST,
                    "Failed to write weight list to XML file '" + destination + "' ...", e);
        }
    }

    /**
     * Creates the "weight-list" element with all weights for the specified
     * weight list.
     *
     * @param weightList the WeightList to store
     * @return the created Element
     */
    private Element createWeightListElement(WeightList weightList) {
        Element eWeightList = new Element("weight-list");

        weightList.forEach(weight -> {
            Element eWeight = new Element("weight");
            eWeightList.addContent(eWeight);

            XMLUtils.addElement(eWeight, "id", String.valueOf(weight.getId()));
            XMLUtils.addElement(eWeight, "date", SD_FORMAT.format(weight.getDate()));
            XMLUtils.addElement(eWeight, "value", String.valueOf(weight.getValue()));
            XMLUtils.addElement(eWeight, "comment", weight.getComment());
        });
        return eWeightList;
    }
}