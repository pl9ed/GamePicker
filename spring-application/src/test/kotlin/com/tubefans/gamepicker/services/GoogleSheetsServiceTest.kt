package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.repositories.GoogleSheetsRepository
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GoogleSheetsServiceTest {
    private var sheets: GoogleSheetsRepository = mockk()
    private var googleSheetsService: GoogleSheetsService = GoogleSheetsService(sheets)

    @Test
    fun `should map 2d array to k=user, v=game scores`() {
        val list =
            listOf(
                listOf("", "game1", "game2"),
                listOf("", "genre", "genre"),
                listOf("USER1", "x", "x"),
                listOf("USER2", "10", "0"),
            )

        val expectedMap =
            mapOf(
                "USER1" to
                    listOf(
                        Pair("game1", 10L),
                        Pair("game2", 10L),
                    ),
                "USER2" to
                    listOf(
                        Pair("game1", 10L),
                        Pair("game2", 0L),
                    ),
            )

        assertEquals(expectedMap, googleSheetsService.mapToScores(list))
    }

    @Test
    fun `should skip users with no score entries`() {
        val list =
            listOf(
                listOf("", "game1", "game2"),
                listOf("", "genre", "genre"),
                listOf("USER1", "x", "x"),
                listOf("USER2", "", ""),
            )

        val expectedMap =
            mapOf(
                "USER1" to
                    listOf(
                        Pair("game1", 10L),
                        Pair("game2", 10L),
                    ),
            )

        assertEquals(expectedMap, googleSheetsService.mapToScores(list))
    }

    @Test
    fun `should handle blank game cells`() {
        val list =
            listOf(
                listOf("", "game1", "", "game2"),
                listOf("", "genre", "genre", "genre"),
                listOf("USER1", "x", "", "x"),
                listOf("USER2", "10", "", "0"),
            )

        val expectedMap =
            mapOf(
                "USER1" to
                    listOf(
                        Pair("game1", 10L),
                        Pair("game2", 10L),
                    ),
                "USER2" to
                    listOf(
                        Pair("game1", 10L),
                        Pair("game2", 0L),
                    ),
            )

        assertEquals(expectedMap, googleSheetsService.mapToScores(list))
    }

    @Test
    fun `should generate empty map for blank sheets`() {
        assertTrue(googleSheetsService.mapToScores(emptyList()).isEmpty())
    }
}
