package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.extensions.updateGame
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DiscordUserService @Autowired constructor(
    private val discordUserRepository: DiscordUserRepository
) {

    fun existsById(id: String) = discordUserRepository.existsById(id)

    fun findById(id: String) = discordUserRepository.findById(id)

    fun insert(user: DiscordUser) = discordUserRepository.insert(user)

    fun save(user: DiscordUser): DiscordUser = discordUserRepository.save(user)

    fun updateGameForUserWithName(name: String, game: String, score: Long): DiscordUser =
        save(discordUserRepository.findOneByName(name).get().updateGame(game, score))

    fun updateGameForUserWithId(id: String, game: String, score: Long): DiscordUser =
        save(discordUserRepository.findById(id).get().updateGame(game, score))

    fun getUsersFromNames(names: Collection<String>) = runBlocking {
        val userSet = mutableSetOf<DiscordUser>()
        val failedSet = mutableSetOf<String>()
        names.map { name ->
            async {
                try {
                    userSet.add(discordUserRepository.findOneByName(name).get())
                } catch (e: NoSuchElementException) {
                    failedSet.add(name)
                }
            }
        }.awaitAll()

        return@runBlocking Pair(userSet, failedSet)
    }
}
