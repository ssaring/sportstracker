package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.SportSubType
import de.saring.sportstracker.data.SportType
import de.saring.util.unitcalc.SpeedMode
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Unit tests of  the [SportTypeRepository] class. The tests are using this repository provided by the [DbStorage] 
 * class, this handles also the database connection and schema setup. The create() method is tested automatically during
 * the test data setup.
 *
 * @author Stefan Saring
 */
class SportTypeRepositoryTest : DbStorageTestBase() {

    private lateinit var sportType1: SportType
    private lateinit var sportType2: SportType

    override fun setUpTestData() {
        creatSportType("Cycling", Color.BLUE, SpeedMode.SPEED, 3)
        creatSportType("Running", Color.GREEN, SpeedMode.PACE, 5)

        // reload of created sport types must be done via readAll(), otherwise the sport subtypes and equipments are not loaded
        val sportTypes = dbStorage.sportTypeRepository.readAll()
        sportType1 = sportTypes[0]
        sportType2 = sportTypes[1]
    }

    /**
     * Test of readAll(): needs to provide all existing sport types.
     */
    @Test
    fun testReadAll() {
        val sportTypes = dbStorage.sportTypeRepository.readAll()
        Assertions.assertEquals(2, sportTypes.size)

        // check sport subtypes and equipment list of first sport type, these lists are not read in readById()
        val stFirst = sportTypes[0]
        Assertions.assertEquals(sportType1.getName(), stFirst.getName())

        Assertions.assertEquals(2, stFirst.sportSubTypeList.size())
        Assertions.assertEquals("${stFirst.getName()} Subtype 1", stFirst.sportSubTypeList.getAt(0).getName())
        Assertions.assertEquals(11, stFirst.sportSubTypeList.getAt(0).fitId)

        Assertions.assertEquals(2, stFirst.equipmentList.size())
        Assertions.assertEquals("${stFirst.getName()} Equipment 1", stFirst.equipmentList.getAt(0).getName())
        Assertions.assertFalse(stFirst.equipmentList.getAt(0).isNotInUse)
    }

    /**
     * Test of readById(): needs to provide a existing sport type with proper data.
     */
    @Test
    fun testReadById() {
        val sportType = dbStorage.sportTypeRepository.readById(sportType1.id!!)

        Assertions.assertEquals(sportType1.id, sportType.id)
        Assertions.assertEquals(sportType1.getName(), sportType.getName())
        Assertions.assertEquals(sportType1.isRecordDistance, sportType.isRecordDistance)
        Assertions.assertEquals(sportType1.color, sportType.color)
        Assertions.assertEquals(sportType1.icon, sportType.icon)
        Assertions.assertEquals(sportType1.speedMode, sportType.speedMode)
        Assertions.assertEquals(sportType1.fitId, sportType.fitId)

        // sport types and equipments are loaded only in method readAll(), will be tested in its test
    }

    /**
     * Test of update(): needs to update the basic properties of an existing sport type, will be verified by reading
     * the sport type.
     */
    @Test
    fun testUpdateSportTypeProperties() {
        sportType1.setName("FooBar")
        sportType1.icon = "FooBar.png"
        dbStorage.sportTypeRepository.update(sportType1)

        val sportType = dbStorage.sportTypeRepository.readById(sportType1.id!!)
        Assertions.assertEquals("FooBar", sportType.getName())
        Assertions.assertEquals("FooBar.png", sportType.icon)
    }

    /**
     * Test of update(): needs to update the modified sport subtypes and equipments of an existing sport type, will be
     * verified by reading the the sport type.
     */
    @Test
    fun testUpdateSportTypeModifiedSportSubtypesAndEquipments() {

        // delete first sport subtype, modify second sport subtype and add a new sport subtype
        val sportSubType1 = sportType1.sportSubTypeList.getAt(0)
        val sportSubType2 = sportType1.sportSubTypeList.getAt(1)
        val sportSubType3 = creatSportSubType("Subtype 3", null)
        sportType1.sportSubTypeList.removeByID(sportSubType1.id!!)
        sportSubType2.setName("SST2 FooBar")
        sportType1.sportSubTypeList.set(sportSubType3)

        // delete first equipment, modify second equipment and add a new equipment
        val equipment1 = sportType1.equipmentList.getAt(0)
        val equipment2 = sportType1.equipmentList.getAt(1)
        val equipment3 = createEquipment("Equipment 3", true)
        sportType1.equipmentList.removeByID(equipment1.id!!)
        equipment2.setName("EQ2 FooBar")
        sportType1.equipmentList.set(equipment3)

        dbStorage.sportTypeRepository.update(sportType1)

        // reload of updated sportType must be done via readAll(), otherwise the sport subtypes and equipments are not loaded
        val sportType = dbStorage.sportTypeRepository.readAll().first()

        // verify modified sport subtype list
        Assertions.assertEquals(2, sportType.sportSubTypeList.size())
        Assertions.assertEquals(equipment2.id, sportType.sportSubTypeList.getAt(0).id)
        Assertions.assertEquals("SST2 FooBar", sportType.sportSubTypeList.getAt(0).getName())
        Assertions.assertEquals("Subtype 3", sportType.sportSubTypeList.getAt(1).getName())

        // verify modified equipment list
        Assertions.assertEquals(2, sportType.equipmentList.size())
        Assertions.assertEquals(equipment2.id, sportType.equipmentList.getAt(0).id)
        Assertions.assertEquals("EQ2 FooBar", sportType.equipmentList.getAt(0).getName())
        Assertions.assertEquals("Equipment 3", sportType.equipmentList.getAt(1).getName())
    }

    /**
     * Test of delete(): needs to delete an existing sport type, will be verified by reading all sport types.
     */
    @Test
    fun testDelete() {
        dbStorage.sportTypeRepository.delete(sportType1.id!!)

        val sportTypes = dbStorage.sportTypeRepository.readAll()
        Assertions.assertEquals(1, sportTypes.size)
        Assertions.assertEquals(sportType2.id, sportTypes[0].id)
    }

    private fun creatSportType(name: String, color: Color, speedMode: SpeedMode, fitId: Int?): SportType {
        var sportType = SportType(null)
        sportType.setName(name)
        sportType.isRecordDistance = true
        sportType.color = color
        sportType.icon = "$name.png"
        sportType.speedMode = speedMode
        sportType.fitId = fitId

        sportType.sportSubTypeList.set(creatSportSubType("$name Subtype 1", 11))
        sportType.sportSubTypeList.set(creatSportSubType("$name Subtype 2", null))

        sportType.equipmentList.set(createEquipment("$name Equipment 1", false))
        sportType.equipmentList.set(createEquipment("$name Equipment 2", true))

        return dbStorage.sportTypeRepository.create(sportType)
    }

    private fun creatSportSubType(name: String, fitId: Int?): SportSubType {
        val sportSubType = SportSubType(null)
        sportSubType.setName(name)
        sportSubType.fitId = fitId
        return sportSubType
    }

    private fun createEquipment(name: String, isNotInUse: Boolean): Equipment {
        val equipment = Equipment(null)
        equipment.setName(name)
        equipment.isNotInUse = isNotInUse
        return equipment
    }
}