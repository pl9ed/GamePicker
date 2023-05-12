package com.tubefans.gamepicker.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import org.springframework.stereotype.Component

@Component
class PingCommand : SlashCommand {

    override val name = "ping"

    override fun handle(event: ChatInputInteractionEvent) =
        event.reply()
            .withEphemeral(true)
            .withContent("Pong!")
            .withComponents(
                ActionRow.of(Button.success("test-id", "Pong"))
            )
}
