package de.saring.sportstracker.data.statistic

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.ExerciseList
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.data.SportTypeList
import java.time.LocalDate

// TODO document, integrate and test
object EquipmentUsageCalculator {

    fun calculateEquipmentUsage(exerciseList: ExerciseList, sportTypeList: SportTypeList): EquipmentUsages {

        val equipmentUsages = createInitialEquipmentUsages(sportTypeList)

        exerciseList.forEach { exercise ->
            exercise.equipment?.let { equipment ->

                val exerciseDate = exercise.dateTime.toLocalDate()

                // TODO without !! ?
                val equipmentUsage: EquipmentUsage = equipmentUsages.usages[exercise.sportType]!!.usages[equipment]!!

                equipmentUsage.distance += exercise.distance
                equipmentUsage.duration += exercise.duration

                // TODO can this be done simpler?
                equipmentUsage.firstUsage = if (equipmentUsage.firstUsage == null) exerciseDate else {
                    if (exerciseDate.isBefore(equipmentUsage.firstUsage)) exerciseDate else equipmentUsage.firstUsage
                }
                equipmentUsage.lastUsage = if (equipmentUsage.lastUsage == null) exerciseDate else {
                    if (exerciseDate.isAfter(equipmentUsage.lastUsage)) exerciseDate else equipmentUsage.lastUsage
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

class EquipmentUsages(val usages: Map<SportType, EquipmentUsagesInSportType>)

class EquipmentUsagesInSportType(val usages: Map<Equipment, EquipmentUsage>)

class EquipmentUsage(
        val equipment: Equipment,
        var distance: Double = 0.0,
        var duration: Long = 0,
        var firstUsage: LocalDate? = null,
        var lastUsage: LocalDate? = null)
