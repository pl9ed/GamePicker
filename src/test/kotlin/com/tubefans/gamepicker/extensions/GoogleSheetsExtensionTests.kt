package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.services.GameScoreMap.Companion.MAX_SCORE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import toName
import toScore

class GoogleSheetsExtensionTests {

    private val whitespace = listOf(
        " ",
        "\t",
        "\n",
        ""
    )

    @Test
    fun `should return name string`() {
        val name = "name"
        assertEquals(name, name.toName())
    }

    @Test
    fun `should handle type casting`() {
        val name = 100
        assertEquals("100", name.toName())
    }

    @Test
    fun `should return null on null object`() {
        assertEquals(null, null.toName())
    }

    @Test
    fun `should return null name on 'null' string`() {
        assertEquals(null, "null".toName())
    }

    @Test
    fun `should return null on blank string`() {
        whitespace.forEach {
            assertEquals(null, it.toName())
        }
    }

    @Test
    fun `should return score value`() {
        val score = 10L
        assertEquals(score, score.toScore())
    }

    @Test
    fun `should return max score on non-numeric value`() {
        assertEquals(MAX_SCORE, "x".toScore())
        assertEquals(MAX_SCORE, Object().toScore())
    }

    @Test
    fun `should return null on whitespace strings`() {
        whitespace.forEach {
            assertEquals(null, it.toScore())
        }
    }

    @Test
    fun `should return null score on 'null' string`() {
        assertEquals(null, "null".toScore())
    }
}
