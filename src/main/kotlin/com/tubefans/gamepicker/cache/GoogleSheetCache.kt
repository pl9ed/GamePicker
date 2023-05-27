package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.services.GoogleDriveService
import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.GoogleSheetsService.Companion.DEFAULT_SHEET_ID
import mapToString
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GoogleSheetCache @Autowired constructor(
    private val googleSheetsService: GoogleSheetsService,
    private val driveService: GoogleDriveService
) {

    private val logger = LogManager.getLogger()

    private var id = DEFAULT_SHEET_ID
    private var lastUpdate: DateTime = driveService.getLastUpdatedTime(DEFAULT_SHEET_ID)
    private var sheet = googleSheetsService.getSheet(id).mapToString()

    private fun shouldUpdate(lastUpdate: DateTime): Boolean =
        lastUpdate.value > this.lastUpdate.value

    fun getSheet(): List<List<String>> {
        val lastUpdate = driveService.getLastUpdatedTime()

        if (shouldUpdate(lastUpdate)) {
            logger.info("Updating sheet from Google for lastUpdate=$lastUpdate")
            sheet = googleSheetsService.getSheet(id).mapToString()
            this.lastUpdate = lastUpdate
        } else {
            logger.info("Pulling sheet from cache")
        }

        return sheet
    }

    fun lastUpdateTime(): DateTime = lastUpdate
}
