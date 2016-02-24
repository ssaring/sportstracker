package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNot.not;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators.CalculatorFactory;
import de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators.MaxSpeedCalculator;

public class CalculatorFactoryTest {

	@Test
	public void createCalculators_CreatesMaxSpeedCalculator_WhenMaxSpeedIs0() {
		CalculatorFactory classUnderTest = new CalculatorFactory();
		EVExercise exercise = createExersize();

		assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(MaxSpeedCalculator.class)));
	}

	@Test
	public void createCalculators_DoesNotCreateMaxSpeedCalculator_WhenMaxSpeedIsGreater0() {
		CalculatorFactory classUnderTest = new CalculatorFactory();
		EVExercise exercise = createExersize();
		exercise.getSpeed().setSpeedMax(123);

		assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(MaxSpeedCalculator.class))));
	}
	
	@Test
	public void createCalculators_CreatesMaxHeartRateCalculator_WhenMaxSpeedIs0() {
		CalculatorFactory classUnderTest = new CalculatorFactory();
		EVExercise exercise = createExersize();

		assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(MaxHeartRateCalculator.class)));
	}

	@Test
	public void createCalculators_DoesNotCreateMaxHeartRateCalculator_WhenMaxSpeedIsGreater0() {
		CalculatorFactory classUnderTest = new CalculatorFactory();
		EVExercise exercise = createExersize();
		exercise.setHeartRateMax((short) 123);

		assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(MaxHeartRateCalculatorTest.class))));
	}
	
	private EVExercise createExersize() {
		EVExercise exercise = new EVExercise();
		exercise.setSpeed(new ExerciseSpeed());
		return exercise;
	}

}
