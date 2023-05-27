package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import com.tubefans.gamepicker.services.GoogleSheetsService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserCache @Autowired constructor(
    private val googleSheetCache: GoogleSheetCache,
    private val googleSheetsService: GoogleSheetsService,
    private val discordUserRepository: DiscordUserRepository
) {

    private var lastUpdate: DateTime = googleSheetCache.lastUpdateTime()
    private val logger = LogManager.getLogger()

    final var users: MutableSet<DiscordUser> = mutableSetOf()
        get() {
            val lastUpdate = googleSheetCache.lastUpdateTime()
            if (lastUpdate.value > this.lastUpdate.value) {
                this.lastUpdate = lastUpdate
                updateUsers()
            }
            return field
        }

    init {
        updateUsers()
        logger.info(
            "Initialized user cache with users: {}",
            users.joinToString {
                it.name ?: it.username ?: it.discordId
            }
        )
    }

    private fun updateUsers() = runBlocking {
        logger.info("Updating users from google sheets")
        googleSheetsService.mapToScores(googleSheetCache.getSheet())
            .map { (unformattedName, games) ->
                val name = unformattedName.uppercase()
                val discordUser = discordUserRepository.findOneByName(name).get()
                games.map { (unformattedGame, score) ->
                    async {
                        val game = unformattedGame.uppercase()
                        logger.info(
                            "Updating {}[{}]={}",
                            name,
                            game,
                            score
                        )
                        discordUser.gameMap[game] = score
                    }
                }.awaitAll()
                logger.info("Adding {}", discordUser.name)
                users.add(discordUser)
            }
    }
}
