package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class GameScoreMapTest {

    private val users = listOf(
        BotUser(
            "1",
            "user1",
            mutableMapOf(
                "game1" to 10,
                "game2" to 5,
                "game3" to 0,
                "game4" to 0
            )
        ),
        BotUser(
            "2",
            "user2",
            mutableMapOf(
                "game1" to 0,
                "game2" to 0,
                "game3" to 10,
                "game4" to 0
            )
        ),
        BotUser(
            "3",
            "user3",
            mutableMapOf(
                "game1" to 10,
                "game2" to 5,
                "game3" to 0,
                "game4" to 0
            )
        )
    )

    private val gameScoreMap = GameScoreMap(users)

    @Test
    fun `should identify top n games`() {
        val n = 3
        val expectedList = listOf("game1", "game3", "game2")
        val actualList = gameScoreMap.getTopGames(n)

        for (i in 0 until n) {
            assertEquals(expectedList[i], actualList[i])
        }
    }
}