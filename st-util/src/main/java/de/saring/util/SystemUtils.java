package de.saring.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class which provides system and JVM related methods.
 *
 * @author Stefan Saring
 */
public final class SystemUtils {

    private static final Logger LOGGER = Logger.getLogger(SystemUtils.class.getName());

    private static final long DEFAULT_SLEEP_TIME_BEFORE_GC = 400;

    private SystemUtils() {
    }

    /**
     * See {@link #triggerGC(long)}, it uses a default sleep time.
     */
    public static void triggerGC() {
        triggerGC(DEFAULT_SLEEP_TIME_BEFORE_GC);
    }

    /**
     * Triggers an asynchronous garbage collection after waiting the specified sleep time.
     * This method returns immediately, the garbage collection is executed in a new thread.
     * A short pause before GC execution makes sense, when e.g. a dialog has been closed
     * and the UI needs to be updated asynchronously before.
     *
     * @param waitBefore sleep time before GC in msec (0 for no sleep)
     */
    public static void triggerGC(final long waitBefore) {
        new Thread(() -> {

            if (waitBefore > 0) {
                try {
                    Thread.sleep(waitBefore);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Failed to sleep before execution of garbage collection!", e);
                }
            }

            final long start = System.currentTimeMillis();
            System.gc();
            LOGGER.info("Garbage collection completed in " + (System.currentTimeMillis() - start) + " msec");
        }).start();
    }
}
