package com.tubefans.gamepicker.services

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.tubefans.gamepicker.cache.GoogleSheetCache.Companion.END_ROW_TITLE
import org.apache.http.client.HttpResponseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import toName
import toScore

@Service
class GoogleSheetsService
    @Autowired
    constructor(
        private val sheets: Sheets,
    ) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        fun getValueRange(
            id: String,
            range: String,
        ): ValueRange =
            try {
                sheets.spreadsheets().values()[id, range].execute()
            } catch (e: HttpResponseException) {
                logger.error("Failed to get value range from Google API", e)
                ValueRange()
            }

        /**
         * @param id Google Sheet id
         * @param range String representation of range, e.x. 'A1:A5'
         * @return 2D array of objects from the sheet
         */
        fun getSheet(
            id: String,
            range: String,
        ): List<List<Any>> =
            try {
                sheets
                    .spreadsheets()
                    .values()[id, range]
                    .execute()
                    .getValues()
            } catch (e: HttpResponseException) {
                logger.error("Failed to get sheet from Google API", e)
                emptyList()
            }

        /**
         * Helper function to process data from Google sheets
         * @param sheet 2D array to read from
         * @return Map of users -> list of scores
         */
        fun mapToScores(sheet: List<List<Any>>): Map<String, List<Pair<String, Long>>> {
            if (sheet.isEmpty()) return emptyMap()

            val scoreMap: MutableMap<String, MutableList<Pair<String, Long>>> = mutableMapOf()

            // which games are at which column
            val gameIndexMap = mutableMapOf<Int, String>()

            sheet[0].forEachIndexed { i, game ->
                game
                    .toString()
                    .takeIf {
                        it.isNotBlank()
                    }?.let {
                        logger.info("{} at {}", it, i)
                        gameIndexMap[i] = it
                    }
            }

            val maxCol = sheet[0].size

            for (row in 2 until sheet.size) {
                val cols = sheet[row].size
                if (cols == 0) continue
                val name = sheet[row][0].toName() ?: continue
                if (name == END_ROW_TITLE) break

                for (col in 1 until cols) {
                    if (col == maxCol) break
                    val game = gameIndexMap[col] ?: continue
                    val score = sheet[row][col].toScore() ?: continue

                    val entry = Pair(game, score)

                    scoreMap[name]?.add(entry) ?: run {
                        scoreMap[name] = mutableListOf(entry)
                    }
                }
            }

            return scoreMap
        }

        fun writeRange(
            id: String,
            range: String,
            values: List<List<String>>,
        ) {
            logger.info("Attempting to write to sheet at range {} for values {}", range, values.joinToString { it.joinToString() })
            val body = ValueRange().setValues(values)
            try {
                sheets
                    .spreadsheets()
                    .values()
                    .update(id, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute()
            } catch (e: GoogleJsonResponseException) {
                logger.error("Failed to write to google sheet", e)
            }
        }
    }
