package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.models.GameScoreMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService @Autowired constructor(
    private val userCache: UserCache
) {

    companion object Keys {
        const val GAME_NAME_KEY = "game"
        const val GAME_GENRE_KEY = "genre"
        const val GAME_SCORE_KEY = "score"
    }

    fun getSortedGameMap(discordUsers: Collection<DiscordUser>): GameScoreMap =
        GameScoreMap(discordUsers)
}
