/**
 * Demonstrates the visibility fix: @Volatile makes updates from writer visible to reader.
 */
class VisibilityProblem {
    @Volatile
    private var running = true

    fun startWriter(): Thread {
        return Thread {
            repeat(100) {
                Thread.sleep(10)
                Thread.yield()
            }

            running = false
            println("Writer: установил running = false")
        }
    }

    fun startReader(): Thread {
        return Thread {
            println("Reader: начал работу (ждет running = false)")

            while (running) {
                Thread.onSpinWait()
            }

            println("Reader: завершил работу (увидел running = false)")
        }
    }
}
