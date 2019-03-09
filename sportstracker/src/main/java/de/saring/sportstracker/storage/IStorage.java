package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.NoteList;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.WeightList;
import de.saring.util.unitcalc.SpeedMode;

/**
 * This interface defines methods for reading / storing of the application
 * data from / to different data sources (files, database, ...).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public interface IStorage {

    /**
     * Reads the sport type list from the data source.
     *
     * @param source name of data source
     * @param defaultSpeedMode default speed mode to be set when not specified in the data source
     * @return the created SportTypeList
     * @throws STException thrown on read problems
     */
    SportTypeList readSportTypeList(String source, SpeedMode defaultSpeedMode) throws STException;

    /**
     * Writes the sport type list to the data destination.
     *
     * @param sportTypeList the sport type list to store
     * @param destination name of data destination
     * @throws STException thrown on store problems
     */
    void storeSportTypeList(SportTypeList sportTypeList, String destination) throws STException;

    /**
     * Reads the exercise list from the specified data source and maps the sport
     * types by using the specified sport type list.
     *
     * @param source name of data source
     * @param sportTypeList the sport type list for assigning sport types to exercises.
     * @return the created ExerciseList
     * @throws STException thrown on read problems
     */
    ExerciseList readExerciseList(String source, SportTypeList sportTypeList) throws STException;

    /**
     * Writes the exercise list to the data destination.
     *
     * @param exerciseList the exercise list to store
     * @param destination name of data destination
     * @throws STException thrown on store problems
     */
    void storeExerciseList(ExerciseList exerciseList, String destination) throws STException;

    /**
     * Reads the note list from the data source.
     *
     * @param source name of data source
     * @return the created NoteList
     * @throws STException thrown on read problems
     */
    NoteList readNoteList(String source) throws STException;

    /**
     * Writes the note list to the data destination.
     *
     * @param noteList the note list to store
     * @param destination name of data destination
     * @throws STException thrown on store problems
     */
    void storeNoteList(NoteList noteList, String destination) throws STException;

    /**
     * Reads the weight list from the data source.
     *
     * @param source name of data source
     * @return the created WeightList
     * @throws STException thrown on read problems
     */
    WeightList readWeightList(String source) throws STException;

    /**
     * Writes the weight list to the data destination.
     *
     * @param weightList the note list to store
     * @param destination name of data destination
     * @throws STException thrown on store problems
     */
    void storeWeightList(WeightList weightList, String destination) throws STException;
}
