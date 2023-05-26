package com.tubefans.gamepicker.cache

import com.google.api.services.sheets.v4.model.Sheet
import com.tubefans.gamepicker.services.GoogleSheetsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GoogleSheetCache @Autowired constructor(
    private val sheet: Sheet,
) {
    private val backingSheet = sheet
}