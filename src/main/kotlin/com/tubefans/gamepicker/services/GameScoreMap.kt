package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.Game
import com.tubefans.gamepicker.dto.User
import java.util.*

class GameScoreMap(
    users: Collection<User>
) {

    private val map = mutableMapOf<Game, SortedSet<Pair<String, Int>>>()

    init {
        users.forEach { user ->
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

    fun getTopGames(n: Int): List<Game> =
        map.toList()
            .sortedBy { (_, v) ->
                var sum = 0
                v.forEach {
                    sum += it.second
                }
                sum
            }.take(n)
            .map {
                it.first
            }
}
