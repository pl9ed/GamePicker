package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.UserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Component

@Component
class PullFromSheetCommand @Autowired constructor(
    private val userService: UserService,
    private val googleSheetsService: GoogleSheetsService
) : SlashCommand {

    companion object {
        const val SHEET_ID_NAME = "id"
        const val SHEET_RANGE_NAME = "range"

        const val DEFAULT_SHEET = "1FYL7O7RUkm4Fw-D2xw4R48QbY90hKf34oWgZ0_89vX8"
        const val DEFAULT_RANGE = "A1:AA11"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "pull-from-sheet"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        val sheetId: String = try {
            event.options
                .first { it.name == SHEET_ID_NAME }
                .value
                .get()
                .asString()
        } catch (e: NoSuchElementException) {
            DEFAULT_SHEET
        }

        val range: String = try {
            event.options
                .first { it.name == SHEET_RANGE_NAME }
                .value
                .get()
                .asString()
        } catch (e: NoSuchElementException) {
            DEFAULT_RANGE
        }

        val failedUpdateNames = mutableSetOf<String>()

        val usersUpdated = mutableSetOf<String>()

        // TODO: async and block total
        mono {
            googleSheetsService.apply {
                mapToScores(getSheet(sheetId, range)).forEach { (unformattedName, games) ->
                    val name = unformattedName.uppercase()
                    games.forEach { (unformattedGame, score) ->
                        val game = unformattedGame.uppercase()
                        try {
                            userService.updateGameForUserWithName(name, game, score)
                            usersUpdated.add(name)
                        } catch (e: IllegalArgumentException) {
                            logger.error("Null id when updating from sheet for name=$name", e)
                            failedUpdateNames.add(name)
                        } catch (e: EmptyResultDataAccessException) {
                            logger.error("Could not find user with name=$name", e)
                            failedUpdateNames.add(name)
                        }
                    }
                }
            }
        }.block()

        return event.reply()
            .withContent(
                "Updated DB with scores for ${usersUpdated.size} users. " +
                        "Failed to update for ${failedUpdateNames.joinToString()}"
            )
    }
}
