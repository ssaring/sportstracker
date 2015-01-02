package de.saring.sportstracker.gui.dialogs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.StringUtils;
import de.saring.util.unitcalc.CalculationUtils;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;

/**
 * This ViewModel class provides JavaFX properties of all Exercise attributes to be edited in the Exercise Dialog.
 * So they can be bound to the appropriate dialog view controls.<br/>
 * The property attributes are public because they are final and must be accessed from the UI controls for binding,
 * there is no benefit to declare get-methods for all of them.
 *
 * @author Stefan Saring
 */
public class ExerciseViewModel {

    private final int id;
    private final FormatUtils.UnitSystem unitSystem;

    public final ObjectProperty<LocalDate> date;
    public final IntegerProperty hour;
    public final IntegerProperty minute;
    public final ObjectProperty<SportType> sportType;
    public final ObjectProperty<SportSubType> sportSubType;
    public final ObjectProperty<Exercise.IntensityType> intensity;
    public final FloatProperty distance;
    public final FloatProperty avgSpeed;
    public final IntegerProperty duration;
    public final IntegerProperty avgHeartRate;
    public final IntegerProperty ascent;
    public final IntegerProperty calories;
    public final ObjectProperty<Equipment> equipment;
    public final StringProperty hrmFile;
    public final StringProperty comment;

    public final BooleanProperty sportTypeRecordDistance = new SimpleBooleanProperty(false);
    public final BooleanProperty autoCalcDistance = new SimpleBooleanProperty(false);
    public final BooleanProperty autoCalcAvgSpeed = new SimpleBooleanProperty(false);
    public final BooleanProperty autoCalcDuration = new SimpleBooleanProperty(true);

    /**
     * Creates the ExerciseViewModel with JavaFX properties for the passed Exercise object.
     *
     * @param exercise   Exercise to be edited
     * @param unitSystem the unit system currently used in the UI
     */
    public ExerciseViewModel(final Exercise exercise, final FormatUtils.UnitSystem unitSystem) {
        this.id = exercise.getId();
        this.date = new SimpleObjectProperty(exercise.getDateTime().toLocalDate());
        this.hour = new SimpleIntegerProperty(exercise.getDateTime().getHour());
        this.minute = new SimpleIntegerProperty(exercise.getDateTime().getMinute());
        this.sportType = new SimpleObjectProperty(exercise.getSportType());
        this.sportSubType = new SimpleObjectProperty(exercise.getSportSubType());
        this.intensity = new SimpleObjectProperty(exercise.getIntensity());
        this.distance = new SimpleFloatProperty(exercise.getDistance());
        this.avgSpeed = new SimpleFloatProperty(exercise.getAvgSpeed());
        this.duration = new SimpleIntegerProperty(exercise.getDuration());
        this.equipment = new SimpleObjectProperty(exercise.getEquipment());
        this.avgHeartRate = new SimpleIntegerProperty(exercise.getAvgHeartRate());
        this.ascent = new SimpleIntegerProperty(exercise.getAscent());
        this.calories = new SimpleIntegerProperty(exercise.getCalories());
        this.hrmFile = new SimpleStringProperty(StringUtils.getTextOrEmptyString(exercise.getHrmFile()));
        this.comment = new SimpleStringProperty(StringUtils.getTextOrEmptyString(exercise.getComment()));

        // convert weight value when english unit system is enabled
        this.unitSystem = unitSystem;
        if (unitSystem == FormatUtils.UnitSystem.English) {
            this.distance.set((float) ConvertUtils.convertKilometer2Miles(exercise.getDistance(), false));
            this.avgSpeed.set((float) ConvertUtils.convertKilometer2Miles(exercise.getAvgSpeed(), false));
            this.ascent.set(ConvertUtils.convertMeter2Feet(exercise.getAscent()));
        }

        setupSportTypeRecordDistance();
        setupChangeListenersForAutoCalculation(exercise, unitSystem);
    }

    /**
     * Creates a new Exercise domain object from the edited JavaFX properties.
     *
     * @return Exercise
     */
    public Exercise getExercise() {
        final Exercise exercise = new Exercise(id);
        exercise.setDateTime(LocalDateTime.of(date.get(), LocalTime.of(hour.getValue(), minute.getValue())));
        exercise.setSportType(sportType.getValue());
        exercise.setSportSubType(sportSubType.getValue());
        exercise.setIntensity(intensity.getValue());
        exercise.setDistance(distance.getValue());
        exercise.setAvgSpeed(avgSpeed.getValue());
        exercise.setDuration(duration.getValue());
        exercise.setAvgHeartRate(avgHeartRate.getValue());
        exercise.setAscent(ascent.getValue());
        exercise.setCalories(calories.getValue());
        exercise.setEquipment(equipment.getValue());
        // ignore empty text for optional inputs
        exercise.setHrmFile(StringUtils.getTrimmedTextOrNull(hrmFile.getValue()));
        exercise.setComment(StringUtils.getTrimmedTextOrNull(comment.getValue()));

        // convert weight value when english unit system is enabled
        if (unitSystem == FormatUtils.UnitSystem.English) {
            exercise.setDistance((float) ConvertUtils.convertMiles2Kilometer(exercise.getDistance()));
            exercise.setAvgSpeed((float) ConvertUtils.convertMiles2Kilometer(exercise.getAvgSpeed()));
            exercise.setAscent(ConvertUtils.convertFeet2Meter(exercise.getAscent()));
        }
        return exercise;
    }

    /**
     * Sets the new values for the properties distance, avg speed and duration depending on the current
     * auto calculation mode. The auto calculated field will not be set, to ensure consistent data.
     *
     * @param newDistance new distance in km (will be converted when english unit system is used)
     * @param newAvgSpeed new avg speed in km/h (will be converted when english unit system is used)
     * @param newDuration new duration in seconds
     */
    public void setAutoCalcFields(final float newDistance, final float newAvgSpeed, final int newDuration) {

        if (!autoCalcDistance.get()) {
            if (unitSystem == FormatUtils.UnitSystem.English) {
                this.distance.set((float) ConvertUtils.convertKilometer2Miles(newDistance, false));
            } else {
                this.distance.set(newDistance);
            }
        }
        if (!autoCalcAvgSpeed.get()) {
            if (unitSystem == FormatUtils.UnitSystem.English) {
                this.avgSpeed.set((float) ConvertUtils.convertKilometer2Miles(newAvgSpeed, false));
            } else {
                this.avgSpeed.set(newAvgSpeed);
            }
        }
        if (!autoCalcDuration.get()) {
            this.duration.set(newDuration);
        }
    }

    /**
     * Setup of the sportTypeRecordDistance property. It will be updated every time the sport type
     * changes. When the new sport type does not record the distance, then the distance and avg speed
     * values will be set to 0.
     */
    private void setupSportTypeRecordDistance() {
        sportTypeRecordDistance.set(sportType.get() == null || sportType.get().isRecordDistance());

        sportType.addListener((observable, oldValue, newValue) -> {
            sportTypeRecordDistance.set(newValue.isRecordDistance());

            if (!sportTypeRecordDistance.get()) {
                distance.set(0);
                avgSpeed.set(0);
            }

            autoCalculate();

            // force value changes for distance and avg speed, so the validation will be executed
            // depending on the new selected sport type (no other way to fire the event)
            distance.set(distance.get() + 1);
            distance.set(distance.get() - 1);
            avgSpeed.set(avgSpeed.get() + 1);
            avgSpeed.set(avgSpeed.get() - 1);
        });
    }

    /**
     * Setup the value change listeners which perform the automatic calculation depending on the current
     * auto calculation mode.
     */
    private void setupChangeListenersForAutoCalculation(Exercise exercise, FormatUtils.UnitSystem unitSystem) {
        distance.addListener((observable, oldValue, newValue) -> {
            if (!autoCalcDistance.get()) {
                autoCalculate();
            }
        });

        avgSpeed.addListener((observable, oldValue, newValue) -> {
            if (!autoCalcAvgSpeed.get()) {
                autoCalculate();
            }
        });

        duration.addListener((observable, oldValue, newValue) -> {
            if (!autoCalcDuration.get()) {
                autoCalculate();
            }
        });
    }

    /**
     * Performs the calculation of the value which needs to be calculated automatically. The
     * calculated value will be 0 when one of the other values is 0 or not available.
     * The current unit system can be ignored here, all inputs are using the same.<br/>
     * The calculation will not be performed when the current sport type does not records
     * the distance!
     */
    private void autoCalculate() {
        if (sportTypeRecordDistance.get()) {
            if (autoCalcDistance.get()) {
                if (avgSpeed.get() > 0 && duration.get() > 0) {
                    distance.set(CalculationUtils.calculateDistance(avgSpeed.get(), duration.get()));
                } else {
                    distance.set(0);
                }
            } else if (autoCalcAvgSpeed.get()) {
                if (distance.get() > 0 && duration.get() > 0) {
                    avgSpeed.set(CalculationUtils.calculateAvgSpeed(distance.get(), duration.get()));
                } else {
                    avgSpeed.set(0);
                }
            } else if (autoCalcDuration.get()) {
                if (distance.get() > 0 && avgSpeed.get() > 0) {
                    duration.set(CalculationUtils.calculateDuration(distance.get(), avgSpeed.get()));
                } else {
                    duration.set(0);
                }
            } else {
                throw new IllegalStateException("Invalid auto calculation mode!");
            }
        }
    }
}
