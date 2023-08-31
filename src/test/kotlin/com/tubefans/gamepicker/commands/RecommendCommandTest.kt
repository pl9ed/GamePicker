package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.commands.RecommendCommand.Companion.NO_GAMES_RESPONSE
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.ChatInputInteractionEventService
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val chatInputInteractionEventService: ChatInputInteractionEventService = mockk()
    private val userCache: UserCache = mockk()
    private val command = RecommendCommand(chatInputInteractionEventService, userCache)

    private val user1 = DiscordUser(Snowflake.of(1), "a", mutableMapOf("a" to 9, "b" to 5, "c" to 3))
    private val user2 = DiscordUser(Snowflake.of(2), "b", mutableMapOf("a" to 0, "b" to 0, "c" to 0))
    private val emptyUser = DiscordUser(Snowflake.of(0), "empty")

    private val gameScoreMap = GameScoreMap(setOf(user1, user2, emptyUser))

    private val responseTemplate = """
        ```
        TOP %d GAMES:
        | 1. | %s | %d | Fans: %s | Excludes: %s |
        | 2. | %s | %d | Fans: %s | Excludes: %s |
        ```
    """.trimIndent()

    @Test
    fun `should generate top games list`() {
        assertEquals(
            String.format(
                responseTemplate,
                2,
                "a", 9, "a", "b, empty",
                "b", 5, "a", "b, empty"
            ),
            command.getReplyString(gameScoreMap, 2)
        )
    }

    @Test
    fun `should respond with separate string when no games are found`() {
        assertEquals(NO_GAMES_RESPONSE, command.getReplyString(GameScoreMap(emptyList()), 1))
    }

    @Test
    fun `should generate properly validated row`() {
        val rank = 1
        val game = "game"
        val score = 100L
        val fans = listOf(user1)
        val excludes = listOf(user2, emptyUser)
        val expected = listOf(
            "1.",
            game,
            score,
            "Fans: " + fans.joinToString { it.name!! },
            "Excludes: " + excludes.map { it.name }.joinToString()
        )

        val actual = command.generateRowData(rank, game, score, fans, excludes)
        for (i in expected.indices) {
            assertEquals(expected[i].toString(), actual[i])
        }
    }

    @Test
    fun `should fallback to discordId if name is null`() {
        val rank = 1
        val game = "game"
        val score = 100L
        val fans = listOf(DiscordUser(Snowflake.of(1), null, mutableMapOf("a" to 10)))
        val excludes = listOf(DiscordUser(Snowflake.of(2), null), emptyUser)
        assertEquals("Fans: 1", command.generateRowData(rank, game, score, fans, excludes)[3])
        assertEquals("Excludes: 2, empty", command.generateRowData(rank, game, score, fans, excludes)[4])
    }

    @Test
    fun `should be able to explicitly set game count`() {
        val singleResponseTemplate = """
            ```
            TOP %d GAMES:
            | 1. | %s | %d | Fans: %s | Excludes: %s |
            ```
        """.trimIndent()

        assertEquals(
            String.format(
                singleResponseTemplate,
                1,
                "a",
                9,
                "a",
                "b, empty"
            ).trimIndent(),
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

        for (i in 2..15) {
            val pair = Pair("$i", i.toLong())
            scores.add(pair)
            if (topTen.size < 10) {
                topTen.add(pair)
            }
        }

        val gameScoreMap: GameScoreMap = mockk {
            every { getTopGames(10) } returns topTen
            every { getTopPlayersForGame(any(), any()) } returns listOf(user1)
            every { getNonPlayersForGame(any()) } returns listOf(user2)
        }

        val replyString = command.getReplyString(gameScoreMap, 50)
        // 10 + header + 2 ``` rows
        assertEquals(13, replyString.split("\n").size)
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
