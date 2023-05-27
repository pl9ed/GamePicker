package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
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
    private val userCache: UserCache
) {

    fun existsById(id: String) = userCache.users.any { it.discordId == id }

    fun findById(id: String) = userCache.users.first { it.discordId == id }

    fun findOneByName(name: String) = userCache.users.first { it.name == name }

    fun save(user: DiscordUser): DiscordUser {
        userCache.users.apply {
            removeIf { it.discordId == user.discordId }
            add(user)
        }
        return user
    }

    fun updateGameForUserWithName(name: String, game: String, score: Long): DiscordUser =
        save(findOneByName(name).updateGame(game, score))

    fun updateGameForUserWithId(id: String, game: String, score: Long) =
        save(findById(id).updateGame(game, score))

    fun getUsersFromNames(names: Collection<String>) = runBlocking {
        val userSet = mutableSetOf<DiscordUser>()
        val failedSet = mutableSetOf<String>()
        names.map { name ->
            async {
                try {
                    userSet.add(findOneByName(name))
                } catch (e: NoSuchElementException) {
                    failedSet.add(name)
                }
            }
        }.awaitAll()

        return@runBlocking Pair(userSet, failedSet)
    }
}
