package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameScoreMapTest {

    private val user1 = BotUser(
        "1",
        "user1",
        mutableMapOf(
            "game1" to 10,
            "game2" to 5,
            "game3" to 1,
            "game4" to 0
        )
    )
    private val user2 = BotUser(
        "2",
        "user2",
        mutableMapOf(
            "game1" to 0,
            "game2" to 0,
            "game3" to 10,
            "game4" to 0
        )
    )
    private val user3 = BotUser(
        "3",
        "user3",
        mutableMapOf(
            "game1" to 9,
            "game2" to 5,
            "game3" to 0,
            "game4" to 0
        )
    )
    private val blankUser = BotUser(
        "blank",
        "blankuser"
    )

    @Test
    fun `should identify top n games`() {
        val users = listOf(user1, user2, user3)
        val gameScoreMap = GameScoreMap(users)

        val n = 3
        val expectedList = listOf("game1", "game3", "game2")
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
        val emptyUsers = listOf(
            BotUser(
                "1",
                "user1"
            ),
            BotUser(
                "2",
                "user2"
            )
        )
        assertTrue(GameScoreMap(emptyUsers).getTopGames(5).isEmpty())

        assertEquals(
            listOf("game1", "game2", "game3"),
            GameScoreMap(listOf(user1, blankUser)).getTopGames(3)
        )
    }

    @Test
    fun `should get top players for a game`() {
        val expectedList = listOf(user1, user3)
        val gameScoreMap = GameScoreMap(listOf(user1, user2, user3))
        assertEquals(
            expectedList,
            gameScoreMap.getTopPlayersForGame("game1", 2)
                .map { it.user }
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
            game1NonPlayers.first()
        )

        assertEquals(
            listOf(user1, user2, user3).toSet(),
            game4NonPlayers.toSet()
        )
    }
}
