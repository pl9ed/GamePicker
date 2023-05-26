package com.tubefans.gamepicker.models

import com.tubefans.gamepicker.dto.DiscordUser
import java.util.*

class GameScoreMap(
    val discordUsers: Collection<DiscordUser>
) {

    companion object {
        const val MAX_SCORE = 10L
        const val MIN_SCORE = 0L
    }

    private val map = mutableMapOf<String, SortedSet<DiscordUser>>()

    init {
        discordUsers.forEach { user ->
            user.gameMap.forEach { (game, _) ->
                map[game]?.add(user) ?: run {
                    map[game] = sortedSetOf(DiscordUserComparator(game), user)
                }
            }
        }
    }

    fun getTopGames(n: Int): List<Pair<String, Long>> =
        map.toList()
            .map { (game, userSet) ->
                var sum = 0L
                userSet.forEach { user ->
                    sum += user.gameMap[game] ?: 0
                }
                Pair(game, sum)
            }.sortedByDescending {
                it.second
            }.take(n)

    fun getTopPlayersForGame(game: String, n: Int = 3): List<DiscordUser> =
        map[game]?.filter { it.gameMap[game] != 0L }
            ?.sortedByDescending {
                it.gameMap[game]
            }?.take(n) ?: emptyList()

    fun getNonPlayersForGame(game: String): List<DiscordUser> =
        discordUsers.filter {
            it.gameMap[game] == 0L || it.gameMap[game] == null
        }
}
