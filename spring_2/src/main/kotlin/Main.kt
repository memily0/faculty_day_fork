package org.example

import sun.security.krb5.Confounder.bytes
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.util.Base64

// ===========================================
// Задача 1. HTTP-запросы через HttpURLConnection
// ===========================================
// Цель: научиться отправлять GET и POST запросы, читать ответ и статус-код.
// API: https://jsonplaceholder.typicode.com
//
// TODO 1: Отправить GET /posts/1, вывести статус-код и тело ответа
// TODO 2: Отправить POST /posts с JSON-телом, вывести статус-код и тело
// TODO 3: Отправить GET /posts/9999, обработать ошибку (код != 2xx)
//
// Подсказки:
//   val connection = URL("...").openConnection() as HttpURLConnection
//   connection.requestMethod = "GET"             — задать метод
//   connection.doOutput = true                   — разрешить отправку тела
//   connection.setRequestProperty("Content-Type", "application/json") — заголовок
//   connection.outputStream.write(json.toByteArray())                 — записать тело
//   connection.responseCode                      — получить статус-код
//   connection.inputStream.bufferedReader().readText()  — прочитать тело ответа
//   connection.errorStream                       — поток ошибок (при коде 4xx/5xx)
//   connection.disconnect()                      — закрыть соединение


fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAll, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}

// ===========================================
// Задача 3. JWT — авторизация
// ===========================================
// Цель: понять структуру JWT, собрать и декодировать токен, отправить запрос с Bearer-авторизацией.
// API: https://httpbin.org/bearer (возвращает 200 если есть Bearer, 401 если нет)
//
// TODO 1: Собрать JWT из трёх частей (header, payload, signature) в Base64URL
// TODO 2: Декодировать JWT обратно — вывести header и payload как JSON
// TODO 3: Отправить GET https://httpbin.org/bearer с заголовком Authorization: Bearer <token>
// TODO 4: Отправить тот же запрос БЕЗ токена — убедиться, что вернулся 401
// TODO 5: Подменить payload (role: student → admin), объяснить почему сервер отвергнет
//
// Подсказки:
//   Base64.getUrlEncoder().withoutPadding().encodeToString(bytes) — кодирование
//   Base64.getUrlDecoder().decode(string)                        — декодирование
//   JWT = base64(header) + "." + base64(payload) + "." + base64(signature)
//
// Вопросы после выполнения:
//   - Из каких 3 частей состоит JWT?
//   - Можно ли подменить payload и использовать токен? Почему нет?
//   - Что такое access token и refresh token?
//

fun main() {
    disableSslVerification()

    // TODO 1: GET /posts/1
    println("=== GET /posts/1 ===")
    // === GET запрос ===
    val getUrl = URL("https://jsonplaceholder.typicode.com/posts/1")
    val getConn = getUrl.openConnection() as HttpURLConnection
    getConn.requestMethod = "GET"

    println("Код: ${getConn.responseCode}")
    val getBody = getConn.inputStream.bufferedReader().readText()
    println("Тело: $getBody")
    getConn.disconnect()

    // TODO 2: POST /posts
    println("\n=== POST /posts ===")

    val connection = URL("https://jsonplaceholder.typicode.com/posts").openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true
    connection.doInput = true
    connection.connect()
    connection.setRequestProperty("Content-Type", "application/json")
    val json = connection.inputStream.bufferedReader().readText()
    connection.outputStream.write(json.toByteArray())
    println("код: ${connection.responseCode}")
    println("тело: ${connection.responseCode}")
    connection.disconnect()


    // TODO 3: GET /posts/9999 (несуществующий ресурс)
    println("\n=== GET /posts/9999 ===")


    // TODO 3: вызвать каждую функцию и вывести результат
    println("=== GET ALL ===")

    println("\n=== GET ONE ===")

    println("\n=== CREATE ===")

    println("\n=== UPDATE ===")

    val encoder = Base64.getUrlEncoder().withoutPadding()

    // TODO 1: Собрать JWT
    println("=== Сборка JWT ===")
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "dummysignature"

    val encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.toByteArray())
    val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.toByteArray())
    val encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(fakeSignature.toByteArray())

    val token = encodedHeader + "." + encodedPayload + "." + encodedSignature
    println("Собранный JWT: $token")

    // TODO 2: Декодировать JWT
    println("\n=== Декодирование JWT ===")
    // Разделить token по ".", декодировать header и payload, вывести
    val parts = token.split(".")
    val predecodedHeader = String(Base64.getUrlDecoder().decode(parts[0]))
    val predecodedPayload = String(Base64.getUrlDecoder().decode(parts[1]))

    val decodedHeaderBytes = Base64.getUrlDecoder().decode(predecodedHeader)
    val decodedHeader = String(decodedHeaderBytes)

    val decodedPayloadBytes = Base64.getUrlDecoder().decode(predecodedPayload)
    val decodedPayload = String(decodedPayloadBytes)

    println("header: $decodedHeader")
    println("payload: $decodedPayload")

    // TODO 3: GET /bearer с токеном
    println("\n=== GET /bearer (с токеном) ===")

    val connect =
    // Отправить GET на https://httpbin.org/bearer
    // Добавить заголовок: connection.setRequestProperty("Authorization", "Bearer $token")
    // Вывести код и тело ответа

    // TODO 4: GET /bearer без токена
    println("\n=== GET /bearer (без токена) ===")
    // Отправить тот же запрос без заголовка Authorization
    // Ожидаемый результат: 401

    // TODO 5: Подмена payload
    println("\n=== Подмена payload ===")
    // Изменить role на "admin", собрать новый токен
    // Объяснить почему сервер его отвергнет
}

// ===========================================
// Задача 2. REST — полный CRUD
// ===========================================
// Цель: реализовать все CRUD-операции для ресурса /posts.
// API: https://jsonplaceholder.typicode.com/posts
//
// TODO 1: Реализовать sendRequest() — универсальную функцию отправки запросов
// TODO 2: Реализовать 5 CRUD-функций (ниже)
// TODO 3: Вызвать каждую функцию в main() и вывести результат
//
// Вопросы после выполнения:
//   - В чём разница между PUT и PATCH?
//   - Почему POST возвращает 201, а PUT возвращает 200?
//   - Какой метод идемпотентный, а какой нет?

val BASE_URL = "https://jsonplaceholder.typicode.com/posts"

/** Универсальная функция для отправки HTTP-запросов.
 *  @param urlStr  — полный URL
 *  @param method  — HTTP-метод (GET, POST, PUT, DELETE)
 *  @param body    — тело запроса в формате JSON (null для GET/DELETE)
 *  @return Pair(statusCode, responseBody)
 */
fun sendRequest(urlStr: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(urlStr).openConnection() as HttpURLConnection
    connection.requestMethod = method
    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.write(body.toByteArray())
    }
    val code = connection.responseCode
    val response = (if (code in 200..299) connection.inputStream else connection.errorStream)
        ?.bufferedReader()?.readText() ?: ""
    connection.disconnect()
    return code to response
}
/** GET /posts — получить все посты */
fun getPosts(): String {
    // TODO 2a
    TODO("Реализуй getPosts")
}

/** GET /posts/{id} — получить пост по ID */
fun getPost(id: Int): String {
    // TODO 2b
    TODO("Реализуй getPost")
}

/** POST /posts — создать новый пост. Тело: {"title":"...", "body":"...", "userId":1} */
fun createPost(json: String): String {
    // TODO 2c
    TODO("Реализуй createPost")
}

/** PUT /posts/{id} — полностью обновить пост */
fun updatePost(id: Int, json: String): String {
    // TODO 2d
    TODO("Реализуй updatePost")
}

/** DELETE /posts/{id} — удалить пост, вернуть статус-код */
fun deletePost(id: Int): Int {
    // TODO 2e
    TODO("Реализуй deletePost")
}

