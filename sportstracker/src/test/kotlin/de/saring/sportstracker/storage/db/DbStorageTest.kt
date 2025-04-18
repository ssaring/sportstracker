package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Weight
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Basic unit tests of  the [DbStorage] class (open and close database, basic access, commit).
 * More detailed tests are done implicitly in the Repository tests.
 *
 * @author Stefan Saring
 */
class DbStorageTest : DbStorageTestBase() {

    /**
     * Test database schema version, which also ensures that all schema update files have been executed properly.
     */
    @Test
    fun testGetSchemaVersion() {
        Assertions.assertEquals(DbStorage.SCHEMA_VERSION, dbStorage.getSchemaVersion())
    }

    /**
     * Basic storage tests for creating a new Weight entity, commit the change and read from database.
     */
    @Test
    fun testStorage() {

        val weight = Weight(null);
        weight.dateTime = LocalDateTime.now()
        weight.value = 75.0
        weight.comment = "FooBar"

        dbStorage.weightRepository.create(weight)
        dbStorage.commitChanges()

        val weights = dbStorage.weightRepository.readAll()
        Assertions.assertEquals(1, weights.size)
        Assertions.assertEquals(75.0, weights[0].value)
        Assertions.assertEquals("FooBar", weights[0].comment)
    }
}