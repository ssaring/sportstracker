package de.saring.sportstracker.storage;

import javax.inject.Singleton;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.NoteList;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.WeightList;

/**
 * This class is for reading / storing of the application data from / to 
 * different XML files. It does not contain the appropriate functionality, 
 * this is done by delegates. This interface defines methods for reading / 
 * storing of the application data from / to different data sources 
 * (files, database, ...).
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@Singleton
public class XMLStorage implements IStorage {

    private XMLSportTypeList xmlSportTypeList;
    private XMLExerciseList xmlExerciseList;
    private XMLNoteList xmlNoteList;
    private XMLWeightList xmlWeightList;

    /**
     * Standard c'tor.
     */
    public XMLStorage () {
        xmlSportTypeList = new XMLSportTypeList ();
        xmlExerciseList = new XMLExerciseList ();
        xmlNoteList = new XMLNoteList ();
        xmlWeightList = new XMLWeightList ();
    }

    
    /** {@inheritDoc} */
    @Override
    public SportTypeList readSportTypeList (String source) throws STException {
        return xmlSportTypeList.readSportTypeList (source);
    }

    /** {@inheritDoc} */
    @Override
    public void storeSportTypeList (SportTypeList sportTypeList, String destination) throws STException {
        xmlSportTypeList.storeSportTypeList (sportTypeList, destination);
    }

    /** {@inheritDoc} */
    @Override
    public ExerciseList readExerciseList (String source, SportTypeList sportTypeList) throws STException {
        return xmlExerciseList.readExerciseList (source, sportTypeList);
    }

    /** {@inheritDoc} */
    @Override
    public void storeExerciseList (ExerciseList exerciseList, String destination) throws STException {
        xmlExerciseList.storeExerciseList (exerciseList, destination);
    }

    /** {@inheritDoc} */
    public NoteList readNoteList (String source) throws STException {
        return xmlNoteList.readNoteList (source);
    }

    /** {@inheritDoc} */
    public void storeNoteList (NoteList noteList, String destination) throws STException {
        xmlNoteList.storeNoteList (noteList, destination);
    }

    /** {@inheritDoc} */
    public WeightList readWeightList (String source) throws STException {
        return xmlWeightList.readWeightList (source);
    }

    /** {@inheritDoc} */
    public void storeWeightList (WeightList weightList, String destination) throws STException {
        xmlWeightList.storeWeightList (weightList, destination);
    }
}
