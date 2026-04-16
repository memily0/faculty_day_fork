package com.example.library.service

import com.example.library.entity.Author
import com.example.library.entity.Book
import com.example.library.entity.Genre
import com.example.library.repository.AuthorRepository
import com.example.library.repository.BookRepository
import com.example.library.repository.GenreRepository
import com.example.library.repository.ReaderRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.Optional
import kotlin.jvm.java
import io.mockk.slot
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * ШАБЛОН ЗАНЯТИЯ (без готового кода тестов).
 *
 * Как работать:
 * 1. Сними [@Disabled] с класса ниже, когда начнёшь писать код.
 * 2. Для каждого теста следуй блоку «ИНСТРУКЦИЯ» — шаг за шагом.
 *
 * Подключи в начале файла импорты по мере необходимости, например:
 * - io.mockk: mockk, every, verify, slot, capture, any, eq, match
 * - org.junit.jupiter.api.Assertions.* (assertEquals, assertThrows — для проверки **результата** сервиса)
 * - Матчеры **eq** / **any** / **match** используются внутри `every { }` и `verify { }` для **аргументов** мока
 * - сущности и репозитории из com.example.library.*
 * - java.util.Optional — для findById у Spring Data
 * - org.springframework.data.domain.* — для постраничности
 */
//@Mockk("Учебный шаблон: сними @Disabled и поставь @Mockk и допиши тесты по инструкциям")
@MockK
class LibraryServiceMockkTest {

    // ИНСТРУКЦИЯ (общая):
    // Объяви четыре репозитория как mockk<...>() — как в рабочем файле тестов.
    // Объяви lateinit var service: LibraryService
    // В @BeforeEach создай LibraryService, передав в конструктор все четыре мока.

    private val authorRepository = mockk<AuthorRepository>()
    private val bookRepository = mockk<BookRepository>()
    private val genreRepository = mockk<GenreRepository>()
    private val readerRepository = mockk<ReaderRepository>()

    lateinit var service: LibraryService

    @BeforeEach
    fun setUp() {
        /*
         * ИНСТРУКЦИЯ:
         * 1. Присвой service = LibraryService(authorRepository, bookRepository, genreRepository, readerRepository)
         */
        service = LibraryService(
            authorRepository,
            bookRepository,
            genreRepository,
            readerRepository
        )
    }

    @Test
    fun `createAuthor возвращает того же автора что вернул save`() {
        /*
         * ИНСТРУКЦИЯ — основы every и verify:
         * 1. Создай автора Author(name = "...") — как будто его только что создали в коде сервиса.
         * 2. Создай «сохранённого» автора с id (например 42L) и тем же именем.
         * 3. Напиши: every { authorRepository.save(eq(тот_что_без_id)) } returns сохранённый_с_id
         * 4. Вызови service.createAuthor("то же имя")
         * 5. assertEquals по id и имени результата (JUnit — проверка возвращаемого значения)
         * 6. verify(exactly = 1) { authorRepository.save(eq(тот_что_без_id)) } — матчер eq в MockK
         */

        val authorWithoutId = Author(name = "Author")
        val authorWithId = Author(id = 2, name = "Author")
        every {authorRepository.save(eq(authorWithoutId))} returns authorWithId

        val result = service.createAuthor("Author")

        assertEquals(2, result.id)
        assertEquals("Author", result.name)

        verify(exactly = 1) {authorRepository.save(eq(authorWithoutId))}

    }

    @Test
    fun `getAllGenres возвращает список из genreRepository findAll`() {
        /*
         * ИНСТРУКЦИЯ — стаб (заглушка) возвращает данные:
         * 1. Собери список из двух Genre(id, name).
         * 2. every { genreRepository.findAll() } returns этот_список
         * 3. Вызови service.getAllGenres()
         * 4. Проверь размер списка и имя первого жанра (assertEquals).
         * 5. verify { genreRepository.findAll() } — убедись, что метод вызывался.
         */

        val genres = listOf(Genre(id = 1L, name = "Fantasy"), Genre(id = 2L, name = "Detective"))

        every { genreRepository.findAll() } returns genres

        val result = service.getAllGenres()

        assertEquals(2, result.size)
        assertEquals("Fantasy", result[0].name)

        verify { genreRepository.findAll() }


    }

    @Test
    fun `createBook бросает EntityNotFoundException если автор не найден`() {
        /*
         * ИНСТРУКЦИЯ — исключения и verify(exactly = 0):
         * 1. Настрой every { authorRepository.findById(eq(99L)) } returns Optional.empty()
         *    (в Kotlin: import java.util.Optional).
         * 2. Используй assertThrows(EntityNotFoundException::class.java) { service.createBook(...) }
         * 3. Проверь message у исключения (должен совпадать с текстом в LibraryService).
         * 4. verify(exactly = 1) { authorRepository.findById(eq(99L)) }
         * 5. verify(exactly = 0) { bookRepository.save(any()) } — книга не сохранялась.
         *    Для any() нужен импорт io.mockk.any (или звёздочка io.mockk.*).
         */

        every { authorRepository.findById(eq(99L)) } returns Optional.empty()
        val exception = assertThrows(EntityNotFoundException::class.java) {
            service.createBook(
                "Test Book",
                "123-456",
                99L,
                1L
            )
        }

        assertEquals("Author not found with id: 99", exception.message)
        verify(exactly = 1) { authorRepository.findById(eq(99L)) }
        verify(exactly = 0) { genreRepository.findById(any()) }
        verify(exactly = 0) { bookRepository.save(any()) }
    }

    @Test
    fun `createBook передаёт в save книгу с нужным названием и ISBN slot ловит аргумент`() {
        val author = Author(id = 23L, name = "Author")
        val genre = Genre(id = 2L, name = "Fantasy")

        every { authorRepository.findById(eq(1L)) } returns Optional.of(author)
        every { genreRepository.findById(eq(2L)) } returns Optional.of(genre)

        val bookSlot = slot<Book>()
        every { bookRepository.save(capture(bookSlot)) } answers {
            bookSlot.captured.copy(id = 100L)
        }

        val result = service.createBook(
            "NameOfBook",
            "123-456",
            1L,
            2L
        )

        assertEquals(100L, result.id)
        assertEquals("NameOfBook", bookSlot.captured.title)
        assertEquals("123-456", bookSlot.captured.isbn)
        assertEquals(author, bookSlot.captured.author)
        assertEquals(genre, bookSlot.captured.genre)

        verify(exactly = 1) { authorRepository.findById(eq(1L)) }
        verify(exactly = 1) { genreRepository.findById(eq(2L)) }
        verify(exactly = 1) {
            bookRepository.save(match {
                it.title == "NameOfBook" &&
                        it.isbn == "123-456" &&
                        it.author == author &&
                        it.genre == genre
            })
        }
    }

@Test
fun `getBooksPage делегирует в bookRepository findAll с постраничностью`() {

    /*
         * ИНСТРУКЦИЯ — мок Page и точное совпадение аргумента:
         * 1. Создай минимум одну Book в списке (нужны author и genre для конструктора Book).
         * 2. Собери PageImpl(список, PageRequest.of(0, 20, Sort.by("title")), totalElements)
         *    (импорты из org.springframework.data.domain).
         * 3. Сохрани val pageRequest = PageRequest.of(0, 20, Sort.by("title")); every { bookRepository.findAll(eq(pageRequest)) } returns page
         * 4. Вызови service.getBooksPage(page = 0, size = 20)
         * 5. Проверь content.size и title первой книги (JUnit).
         * 6. verify(exactly = 1) { bookRepository.findAll(eq(pageRequest)) }
         */


    val author = Author(id = 1L, name = "Author")
    val genre = Genre(id = 2L, name = "Fantasy")

    val book = Book(
        id = 10L,
        title = "BookName",
        isbn = "123-456",
        author = author,
        genre = genre
    )

    val pageRequest = PageRequest.of(0, 20, Sort.by("title"))
    val page = PageImpl(listOf(book), pageRequest, 1)

    every { bookRepository.findAll(eq(pageRequest)) } returns page

    val result = service.getBooksPage(page = 0, size = 20)

    assertEquals(1, result.content.size)
    assertEquals("BookName", result.content[0].title)

    verify(exactly = 1) {
        bookRepository.findAll(eq(pageRequest))
    }
}
}

