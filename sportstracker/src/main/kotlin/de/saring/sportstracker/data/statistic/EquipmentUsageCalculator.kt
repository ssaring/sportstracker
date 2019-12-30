package de.saring.sportstracker.data.statistic

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.ExerciseList
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.data.SportTypeList
import java.time.LocalDate

/**
 * Calculator for the usage of equipment, grouped by sport types.
 *
 * @author Stefan Saring
 */
object EquipmentUsageCalculator {

    /**
     * Calculates the usage of equipment in all exercises. The usage will be calculated for all equipments defined
     * in the passed sport types, also when it has not been used.
     *
     * @param exerciseList list of all Exercises
     * @param sportTypeList list of all SportTypes
     * @return map of equipment usages, grouped by sport types
     */
    fun calculateEquipmentUsage(exerciseList: ExerciseList, sportTypeList: SportTypeList): EquipmentUsages {

        val equipmentUsages = createInitialEquipmentUsages(sportTypeList)

        exerciseList.forEach { exercise ->
            exercise.equipment?.let { equipment ->

                val eqUsagesInSportType = equipmentUsages.sportTypeMap[exercise.sportType] ?: error("Not found for SportType!")
                val equipmentUsage: EquipmentUsage = eqUsagesInSportType.equipmentMap[equipment] ?: error("Not found for Equipment!")

                equipmentUsage.distance += exercise.distance
                equipmentUsage.duration += exercise.duration

                val exerciseDate = exercise.dateTime.toLocalDate()
                if (equipmentUsage.firstUsage == null || exerciseDate.isBefore(equipmentUsage.firstUsage)) {
                    equipmentUsage.firstUsage = exerciseDate
                }
                if (equipmentUsage.lastUsage == null || exerciseDate.isAfter(equipmentUsage.lastUsage)) {
                    equipmentUsage.lastUsage = exerciseDate
                }
            }
        }

        return equipmentUsages
    }

    private fun createInitialEquipmentUsages(sportTypes: SportTypeList): EquipmentUsages {
        return EquipmentUsages(sportTypes.map { sportType ->
            sportType to EquipmentUsagesInSportType(sportType.equipmentList.map { equipment ->
                equipment to EquipmentUsage(equipment)
            }.toMap())
        }.toMap())
    }
}

/**
 * Container class for all equipment usages.
 *
 * @property sportTypeMap map of usages grouped by sport types
 */
class EquipmentUsages(val sportTypeMap: Map<SportType, EquipmentUsagesInSportType>)

/**
 * Container class for the equipment usages in one single sport type.
 *
 * @property equipmentMap map of usages grouped by equipments
 */
class EquipmentUsagesInSportType(val equipmentMap: Map<Equipment, EquipmentUsage>)

/**
 * Container class for the usage of one single equipment.
 *
 * @property equipment the used equipment
 * @property distance: total usage distance in kilometers
 * @property duration total usage duration in seconds
 * @property firstUsage first usage date of the equipment (null when unused)
 * @property lastUsage last usage date of the equipment (null when unused)
 */
class EquipmentUsage(
        val equipment: Equipment,
        var distance: Double = 0.0,
        var duration: Long = 0,
        var firstUsage: LocalDate? = null,
        var lastUsage: LocalDate? = null)
