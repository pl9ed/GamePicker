package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.services.GoogleDriveService
import com.tubefans.gamepicker.services.GoogleSheetsService
import mapToString
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GoogleSheetCache
@Autowired
constructor(
    private val googleSheetsService: GoogleSheetsService,
    private val driveService: GoogleDriveService
) {
    companion object {
        const val SHEET_ID = "1FYL7O7RUkm4Fw-D2xw4R48QbY90hKf34oWgZ0_89vX8"
        const val DATA_RANGE = "Data"
        const val USER_RANGE = "id-mapping"
        const val YO_RANGE = "yo-count"
        const val END_ROW_TITLE = "SUM"
    }

    private val logger = LogManager.getLogger()
    private var lastUpdate: DateTime = driveService.getLastUpdatedTime(SHEET_ID)

    var dataSheet = googleSheetsService.getSheet(SHEET_ID, DATA_RANGE).mapToString()
        get() {
            val lastUpdate = driveService.getLastUpdatedTime()

            if (shouldUpdate(lastUpdate)) {
                logger.info("Updating data sheet from Google for lastUpdate={}", lastUpdate)
                field = googleSheetsService.getSheet(SHEET_ID, DATA_RANGE).mapToString()
                this.lastUpdate = lastUpdate
            } else {
                logger.info("Pulling sheet from cache")
            }

            return field
        }

    var userSheet = googleSheetsService.getSheet(SHEET_ID, USER_RANGE).mapToString()
        get() {
            val lastUpdate = driveService.getLastUpdatedTime()

            if (shouldUpdate(lastUpdate)) {
                logger.info("Updating user sheet from Google for lastUpdate={}", lastUpdate)
                field = googleSheetsService.getSheet(SHEET_ID, USER_RANGE).mapToString()
                this.lastUpdate = lastUpdate
            } else {
                logger.info("Pulling sheet from cache")
            }

            return field
        }

    // there's some delay in update timestamp, which matter for sequential yo counts
    val yoSheet: List<List<String>>
        get() {
            logger.info("Getting data from yo sheet")
            return googleSheetsService.getSheet(SHEET_ID, YO_RANGE).mapToString()
        }

    private fun shouldUpdate(lastUpdate: DateTime): Boolean = lastUpdate.value > this.lastUpdate.value
}
