package de.saring.sportstracker.gui.dialogsfx;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;

/**
 * This ViewModel class provides JavaFX properties of all ExerciseFilter attributes to be edited in the
 * Filter dialog. So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class FilterViewModel {

    private ObjectProperty<LocalDate> dateStart;
    private ObjectProperty<LocalDate> dateEnd;
    private ObjectProperty<SportType> sportType;
    private ObjectProperty<SportSubType> sportSubType;
    private ObjectProperty<Exercise.IntensityType> intensity;
    private ObjectProperty<Equipment> equipment;
    private StringProperty commentSubString;
    private BooleanProperty regularExpressionMode;

    /**
     * Creates the FilterViewModel with JavaFX properties for the passed ExerciseFilter object.
     *
     * @param exerciseFilter filter to be edited
     */
    public FilterViewModel(final ExerciseFilter exerciseFilter) {
        // TODO
        // this.date = new SimpleObjectProperty(note.getDateTime().toLocalDate());
        // this.hour = new SimpleIntegerProperty(note.getDateTime().getHour());
        // this.minute = new SimpleIntegerProperty(note.getDateTime().getMinute());
        // this.text = new SimpleStringProperty(note.getText());
    }

    /**
     * Creates a new ExerciseFilter domain object from the edited JavaFX properties.
     *
     * @return ExerciseFilter
     */
    public ExerciseFilter getExerciseFilter() {
        final ExerciseFilter exerciseFilter = new ExerciseFilter();
        exerciseFilter.setDateStart(dateStart.get());
        exerciseFilter.setDateEnd(dateEnd.get());
        // TODO the other attys
        return exerciseFilter;
    }
}
