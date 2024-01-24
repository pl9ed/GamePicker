package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.EC2Service
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

class StartServerCommand @Autowired constructor(
    eC2Service: EC2Service
) : SlashCommand {
    override val name = "start-server"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        TODO("Not yet implemented")
    }

}