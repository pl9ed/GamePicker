package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.commands.RecommendCommand.Companion.NO_GAMES_RESPONSE
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.EventService
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val eventService: EventService = mockk()
    private val userCache: UserCache = mockk()
    private val command = RecommendCommand(eventService, userCache)

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

    @Test
    fun `should be able to explicitly set game count`() {
        val singleResponseTemplate = """
        TOP %d GAMES:
        1: %s | %d | Fans: %s | Excludes: %s
        """.trimIndent()

        assertEquals(
            String.format(
                singleResponseTemplate,
                1,
                "a",
                10,
                "a",
                "b, empty"
            ).trim(),
            command.getReplyString(gameScoreMap, 1)
        )
    }

    @Test
    fun `should display no games string when score map is empty`() {
        val gameScoreMap: GameScoreMap = mockk {
            every { getTopGames(any()) } returns emptyList()
        }
        assertEquals(
            NO_GAMES_RESPONSE,
            command.getReplyString(gameScoreMap, 0)
        )
    }

    @Test
    fun `should display max 10 games`() {
        val scores = mutableListOf(Pair("1", 1L))
        val topTen = mutableListOf<Pair<String, Long>>()

        for (i in 2..11) {
            val pair = Pair("$i", i.toLong())
            scores.add(pair)
            topTen.add(pair)
        }

        val gameScoreMap: GameScoreMap = mockk {
            every { getTopGames(10) } returns topTen
            every { getTopPlayersForGame(any(), any()) } returns listOf(user1)
            every { getNonPlayersForGame(any()) } returns listOf(user2)
        }

        val replyString = command.getReplyString(gameScoreMap, 50)

        assertTrue(!replyString.contains(": 1 | 1"))

        for (i in 2..11) {
            assertTrue(
                replyString.contains("$i | $i")
            )
        }
    }

    @Test
    fun `should display min 1 game`() {
        val scores = mutableListOf<Pair<String, Long>>()

        for (i in 1..3) {
            val pair = Pair("$i", i.toLong())
            scores.add(pair)
        }

        val gameScoreMap: GameScoreMap = mockk {
            every { getTopGames(1) } returns listOf(scores.first())
            every { getTopPlayersForGame(any(), any()) } returns listOf(user1)
            every { getNonPlayersForGame(any()) } returns listOf(user2)
        }

        val replyString = command.getReplyString(gameScoreMap, -10)

        assertTrue(replyString.contains("1 | 1"))

        for (i in 2..3) {
            assertTrue(
                !replyString.contains("$i | $i")
            )
        }
    }
}
