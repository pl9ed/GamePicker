package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.repositories.BotUserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock

class GoogleSheetsServiceTest {

    private var botUserRepository: BotUserRepository = mockk()

    @Mock
    private var sheets: Sheets = mockk()

    @InjectMocks
    private var googleSheetsService: GoogleSheetsService = GoogleSheetsService(sheets, botUserRepository)

    private val sheetId = "sheet-id"
    private val sheetName = "Sheet1"
    private val range = "range"

    private val mockSheet = listOf(
        listOf("")
    )

    @BeforeEach
    fun setup() {
        every {
            sheets.spreadsheets()
                .values()
                .get(sheetId, range)
                .execute()
                .getValues()
        } returns listOf(
            listOf()
        )
    }

    @Test
    fun `should update db from sheets`() {
    }
}
