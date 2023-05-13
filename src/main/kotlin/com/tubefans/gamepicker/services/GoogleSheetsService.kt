package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GoogleSheetsService @Autowired constructor(
    private val sheets: Sheets
) {

    private fun getValueRange(id: String, range: String): ValueRange =
        sheets.spreadsheets().values().get(id, range).execute()

}