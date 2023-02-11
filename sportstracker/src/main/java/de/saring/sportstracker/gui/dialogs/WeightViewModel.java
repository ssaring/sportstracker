package de.saring.sportstracker.gui.dialogs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.saring.util.unitcalc.UnitSystem;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.saring.sportstracker.data.Weight;
import de.saring.util.StringUtils;
import de.saring.util.unitcalc.ConvertUtils;

/**
 * This ViewModel class provides JavaFX properties of all Weight attributes to be edited in the dialog.
 * So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class WeightViewModel {

    private final Integer id;
    private final UnitSystem unitSystem;

    public final ObjectProperty<LocalDate> date;
    public final ObjectProperty<LocalTime> time;
    public final FloatProperty value;
    public final StringProperty comment;

    /**
     * Creates the WeightViewModel with JavaFX properties for the passed Weight object.
     *
     * @param weight Weight to be edited
     * @param unitSystem the unit system currently used in the UI
     */
    public WeightViewModel(final Weight weight, final UnitSystem unitSystem) {
        this.id = weight.getId();
        this.date = new SimpleObjectProperty<>(weight.getDateTime().toLocalDate());
        this.time = new SimpleObjectProperty<>(weight.getDateTime().toLocalTime());
        this.value = new SimpleFloatProperty(weight.getValue());
        this.comment = new SimpleStringProperty(StringUtils.getTextOrEmptyString(weight.getComment()));

        // convert weight value when english unit system is enabled
        this.unitSystem = unitSystem;
        if (unitSystem == UnitSystem.ENGLISH) {
            this.value.set((float) ConvertUtils.convertKilogram2Lbs(weight.getValue()));
        }
    }

    /**
     * Creates a new Weight domain object from the edited JavaFX properties.
     *
     * @return Weight
     */
    public Weight getWeight() {
        final Weight weight = new Weight(id);
        weight.setDateTime(LocalDateTime.of(date.get(), time.get()));
        weight.setValue(value.get());
        // ignore empty comment for optional inputs
        weight.setComment(StringUtils.getTrimmedTextOrNull(comment.getValue()));

        // convert weight value when english unit system is enabled
        if (unitSystem == UnitSystem.ENGLISH) {
            weight.setValue((float) ConvertUtils.convertLbs2Kilogram(weight.getValue()));
        }
        return weight;
    }
}
