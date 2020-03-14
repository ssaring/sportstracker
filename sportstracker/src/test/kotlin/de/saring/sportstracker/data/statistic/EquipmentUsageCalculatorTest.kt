package de.saring.sportstracker.data.statistic

import de.saring.sportstracker.data.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * This class contains all unit tests for the [EquipmentUsageCalculator] class.
 *
 * @author Stefan Saring
 */
class EquipmentUsageCalculatorTest {

    private val eqRoadBike = Equipment(1).apply {
        setName("Road bike")
    }
    private val eqMTB = Equipment(2).apply {
        setName("MTB")
    }

    private val stCycling = SportType(1).apply {
        setName("Cycling")
        equipmentList.set(eqRoadBike)
        equipmentList.set(eqMTB)
    }

    private val eqRoadShoes = Equipment(1).apply {
        setName("Road shows")
    }
    private val eqTrailShoes = Equipment(2).apply {
        setName("Trail shoes")
    }

    private val stRunning = SportType(2).apply {
        setName("Running")
        equipmentList.set(eqRoadShoes)
        equipmentList.set(eqTrailShoes)
    }

    private val stSwimming = SportType(3).apply {
        setName("Swimming")
    }

    private val sportTypeList = SportTypeList().apply {
        set(stCycling)
        set(stRunning)
        set(stSwimming)
    }

    /**
     * Tests the calculation: no exercises are available, so the calculated usage has to be empty for all equipment
     * entries.
     */
    @Test
    fun testStatisticCalculatorNoExercises() {

        // prepare
        val exerciseList = ExerciseList()

        // test
        val usages = EquipmentUsageCalculator.calculateEquipmentUsage(exerciseList, this.sportTypeList)

        // verify
        assertEquals(3, usages.sportTypeMap.size)

        // check cycling usage
        val euCycling = usages.sportTypeMap[stCycling]
        assertEquals(2, euCycling!!.equipmentMap.size)
        val euRoadBike = euCycling.equipmentMap[eqRoadBike]
        assertUsage(euRoadBike!!, eqRoadBike, 0.0, 0, null, null)
        val euMTB = euCycling.equipmentMap[eqMTB]
        assertUsage(euMTB!!, eqMTB,0.0, 0, null, null)

        // check running usage
        val euRunning = usages.sportTypeMap[stRunning]
        assertEquals(2, euRunning!!.equipmentMap.size)
        val euRoadShoes = euRunning.equipmentMap[eqRoadShoes]
        assertUsage(euRoadShoes!!, eqRoadShoes, 0.0, 0, null, null)
        val euTrailShoes = euRunning.equipmentMap[eqTrailShoes]
        assertUsage(euTrailShoes!!, eqTrailShoes,0.0, 0, null, null)

        // check swimming usage
        val euSwimming = usages.sportTypeMap[stSwimming]
        assertTrue(euSwimming!!.equipmentMap.isEmpty())
    }

    /**
     * Tests the calculation: there are exercises with equipment usage available, so the calculated usage has to be
     * valid.
     */
    @Test
    fun testStatisticCalculatorWithExercises() {

        // prepare
        val exerciseList = ExerciseList().apply {

            // cycling exercises (2x Road Bike, 1x MTB)
            set(Exercise(0).apply {
                dateTime = LocalDateTime.of(2019, 5, 15, 14, 30, 0)
                distance = 40.0f
                duration = (1.5 * 3600).toInt()
                sportType = stCycling
                equipment = eqRoadBike
            })
            set(Exercise(1).apply {
                dateTime = LocalDateTime.of(2019, 3, 20, 14, 30, 0)
                distance = 25.0f
                duration = (1 * 3600).toInt()
                sportType = stCycling
                equipment = eqRoadBike
            })
            set(Exercise(2).apply {
                dateTime = LocalDateTime.of(2019, 7, 18, 14, 30, 0)
                distance = 44.0f
                duration = (2 * 3600).toInt()
                sportType = stCycling
                equipment = eqMTB
            })

            // running exercises (2x Trail Shoes, 1x without equipment)
            set(Exercise(10).apply {
                dateTime = LocalDateTime.of(2019, 7, 15, 14, 30, 0)
                distance = 13.5f
                duration = (1.2 * 3600).toInt()
                sportType = stRunning
                equipment = eqTrailShoes
            })
            set(Exercise(11).apply {
                dateTime = LocalDateTime.of(2019, 6, 20, 14, 30, 0)
                distance = 15.5f
                duration = (1.5 * 3600).toInt()
                sportType = stRunning
                equipment = eqTrailShoes
            })
            set(Exercise(12).apply {
                dateTime = LocalDateTime.of(2019, 10, 18, 14, 30, 0)
                distance = 20.0f
                duration = (1.8 * 3600).toInt()
                sportType = stRunning
                equipment = null
            })
        }

        // test
        val usages = EquipmentUsageCalculator.calculateEquipmentUsage(exerciseList, this.sportTypeList)

        // verify
        assertEquals(3, usages.sportTypeMap.size)

        // check cycling usage
        val euCycling = usages.sportTypeMap[stCycling]
        assertEquals(2, euCycling!!.equipmentMap.size)
        val euRoadBike = euCycling.equipmentMap[eqRoadBike]
        assertUsage(euRoadBike!!, eqRoadBike, 65.0, (2.5 * 3600).toLong(),
                LocalDate.of(2019, 3, 20),
                LocalDate.of(2019, 5, 15))
        val euMTB = euCycling.equipmentMap[eqMTB]
        assertUsage(euMTB!!, eqMTB, 44.0, (2 * 3600).toLong(),
                LocalDate.of(2019, 7, 18),
                LocalDate.of(2019, 7, 18))

        // check running usage
        val euRunning = usages.sportTypeMap[stRunning]
        assertEquals(2, euRunning!!.equipmentMap.size)
        val euRoadShoes = euRunning.equipmentMap[eqRoadShoes]
        assertUsage(euRoadShoes!!, eqRoadShoes, 0.0, 0, null, null)
        val euTrailShoes = euRunning.equipmentMap[eqTrailShoes]
        assertUsage(euTrailShoes!!, eqTrailShoes, 29.0, (2.7 * 3600).toLong(),
                LocalDate.of(2019, 6, 20),
                LocalDate.of(2019, 7, 15))

        // check swimming usage
        val euSwimming = usages.sportTypeMap[stSwimming]
        assertTrue(euSwimming!!.equipmentMap.isEmpty())
    }

    private fun assertUsage(equipmentUsage: EquipmentUsage, expectedEquipment: Equipment,
                            expectedDistance: Double, expectedDuration: Long,
                            expectedFirstUsage: LocalDate?, expectedLastUsage: LocalDate?) {

        assertEquals(expectedEquipment, equipmentUsage.equipment)
        assertEquals(expectedDistance, equipmentUsage.distance)
        assertEquals(expectedDuration, equipmentUsage.duration)
        assertEquals(expectedFirstUsage, equipmentUsage.firstUsage)
        assertEquals(expectedLastUsage, equipmentUsage.lastUsage)
    }
}
