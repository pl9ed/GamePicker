package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.UserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InitCommand @Autowired constructor(
    private val userService: UserService,
    private val googleSheetsService: GoogleSheetsService
) : SlashCommand {

    override val name = "init"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        TODO("Not yet implemented")
    }
}