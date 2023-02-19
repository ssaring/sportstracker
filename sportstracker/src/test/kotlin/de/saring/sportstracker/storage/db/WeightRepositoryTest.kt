package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Weight
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Unit tests of  the [WeightRepository] class. The tests are using this repository provided by the [DbStorage] class,
 * this handles also the database connection and schema setup. The create() method is tested automatically during the
 * test data setup.
 *
 * @author Stefan Saring
 */
class WeightRepositoryTest : DbStorageTestBase() {

    private lateinit var weight1: Weight
    private lateinit var weight2: Weight

    override fun setUpTestData() {
        weight1 = createWeight(75.0, "Weight 1")
        weight2 = createWeight(77.5, null)
    }

    /**
     * Test of readAll(): needs to provide all existing weights.
     */
    @Test
    fun testReadAll() {
        val notes = dbStorage.weightRepository.readAll()
        Assertions.assertEquals(2, notes.size)
    }

    /**
     * Test of readById(): needs to provide a existing weight with proper data.
     */
    @Test
    fun testReadById() {
        val weight = dbStorage.weightRepository.readById(weight1.id!!)

        Assertions.assertEquals(weight1.id, weight.id)
        Assertions.assertEquals(weight1.dateTime, weight.dateTime)
        Assertions.assertEquals(weight1.value, weight.value)
        Assertions.assertEquals(weight1.comment, weight.comment)
    }

    /**
     * Test of update(): needs to update an existing weight, will be verified by reading the weight.
     */
    @Test
    fun testUpdate() {
        weight1.value = 73.123
        weight1.comment = "FooBar"
        dbStorage.weightRepository.update(weight1)

        val weight = dbStorage.weightRepository.readById(weight1.id!!)
        Assertions.assertEquals(73.123, weight.value)
        Assertions.assertEquals("FooBar", weight.comment)
    }

    /**
     * Test of delete(): needs to delete an existing weight, will be verified by reading all weights.
     */
    @Test
    fun testDelete() {
        dbStorage.weightRepository.delete(weight1.id!!)

        val weights = dbStorage.weightRepository.readAll()
        Assertions.assertEquals(1, weights.size)
        Assertions.assertEquals(weight2.id, weights[0].id)
    }

    private fun createWeight(value: Double, comment: String?): Weight {
        val weight = Weight(null)
        weight.dateTime = LocalDateTime.now()
        weight.value = value
        weight.comment = comment
        return dbStorage.weightRepository.create(weight)
    }
}