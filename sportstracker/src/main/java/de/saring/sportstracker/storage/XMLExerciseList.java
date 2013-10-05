package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * This class is for reading or writing an ExerciseList object from or to a XML
 * file.
 *
 * @author Stefan Saring
 * @version 2.0
 */
public class XMLExerciseList {

    /**
     * The XSD filename with the structure of the exercise list.
     */
    private static final String XSD_EXERCISES = "exercises.xsd";

    /**
     * The date and time parser instance.
     */
    private SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Reads the exercise list from the specified XML file and maps the sport types
     * by using the specified sport type list.
     * Returns an empty list when the file doesn't exists yet.
     *
     * @param source name of the XML file to read from
     * @param sportTypeList the sport type list for assigning sport types to exercises.
     * @return the created ExerciseList
     * @throws STException thrown on read problems
     */
    public ExerciseList readExerciseList(String source, SportTypeList sportTypeList) throws STException {

        try {
            // return an empty list if the file doesn't exists yet
            ExerciseList exerciseList = new ExerciseList();
            File fSource = new File(source);
            if (!fSource.exists()) {
                return exerciseList;
            }

            // create JDOM Document from XML with XSD validation
            Document document = XMLUtils.getJDOMDocument(fSource, XSD_EXERCISES);

            // get root element and read all the contained exercises
            Element eExerciseList = document.getRootElement();

            eExerciseList.getChildren("exercise").forEach(eExercise ->
                exerciseList.set(readExercise(eExercise, sportTypeList)));

            return exerciseList;
        } catch (Exception e) {
            throw new STException(STExceptionID.XMLSTORAGE_READ_EXERCISE_LIST,
                    "Failed to read exercise list from XML file '" + source + "' ...", e);
        }
    }

    /**
     * Reads the data from the specified sport-type element and returns the created
     * Exercise object.
     *
     * @param eExercise exercise JDOM element
     * @param sportTypeList the sport type list for assigning sport types to exercises.
     * @return the created Exercise object
     */
    private Exercise readExercise(Element eExercise, SportTypeList sportTypeList) {

        Exercise exercise = new Exercise(
                Integer.parseInt(eExercise.getChildText("id")));

        // get sport type by parsed ID
        int sportTypeID = Integer.parseInt(eExercise.getChildText("sport-type-id"));
        SportType sportType = sportTypeList.getByID(sportTypeID);
        if (sportType == null) {
            throw new IllegalArgumentException("Failed to parse exercise with ID '" + exercise.getId() +
                    "', the sport type ID '" + sportTypeID + "' is unknown!");
        }
        exercise.setSportType(sportType);

        // get sport subtype by parsed ID
        int sportSubTypeID = Integer.parseInt(eExercise.getChildText("sport-subtype-id"));
        SportSubType sportSubType = sportType.getSportSubTypeList().getByID(sportSubTypeID);
        if (sportSubType == null) {
            throw new IllegalArgumentException("Failed to parse exercise with ID '" + exercise.getId() +
                    "', the sport subtype ID '" + sportSubTypeID + "' is unknown!");
        }
        exercise.setSportSubType(sportSubType);

        // get and convert date (format allready checked by XSD schema)
        String strDate = eExercise.getChildText("date");
        try {
            exercise.setDate(sdFormat.parse(strDate));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse exercise with ID '" + exercise.getId() +
                    "', the date format '" + strDate + "' is not valid!");
        }

        exercise.setDuration(Integer.parseInt(eExercise.getChildText("duration")));
        exercise.setDistance(Float.parseFloat(eExercise.getChildText("distance")));
        exercise.setAvgSpeed(Float.parseFloat(eExercise.getChildText("avg-speed")));

        // get and parse intensity type
        String strIntensity = eExercise.getChildText("intensity");
        try {
            exercise.setIntensity(Exercise.IntensityType.valueOf(strIntensity));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse exercise with ID '" + exercise.getId() +
                    "', the intensity '" + strIntensity + "' is not valid!");
        }

        // get all the optional exercise data
        String strAvgHeartRate = eExercise.getChildText("avg-heartrate");
        if (strAvgHeartRate != null) {
            exercise.setAvgHeartRate(Integer.parseInt(strAvgHeartRate));
        }

        String strAscent = eExercise.getChildText("ascent");
        if (strAscent != null) {
            exercise.setAscent(Integer.parseInt(strAscent));
        }

        String strCalories = eExercise.getChildText("calories");
        if (strCalories != null) {
            exercise.setCalories(Integer.parseInt(strCalories));
        }

        exercise.setHrmFile(eExercise.getChildText("hrm-file"));
        exercise.setComment(eExercise.getChildText("comment"));

        // get equipment by parsed ID (optional)
        String strEquipmentID = eExercise.getChildText("equipment-id");
        if (strEquipmentID != null) {
            int equipmentID = Integer.parseInt(strEquipmentID);
            Equipment equipment = sportType.getEquipmentList().getByID(equipmentID);
            if (equipment == null) {
                throw new IllegalArgumentException("Failed to parse exercise with ID '" + exercise.getId() +
                        "', the equipment ID '" + equipmentID + "' is unknown!");
            }
            exercise.setEquipment(equipment);
        }

        return exercise;
    }

    /**
     * Writes the exercise list to the specified XML file..
     *
     * @param exerciseList the exercise list to store
     * @param destination name of the XML file to write to
     * @throws STException thrown on store problems
     */
    public void storeExerciseList(ExerciseList exerciseList, String destination) throws STException {

        // create JDOM element with all exercises
        Element eExerciseList = createExerciseListElement(exerciseList);

        // write the element to XML file
        try {
            XMLUtils.writeXMLFile(eExerciseList, destination);
        } catch (IOException e) {
            throw new STException(STExceptionID.XMLSTORAGE_STORE_EXERCISE_LIST,
                    "Failed to write exercise list to XML file '" + destination + "' ...", e);
        }
    }

    /**
     * Creates the "exercise-list" element with all exercises for the specified
     * exercise list.
     */
    private Element createExerciseListElement(ExerciseList exerciseList) {
        Element eExerciseList = new Element("exercise-list");

        // append an exercise element for each exercise
        exerciseList.forEach(exercise -> {
            Element eExercise = new Element("exercise");
            eExerciseList.addContent(eExercise);

            // create required exercise elements
            XMLUtils.addElement(eExercise, "id", String.valueOf(exercise.getId()));
            XMLUtils.addElement(eExercise, "sport-type-id", String.valueOf(exercise.getSportType().getId()));
            XMLUtils.addElement(eExercise, "sport-subtype-id", String.valueOf(exercise.getSportSubType().getId()));
            XMLUtils.addElement(eExercise, "date", sdFormat.format(exercise.getDate()));
            XMLUtils.addElement(eExercise, "duration", String.valueOf(exercise.getDuration()));
            XMLUtils.addElement(eExercise, "intensity", exercise.getIntensity().toStringEnum());
            XMLUtils.addElement(eExercise, "distance", String.valueOf(exercise.getDistance()));
            XMLUtils.addElement(eExercise, "avg-speed", String.valueOf(exercise.getAvgSpeed()));

            // create optional exercise elements
            if (exercise.getAvgHeartRate() != 0) {
                XMLUtils.addElement(eExercise, "avg-heartrate", String.valueOf(exercise.getAvgHeartRate()));
            }
            if (exercise.getAscent() != 0) {
                XMLUtils.addElement(eExercise, "ascent", String.valueOf(exercise.getAscent()));
            }
            if (exercise.getCalories() != 0) {
                XMLUtils.addElement(eExercise, "calories", String.valueOf(exercise.getCalories()));
            }
            if (exercise.getHrmFile() != null) {
                XMLUtils.addElement(eExercise, "hrm-file", exercise.getHrmFile());
            }
            if (exercise.getEquipment() != null) {
                XMLUtils.addElement(eExercise, "equipment-id", String.valueOf(exercise.getEquipment().getId()));
            }
            if (exercise.getComment() != null) {
                XMLUtils.addElement(eExercise, "comment", exercise.getComment());
            }
        });

        return eExerciseList;
    }
}
