package de.saring.sportstracker.gui.dialogs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.saring.util.unitcalc.SpeedToStringConverter;
import de.saring.util.unitcalc.UnitSystem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import de.saring.util.unitcalc.SpeedMode;

/**
 * This ViewModel class provides JavaFX properties of all Exercise attributes to be edited in the Exercise Dialog.
 * So they can be bound to the appropriate dialog view controls.<br/>
 * The property attributes are public because they are final and must be accessed from the UI controls for binding,
 * there is no benefit to declare get-methods for all of them.
 *
 * @author Stefan Saring
 */
public class ExerciseViewModel {

    private final Long id;
    private final FormatUtils formatUtils;

    public final ObjectProperty<LocalDate> date;
    public final ObjectProperty<LocalTime> time;
    public final ObjectProperty<SportType> sportType;
    public final ObjectProperty<SportSubType> sportSubType;
    public final ObjectProperty<Exercise.IntensityType> intensity;
    public final DoubleProperty distance;
    public final IntegerProperty duration;
    public final IntegerProperty avgHeartRate;
    public final IntegerProperty ascent;
    public final IntegerProperty descent;
    public final IntegerProperty calories;
    public final ObjectProperty<Equipment> equipment;
    public final StringProperty hrmFile;
    public final StringProperty comment;

    /**
     * Unfortunately the average speed needs to be handled as a StringProperty and the double to string conversion can't
     * be done automatically by using a converter in the binding. Although JavaFX supports such a conversion, it's not
     * possible to switch the speed modes for this automatic conversion. The text input throws weird exceptions then and
     * is not usable anymore.
     */
    public final StringProperty avgSpeed;

    public final BooleanProperty sportTypeRecordDistance = new SimpleBooleanProperty(false);
    public final BooleanProperty autoCalcDistance = new SimpleBooleanProperty(false);
    public final BooleanProperty autoCalcAvgSpeed = new SimpleBooleanProperty(false);
    public final BooleanProperty autoCalcDuration = new SimpleBooleanProperty(true);

    private final SpeedToStringConverter speedConverter;

    /**
     * Creates the ExerciseViewModel with JavaFX properties for the passed Exercise object.
     *
     * @param exercise Exercise to be edited
     * @param formatUtils the format utils with the current settings
     * @param preferredSpeedMode the preferred speed mode when no sport type is specified
     */
    public ExerciseViewModel(final Exercise exercise, final FormatUtils formatUtils, final SpeedMode preferredSpeedMode) {

        this.formatUtils = formatUtils;

        this.id = exercise.getId();
        this.date = new SimpleObjectProperty<>(exercise.getDateTime().toLocalDate());
        this.time = new SimpleObjectProperty<>(exercise.getDateTime().toLocalTime());
        this.sportType = new SimpleObjectProperty<>(exercise.getSportType());
        this.sportSubType = new SimpleObjectProperty<>(exercise.getSportSubType());
        this.intensity = new SimpleObjectProperty<>(exercise.getIntensity());
        this.distance = new SimpleDoubleProperty(exercise.getDistance());
        this.duration = new SimpleIntegerProperty(exercise.getDuration());
        this.equipment = new SimpleObjectProperty<>(exercise.getEquipment());
        // set optional Integer properties to 0 when value is null (IntegerProperty can't handle null values)
        this.avgHeartRate = new SimpleIntegerProperty(exercise.getAvgHeartRate() == null ? 0 : exercise.getAvgHeartRate());
        this.ascent = new SimpleIntegerProperty(exercise.getAscent() == null ? 0 : exercise.getAscent());
        this.descent = new SimpleIntegerProperty(exercise.getDescent() == null ? 0 : exercise.getDescent());
        this.calories = new SimpleIntegerProperty(exercise.getCalories() == null ? 0 : exercise.getCalories());
        this.hrmFile = new SimpleStringProperty(StringUtils.getTextOrEmptyString(exercise.getHrmFile()));
        this.comment = new SimpleStringProperty(StringUtils.getTextOrEmptyString(exercise.getComment()));

        // convert exercise values when english unit system is enabled
        double dAvgSpeed = exercise.getAvgSpeed();
        if (formatUtils.getUnitSystem() == UnitSystem.ENGLISH) {
            this.distance.set(ConvertUtils.convertKilometer2Miles(exercise.getDistance(), false));
            dAvgSpeed = ConvertUtils.convertKilometer2Miles(dAvgSpeed, false);
            this.ascent.set(ConvertUtils.convertMeter2Feet(this.ascent.get()));
            this.descent.set(ConvertUtils.convertMeter2Feet(this.descent.get()));
        }

        // convert avg speed value to string property by using the proper speed mode
        final SpeedMode speedMode = exercise.getSportType() == null ?
                preferredSpeedMode : exercise.getSportType().getSpeedMode();
        this.speedConverter = new SpeedToStringConverter(speedMode);
        this.avgSpeed = new SimpleStringProperty(speedConverter.doubleSpeedtoString(dAvgSpeed));

        setupSportTypeRecordDistance();
        setupChangeListenersForAutoCalculation();
        setupSpeedModeUpdateOnSportTypeChanges();
    }

    /**
     * Creates a new Exercise domain object from the edited JavaFX properties.
     *
     * @return Exercise
     */
    public Exercise getExercise() {
        final Exercise exercise = new Exercise(id);
        exercise.setDateTime(LocalDateTime.of(date.get(), time.get()));
        exercise.setSportType(sportType.getValue());
        exercise.setSportSubType(sportSubType.getValue());
        exercise.setIntensity(intensity.getValue());
        exercise.setDistance(distance.getValue());
        exercise.setAvgSpeed(speedConverter.stringSpeedToDouble(avgSpeed.getValue()));
        exercise.setDuration(duration.getValue());
        exercise.setAvgHeartRate(avgHeartRate.getValue());
        exercise.setAscent(ascent.getValue());
        exercise.setDescent(descent.getValue());
        exercise.setCalories(calories.getValue());
        exercise.setEquipment(equipment.getValue());
        // ignore empty comment for optional inputs
        exercise.setHrmFile(StringUtils.getTrimmedTextOrNull(hrmFile.getValue()));
        exercise.setComment(StringUtils.getTrimmedTextOrNull(comment.getValue()));

        // convert weight value when english unit system is enabled
        if (this.formatUtils.getUnitSystem() == UnitSystem.ENGLISH) {
            exercise.setDistance(ConvertUtils.convertMiles2Kilometer(exercise.getDistance()));
            exercise.setAvgSpeed(ConvertUtils.convertMiles2Kilometer(exercise.getAvgSpeed()));
            exercise.setAscent(ConvertUtils.convertFeet2Meter(exercise.getAscent()));
            exercise.setDescent(ConvertUtils.convertFeet2Meter(exercise.getDescent()));
        }

        // set optional Integer values to null when value is null (IntegerProperty can't handle null values)
        exercise.setAvgHeartRate(exercise.getAvgHeartRate() == 0 ? null : exercise.getAvgHeartRate());
        exercise.setAscent(exercise.getAscent() == 0 ? null : exercise.getAscent());
        exercise.setDescent(exercise.getDescent() == 0 ? null : exercise.getDescent());
        exercise.setCalories(exercise.getCalories() == 0 ? null : exercise.getCalories());
        return exercise;
    }

    /**
     * Returns the converter for speed values, which also handles the current speed mode.
     *
     * @return SpeedToStringConverter
     */
    public SpeedToStringConverter getSpeedConverter() {
        return speedConverter;
    }

    /**
     * Sets the new values for the properties distance, avg speed and duration depending on the current
     * auto calculation mode. The auto calculated field will not be set, to ensure consistent data.
     *
     * @param newDistance new distance in km (will be converted when english unit system is used)
     * @param newAvgSpeed new avg speed in km/h (will be converted when english unit system is used)
     * @param newDuration new duration in seconds
     */
    public void setAutoCalcFields(final double newDistance, final double newAvgSpeed, final int newDuration) {

        if (!autoCalcDistance.get()) {
            if (this.formatUtils.getUnitSystem() == UnitSystem.ENGLISH) {
                this.distance.set(ConvertUtils.convertKilometer2Miles(newDistance, false));
            } else {
                this.distance.set(newDistance);
            }
        }
        if (!autoCalcAvgSpeed.get()) {
            setAvgSpeedDoubleValue(newAvgSpeed);
        }
        if (!autoCalcDuration.get()) {
            this.duration.set(newDuration);
        }
    }

    /**
     * Sets the specified average speed double value. This is converted by using the current speed mode and unit system.
     * The auto calculation field will not be handled here!
     *
     * @param dAvgSpeedMetric avg speed value in metric unit
     */
    public void setAvgSpeedDoubleValue(double dAvgSpeedMetric) {
        double fAvgSpeedCurrentUnit = dAvgSpeedMetric;
        if (this.formatUtils.getUnitSystem() == UnitSystem.ENGLISH) {
            fAvgSpeedCurrentUnit = ConvertUtils.convertKilometer2Miles(dAvgSpeedMetric, false);
        }
        this.avgSpeed.set(speedConverter.doubleSpeedtoString(fAvgSpeedCurrentUnit));
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
                avgSpeed.set(speedConverter.doubleSpeedtoString(0.0));
            }

            autoCalculate();

            // force value changes for distance and avg speed, so the validation will be executed
            // depending on the new selected sport type (no other way to fire the event)
            distance.set(distance.get() + 1);
            distance.set(distance.get() - 1);

            String sAvgSpeed = avgSpeed.get();
            avgSpeed.set("");
            avgSpeed.set(sAvgSpeed);
        });
    }

    /**
     * Setup the value change listeners which perform the automatic calculation depending on the current
     * auto calculation mode.
     */
    private void setupChangeListenersForAutoCalculation() {
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
     * Setup the listener for switching to the proper speed mode whenever the selected sport type changes.
     */
    private void setupSpeedModeUpdateOnSportTypeChanges() {
        sportType.addListener((observable, oldValue, newValue) -> {

            // If speed mode has changed: get the current avg speed double value by using the old speed mode.
            // Then switch the converter to the new speed mode and reset the double value - this does the conversion.
            SpeedMode oldSpeedMode = speedConverter.getSpeedMode();
            SpeedMode newSpeedMode = newValue == null ? null : newValue.getSpeedMode();
            if (oldSpeedMode != newSpeedMode) {

                Double dOldAvgSpeed = speedConverter.stringSpeedToDouble(avgSpeed.get());
                speedConverter.setSpeedMode(newSpeedMode);
                avgSpeed.set(speedConverter.doubleSpeedtoString(dOldAvgSpeed));
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
        Double dAvgSpeed = speedConverter.stringSpeedToDouble(avgSpeed.get());
        dAvgSpeed = dAvgSpeed == null ? 0f : dAvgSpeed;

        if (sportTypeRecordDistance.get() && dAvgSpeed != null) {
            if (autoCalcDistance.get()) {
                if (dAvgSpeed > 0 && duration.get() > 0) {
                    distance.set(CalculationUtils.calculateDistance(dAvgSpeed, duration.get()));
                } else {
                    distance.set(0);
                }
            } else if (autoCalcAvgSpeed.get()) {
                double fCalculatedAvgSpeed = 0;
                if (distance.get() > 0 && duration.get() > 0) {
                    fCalculatedAvgSpeed = CalculationUtils.calculateAvgSpeed(distance.get(), duration.get());
                }
                avgSpeed.set(speedConverter.doubleSpeedtoString(fCalculatedAvgSpeed));
            } else if (autoCalcDuration.get()) {
                if (distance.get() > 0 && dAvgSpeed > 0) {
                    duration.set(CalculationUtils.calculateDuration(distance.get(), dAvgSpeed));
                } else {
                    duration.set(0);
                }
            } else {
                throw new IllegalStateException("Invalid auto calculation mode!");
            }
        }
    }
}
