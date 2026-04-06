package com.example.spring_1.controller

import com.example.spring_1.enity.Nigga
import com.example.spring_1.service.NiggaService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/niggas")
class NiggaController(private val service: NiggaService) {

    @PostMapping
    fun create(@RequestBody nigga: Nigga): Nigga? = service.create(nigga)

    @GetMapping
    fun getAll(): List<Nigga> = service.getAll()
}