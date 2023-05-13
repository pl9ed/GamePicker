package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.tubefans.gamepicker.repositories.UserRepository
import com.tubefans.gamepicker.services.GameScoreMap.Companion.MAX_SCORE
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GoogleSheetsService @Autowired constructor(
    private val sheets: Sheets,
    private val userRepository: UserRepository
) {

    fun getValueRange(id: String, range: String): ValueRange =
        sheets.spreadsheets().values().get(id, range).execute()

    fun getAll(id: String, sheetName: String = "Sheet1"): ValueRange =
        sheets.spreadsheets().values().get(id, "Sheet1").execute()

    fun updateUserScores(id: String, range: String) {
        return
        TODO("Test")
        val sheet = sheets.spreadsheets()
            .values()
            .get(id, range)
            .execute()
            .getValues()

        val gameIndexMap = mutableMapOf<Int, String>()

        sheet[0].forEachIndexed { i, game ->
            game?.toString()?.takeIf {
                it.isNotBlank()
            }?.let {
                gameIndexMap[i] = it
            }
        }

        for (row in 1 until sheet.size) {
            val cols = sheet[row].size

            // names
            sheet[row][0]

            sheet[row][0].toName()?.let { name ->
                val user = userRepository.findOneByName(name)
                for (col in 1 until cols) {
                    sheet[row][col]?.toScore()?.let { score ->
                        gameIndexMap[col]?.let { game ->
                            user.gameMap[game] = score
                        }
                    }
                }
                userRepository.save(user)
            }
        }
    }

    private fun Any?.toName(): String? =
        this?.toString()?.takeIf {
            it != "null" && it.isNotBlank()
        }

    private fun Any?.toScore(): Long? {
        if (this.toString().isBlank()) return null
        if (this.toString() == "null") return null
        return try {
            this.toString().toLong()
        } catch (e: ClassCastException) {
            MAX_SCORE
        }
    }
}
