package de.saring.sportstracker.util

import org.mockito.Mockito


/**
 * Some helpers to make Mockito usage in Kotlin easier.
 *
 * @author Stefan Saring
 */
object MockitoKotlin {

    /**
     * Workaround for Mockito problems in Kotlin: returns Mockito.any() as nullable type to avoid
     * java.lang.IllegalStateException when null is returned.
     */
    fun <T> any(): T = Mockito.any<T>()
}
