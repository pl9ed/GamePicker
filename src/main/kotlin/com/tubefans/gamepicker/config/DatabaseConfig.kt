package com.tubefans.gamepicker.config

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.repositories.GoogleSheetYoCountRepository
import com.tubefans.gamepicker.repositories.YoCountRepository
import com.tubefans.gamepicker.services.GoogleSheetsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig
    @Autowired
    constructor(
        val googleSheetCache: GoogleSheetCache,
        val googleSheetsService: GoogleSheetsService,
    ) {
        @Bean
        fun yoCountRepository(): YoCountRepository = GoogleSheetYoCountRepository(googleSheetCache, googleSheetsService)
    }
