package homework

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Transforms each item in a separate coroutine and preserves the original order.
 */
suspend fun <T, R> parallelTransform(
    items: List<T>,
    transform: suspend (T) -> R
): List<R> = coroutineScope {
    items
        .map { item -> async { transform(item) } }
        .awaitAll()
}
