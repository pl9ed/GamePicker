package com.tubefans.gamepicker.cache

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.services.GoogleSheetsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GoogleSheetCache @Autowired constructor(
    private val googleSheetsService: GoogleSheetsService
) {
    private var lastUpdate = System.currentTimeMillis()
    private var _sheet = googleSheetsService.getSheet()
    var sheet: List<List<Any>> = _sheet
        get() = _sheet

}
