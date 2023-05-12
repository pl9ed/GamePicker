package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.dto.Game
import java.util.*

class GameScoreMap(
    botUsers: Collection<BotUser>
) {

    private val map = mutableMapOf<String, SortedSet<Pair<String, Long>>>()

    init {
        botUsers.forEach { user ->
            user.gameMap.forEach { (game, score) ->
                if (map[game] == null) {
                    map[game] = sortedSetOf(
                        Comparator { o1, o2 -> o1.second.compareTo(o2.second) },
                        Pair(user.username, score)
                    )
                } else {
                    map[game]?.add(Pair(user.username, score))
                }
            }
        }
    }

    fun getTopGames(n: Int): List<String> =
        map.toList()
            .sortedBy { (_, v) ->
                var sum = 0L
                v.forEach {
                    sum += it.second
                }
                sum
            }.take(n)
            .map {
                it.first
            }
}
