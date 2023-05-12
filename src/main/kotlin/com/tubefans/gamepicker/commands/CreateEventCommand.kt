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

    @Autowired
    lateinit var gateway: GatewayDiscordClient

    override val name = "create_event"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        // TODO: implement
        var channelName: String? = null
        val usersInChannel = mutableListOf<String>()

        mono {
            gateway.guilds.filter { it.id == Snowflake.of(1106152112557146152) }
                .flatMap { guild ->
                    guild.channels.filter { channel ->
                        channel.id == Snowflake.of(1106152113001730091)
                    }
                }.flatMap {
                    channelName = it.name
                    (it as VoiceChannel).voiceStates
                }.flatMap {
                    it.user
                }.map {
                    it.username
                }.subscribe {
                    usersInChannel.add(it)
                }
        }.block()

        return event.reply().withEphemeral(false).withContent("$channelName has ${usersInChannel.joinToString()}")
    }
}
