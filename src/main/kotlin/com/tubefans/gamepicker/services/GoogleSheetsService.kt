package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.tubefans.gamepicker.repositories.BotUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import toName
import toScore

@Service
class GoogleSheetsService @Autowired constructor(
    private val sheets: Sheets,
    private val botUserRepository: BotUserRepository
) {

    fun getValueRange(id: String, range: String): ValueRange =
        sheets.spreadsheets().values()[id, range].execute()

    fun getAll(id: String, sheetName: String = "Sheet1"): ValueRange =
        sheets.spreadsheets().values()[id, "Sheet1"].execute()

    /**
     * Maps scores from the Google sheet. Assumes proper configuration of sheet with header rows for game name and
     * genre.
     * @param id Google Sheet id
     * @param range String representation of range, e.x. 'A1:A5'
     */
    fun getUserScores(id: String, range: String): Map<String, Pair<String, Long>> {
        val userScores: MutableMap<String, Pair<String, Long>> = mutableMapOf()
        val sheet = getSheet(id, range) // 2d list

        // which games are at which column
        val gameIndexMap = mutableMapOf<Int, String>()

        sheet[0].forEachIndexed { i, game ->
            game?.toString()?.takeIf {
                it.isNotBlank()
            }?.let {
                gameIndexMap[i] = it
            }
        }

        for (row in 2 until sheet.size) {
            val cols = sheet[row].size
            sheet[row][0].toName()?.let { name ->
                for (col in 1 until cols) {
                    sheet[row][col].toScore()?.let { score ->
                        userScores[name] = Pair(name, score)
                    }
                }
            }
        }

        return userScores
    }

    private fun getSheet(id: String, range: String) = sheets.spreadsheets()
        .values()
        .get(id, range)
        .execute()
        .getValues()
}
