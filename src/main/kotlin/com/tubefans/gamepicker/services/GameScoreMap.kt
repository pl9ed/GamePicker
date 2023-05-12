package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.dto.UserScore
import java.util.*

class GameScoreMap(
    botUsers: Collection<BotUser>
) {

    private val map = mutableMapOf<String, SortedSet<UserScore>>()

    init {
        botUsers.forEach { user ->
            user.gameMap.forEach { (game, score) ->
                map[game]?.add(UserScore(user, score)) ?: run {
                    map[game] = sortedSetOf(UserScore(user, score))
                }
            }
        }
    }

    fun getTopGames(n: Int): List<String> =
        map.toList()
            .sortedByDescending { (_, v) ->
                var sum = 0L
                v.forEach {
                    sum += it.score
                }
                sum
            }.take(n)
            .map {
                it.first
            }

    fun getTopPlayersForGame(game: String, n: Int = 3): List<UserScore> =
        map[game]?.sortedDescending()?.take(n) ?: emptyList()

    fun getNonPlayersForGame(game: String): List<BotUser> =
        map[game]?.filter {
            it.score == 0L
        }?.map {
            it.user
        } ?: emptyList()
}
