package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import discord4j.common.util.Snowflake
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DiscordUserService @Autowired constructor(
    private val userCache: UserCache
) {

    fun findById(id: Snowflake) = userCache.users.first { it.discordId == id }

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
            async {
                try {
                    userSet.add(findOneByName(name))
                } catch (e: NoSuchElementException) {
                    failedSet.add(name)
                }
            }
        }.awaitAll()

        Pair(userSet, failedSet)
    }
}
