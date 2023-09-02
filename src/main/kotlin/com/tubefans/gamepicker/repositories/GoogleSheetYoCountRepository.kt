package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.SHEET_ID
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.YO_RANGE
import com.tubefans.gamepicker.services.GoogleSheetsService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

@Component
class GoogleSheetYoCountRepository @Autowired constructor(
    private val googleSheetCache: GoogleSheetCache,
    private val googleSheetsService: GoogleSheetsService
) : YoCountRepository {
    companion object {
        const val DEFAULT_THRESHOLD = 50
        val DEFAULT_INIT_DATE = LocalDate.of(2023, 9, 1)
        const val COUNT_CELL = "B1:B1"
        val remoteDateFormat = SimpleDateFormat("yyyy/MM/dd")
    }

    private val logger = LogManager.getLogger()
    private var initDate: LocalDate? = null
    private var threshold: Int? = null

    override var serviceInitDate: LocalDate = try {
        initDate!!
    } catch (e: NullPointerException) {
        LocalDate.ofInstant(remoteDateFormat.parse(googleSheetCache.yoSheet[1][1]).toInstant(), ZoneId.systemDefault())
            .also {
                initDate = it
            }
    } catch (e: RuntimeException) {
        logger.error("Failed to get start date from Google Sheets. Falling back to default $DEFAULT_INIT_DATE")
        DEFAULT_INIT_DATE
    }

    override fun getThreshold(): Int = try {
        threshold!!
    } catch (e: NullPointerException) {
        googleSheetCache.yoSheet[2][1].toInt().also {
            threshold = it
        }
    } catch (e: RuntimeException) {
        logger.error("Failed to get threshold. Falling back to default of $DEFAULT_THRESHOLD", e)
        DEFAULT_THRESHOLD
    }

    override fun findCount(): Int = try {
        googleSheetCache.yoSheet[0][1].toInt()
    } catch (e: NumberFormatException) {
        logger.error("Failed to parse count from google sheet", e)
        0
    } catch (e: IndexOutOfBoundsException) {
        logger.error("Failed to find count at position (0,1)", e)
        0
    }

    override fun increment(): Int {
        val updatedCount = findCount() + 1
        val values = listOf(listOf(updatedCount.toString()))
        googleSheetsService.writeRange(SHEET_ID, "$YO_RANGE!$COUNT_CELL", values)
        return updatedCount
    }
}
