package com.tubefans.gamepicker.services

import com.google.api.client.util.DateTime
import com.google.api.services.drive.Drive
import com.tubefans.gamepicker.services.GoogleSheetsService.Companion.DEFAULT_SHEET_ID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GoogleDriveService @Autowired constructor(
    private val drive: Drive
) {

    companion object {
        const val MODIFIED_TIME_KEY = "modifiedTime"
    }

    fun getLastUpdatedTime(id: String = DEFAULT_SHEET_ID): DateTime =
        drive.Files()
            .get(id)
            .setFields(MODIFIED_TIME_KEY)
            .execute()
            .modifiedTime
}
