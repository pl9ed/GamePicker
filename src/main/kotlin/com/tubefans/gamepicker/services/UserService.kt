package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.extensions.updateGame
import com.tubefans.gamepicker.repositories.BotUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    val botUserRepository: BotUserRepository
) {

    fun updateUser(user: BotUser): BotUser = try {
        botUserRepository.save(user)
    } catch (e: IllegalArgumentException) {
        botUserRepository.insert(user)
    }

    fun updateGameForUserWithName(name: String, game: String, score: Long): BotUser =
        updateUser(botUserRepository.findOneByName(name).updateGame(game, score))

    fun updateGameForUserWithId(id: String, game: String, score: Long): BotUser =
        updateUser(botUserRepository.findById(id).get().updateGame(game, score))
}
