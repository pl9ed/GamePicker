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

    var users: MutableSet<DiscordUser> = updateUsers()
        get() {
            val lastUpdate = googleSheetCache.lastUpdateTime()
            if (lastUpdate.value > this.lastUpdate.value) {
                this.lastUpdate = lastUpdate
                field = updateUsers()
            }
            return field
        }

    private fun updateUsers(): MutableSet<DiscordUser> = runBlocking {
        val userSet = mutableSetOf<DiscordUser>()

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
            }

        return@runBlocking userSet
    }
}
