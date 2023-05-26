package com.tubefans.gamepicker.commands

import com.mongodb.internal.VisibleForTesting
import com.tubefans.gamepicker.services.DiscordUserService
import com.tubefans.gamepicker.services.GoogleSheetsService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import java.io.IOException
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PullFromSheetCommand @Autowired constructor(
    private val discordUserService: DiscordUserService,
    private val googleSheetsService: GoogleSheetsService
) : SlashCommand {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "pull-from-sheet"

    override fun handle(event: ChatInputInteractionEvent) =
        event.deferReply()
            .then(updateDbFromSheet(event))
            .flatMap {
                event.editReply(it)
            }.then()

    fun updateDbFromSheet(event: ChatInputInteractionEvent): Mono<String> = mono {
        val sheet = try {
            googleSheetsService.getSheet()
        } catch (e: IOException) {
            emptyList()
        }

        val failedNames = mutableSetOf<String>()
        val usersUpdated = mutableSetOf<String>()

        // TODO: optimize and update asynchronously, also split up method so it's not so large
        googleSheetsService.apply {
            mapToScores(sheet).forEach { (unformattedName, games) ->
                val name = unformattedName.uppercase()
                logger.info("Found user $name on Google sheet")
                games.forEach { (unformattedGame, score) ->
                    val game = unformattedGame.uppercase()
                    try {
                        discordUserService.updateGameForUserWithName(name, game, score)
                        usersUpdated.add(name)
                    } catch (e: NoSuchElementException) {
                        logger.error("Could not find user with name=$name", e)
                        failedNames.add(name)
                    }
                }
            }
        }

        createReplyMessage(usersUpdated.size, failedNames)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    fun createReplyMessage(succeeded: Int, failedNames: Collection<String>): String {
        val replyString = StringBuilder("Updated DB with scores for $succeeded users.")

        if (failedNames.isNotEmpty()) {
            replyString.append(" Failed to update for ${failedNames.joinToString()}")
        }

        return replyString.toString()
    }
}
