package com.example.spring_1.service

import org.slf4j.LoggerFactory
import com.example.spring_1.config.BookServiceConfig
import com.example.spring_1.enity.Book
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val log = LoggerFactory.getLogger(BookService::class.java)

@Service
class BookService(
    private val config: BookServiceConfig
) {

    private val books = ConcurrentHashMap<Int, Book>()
    private val idGenerator = AtomicInteger(1)

    fun create(book: Book): Book? {
        if (book.title in config.forbiddenTitles) {
            log.warn("Попытка создать запрещённую книгу: ${book.title}")
            return null
        }

        if (books.size >= config.maxBooks) {
            log.warn("Лимит книг превышен")
            return null
        }

        val newBook = book.copy(id = idGenerator.getAndIncrement())
        books[newBook.id] = newBook
        log.info("Книга создана: $newBook")
        return newBook
    }

    fun getAll(): List<Book> = books.values.toList()

    fun getById(id: Int): Book? = books[id]

    fun update(id: Int, updated: Book): Book? {
        return if (books.containsKey(id)) {
            val book = updated.copy(id = id)
            books[id] = book
            book
        } else null
    }

    fun delete(id: Int): Boolean = books.remove(id) != null
}