package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.RecommendCommand.Companion.NO_GAMES_RESPONSE
import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.dto.UserScore
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.EventService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val eventService: EventService = mockk()
    private val command = RecommendCommand(eventService)

    private val user1 = BotUser("a", "a", "a", mutableMapOf("a" to 10, "b" to 5, "c" to 3))
    private val user2 = BotUser("b", "b", "b", mutableMapOf("a" to 0, "b" to 0, "c" to 0))

    private val gameScoreMap = GameScoreMap(setOf(user1, user2))

    private val responseTemplate = """
        TOP %d GAMES:
        1: %s | %d | Fans: %s | Excludes: %s 
    """.trimIndent()

    @Test
    fun `should generate top games list`() {
        assertEquals(
            String.format(
                responseTemplate,
                1,
                "a",
                10,
                "a",
                "b",
            ).trim(),
            command.getReplyString(gameScoreMap, 1)
        )
    }

    @Test
    fun `should respond with separate string when no games are found`() {
        assertEquals(NO_GAMES_RESPONSE, command.getReplyString(GameScoreMap(emptyList()), 1))
    }

    @Test
    fun `should generate properly validated row`() {
        val game = "game"
        val score = 100L
        val fans = listOf(UserScore(user1, 10))
        val excludes = listOf(user2)
        val row = String.format(
            "%s | %d | Fans: %s | Excludes: %s",
            game,
            score,
            fans.map { it.user.name }.joinToString(),
            excludes.map { it.name }.joinToString()
        )
        assertEquals(row, command.generateRow(game, score, fans, excludes))
    }
}
