package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.extensions.updateGame
import com.tubefans.gamepicker.repositories.BotUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BotUserService @Autowired constructor(
    private val botUserRepository: BotUserRepository
) {

    fun existsById(id: String) = botUserRepository.existsById(id)

    fun findById(id: String) = botUserRepository.findById(id)

    fun insert(user: DiscordUser) = botUserRepository.insert(user)

    fun save(user: DiscordUser): DiscordUser = botUserRepository.save(user)

    fun updateGameForUserWithName(name: String, game: String, score: Long): DiscordUser =
        save(botUserRepository.findOneByName(name).get().updateGame(game, score))

    fun updateGameForUserWithId(id: String, game: String, score: Long): DiscordUser =
        save(botUserRepository.findById(id).get().updateGame(game, score))

    fun getUsersFromNames(names: Collection<String>) = runBlocking {
        val userSet = mutableSetOf<DiscordUser>()
        val failedSet = mutableSetOf<String>()
        names.map { name ->
            async {
                try {
                    userSet.add(botUserRepository.findOneByName(name).get())
                } catch (e: NoSuchElementException) {
                    failedSet.add(name)
                }
            }
        }.awaitAll()

        return@runBlocking Pair(userSet, failedSet)
    }
}
