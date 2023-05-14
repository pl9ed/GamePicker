package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.repositories.BotUserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoogleSheetsServiceTest {

    private var botUserRepository: BotUserRepository = mockk()
    private var sheets: Sheets = mockk()

    private var googleSheetsService: GoogleSheetsService = GoogleSheetsService(sheets, botUserRepository)

    private val sheetId = "sheet-id"
    private val range = "range"

    private val testSheetPath = "/testdata/test-sheet.csv"

    private val mockSheet: MutableList<List<Any>> = mutableListOf()

    init {
        val text = String(javaClass.getResourceAsStream(testSheetPath)!!.readBytes())
        text.split("\n").forEach { line ->
            val row = line.replace("\r", "")
                .replace("\n", "")
                .split(",")
                .toMutableList()
            mockSheet.add(row)
        }
    }

    @BeforeEach
    fun setup() {
        every {
            sheets.spreadsheets()
                .values()
                .get(sheetId, range)
                .execute()
                .getValues()
        } returns mockSheet
    }

    @Test
    fun `should update db from sheets`() {
        val row1 = mutableListOf("")
        for (i in 1..11) {
            row1.add("Game$i")
        }
        assertEquals(row1, googleSheetsService.getSheet(sheetId, range)[0])
    }

    @Test
    fun `should map 2d array to k=user, v=game scores`() {
        val list = listOf(
            listOf("", "game1", "game2"),
            listOf("", "genre", "genre"),
            listOf("user1", "x", "x"),
            listOf("user2", "10", "0")
        )

        val expectedMap = mapOf(
            "user1" to listOf(
                Pair("game1", 10L),
                Pair("game2", 10L)
            ),
            "user2" to listOf(
                Pair("game1", 10L),
                Pair("game2", 0L)
            )
        )

        assertEquals(expectedMap, googleSheetsService.mapToScores(list))
    }

    @Test
    fun `should skip users with no score entries`() {
        val list = listOf(
            listOf("", "game1", "game2"),
            listOf("", "genre", "genre"),
            listOf("user1", "x", "x"),
            listOf("user2", "", "")
        )

        val expectedMap = mapOf(
            "user1" to listOf(
                Pair("game1", 10L),
                Pair("game2", 10L)
            )
        )

        assertEquals(expectedMap, googleSheetsService.mapToScores(list))
    }

    @Test
    fun `should handle blank game cells`() {
        val list = listOf(
            listOf("", "game1", "", "game2"),
            listOf("", "genre", "genre", "genre"),
            listOf("user1", "x", "", "x"),
            listOf("user2", "10", "", "0")
        )

        val expectedMap = mapOf(
            "user1" to listOf(
                Pair("game1", 10L),
                Pair("game2", 10L)
            ),
            "user2" to listOf(
                Pair("game1", 10L),
                Pair("game2", 0L)
            )
        )

        assertEquals(expectedMap, googleSheetsService.mapToScores(list))
    }

    @Test
    fun `should generate empty map for blank sheets`() {
        assertTrue(googleSheetsService.mapToScores(emptyList()).isEmpty())
    }
}
