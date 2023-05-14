package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.extensions.updateGame
import com.tubefans.gamepicker.repositories.BotUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BotUserService @Autowired constructor(
    private val botUserRepository: BotUserRepository
) {

    fun insertUser(user: BotUser) = botUserRepository.insert(user)

    fun updateUser(user: BotUser): BotUser = botUserRepository.save(user)

    fun updateGameForUserWithName(name: String, game: String, score: Long): BotUser =
        updateUser(botUserRepository.findOneByName(name).get().updateGame(game, score))

    fun updateGameForUserWithId(id: String, game: String, score: Long): BotUser =
        updateUser(botUserRepository.findById(id).get().updateGame(game, score))
}
