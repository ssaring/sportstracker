package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import static org.junit.Assert.assertTrue;

import org.junit.Before;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseAltitude;
import de.saring.exerciseviewer.data.ExerciseSample;

public class AltitudeSummaryCalculatorTest {

    private ExerciseSample altMin;
    private ExerciseSample altMax;

    @Before
    public void initSamples() {
        altMin = createSample(10);
        altMax = createSample(100);
    }

    @Test
    public void setsMinAltitude() {
        EVExercise exercise = createExercise();
        executeClassUnderTestAndAggregateSamples(exercise);

        assertThat(exercise.getAltitude().getAltitudeMin(), equalTo(altMin.getAltitude()));
    }
    
    @Test
    public void setsAvgAltitude() {
        EVExercise exercise = createExercise();
        executeClassUnderTestAndAggregateSamples(exercise);

        short expected = (short) ((altMin.getAltitude() + altMax.getAltitude()) / 2);
        assertThat(exercise.getAltitude().getAltitudeAVG(), equalTo(expected));
    }

    @Test
    public void setsMaxAltitude() {
        EVExercise exercise = createExercise();
        executeClassUnderTestAndAggregateSamples(exercise);

        assertThat(exercise.getAltitude().getAltitudeMax(), equalTo(altMax.getAltitude()));
    }

    @Test
    public void createsAltitude_ifItWasNull() {
        EVExercise exercise = createExercise();
        AltituteSummaryCalculator classUnderTest = new AltituteSummaryCalculator(exercise);

        classUnderTest.aggregateAndUpdateExercise();

        assertThat(exercise.getAltitude(), not(nullValue()));
    }

    @Test
    public void doesNotCreateAltitude_ifItWasNotNull() {
        EVExercise exercise = createExercise();
        final ExerciseAltitude expectedAltitude = new ExerciseAltitude();
        exercise.setAltitude(expectedAltitude);
        AltituteSummaryCalculator classUnderTest = new AltituteSummaryCalculator(exercise);

        classUnderTest.aggregateAndUpdateExercise();

        assertThat(exercise.getAltitude(), sameInstance(expectedAltitude));
    }
    
    private void executeClassUnderTestAndAggregateSamples(EVExercise exercise) {
        AltituteSummaryCalculator classUnderTest = new AltituteSummaryCalculator(exercise);
        classUnderTest.collect(altMin);
        classUnderTest.collect(altMax);
        classUnderTest.aggregateAndUpdateExercise();
    }

    private ExerciseSample createSample(int altitude) {
        assertTrue(altitude <= Short.MAX_VALUE);
        ExerciseSample sample = new ExerciseSample();
        sample.setAltitude((short) altitude);
        return sample;
    }

    private EVExercise createExercise() {
        EVExercise exercise = new EVExercise();
        exercise.setSampleList(new ExerciseSample[]{altMin, altMax});
        return exercise;
    }

}
