package de.saring.sportstracker.gui.dialogsfx;

import java.time.LocalDate;

import de.saring.util.StringUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

    public final ObjectProperty<LocalDate> dateStart;
    public final ObjectProperty<LocalDate> dateEnd;
    public final ObjectProperty<SportType> sportType;
    public final ObjectProperty<SportSubType> sportSubtype;
    public final ObjectProperty<Exercise.IntensityType> intensity;
    public final ObjectProperty<Equipment> equipment;
    public final StringProperty commentSubString;
    public final BooleanProperty regularExpressionMode;

    /**
     * Creates the FilterViewModel with JavaFX properties for the passed ExerciseFilter object.
     *
     * @param exerciseFilter filter to be edited
     */
    public FilterViewModel(final ExerciseFilter exerciseFilter) {
        dateStart = new SimpleObjectProperty<>(exerciseFilter.getDateStart());
        dateEnd = new SimpleObjectProperty<>(exerciseFilter.getDateEnd());
        sportType = new SimpleObjectProperty<>(exerciseFilter.getSportType());
        sportSubtype = new SimpleObjectProperty<>(exerciseFilter.getSportSubType());
        intensity = new SimpleObjectProperty<>(exerciseFilter.getIntensity());
        equipment = new SimpleObjectProperty<>(exerciseFilter.getEquipment());
        commentSubString = new SimpleStringProperty(StringUtils.getTextOrEmptyString(
                exerciseFilter.getCommentSubString()));
        regularExpressionMode = new SimpleBooleanProperty(exerciseFilter.isRegularExpressionMode());
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
        exerciseFilter.setSportType(sportType.get());
        exerciseFilter.setSportSubType(sportSubtype.get());
        exerciseFilter.setIntensity(intensity.get());
        exerciseFilter.setEquipment(equipment.get());
        exerciseFilter.setCommentSubString(StringUtils.getTrimmedTextOrNull(commentSubString.get()));
        exerciseFilter.setRegularExpressionMode(regularExpressionMode.get());
        return exerciseFilter;
    }
}
