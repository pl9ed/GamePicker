package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.models.GameScoreMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService @Autowired constructor() {

    companion object Keys {
        const val GAME_NAME_KEY = "game"
        const val GAME_GENRE_KEY = "genre"
        const val GAME_SCORE_KEY = "score"
    }

    fun getSortedGameMap(botUsers: Collection<BotUser>): GameScoreMap =
        GameScoreMap(botUsers)
}
