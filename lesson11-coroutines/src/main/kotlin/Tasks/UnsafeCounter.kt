package homework

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread-safe counter for concurrent coroutine increments.
 */
class UnsafeCounter {

    private val value = AtomicInteger(0)

    suspend fun increment() {
        delay(1)
        value.incrementAndGet()
    }

    fun getValue(): Int = value.get()

    fun reset() {
        value.set(0)
    }

    suspend fun runConcurrentIncrements(
        coroutineCount: Int = 10,
        incrementsPerCoroutine: Int = 1000
    ): Int = coroutineScope {
        val jobs = List(coroutineCount) {
            launch(Dispatchers.Default) {
                repeat(incrementsPerCoroutine) {
                    increment()
                }
            }
        }

        jobs.joinAll()
        getValue()
    }
}
