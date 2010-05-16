package de.saring.sportstracker.gui;

import com.google.inject.ImplementedBy;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.views.EntryView;
import de.saring.util.data.IdDateObjectList;

/**
 * This interface provides all view (MVC) related data and functionality of the 
 * SportsTracker application.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@ImplementedBy(STViewImpl.class)
public interface STView {

    /**
     * Initualizes the view.
     */
    void initView ();

    /**
     * Initialization of the View after it has been displayed.
     */
    void postInit ();
    
    /**
     * Returns the current displayed entry view instance.
     * @return the current view
     */
    EntryView getCurrentView ();

    /**
     * Updates the complete view to show the current application data.
     */
    void updateView ();
    
    /**
     * Returns the list of exercises which needs to be displayed in the view
     * (this can be the filtered list when a filter is active).
     * @return the list of exercises to be displayed
     */
    IdDateObjectList<Exercise> getDisplayedExercises ();
    
    /**
     * Switches the view to the specified exercise view type.
     * @param view the exercise view type to display
     */
    void switchToView (EntryView.ViewType viewType);

    /**
     * Updates the entry actions and the statusbar text depending on the current
     * entry selection.
     */
    void updateEntryActions ();
    
    /** 
     * Updates the save action. It will be enabled when the document contains dirty data.
     */
    void updateSaveAction ();
}
