package com.example.spring_1.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.book-service")
class BookServiceConfig {
    var forbiddenTitles: List<String> = listOf()
    var maxBooks: Int = 10
}