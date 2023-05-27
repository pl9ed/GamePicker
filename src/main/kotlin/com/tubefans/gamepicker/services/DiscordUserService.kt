package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
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

    fun getUsersFromNames(names: Collection<String>) = runBlocking {
        val userSet = mutableSetOf<DiscordUser>()
        val failedSet = mutableSetOf<String>()
        names.map { name ->
            try {
                userSet.add(findOneByName(name))
            } catch (e: NoSuchElementException) {
                failedSet.add(name)
            }
        }

        Pair(userSet, failedSet)
    }
}
