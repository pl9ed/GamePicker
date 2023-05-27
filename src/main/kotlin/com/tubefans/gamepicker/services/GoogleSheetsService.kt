package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import toName
import toScore

@Service
class GoogleSheetsService @Autowired constructor(
    private val sheets: Sheets
) {

    companion object {
        const val DEFAULT_SHEET_ID = "1FYL7O7RUkm4Fw-D2xw4R48QbY90hKf34oWgZ0_89vX8"
        const val DEFAULT_RANGE = "Data" // name of the 'sheet' tab on the web UI
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getValueRange(id: String, range: String): ValueRange = sheets.spreadsheets().values()[id, range].execute()

    /**
     * @param id Google Sheet id
     * @param range String representation of range, e.x. 'A1:A5'
     * @return 2D array of objects from the sheet
     */
    fun getSheet(id: String = DEFAULT_SHEET_ID, range: String = DEFAULT_RANGE): List<List<Any>> =
        sheets.spreadsheets().values()[id, range].execute().getValues()

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
            game.toString().takeIf {
                it.isNotBlank()
            }?.let {
                logger.info("{} at {}", it, i)
                gameIndexMap[i] = it
            }
        }

        for (row in 2 until sheet.size) {
            val cols = sheet[row].size
            if (cols == 0) continue
            val name = sheet[row][0].toName() ?: continue

            for (col in 1 until cols) {
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
}
