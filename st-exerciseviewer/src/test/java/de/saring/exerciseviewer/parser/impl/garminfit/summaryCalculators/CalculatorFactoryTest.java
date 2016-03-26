package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.testutil.ExerciseBuilder;

public class CalculatorFactoryTest {

    @Test
    public void createCalculators_CreatesMaxSpeedCalculator_WhenMaxSpeedIs0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().withSpeed(s -> {
        }).get();

        assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(MaxSpeedCalculator.class)));
    }

    @Test
    public void createCalculators_DoesNotCreateMaxSpeedCalculator_WhenMaxSpeedIsGreater0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().withSpeed(s -> s.max(123)).get();

        assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(MaxSpeedCalculator.class))));
    }

    @Test
    public void createCalculators_CreatesMaxHeartRateCalculator_WhenMaxHeartRateIs0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().get();

        assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(MaxHeartRateCalculator.class)));
    }

    @Test
    public void createCalculators_DoesNotCreateMaxHeartRateCalculator_WhenMaxHeartRateIsGreater0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().withHeartRateMax(123).get();

        assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(MaxHeartRateCalculatorTest.class))));
    }

    @Test
    public void createCalculators_CreatesAVGHeartRateCalculator_WhenAVGHeartRateIs0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().get();

        assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(AVGHeartRateCalculator.class)));
    }

    @Test
    public void createCalculators_DoesNotCreateAVGHeartRateCalculator_WhenAVGHeartRateIsGreater0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().WithHeartRateAVG(123).get();

        assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(AVGHeartRateCalculator.class))));
    }

    @Test
    public void createCalculators_DoesNotCreateAltitudeSummaryCalculator_WhenAllAltitudeValuesAreSet() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().WithAltitude(alt -> alt.min(1).avg(2).max(3)).get();

        assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(AltituteSummaryCalculator.class))));
    }

    @Test
    public void createCalculators_DoesNotCreateAltitudeSummaryCalculator_WhenRecordingModeAltitudeWasFalse() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().WithRecordingMode(r -> r.isAltitude(false)).get();

        assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(AltituteSummaryCalculator.class))));
    }

    @Test
    public void createCalculators_CreatesAltitudeSummaryCalculator_WhenAltMinIs0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().WithRecordingMode(r -> r.isAltitude(true))
                .WithAltitude(alt -> alt.avg(5).max(10)).get();

        assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(AltituteSummaryCalculator.class)));
    }

    @Test
    public void createCalculators_CreatesAltitudeSummaryCalculator_WhenAltAvgIs0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().WithRecordingMode(r -> r.isAltitude(true))
                .WithAltitude(alt -> alt.min(5).max(10)).get();

        assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(AltituteSummaryCalculator.class)));
    }

    @Test
    public void createCalculators_CreatesAltitudeSummaryCalculator_WhenAltMaxIs0() {
        CalculatorFactory classUnderTest = new CalculatorFactory();
        EVExercise exercise = ExerciseBuilder.createExercise().WithRecordingMode(r -> r.isAltitude(true))
                .WithAltitude(alt -> alt.min(5).avg(10)).get();

        assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(AltituteSummaryCalculator.class)));
    }
}
