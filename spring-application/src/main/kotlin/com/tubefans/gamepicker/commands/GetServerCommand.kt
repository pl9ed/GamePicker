package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.extensions.getStringOption
import com.tubefans.gamepicker.extensions.toDisplayString
import com.tubefans.gamepicker.services.EC2Service
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetServerCommand
    @Autowired
    constructor(
        private val ec2Service: EC2Service,
    ) : SlashCommand {
        companion object {
            const val NAME_KEY = "name"
            const val ERROR_TEMPLATE = "Failed to get instance status for %s: %s, %s"
        }

        private val logger = LoggerFactory.getLogger(this::class.java)
        override val name = "get-server"

        override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
            val serverName = event.getStringOption(NAME_KEY)
            return event
                .deferReply()
                .then(ec2Service.getInstanceStatus(serverName))
                .map { status ->
                    status.toDisplayString(serverName)
                }.onErrorResume { e ->
                    logger.error("Uncaught exception getting instance status", e)
                    Mono.just(String.format(ERROR_TEMPLATE, serverName, e::class.simpleName, e.message))
                }.flatMap { message ->
                    event.editReply(message)
                }.then()
        }
    }
