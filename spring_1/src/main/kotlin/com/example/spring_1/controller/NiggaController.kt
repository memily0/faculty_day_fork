package com.example.spring_1.controller

import com.example.spring_1.enity.Book
import com.example.spring_1.service.BookService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

    @PostMapping
    fun create(@RequestBody book: Book): Book? = service.create(book)

    @GetMapping
    fun getAll(): List<Book> = service.getAll()
}