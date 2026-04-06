package com.example.spring_1.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.nigga-service")
class NiggaServiceConfig {
    var forbiddenNames: List<String> = listOf()
    var maxNiggas: Int = 10
}