package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.repositories.BotUserRepository
import discord4j.core.`object`.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    val botUserRepository: BotUserRepository
) {

    fun updateGame(discordUser: User, game: String, score: Long): BotUser =
        if (botUserRepository.existsById(discordUser.id.toString())) {
            botUserRepository.findById(discordUser.id.toString())
                .get()
                .let {
                    it.gameMap[game] = score
                    return botUserRepository.save(it)
                }
        } else {
            botUserRepository.insert(
                BotUser(
                    discordUser.id.toString(),
                    discordUser.username,
                    "",
                    mutableMapOf(
                        game to score
                    )
                )
            )
        }
}
