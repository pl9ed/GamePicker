package com.tubefans.gamepicker.listeners

import com.tubefans.gamepicker.commands.SlashCommand
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
final class CommandListener @Autowired constructor(
    private val commands: List<SlashCommand>,
    private val client: GatewayDiscordClient
) {

    init {
        client.on(ChatInputInteractionEvent::class.java, this::handle)
            .subscribe()
    }

    private fun handle(event: ChatInputInteractionEvent) =
        commands.first { it.name == event.commandName }.handle(event)
}
