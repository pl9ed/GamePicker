package com.tubefans.gamepicker.models

import com.tubefans.gamepicker.dto.DiscordUser
import discord4j.common.util.Snowflake
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameScoreMapTest {
    private val user1 =
        DiscordUser(
            Snowflake.of(1),
            "name1",
            mutableMapOf(
                "game1" to 10,
                "game2" to 5,
                "game3" to 1,
                "game4" to 0,
            ),
        )
    private val user2 =
        DiscordUser(
            Snowflake.of(2),
            "name2",
            mutableMapOf(
                "game1" to 0,
                "game2" to 0,
                "game3" to 10,
                "game4" to 0,
            ),
        )
    private val user3 =
        DiscordUser(
            Snowflake.of(3),
            "name3",
            mutableMapOf(
                "game1" to 9,
                "game2" to 5,
                "game3" to 0,
                "game4" to 0,
            ),
        )
    private val user4 =
        DiscordUser(
            Snowflake.of(4),
            "name4",
            mutableMapOf(
                "game1" to 10,
                "game2" to 5,
                "game3" to 10,
                "game4" to 10,
            ),
        )
    private val blankUser =
        DiscordUser(
            Snowflake.of(0),
            "blankname",
        )

    @Test
    fun `should identify top n games`() {
        val users = listOf(user1, user2, user3)
        val gameScoreMap = GameScoreMap(users)

        val n = 3
        val expectedList: List<Pair<String, Long>> =
            listOf(
                "game1" to 19,
                "game3" to 11,
                "game2" to 10,
            )
        val actualList = gameScoreMap.getTopGames(n)

        for (i in 0 until n) {
            assertEquals(expectedList[i], actualList[i])
        }
    }

    @Test
    fun `should handle empty user collection`() {
        assertTrue(GameScoreMap(emptyList()).getTopGames(5).isEmpty())
    }

    @Test
    fun `should handle blank user maps`() {
        val emptyUsers =
            listOf(
                DiscordUser(
                    Snowflake.of(1),
                    "name1",
                ),
                DiscordUser(
                    Snowflake.of(2),
                    "name2",
                ),
            )
        assertTrue(GameScoreMap(emptyUsers).getTopGames(5).isEmpty())

        assertEquals(
            listOf("game1" to 10L, "game2" to 5L, "game3" to 1L),
            GameScoreMap(listOf(user1, blankUser)).getTopGames(3),
        )
    }

    @Test
    fun `should get top players for a game`() {
        val expectedList = listOf(user1, user3)
        val gameScoreMap = GameScoreMap(listOf(user1, user2, user3))
        assertEquals(
            expectedList,
            gameScoreMap.getTopPlayersForGame("game1", 2),
        )
    }

    @Test
    fun `should get players that don't play a game`() {
        val gameScoreMap = GameScoreMap(listOf(user1, user2, user3))
        val game1NonPlayers = gameScoreMap.getNonPlayersForGame("game1")
        val game4NonPlayers = gameScoreMap.getNonPlayersForGame("game4")

        assertEquals(1, game1NonPlayers.size)
        assertEquals(
            user2,
            game1NonPlayers.first(),
        )

        assertEquals(
            listOf(user1, user2, user3).toSet(),
            game4NonPlayers.toSet(),
        )
    }

    @Test
    fun `getNonPlayersForGame() should include players with no entry`() {
        val gameScoreMap = GameScoreMap(listOf(user1, blankUser))

        assertEquals(
            listOf(blankUser),
            gameScoreMap.getNonPlayersForGame("game1"),
        )
    }
}
