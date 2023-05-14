package com.tubefans.gamepicker.models

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.dto.UserScore
import java.util.*

class GameScoreMap(
    botUsers: Collection<BotUser>
) {

    companion object {
        const val MAX_SCORE = 10L
        const val MIN_SCORE = 0L
    }

    private val map = mutableMapOf<String, SortedSet<UserScore>>()

    init {
        botUsers.forEach { user ->
            user.gameMap.forEach { (game, unboundedScore) ->
                val score = minOf(maxOf(unboundedScore, MIN_SCORE), MAX_SCORE)
                map[game]?.add(UserScore(user, score)) ?: run {
                    map[game] = sortedSetOf(UserScore(user, score))
                }
            }
        }
    }

    fun getTopGames(n: Int): List<Pair<String, Long>> =
        map.toList()
            .sortedByDescending { (_, v) ->
                var sum = 0L
                v.forEach {
                    sum += it.score
                }
                sum
            }.take(n)
            .map { pair ->
                val sum = pair.second.sumOf {
                    it.score
                }
                Pair(pair.first, sum)
            }

    fun getTopPlayersForGame(game: String, n: Int = 3): List<UserScore> =
        map[game]?.filter { it.score != 0L }?.sortedDescending()?.take(n) ?: emptyList()

    fun getNonPlayersForGame(game: String): List<BotUser> =
        map[game]?.filter {
            it.score == 0L
        }?.map {
            it.user
        } ?: emptyList()
}
