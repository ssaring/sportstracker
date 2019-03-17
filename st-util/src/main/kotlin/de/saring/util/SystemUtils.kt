package de.saring.util

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Helper class which provides system and JVM related methods.
 *
 * @author Stefan Saring
 */
object SystemUtils {

    private val LOGGER = Logger.getLogger(SystemUtils::class.java.name)

    private const val DEFAULT_SLEEP_TIME_BEFORE_GC: Long = 400

    /**
     * Triggers an asynchronous garbage collection after waiting the specified sleep time. This method returns
     * immediately, the garbage collection is executed in a new thread. Before GC execution this thread waits a short
     * while (400 msec). When e.g. a dialog has been closed, the UI can be updated asynchronously before.
     */
    @JvmStatic
    fun triggerGC() {

        Thread {
            try {
                Thread.sleep(DEFAULT_SLEEP_TIME_BEFORE_GC)
            } catch (e: InterruptedException) {
                LOGGER.log(Level.SEVERE, "Failed to sleep before execution of garbage collection!", e)
            }

            val start = System.currentTimeMillis()
            System.gc()
            LOGGER.fine("Garbage collection completed in ${System.currentTimeMillis() - start} msec")
        }.start()
    }
}
