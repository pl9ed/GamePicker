package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`

class GoogleSheetsServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var sheets: Sheets

    @InjectMocks
    private val googleSheetsService = GoogleSheetsService(sheets, userRepository)

    private val sheetId = "sheet-id"
    private val sheetName = "Sheet1"
    private val range = "range"

    private val mockSheet = listOf(
        listOf("")
    )

    @BeforeEach
    fun setup() {
        `when`(
            sheets.spreadsheets()
                .values()
                .get(sheetId, range)
                .execute()
                .getValues()
        ).thenReturn(
            listOf(
                listOf()
            )
        )
    }

    @Test
    fun `should update db from sheets`() {
    }
}
