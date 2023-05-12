package com.tubefans.gamepicker.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono

interface SlashCommand {

    val name: String
    fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono
}
