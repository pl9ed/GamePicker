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

        const val DEFAULT_RANGE = "A1:AA11"
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
            DEFAULT_RANGE
        }

        val updateCount = mono {
            googleSheetsService.apply {
                val scoreMap: Map<String, List<Pair<String, Long>>> = mapToScores(getSheet(sheetId, range))
                return@mono scoreMap.size
            }
        }.block()

        return event.reply().withEphemeral(false)
            .withContent("Updated DB with scores from $updateCount users")
    }
}
