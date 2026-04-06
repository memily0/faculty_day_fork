package org.example

import java.net.HttpURLConnection
import java.net.URL

// ===========================================
// Задача 6. Клиент для сервера заметок
// ===========================================
// Цель: написать клиент, который тестирует все эндпоинты сервера.
// Перед запуском: запустить Task6_Server.kt

// я честно не знаю оно работает или нет потому что я не нашла Task6_Server.kt
// о святые корутины, прошу простить меня грешную я вымолю все грехи и предстану под высшим судом на судном дне
//

//
// TODO 1: Реализовать request() — универсальную функцию отправки запросов
// TODO 2: В main() выполнить 8 шагов (ниже), вывести код и тело каждого ответа

val BASE = "http://localhost:8080/api/notes"

/** Отправить HTTP-запрос.
 *  @param url    — полный URL
 *  @param method — HTTP-метод
 *  @param body   — JSON-тело (null для GET/DELETE)
 *  @return Pair(statusCode, responseBody)
 */
fun request(url: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.setRequestProperty("Content-Type", "application/json")

    if (body != null) {
        connection.doOutput = true
        connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
    }

    val code = connection.responseCode
    val stream = try {
        if (code in 200..299) connection.inputStream else connection.errorStream
    } catch (e: Exception) {
        connection.errorStream
    }

    val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
    connection.disconnect()
    return code to response
}

fun main() {
    // TODO 2: выполнить 8 шагов, каждый раз вызывая request() и выводя результат

    // Шаг 1: получить все заметки
    println("=== 1. GET /api/notes — все заметки ===")
    val (code1, body1) = request(BASE, "GET")
    println("Code1: $code1")
    println("Body1: $body1")

    // Шаг 2: создать новую заметку
    println("\n=== 2. POST /api/notes — создать заметку ===")
    // JSON: {"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}
    val (code2, body2) = request(BASE, "POST", """{"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}""")
    println("Code2: $code2")
    println("Body2: $body2")

    // Шаг 3: получить заметку по id
    println("\n=== 3. GET /api/notes/1 — одна заметка ===")
    val (code3, body3) = request("$BASE/1", "GET")
    println("Code3: $code3")
    println("Body3: $body3")

    // Шаг 4: обновить заметку
    println("\n=== 4. PUT /api/notes/1 — обновить заметку ===")
    // JSON: {"title":"Покупки (обновлено)","content":"Молоко, хлеб, яйца, сыр","tag":"личное"}
    val (code4, body4) = request("$BASE/1", "PUT", """{"title":"Покупки (обновлено)","content":"Молоко, хлеб, яйца, сыр","tag":"личное"}""")
    println("Code4: $code4")
    println("Body4: $body4")

    // Шаг 5: фильтр по тегу
    println("\n=== 5. GET /api/notes?tag=учёба — фильтр по тегу ===")
    val tag = java.net.URLEncoder.encode("учёба", "UTF-8")
    val (code5, body5) = request("$BASE?tag=$tag", "GET")
    println("Code5: $code5")
    println("Body5: $body5")

    // Шаг 6: удалить заметку
    println("\n=== 6. DELETE /api/notes/1 — удалить заметку ===")
    val (code6, body6) = request("$BASE/1", "DELETE")
    println("Code6: $code6")
    println("Body6: $body6")

    // Шаг 7: запросить несуществующую заметку (ожидаем 404)
    println("\n=== 7. GET /api/notes/999 — несуществующая заметка ===")
    val (code7, body7) = request("$BASE/999", "GET")
    println("Code7: $code7")
    println("Body7: $body7")

    // Шаг 8: финальное состояние
    println("\n=== 8. GET /api/notes — финальное состояние ===")
    val (code8, body8) = request(BASE, "GET")
    println("Code8: $code8")
    println("Body8: $body8")
}