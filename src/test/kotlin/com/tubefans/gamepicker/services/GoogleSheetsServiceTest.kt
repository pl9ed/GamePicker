package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.repositories.BotUserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
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
        text.split("\n").forEach {
            val row = it.split(",").toMutableList()
            row.removeIf { cell -> cell == "\r" }
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
}
