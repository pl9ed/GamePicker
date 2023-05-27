package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.services.GoogleDriveService
import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.GoogleSheetsService.Companion.DEFAULT_SHEET_ID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mapToString
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString

class GoogleSheetCacheTest {

    private val googleSheetsService: GoogleSheetsService = mockk {
        every { getSheet(any()) } returns emptyList()
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

        cache.getSheet()

        // 1 during init, 1 during update
        verify(exactly = 2) {
            googleSheetsService.getSheet(DEFAULT_SHEET_ID)
        }
    }

    @Test
    fun `should not update sheet when lastUpdate is the same`() {
        every { driveService.getLastUpdatedTime(any()) } returnsMany listOf(
            DateTime(earlier),
            DateTime(earlier)
        )

        val cache = GoogleSheetCache(googleSheetsService, driveService)

        cache.getSheet()

        verify(exactly = 1) {
            googleSheetsService.getSheet(DEFAULT_SHEET_ID)
        }
    }

}
