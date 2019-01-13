package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.NoteList;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.WeightList;
import de.saring.util.unitcalc.FormatUtils.SpeedMode;

import javax.inject.Singleton;

/**
 * This class is for reading / storing of the application data from / to
 * different XML files. It does not contain the appropriate functionality,
 * this is done by delegates. This interface defines methods for reading /
 * storing of the application data from / to different data sources
 * (files, database, ...).
 *
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class XMLStorage implements IStorage {

    private final XMLSportTypeList xmlSportTypeList;
    private final XMLExerciseList xmlExerciseList;
    private final XMLNoteList xmlNoteList;
    private final XMLWeightList xmlWeightList;

    /**
     * Standard c'tor.
     */
    public XMLStorage() {
        xmlSportTypeList = new XMLSportTypeList();
        xmlExerciseList = new XMLExerciseList();
        xmlNoteList = new XMLNoteList();
        xmlWeightList = new XMLWeightList();
    }

    @Override
    public SportTypeList readSportTypeList(String source, SpeedMode defaultSpeedMode) throws STException {
        return xmlSportTypeList.readSportTypeList(source, defaultSpeedMode);
    }

    @Override
    public void storeSportTypeList(SportTypeList sportTypeList, String destination) throws STException {
        xmlSportTypeList.storeSportTypeList(sportTypeList, destination);
    }

    @Override
    public ExerciseList readExerciseList(String source, SportTypeList sportTypeList) throws STException {
        return xmlExerciseList.readExerciseList(source, sportTypeList);
    }

    @Override
    public void storeExerciseList(ExerciseList exerciseList, String destination) throws STException {
        xmlExerciseList.storeExerciseList(exerciseList, destination);
    }

    @Override
    public NoteList readNoteList(String source) throws STException {
        return xmlNoteList.readNoteList(source);
    }

    @Override
    public void storeNoteList(NoteList noteList, String destination) throws STException {
        xmlNoteList.storeNoteList(noteList, destination);
    }

    @Override
    public WeightList readWeightList(String source) throws STException {
        return xmlWeightList.readWeightList(source);
    }

    @Override
    public void storeWeightList(WeightList weightList, String destination) throws STException {
        xmlWeightList.storeWeightList(weightList, destination);
    }
}
