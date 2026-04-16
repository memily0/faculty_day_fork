package com.example.spring_1.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.pet-service")
class PetServiceConfig {
    var forbiddenNames: List<String> = listOf()
    var maxPets: Int = 10
}
