package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.DATA_RANGE
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.SHEET_ID
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.USER_RANGE
import com.tubefans.gamepicker.services.GoogleDriveService
import com.tubefans.gamepicker.services.GoogleSheetsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GoogleSheetCacheTest {

    private val googleSheetsService: GoogleSheetsService = mockk {
        every { getSheet(any(), any()) } returns emptyList()
    }
    private val driveService: GoogleDriveService = mockk()

    private val later = System.currentTimeMillis()
    private val earlier = later - 1000L

    @Test
    fun `should update sheet when lastUpdate is greater than current value`() {
        every { driveService.getLastUpdatedTime(any()) } returnsMany listOf(
            DateTime(earlier),
            DateTime(later)
        )

        val cache = GoogleSheetCache(googleSheetsService, driveService)

        cache.dataSheet

        verify {
            // init
            googleSheetsService.getSheet(SHEET_ID, DATA_RANGE)
            googleSheetsService.getSheet(SHEET_ID, USER_RANGE)

            googleSheetsService.getSheet(any(), any())
        }
    }

    @Test
    fun `should not update sheet when lastUpdate is the same`() {
        every { driveService.getLastUpdatedTime(any()) } returnsMany listOf(
            DateTime(earlier),
            DateTime(earlier)
        )

        val cache = GoogleSheetCache(googleSheetsService, driveService)

        cache.dataSheet

        verify {
            googleSheetsService.getSheet(SHEET_ID, DATA_RANGE)
            googleSheetsService.getSheet(SHEET_ID, USER_RANGE)
        }
    }
}
