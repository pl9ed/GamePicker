package com.tubefans.gamepicker.config

import com.tubefans.gamepicker.services.GoogleSheetsService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SheetsConfigTest {

    companion object {

        private var row1 = mutableListOf("")

        @JvmStatic
        @BeforeAll
        fun setupRows() {
            for (i in 1..11) {
                row1.add("Game$i")
            }
        }

        @JvmStatic
        @AfterAll
        fun teardownRows() {
            row1 = mutableListOf("")
        }
    }

    private val testSheetId = "1674LxS7oI8eWWMMQljtFFJcoOpvn-mBTgAVZ3jnikb0"
    private val testSheetName = "Sheet1"
    private val dataRange = "A1:L11"
    private val userRange = "A9:L11"

    private val row10 = listOf(
        "User2",
        "10",
        "0",
        "5",
        "5",
        "1",
        "5",
        "5",
        "5",
        "5",
        "5",
        "5"
    )

    @Autowired
    private lateinit var googleSheetsService: GoogleSheetsService

    @Test
    fun `should connect to google sheets`() {
        val response = googleSheetsService.getValueRange(testSheetId, "Sheet1")
        Assertions.assertTrue(response.size > 0)
    }

    @Test
    fun `should get correct row from test sheet`() {
        val actualRow = googleSheetsService.getValueRange(testSheetId, userRange)
            .getValues()[1]

        assertEquals(row10, actualRow)
    }

    @Test
    fun `should handle blank cells`() {
        val actualRow1 = googleSheetsService.getValueRange(testSheetId, dataRange)
            .getValues()[0]

        assertEquals(row1, actualRow1)
    }
}
