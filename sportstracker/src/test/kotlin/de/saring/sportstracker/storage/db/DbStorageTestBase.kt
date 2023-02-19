package de.saring.sportstracker.storage.db

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
}