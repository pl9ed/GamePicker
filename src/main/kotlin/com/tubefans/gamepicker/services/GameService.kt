package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GameService @Autowired constructor() {

    fun getSortedGameMap(users: Collection<User>): GameScoreMap =
        GameScoreMap(users)
}
