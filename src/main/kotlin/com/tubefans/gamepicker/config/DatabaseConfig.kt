package com.tubefans.gamepicker.config

import com.tubefans.gamepicker.repositories.InMemoryYoCountRepository
import com.tubefans.gamepicker.repositories.YoCountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig @Autowired constructor() {
    @Bean
    fun yoCountRepository(): YoCountRepository = InMemoryYoCountRepository()
}
