package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.database.UserRepository
import com.tubefans.gamepicker.dto.BotUser
import discord4j.core.`object`.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    val userRepository: UserRepository
) {

    fun updateGame(discordUser: User, game: String, score: Long): BotUser =
        if (userRepository.existsById(discordUser.id.toString())) {
            userRepository.findById(discordUser.id.toString())
                .get()
                .let {
                    it.gameMap[game] = score
                    return userRepository.save(it)
                }
        } else {
            userRepository.insert(
                BotUser(
                    discordUser.id.toString(),
                    discordUser.username,
                    mutableMapOf(
                        game to score
                    )
                )
            )
        }
}
