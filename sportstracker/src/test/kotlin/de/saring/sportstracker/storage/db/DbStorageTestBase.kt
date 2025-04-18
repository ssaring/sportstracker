package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.SportSubType
import de.saring.sportstracker.data.SportType
import de.saring.util.unitcalc.SpeedMode
import javafx.scene.paint.Color
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Base class for all database storage tests, it provides the setup and tear down of the database connection.
 * For each test there will be a new in-memory database created which contains the full schema but no data.
 *
 * @author Stefan Saring
 */
abstract class DbStorageTestBase {

    protected val dbStorage = DbStorage()

    @BeforeEach
    fun setUp() {
        dbStorage.openDatabase(DbStorage.IN_MEMORY_FILENAME)
        setUpTestData()
    }

    @AfterEach
    fun tearDown() {
        dbStorage.closeDatabase()
    }

    /**
     * Creates the test data before execution of each test. By default there's no test data, can be overwritten.
     */
    protected open fun setUpTestData() {
    }

    /**
     * Creates and persists a test SportType object with the specified name.
     */
    protected fun creatSportType(name: String): SportType {
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