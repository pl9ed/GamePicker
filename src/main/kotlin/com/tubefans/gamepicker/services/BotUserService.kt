package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.extensions.updateGame
import com.tubefans.gamepicker.repositories.BotUserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

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

    fun getUsersFromNames(names: Collection<String>) = runBlocking {
        val userSet = mutableSetOf<BotUser>()
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

        return@runBlocking listOf(userSet, failedSet)
    }
}
