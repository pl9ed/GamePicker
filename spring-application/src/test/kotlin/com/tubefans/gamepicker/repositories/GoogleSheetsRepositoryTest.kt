package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.repositories.GoogleSheetsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoogleSheetsRepositoryTest {
    private var sheets: Sheets = mockk()

    private var googleSheetsService: GoogleSheetsRepository = GoogleSheetsRepository(sheets)

    private val sheetId = "sheet-id"
    private val range = "range"

    private val testSheetPath = "/testdata/test-sheet.csv"

    private val mockSheet: MutableList<List<Any>> = mutableListOf()

    init {
        val text = String(javaClass.getResourceAsStream(testSheetPath)!!.readBytes())
        text.split("\n").forEach { line ->
            val row =
                line
                    .replace("\r", "")
                    .replace("\n", "")
                    .split(",")
                    .toMutableList()
            mockSheet.add(row)
        }
    }

    @BeforeEach
    fun setup() {
        every {
            sheets
                .spreadsheets()
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
    fun `should write to sheet`() {
        every {
            sheets
                .spreadsheets()
                .values()
                .update(sheetId, range, any())
                .setValueInputOption("USER_ENTERED")
                .execute()
        } returns mockk()

        val values =
            listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
            )

        googleSheetsService.writeRange(sheetId, range, values)

        verify {
            sheets
                .spreadsheets()
                .values()
                .update(sheetId, range, any())
                .setValueInputOption("USER_ENTERED")
                .execute()
        }
    }
}
