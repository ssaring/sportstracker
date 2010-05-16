package de.saring.sportstracker.gui;

import com.google.inject.ImplementedBy;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.NoteList;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.WeightList;
import de.saring.util.data.IdDateObjectList;
import de.saring.util.data.IdObjectListChangeListener;

/**
 * This interface provides all document (MVC) related data and functionality of the 
 * SportsTracker application.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@ImplementedBy(STDocumentImpl.class)
public interface STDocument extends IdObjectListChangeListener {
    
    /** Command line parameter for a specific data directory (optional). */
    String PARAMETER_DATA_DIR = "--datadir=";


    /***** BEGIN: Getters and Setters for application data *****/
    
    SportTypeList getSportTypeList ();
    ExerciseList getExerciseList();
    NoteList getNoteList ();
    WeightList getWeightList ();

    STOptions getOptions();
    
    boolean isDirtyData();

    boolean isFilterEnabled();
    void setFilterEnabled(boolean filterEnabled);
    
    ExerciseFilter getCurrentFilter();
    void setCurrentFilter(ExerciseFilter currentFilter);

    /***** END: Getters and Setters for application data *****/

    /**
     * Evaluates the passed command line parameters.
     * @param parameters array of specified command line parameters
     */
    void evaluateCommandLineParameters (String[] parameters);
    
    /**
     * Loads the application options or creates default options when not available.
     */
    void loadOptions ();
    
    /**
     * Stores the application options to file.
     */
    void storeOptions ();
    
    /**
     * This method makes sure that the application data directory exists, otherwise it creates it.
     * @throws STException thrown on problems creating application directory
     */
    void createApplicationDirectory () throws STException;
    
    /**
     * This method returns the list of exercises for display in the GUI.
     * If the filter is enabled, the returned list will contains just the 
     * filtered exercises, otherwise it will contain all.
     * @return list of Exercise objects
     */
    IdDateObjectList<Exercise> getFilterableExerciseList ();
    
    /**
     * This method reads both the exercise and the sport-type list from the 
     * storage (e.g. XML files). On read problems empty lists will be created.
     * @throws STException thrown on read problems
     */
    void readApplicationData () throws STException;
    
    /**
     * This method stores both the exercise and the sport-type list in the storage
     * (e.g. XML files). On success the dirty data flag will be set to false.
     * @throws STException thrown on store problems
     */
    void storeApplicationData () throws STException; 
}
