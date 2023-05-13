package com.tubefans.gamepicker.services

import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GoogleSheetsServiceTest {

    @InjectMocks
    @Autowired
    private lateinit var googleSheetsService: GoogleSheetsService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var sheets: Sheets

    private val testSheetId = "1674LxS7oI8eWWMMQljtFFJcoOpvn-mBTgAVZ3jnikb0"
    private val testSheetName = "Sheet1"
    private val dataRange = "A1:L11"
    private val userRange = "A9:L11"

    @Test
    fun `should be able to see google sheets`() {
        val response = googleSheetsService.getValueRange(testSheetId, "Sheet1")
        assertTrue(response.size > 0)
    }
}
