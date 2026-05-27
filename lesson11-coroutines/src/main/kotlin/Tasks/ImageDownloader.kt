package org.example.MDTask

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import java.io.File
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

data class Info(
    val totalTime: Long,
    val yes: Int,
    val no: Int
)

object ImageDownloader {
    fun run(
        links: List<String>,
        outputDir: File = File("lesson11-coroutines/downloads")
    ): Info = runBlocking {
        outputDir.mkdirs()
        val yes = AtomicInteger(0)
        val no = AtomicInteger(0)
        val completed = AtomicInteger(0)

        val time = measureTimeMillis {
            supervisorScope {
                val downloads = links.mapIndexed { index, link ->
                    async(Dispatchers.IO) {
                        val downloaded = download(link, outputDir, index)
                        if (downloaded) {
                            yes.incrementAndGet()
                        } else {
                            no.incrementAndGet()
                        }

                        val current = completed.incrementAndGet()
                        println("Downloaded $current/${links.size}")
                        downloaded
                    }
                }
                downloads.awaitAll()
            }
        }

        Info(
            totalTime = time,
            yes = yes.get(),
            no = no.get()
        )
    }

    private fun download(link: String, outputDir: File, index: Int): Boolean {
        return runCatching {
            val connection = URL(link).openConnection().apply {
                connectTimeout = 5_000
                readTimeout = 5_000
            }
            val bytes = connection.getInputStream().use { it.readBytes() }
            File(outputDir, "img_$index.jpg").writeBytes(bytes)
        }.isSuccess
    }
}

fun main() {
    val links = List(10) { "https://picsum.photos/200/300" }
    println(ImageDownloader.run(links))
}
