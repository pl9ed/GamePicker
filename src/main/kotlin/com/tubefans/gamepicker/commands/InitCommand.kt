package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.UserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InitCommand @Autowired constructor(
    private val userService: UserService,
    private val googleSheetsService: GoogleSheetsService
) : SlashCommand {

    companion object {
        const val SHEET_ID_NAME = "sheetId"
        const val SHEET_RANGE_NAME = "range"

        const val DEFAULT_RANGE = "A1:AA11"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "init"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        val sheetId: String = event.options
            .first { it.name == SHEET_ID_NAME }
            .value
            .get()
            .asString()

        val range: String = try {
            event.options
                .first { it.name == SHEET_RANGE_NAME }
                .value
                .get()
                .asString()
        } catch (e: NoSuchElementException) {
            DEFAULT_RANGE
        }

        val failedUpdateNames = mutableListOf<String>()

        val updateCount = mono {
            var count = 0
            googleSheetsService.apply {
                mapToScores(getSheet(sheetId, range)).forEach { (name, games) ->
                    games.forEach {(game, score) ->
                        try {
                            userService.updateGameForUserWithName(name, game, score)
                            count++
                        } catch (e: IllegalArgumentException) {
                            logger.error("Null id when updating from sheet for name=$name", e)
                            failedUpdateNames.add(name)
                        } catch (e: NoSuchElementException) {
                            logger.error("Could not find user with name=$name", e)
                            failedUpdateNames.add(name)
                        }
                    }
                }
                return@mono count
            }
        }.block()

        return event.reply().withEphemeral(false)
            .withContent(
                "Updated DB with scores from $updateCount users. " +
                        "Failed to update for ${failedUpdateNames.joinToString()}"
            )
    }
}
