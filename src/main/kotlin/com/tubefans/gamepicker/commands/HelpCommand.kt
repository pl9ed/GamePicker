package com.tubefans.gamepicker.commands

import com.google.common.annotations.VisibleForTesting
import com.tubefans.gamepicker.utils.CommandStringFormatter.toRowString
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class HelpCommand @Autowired constructor(
    private val commands: List<ApplicationCommandRequest>
) : SlashCommand {

    companion object {
        const val GENERIC_HELP_HEADER = "Use /help {cmd} for more details"
    }

    override val name: String = "help"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val response = if (event.options.isEmpty()) {
            getGenericHelpMessage()
        } else {
            "NOT YET IMPLEMENTED"
        }
        return event.reply()
            .withEphemeral(true)
            .withContent(response)
    }

    @VisibleForTesting
    fun getGenericHelpMessage(): String {
        val message = StringBuilder("$GENERIC_HELP_HEADER\n")
        commands.forEach {
            message.append("${it.toRowString()}\n")
        }
        return message.trim().toString()
    }
}
