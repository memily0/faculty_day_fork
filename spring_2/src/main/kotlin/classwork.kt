package org.example

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

fun task11() {
    val url = URL("https://jsonplaceholder.typicode.com/posts/1")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val code = connection.responseCode
    val stream = try {
        if (code in 200..299) connection.inputStream else connection.errorStream
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    val response = stream?.bufferedReader()?.readText() ?: ""
    println("Code: $code")
    println("Response: $response")
    connection.disconnect()
}

fun task12() {
    val url = URL("https://jsonplaceholder.typicode.com/posts")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true
    connection.setRequestProperty("Content-Type", "application/json")

    val json = """
        {
          "title": "acquaintance",
          "body": "Ich bin Schnappi das kleine Krockodile",
          "userId": 1
        }
    """.trimIndent()

    connection.outputStream.use { it.write(json.toByteArray()) }

    val code = connection.responseCode
    val stream = try {
        if (code in 200..299) connection.inputStream else connection.errorStream
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    val response = stream?.bufferedReader()?.readText() ?: ""
    println("Code: $code")
    println("Response: $response")
    connection.disconnect()
}

fun task13() {
    val url = URL("https://jsonplaceholder.typicode.com/posts/9999")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val code = connection.responseCode
    val stream = try {
        if (code in 200..299) connection.inputStream else connection.errorStream
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    val response = stream?.bufferedReader()?.readText() ?: ""
    println("Code: $code")
    println("Response: $response")
    connection.disconnect()
}

// ===========================================
// Задача 2. REST — полный CRUD
// ===========================================

val BASE_URL = "https://jsonplaceholder.typicode.com/posts"

fun sendRequest(urlStr: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(urlStr).openConnection() as HttpURLConnection
    connection.requestMethod = method
    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { it.write(body.toByteArray()) }
    }

    val code = connection.responseCode
    val stream = try {
        if (code in 200..299) connection.inputStream else connection.errorStream
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    val response = stream?.bufferedReader()?.readText() ?: ""
    connection.disconnect()
    return code to response
}

fun getPosts2(): String {
    val (code, response) = sendRequest(BASE_URL, "GET")
    println("Code: $code")
    println("Response: $response")
    return response
}

fun getPost2(id: Int): String {
    val (code, response) = sendRequest("$BASE_URL/$id", "GET")
    println("Code: $code")
    println("Response: $response")
    return response
}

fun createPost(json: String): String {
    val (code, response) = sendRequest(BASE_URL, "POST", json)
    println("Code: $code")
    println("Response: $response")
    return response
}

fun updatePost(id: Int, json: String): String {
    val (code, response) = sendRequest("$BASE_URL/$id", "PUT", json)
    println("Code: $code")
    println("Response: $response")
    return response
}

fun deletePost(id: Int): Int {
    val (code, response) = sendRequest("$BASE_URL/$id", "DELETE")
    println("Code: $code")
    println("Response: $response")
    return code
}

// ===========================================
// SSL-доверие ко всем сертификатам
// ===========================================

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

fun task3() {
    // 3.1: Сборка JWT
    println("=== Сборка JWT ===")
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "dummysignature"

    val encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.toByteArray())
    val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.toByteArray())
    val encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(fakeSignature.toByteArray())

    val token = "$encodedHeader.$encodedPayload.$encodedSignature"
    println("Собранный JWT: $token")

    // 3.2: Декодирование JWT
    println("\n=== Декодирование JWT ===")
    val parts = token.split(".")
    val decodedHeader = String(Base64.getUrlDecoder().decode(parts[0]))
    val decodedPayload = String(Base64.getUrlDecoder().decode(parts[1]))

    println("header: $decodedHeader")
    println("payload: $decodedPayload")

    // 3.3: GET /bearer с токеном
    println("\n=== GET /bearer (с токеном) ===")
    val connectionWithToken = URL("https://httpbin.org/bearer").openConnection() as HttpURLConnection
    connectionWithToken.requestMethod = "GET"
    connectionWithToken.setRequestProperty("Authorization", "Bearer $token")

    val codeWithToken = connectionWithToken.responseCode
    val responseWithToken = connectionWithToken.inputStream.bufferedReader().readText()
    connectionWithToken.disconnect()
    println("Code: $codeWithToken")
    println("Response: $responseWithToken")

    // 3.4: GET /bearer без токена
    println("\n=== GET /bearer (без токена) ===")
    val connectionWithoutToken = URL("https://httpbin.org/bearer").openConnection() as HttpURLConnection
    connectionWithoutToken.requestMethod = "GET"

    val codeWithoutToken = connectionWithoutToken.responseCode

    val stream = try {
        if (codeWithoutToken in 200..299) connectionWithoutToken.inputStream else connectionWithoutToken.errorStream
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    val responseWithoutToken = stream?.bufferedReader()?.readText() ?: ""
    connectionWithoutToken.disconnect()
    println("Code: $codeWithoutToken (expected 401)")
    println("Response: $responseWithoutToken")

    // 3.5: Подмена payload
    println("\n=== Подмена payload ===")
    val hackedPayload = """{"sub":"1","name":"Ivan Petrov","role":"admin","iat":1234567890}"""
    val hackedEncodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(hackedPayload.toByteArray())
    val hackedToken = "$encodedHeader.$hackedEncodedPayload.$encodedSignature"
    println("Подменённый JWT: $hackedToken")
}

// ===========================================
// Main
// ===========================================

fun main() {
    disableSslVerification()

    println("=== GET /posts/1 ===")
    task11()

    println("\n=== POST /posts ===")
    task12()

    println("\n=== GET /posts/9999 ===")
    task13()

    println("=== GET ALL ===")
    getPosts2()

    println("\n=== GET ONE ===")
    getPost2(1)

    println("\n=== CREATE ===")
    createPost("""{"title": "Milka", "body": "Chocolate", "userId": 1}""")

    println("\n=== UPDATE ===")
    updatePost(1, """{"id":1,"title":"Emily","body":"Updated","userId":1}""")

    println("\n=== DELETE ===")
    deletePost(1)

    task3()
}