package com.example.spring_1.service

import org.slf4j.LoggerFactory
import com.example.spring_1.enity.Nigga
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import com.example.spring_1.config.NiggaServiceConfig

private val log = LoggerFactory.getLogger(NiggaService::class.java)

@Service
class NiggaService(
    private val config: NiggaServiceConfig
) {

    private val niggas = ConcurrentHashMap<Int, Nigga>()
    private val idGenerator = AtomicInteger(1)

    fun create(nigga: Nigga): Nigga? {
        if (nigga.name in config.forbiddenNames) {
            log.warn("Попытка затолкать в подвал элитного ниггу: ${nigga.name}")
            return null
        }

        if (niggas.size >= config.maxNiggas) {
            log.warn("Лимит нигг в подвале превышен")
            return null
        }

        val newNigga = nigga.copy(id = idGenerator.getAndIncrement())
        niggas[newNigga.id] = newNigga
        log.info("Нигга создан: $newNigga")
        return newNigga
    }

    fun getAll(): List<Nigga> = niggas.values.toList()

    fun getById(id: Int): Nigga? = niggas[id]

    fun update(id: Int, updated: Nigga): Nigga? {
        return if (niggas.containsKey(id)) {
            val nigga = updated.copy(id = id)
            niggas[id] = nigga
            nigga
        } else null
    }

    fun delete(id: Int): Boolean = niggas.remove(id) != null
}