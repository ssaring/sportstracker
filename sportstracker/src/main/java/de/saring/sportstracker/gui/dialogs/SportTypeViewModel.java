package de.saring.sportstracker.gui.dialogs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.dialogs.SportTypeDialogController.SpeedModeItem;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObjectList;

/**
 * This ViewModel class provides JavaFX properties of all SportType attributes to be edited in the SportType dialog.
 * So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class SportTypeViewModel {

    public final Long id;
    public final StringProperty name;
    public final BooleanProperty recordDistance;
    public final ObjectProperty<SpeedModeItem> speedMode;
    public final StringProperty icon;
    public final ObjectProperty<Color> color;
    public final ObjectProperty<FitMappingEntry> fitSportType;

    // ObservableLists are not being used here, the dialog needs features of IdObjectList
    public final IdObjectList<SportSubType> sportSubtypes;
    public final IdObjectList<Equipment> equipments;

    /**
     * Creates the SportTypeViewModel with JavaFX properties for the passed SportType object.
     *
     * @param sportType SportType to be edited
     */
    public SportTypeViewModel(final SportType sportType) {
        this.id = sportType.getId();
        this.name = new SimpleStringProperty(sportType.getName());
        this.recordDistance = new SimpleBooleanProperty(sportType.isRecordDistance());
        this.speedMode = new SimpleObjectProperty<>(SpeedModeItem.findBySpeedMode(sportType.getSpeedMode()));
        this.icon = new SimpleStringProperty(sportType.getIcon());
        this.color = new SimpleObjectProperty<>(sportType.getColor() == null ? Color.BLACK : sportType.getColor());
        this.fitSportType = new SimpleObjectProperty<>(new FitMappingEntry(
                sportType.getFitId() == null ? Integer.MAX_VALUE : sportType.getFitId(), ""));

        this.sportSubtypes = sportType.getSportSubTypeList();
        this.equipments = sportType.getEquipmentList();
    }

    /**
     * Creates a new SportType domain object from the edited JavaFX properties.
     *
     * @return SportType
     */
    public SportType getSportType() {
        final SportType sportType = new SportType(id);
        sportType.setName(name.getValue().trim());
        sportType.setRecordDistance(recordDistance.getValue());
        sportType.setSpeedMode(speedMode.getValue().getSpeedMode());
        sportType.setIcon(StringUtils.getTrimmedTextOrNull(icon.getValue()));
        sportType.setColor(color.getValue());
        sportType.setFitId(fitSportType.getValue().fitId == Integer.MAX_VALUE ? null : fitSportType.getValue().fitId);

        sportSubtypes.forEach(sportSubType -> sportType.getSportSubTypeList().set(sportSubType));
        equipments.forEach(equipment -> sportType.getEquipmentList().set(equipment));
        return sportType;
    }

    /**
     * Record for displaying the appropriate Garmin FIT sport type in the mapping selection combobox.
     * The identity is defined by the fitId only, this is sufficient for the JavaFX binding mapping.
     */
    public record FitMappingEntry(int fitId, String displayName) {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FitMappingEntry
                    && fitId == ((FitMappingEntry) obj).fitId;
        }

        @Override
        public int hashCode() {
            return fitId;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
