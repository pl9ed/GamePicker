package com.tubefans.gamepicker.listeners

import com.tubefans.gamepicker.commands.SlashCommand
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
final class CommandListener
    @Autowired
    constructor(
        private val commands: List<SlashCommand>,
        private val client: GatewayDiscordClient,
    ) {
        private val logger = LogManager.getLogger(this::class.java)

        init {
            logger.info("Subscribing to commands: ${commands.joinToString { it.name }}")
            client.on(ChatInputInteractionEvent::class.java, this::handle)
                .doOnEach {
                    System.gc() // ¯\_(ツ)_/¯ blame Connor
                }
                .subscribe()
        }

        private fun handle(event: ChatInputInteractionEvent) = commands.first { it.name == event.commandName }.handle(event)
    }
