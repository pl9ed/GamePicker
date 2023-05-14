package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.UserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import kotlinx.coroutines.reactor.mono
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

        const val DEFAULT_RANGE = ""
    }

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
            "A1:AA11"
        }

        val updateCount = mono {
            var count = 0

            val scoreMap: Map<String, Pair<String, Long>> = googleSheetsService.getUserScores()
        }

        return event.reply().withEphemeral(false)
            .withContent("Updated DB with scores from $updateCount users")
    }
}
