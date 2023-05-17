package com.tubefans.gamepicker.commands

import com.mongodb.internal.VisibleForTesting
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class HelpCommand @Autowired constructor(
    private val commands: List<ApplicationCommandRequest>
) : SlashCommand {
    override val name: String = "help"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val commandList = getCommandList()
        val response = StringBuilder()
        commandList.forEach {
            response.append(it)
        }
        response.trim()
        return event.reply()
            .withEphemeral(true)
            .withContent(response.toString())
    }

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    fun getCommandList(): List<String> = commands.map { "/${it.name()} - ${it.description()}" }
}
