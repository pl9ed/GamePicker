package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.SHEET_ID
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.YO_RANGE
import com.tubefans.gamepicker.repositories.YoCountRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class YoCountService
    @Autowired
    constructor(
        private val googleSheetCache: GoogleSheetCache,
        private val googleSheetsService: GoogleSheetsService,
    ) : YoCountRepository {
        companion object {
            const val DEFAULT_THRESHOLD = 50
            val DEFAULT_INIT_DATE: LocalDate = LocalDate.of(2023, 9, 1)
            const val COUNT_CELL = "B1:B1"
            val remoteDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        }

        private val logger = LogManager.getLogger()
        private var initDate: LocalDate? = null
        private var threshold: Int? = null

        override var serviceInitDate: LocalDate =
            try {
                initDate!!
            } catch (e: NullPointerException) {
                LocalDate
                    .parse(googleSheetCache.yoSheet[1][1], remoteDateFormat)
                    .also {
                        initDate = it
                    }
            } catch (e: RuntimeException) {
                logger.error("Failed to get start date from Google Sheets. Falling back to default $DEFAULT_INIT_DATE")
                DEFAULT_INIT_DATE
            }

        override fun getThreshold(): Int {
            var error: Throwable? = null
            try {
                threshold!!
            } catch (e: NullPointerException) {
                try {
                    googleSheetCache.yoSheet[2][1].toInt().also {
                        threshold = it
                    }
                } catch (e: Throwable) {
                    error = e
                }
            }

            if (error != null) {
                logger.error("Failed to get threshold from Google Sheets. Falling back to default $DEFAULT_THRESHOLD", error)
            }

            return DEFAULT_THRESHOLD
        }

        override fun findCount(): Int =
            try {
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
