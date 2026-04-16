package com.example.spring_1.service

import org.slf4j.LoggerFactory
import com.example.spring_1.enity.Pet
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import com.example.spring_1.config.PetServiceConfig

private val log = LoggerFactory.getLogger(PetService::class.java)

@Service
class PetService(
    private val config: PetServiceConfig
) {

    private val pets = ConcurrentHashMap<Int, Pet>()
    private val idGenerator = AtomicInteger(1)

    fun create(pet: Pet): Pet? {
        if (pet.name in config.forbiddenNames) {
            log.warn("Попытка создать питомца с запрещенным именем: ${pet.name}")
            return null
        }

        if (pets.size >= config.maxPets) {
            log.warn("Превышен лимит питомцев")
            return null
        }

        val newPet = pet.copy(id = idGenerator.getAndIncrement())
        pets[newPet.id] = newPet
        log.info("Питомец создан: $newPet")
        return newPet
    }

    fun getAll(): List<Pet> = pets.values.toList()

    fun getById(id: Int): Pet? = pets[id]

    fun update(id: Int, updated: Pet): Pet? {
        return if (pets.containsKey(id)) {
            val pet = updated.copy(id = id)
            pets[id] = pet
            pet
        } else null
    }

    fun delete(id: Int): Boolean = pets.remove(id) != null
}
