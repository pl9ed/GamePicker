package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.SHEET_ID
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.YO_RANGE
import com.tubefans.gamepicker.services.YoCountService.Companion.remoteDateFormat
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import java.time.LocalDate

class YoCountServiceTest {
    @Mock
    private var mockSheetCache: GoogleSheetCache = mockk()
    private var mockSheetsService: GoogleSheetsService = mockk()
    private var yoCountService: YoCountService? = null

    private val count = 100
    private val startDate: LocalDate = LocalDate.of(2023, 9, 1)
    private val threshold = 50

    private val mockItems: List<List<String>> =
        listOf(
            listOf("count", count.toString()),
            listOf("start date", remoteDateFormat.format(startDate)),
            listOf("threshold", threshold.toString()),
        )

    @BeforeEach
    fun setup() {
        every { mockSheetCache.yoSheet } returns mockItems
        yoCountService = YoCountService(mockSheetCache, mockSheetsService)
    }

    @Test
    fun getThreshold_returnsThresholdFromCache() {
        assertEquals(threshold, yoCountService!!.getThreshold())
    }

    @Test
    fun getThreshold_fallsBackToDefaultOnException() {
        clearMocks(mockSheetCache)
        every { mockSheetCache.yoSheet } throws RuntimeException("Error")
        assertEquals(YoCountService.DEFAULT_THRESHOLD, yoCountService!!.getThreshold())
    }

    @Test
    fun findCount_returnsCountFromCache() {
        assertEquals(count, yoCountService!!.findCount())
    }

    @Test
    fun findCount_returnsZeroOnNumberFormatException() {
        every { mockSheetCache.yoSheet[0][1] } returns "invalid"
        assertEquals(0, yoCountService!!.findCount())
    }

    @Test
    fun findCount_returnsZeroOnIndexOutOfBoundsException() {
        every { mockSheetCache.yoSheet[0][1] } throws IndexOutOfBoundsException("Error")
        assertEquals(0, yoCountService!!.findCount())
    }

    @Test
    fun increment_incrementsCountAndWritesToSheet() {
        every { mockSheetsService.writeRange(any(), any(), any()) } returns Unit

        assertEquals(count + 1, yoCountService!!.increment())
        verify { mockSheetsService.writeRange(SHEET_ID, "$YO_RANGE!${YoCountService.COUNT_CELL}", listOf(listOf("${count + 1}"))) }
    }

    @Test
    fun serviceInitDate_returnsInitDateFromCache() {
        assertEquals(startDate, yoCountService!!.serviceInitDate)
    }

    @Test
    fun serviceInitDate_fallsBackToDefaultOnException() {
        every { mockSheetCache.yoSheet[1][1] } throws RuntimeException("Error")
        assertTrue(YoCountService.DEFAULT_INIT_DATE == yoCountService!!.serviceInitDate)
    }
}
