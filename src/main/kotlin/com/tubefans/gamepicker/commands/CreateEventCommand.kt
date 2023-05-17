package com.tubefans.gamepicker.commands

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CreateEventCommand : SlashCommand {

    override val name = "create-event"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        // TODO: implement
        return event.reply().withEphemeral(false).withContent("Events still under development")
    }
}
