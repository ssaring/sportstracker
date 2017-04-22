package de.saring.sportstracker.gui.dialogs;

import java.time.LocalDate;

import de.saring.sportstracker.data.EntryFilter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.StringUtils;

/**
 * This ViewModel class provides JavaFX properties of all EntryFilter attributes to be edited in the
 * Filter dialog. So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class FilterViewModel {

    public final ObjectProperty<LocalDate> dateStart;
    public final ObjectProperty<LocalDate> dateEnd;
    public final ObjectProperty<EntryFilter.EntryType> entryType;
    public final ObjectProperty<SportType> sportType;
    public final ObjectProperty<SportSubType> sportSubtype;
    public final ObjectProperty<IntensityItem> intensity;
    public final ObjectProperty<Equipment> equipment;
    public final StringProperty commentSubString;
    public final BooleanProperty regularExpressionMode;
    public final BooleanProperty entryTypeSelectable;

    /**
     * Creates the FilterViewModel with JavaFX properties for the passed EntryFilter object.
     *
     * @param entryFilter filter to be edited
     * @param entryTypeSelectable flag whether the filter entry type can be selected or not
     */
    public FilterViewModel(final EntryFilter entryFilter, final boolean entryTypeSelectable) {
        dateStart = new SimpleObjectProperty<>(entryFilter.getDateStart());
        dateEnd = new SimpleObjectProperty<>(entryFilter.getDateEnd());
        entryType = new SimpleObjectProperty<>(entryFilter.getEntryType());
        sportType = new SimpleObjectProperty<>(entryFilter.getSportType());
        sportSubtype = new SimpleObjectProperty<>(entryFilter.getSportSubType());
        intensity = new SimpleObjectProperty<>(new IntensityItem(entryFilter.getIntensity()));
        equipment = new SimpleObjectProperty<>(entryFilter.getEquipment());
        commentSubString = new SimpleStringProperty(StringUtils.getTextOrEmptyString(
                entryFilter.getCommentSubString()));
        regularExpressionMode = new SimpleBooleanProperty(entryFilter.isRegularExpressionMode());
        this.entryTypeSelectable = new SimpleBooleanProperty(entryTypeSelectable);
    }

    /**
     * Creates a new EntryFilter domain object from the edited JavaFX properties.
     *
     * @return EntryFilter
     */
    public EntryFilter getExerciseFilter() {
        final EntryFilter entryFilter = new EntryFilter();
        entryFilter.setDateStart(dateStart.get());
        entryFilter.setDateEnd(dateEnd.get());
        entryFilter.setEntryType(entryType.get());
        entryFilter.setSportType(sportType.get());
        entryFilter.setSportSubType(sportSubtype.get());
        entryFilter.setIntensity(intensity.get().intensityType);
        entryFilter.setEquipment(equipment.get());
        entryFilter.setCommentSubString(StringUtils.getTrimmedTextOrNull(commentSubString.get()));
        entryFilter.setRegularExpressionMode(regularExpressionMode.get());
        return entryFilter;
    }

    /**
     * Wrapper class for choicebox items for intensity type selection in the filter dialog. The choicebox
     * can't contain IntensityType items, because the selection "all" is not available in the IntensityType
     * enumeration.
     */
    public static class IntensityItem {

        /** The wrapped intensity type. The value null means "all intensities. */
        public final Exercise.IntensityType intensityType;

        /**
         * Standard c'tor.
         *
         * @param intensityType intensity
         */
        public IntensityItem(final Exercise.IntensityType intensityType) {
            this.intensityType = intensityType;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final IntensityItem that = (IntensityItem) o;
            if (intensityType != that.intensityType) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return intensityType != null ? intensityType.hashCode() : 0;
        }
    }
}
