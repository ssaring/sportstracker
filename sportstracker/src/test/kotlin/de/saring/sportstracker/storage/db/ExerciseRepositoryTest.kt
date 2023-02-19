package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.Exercise
import de.saring.sportstracker.data.SportSubType
import de.saring.sportstracker.data.SportType
import de.saring.util.unitcalc.SpeedMode
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Unit tests of  the [ExerciseRepository] class. The tests are using this repository provided by the [DbStorage] class,
 * this handles also the database connection and schema setup. The create() method is tested automatically during the
 * test data setup.
 *
 * @author Stefan Saring
 */
class ExerciseRepositoryTest : DbStorageTestBase() {

    private lateinit var exercise1: Exercise
    private lateinit var exercise2: Exercise

    private lateinit var sportTypes: List<SportType>
    private lateinit var sportType1: SportType
    private lateinit var sportType2: SportType

    override fun setUpTestData() {
        // create some sport types first (needed for exercises)
        creatSportType("Cycling")
        creatSportType("Running")

        // reload of updated sportType must be done via readAll(), otherwise the sport subtypes and equipments are not loaded
        sportTypes = dbStorage.sportTypeRepository.readAll()
        sportType1 = sportTypes[0]
        sportType2 = sportTypes[1]

        createExercise(sportType1, sportType1.sportSubTypeList.first(), sportType1.equipmentList.first(), "Comment 1")
        createExercise(sportType2, sportType2.sportSubTypeList.first(), sportType2.equipmentList.first(), "Comment 2")

        // reload of created exercises must be done via readAll(), otherwise the sport type, subtype and equipment are not loaded
        val exercises = dbStorage.exerciseRepository.readAll(sportTypes)
        exercise1 = exercises[0]
        exercise2 = exercises[1]
    }

    /**
     * Test of readAll(): needs to provide all existing exercises.
     */
    @Test
    fun testReadAll() {
        val exercises = dbStorage.exerciseRepository.readAll(sportTypes)
        Assertions.assertEquals(2, exercises.size)
    }

    /**
     * Test of readById(): needs to provide a existing exercise with proper data.
     */
    @Test
    fun testReadById() {
        val exercise = dbStorage.exerciseRepository.readById(exercise1.id!!)

        Assertions.assertEquals(exercise1.id, exercise.id)
        Assertions.assertEquals(exercise1.dateTime, exercise.dateTime)
        Assertions.assertNull(exercise.sportType)
        Assertions.assertNull(exercise.sportSubType)
        Assertions.assertEquals(exercise1.intensity, exercise.intensity)
        Assertions.assertEquals(exercise1.distance, exercise.distance)
        Assertions.assertEquals(exercise1.avgSpeed, exercise.avgSpeed)
        Assertions.assertEquals(exercise1.duration, exercise.duration)
        Assertions.assertEquals(exercise1.ascent, exercise.ascent)
        Assertions.assertEquals(exercise1.descent, exercise.descent)
        Assertions.assertEquals(exercise1.avgHeartRate, exercise.avgHeartRate)
        Assertions.assertEquals(exercise1.calories, exercise.calories)
        Assertions.assertEquals(exercise1.hrmFile, exercise.hrmFile)
        Assertions.assertNull(exercise.equipment)
        Assertions.assertEquals(exercise1.comment, exercise.comment)
    }

    /**
     * Test of update(): needs to update an existing exercise, will be verified by reading the exercise.
     */
    @Test
    fun testUpdate() {
        exercise1.sportType = sportType2
        exercise1.sportSubType = sportType2.sportSubTypeList.first()
        exercise1.distance = 75.123
        exercise1.comment = "FooBar"
        exercise1.equipment = null
        dbStorage.exerciseRepository.update(exercise1)

        // reload of updated exercise must be done via readAll(), otherwise the sport type, subtype and equipment are not loaded
        val exercise = dbStorage.exerciseRepository.readAll(sportTypes).first()
        Assertions.assertEquals(exercise1.id, exercise.id)
        Assertions.assertEquals(sportType2, exercise.sportType)
        Assertions.assertEquals(sportType2.sportSubTypeList.first(), exercise.sportSubType)
        Assertions.assertEquals(75.123, exercise.distance)
        Assertions.assertEquals("FooBar", exercise.comment)
        Assertions.assertNull(exercise.equipment)
    }

    /**
     * Test of delete(): needs to delete an existing exercise, will be verified by reading all exercises.
     */
    @Test
    fun testDelete() {
        dbStorage.exerciseRepository.delete(exercise1.id!!)

        val exercises = dbStorage.exerciseRepository.readAll(sportTypes)
        Assertions.assertEquals(1, exercises.size)
        Assertions.assertEquals(exercise2.id, exercises[0].id)
    }

    private fun createExercise(
        sportType: SportType,
        sportSubType: SportSubType,
        equipment: Equipment?,
        comment: String?
    ): Exercise {
        val exercise = Exercise(null)
        exercise.dateTime = LocalDateTime.now()
        exercise.sportType = sportType
        exercise.sportSubType = sportSubType
        exercise.intensity = Exercise.IntensityType.HIGH
        exercise.distance = 120.0
        exercise.avgSpeed = 30.0
        exercise.duration = 4 * 3600
        exercise.ascent = 2222
        exercise.descent = 2333
        exercise.avgHeartRate = 123
        exercise.calories = 2345
        exercise.hrmFile = "hrm.fit"
        exercise.equipment = equipment
        exercise.comment = comment
        return dbStorage.exerciseRepository.create(exercise)
    }

    private fun creatSportType(name: String): SportType {
        var sportType = SportType(null)
        sportType.setName(name)
        sportType.isRecordDistance = true
        sportType.color = Color.BLUE
        sportType.icon = "$name.png"
        sportType.speedMode = SpeedMode.SPEED
        sportType.fitId = 12

        sportType.sportSubTypeList.set(creatSportSubType("$name Subtype 1"))
        sportType.equipmentList.set(createEquipment("$name Equipment 1"))

        return dbStorage.sportTypeRepository.create(sportType)
    }

    private fun creatSportSubType(name: String): SportSubType {
        val sportSubType = SportSubType(null)
        sportSubType.setName(name)
        sportSubType.fitId = 23
        return sportSubType
    }

    private fun createEquipment(name: String): Equipment {
        val equipment = Equipment(null)
        equipment.setName(name)
        equipment.isNotInUse = false
        return equipment
    }
}