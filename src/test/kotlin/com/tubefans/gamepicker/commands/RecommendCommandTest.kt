package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.RecommendCommand.Companion.NO_GAMES_RESPONSE
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import com.tubefans.gamepicker.services.EventService
import discord4j.common.util.Snowflake
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val eventService: EventService = mockk()
    private val repository: DiscordUserRepository = mockk()
    private val command = RecommendCommand(eventService, repository)

    private val user1 = DiscordUser(Snowflake.of(1), "a", mutableMapOf("a" to 10, "b" to 5, "c" to 3))
    private val user2 = DiscordUser(Snowflake.of(2), "b", mutableMapOf("a" to 0, "b" to 0, "c" to 0))
    private val emptyUser = DiscordUser(Snowflake.of(0), "empty")

    private val gameScoreMap = GameScoreMap(setOf(user1, user2, emptyUser))

    private val responseTemplate = """
        TOP %d GAMES:
        1: %s | %d | Fans: %s | Excludes: %s
        2: %s | %d | Fans: %s | Excludes: %s
    """.trimIndent()

    @Test
    fun `should generate top games list`() {
        assertEquals(
            String.format(
                responseTemplate,
                2,
                "a", 10, "a", "b, empty",
                "b", 5, "a", "b, empty"
            ).trim(),
            command.getReplyString(gameScoreMap, 2)
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
        val fans = listOf(user1)
        val excludes = listOf(user2, emptyUser)
        val row = String.format(
            "%s | %d | Fans: %s | Excludes: %s",
            game,
            score,
            fans.joinToString { it.name!! },
            excludes.map { it.name }.joinToString()
        )
        assertEquals(row, command.generateRow(game, score, fans, excludes))
    }

    @Test
    fun `should fallback to discordId if name is null`() {
        val game = "game"
        val score = 100L
        val fans = listOf(DiscordUser(Snowflake.of(1), null, mutableMapOf("a" to 10)))
        val excludes = listOf(DiscordUser(Snowflake.of(2), null), emptyUser)
        val row = String.format(
            "%s | %d | Fans: %s | Excludes: %s",
            game,
            score,
            fans.joinToString { it.name ?: it.discordId.asString() },
            excludes.joinToString { it.name ?: it.discordId.asString() }
        )
        assertEquals(row, command.generateRow(game, score, fans, excludes))
    }
}
