package com.example.spring_1.controller

import com.example.spring_1.enity.Pet
import com.example.spring_1.service.PetService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pets")
class PetController(private val service: PetService) {

    @PostMapping
    fun create(@RequestBody pet: Pet): Pet? = service.create(pet)

    @GetMapping
    fun getAll(): List<Pet> = service.getAll()
}
