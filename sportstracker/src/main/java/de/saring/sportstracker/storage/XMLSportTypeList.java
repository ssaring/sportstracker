package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.util.gui.javafx.ColorConverter;
import javafx.scene.paint.Color;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;

/**
 * This class is for reading and writing a SportTypeList object from or to a
 * XML file.
 *
 * @author Stefan Saring
 * @version 2.0
 */
public class XMLSportTypeList {

    /**
     * The XSD filename with the structure of the sport type list.
     */
    private static final String XSD_SPORT_TYPES = "sport-types.xsd";

    /**
     * Reads the sport type list from the specified XML file.
     * Returns an empty list when the file doesn't exists yet.
     *
     * @param source name of the XML file to read from
     * @return the created SportTypeList
     * @throws STException thrown on read problems
     */
    public SportTypeList readSportTypeList(String source) throws STException {

        try {
            // return an empty list if the file doesn't exists yet
            SportTypeList sportTypeList = new SportTypeList();
            File fSource = new File(source);
            if (!fSource.exists()) {
                return sportTypeList;
            }

            // create JDOM Document from XML with XSD validation
            Document document = XMLUtils.getJDOMDocument(fSource, XSD_SPORT_TYPES);

            // get root element and read all the contained sport types
            Element eSportTypeList = document.getRootElement();
            eSportTypeList.getChildren("sport-type").forEach(eSportType ->
                sportTypeList.set(readSportType(eSportType)));

            return sportTypeList;
        } catch (Exception e) {
            throw new STException(STExceptionID.XMLSTORAGE_READ_SPORT_TYPE_LIST,
                    "Failed to read sport type list from XML file '" + source + "' ...", e);
        }
    }

    /**
     * Reads the data from the specified sport-type element and returns the created
     * SportType object.
     *
     * @param eSportType sport-type JDOM element
     * @return the created SportType object
     */
    private SportType readSportType(Element eSportType) {

        SportType sportType = new SportType(Integer.parseInt(eSportType.getChildText("id")));
        sportType.setName(eSportType.getChildText("name"));
        sportType.setIcon(eSportType.getChildText("icon"));

        try {
            Element eColor = eSportType.getChild("color");
            int iRed = eColor.getAttribute("red").getIntValue();
            int iGreen = eColor.getAttribute("green").getIntValue();
            int iBlue = eColor.getAttribute("blue").getIntValue();
            sportType.setColor(Color.rgb(iRed, iGreen, iBlue));
        } catch (DataConversionException dce) {
            throw new IllegalArgumentException("Failed to parse the color of sport type with ID '" + sportType.getId() +
                    "', exception message: " + dce.getMessage());
        }

        // get optional attribute 'record-distance'
        Attribute aRecDistance = eSportType.getAttribute("record-distance");
        if (aRecDistance != null) {
            try {
                sportType.setRecordDistance(aRecDistance.getBooleanValue());
            } catch (DataConversionException dce) {
                throw new IllegalArgumentException("Failed to parse the record-distance of sport type with ID '" +
                        sportType.getId() + "', exception message: " + dce.getMessage());
            }
        }

        // read all contained sport sub types
        Element eSportSubTypeList = eSportType.getChild("sport-subtype-list");
        eSportSubTypeList.getChildren("sport-subtype").forEach(eSportSubType -> {
            SportSubType sportSubType = new SportSubType(Integer.parseInt(eSportSubType.getChildText("id")));
            sportSubType.setName(eSportSubType.getChildText("name"));
            sportType.getSportSubTypeList().set(sportSubType);
        });

        // read all contained equipment
        Element eEquipmentList = eSportType.getChild("equipment-list");
        if (eEquipmentList != null) {

            eEquipmentList.getChildren("equipment").forEach(eEquipment -> {
                Equipment equipment = new Equipment(Integer.parseInt(eEquipment.getChildText("id")));
                equipment.setName(eEquipment.getChildText("name"));
                sportType.getEquipmentList().set(equipment);
            });
        }
        return sportType;
    }

    /**
     * Writes the sport type list to the specified XML file.
     *
     * @param sportTypeList the sport type list to store
     * @param destination name of xml file to write to
     * @throws STException thrown on store problems
     */
    public void storeSportTypeList(SportTypeList sportTypeList, String destination) throws STException {

        // create JDOM element with all sport types
        Element eSportTypeList = createSportTypeListElement(sportTypeList);

        // write the element to XML file
        try {
            XMLUtils.writeXMLFile(eSportTypeList, destination);
        } catch (IOException e) {
            throw new STException(STExceptionID.XMLSTORAGE_STORE_SPORT_TYPE_LIST,
                    "Failed to write sport type list to XML file '" + destination + "' ...", e);
        }
    }

    /**
     * Creates the "sport-type-list" element with all exercises for the specified
     * sport type list.
     */
    private Element createSportTypeListElement(SportTypeList sportTypeList) {

        Element eSportTypeList = new Element("sport-type-list");

        // append an "sport-type" element for each sport type
        sportTypeList.forEach(sportType -> {
            Element eSportType = new Element("sport-type");
            eSportTypeList.addContent(eSportType);

            // create sport type attributes and elements
            eSportType.setAttribute("record-distance", String.valueOf(sportType.isRecordDistance()));
            XMLUtils.addElement(eSportType, "id", String.valueOf(sportType.getId()));
            XMLUtils.addElement(eSportType, "name", sportType.getName());
            XMLUtils.addElement(eSportType, "icon", sportType.getIcon());

            Element eColor = new Element("color");
            java.awt.Color awtColor = ColorConverter.toAwtColor(sportType.getColor());
            eColor.setAttribute("red", String.valueOf(awtColor.getRed()));
            eColor.setAttribute("green", String.valueOf(awtColor.getGreen()));
            eColor.setAttribute("blue", String.valueOf(awtColor.getBlue()));
            eSportType.addContent(eColor);

            // append an "sport-subtype" element for each sport subtype
            Element eSportSubTypeList = new Element("sport-subtype-list");
            eSportType.addContent(eSportSubTypeList);

            sportType.getSportSubTypeList().forEach(sportSubType -> {
                Element eSportSubType = new Element("sport-subtype");
                eSportSubTypeList.addContent(eSportSubType);
                XMLUtils.addElement(eSportSubType, "id", String.valueOf(sportSubType.getId()));
                XMLUtils.addElement(eSportSubType, "name", sportSubType.getName());
            });

            // append an "equipment" element for each equipment
            Element eEquipmentList = new Element("equipment-list");
            eSportType.addContent(eEquipmentList);

            sportType.getEquipmentList().forEach(equipment-> {
                Element eEquipment = new Element("equipment");
                eEquipmentList.addContent(eEquipment);
                XMLUtils.addElement(eEquipment, "id", String.valueOf(equipment.getId()));
                XMLUtils.addElement(eEquipment, "name", equipment.getName());
            });
        });

        return eSportTypeList;
    }
}