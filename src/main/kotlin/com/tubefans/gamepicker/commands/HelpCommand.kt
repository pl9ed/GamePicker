package com.tubefans.gamepicker.commands

import com.google.common.annotations.VisibleForTesting
import com.tubefans.gamepicker.extensions.getStringOption
import com.tubefans.gamepicker.utils.CommandStringFormatter.toHelpString
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
        const val COMMAND_PARAM_KEY = "command"
        const val COMMAND_NOT_FOUND_TEMPLATE = "Command '%s' not found"
    }

    override val name: String = "help"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        if (event.options.isEmpty()) {
            return event.reply()
                .withEphemeral(true)
                .withContent(getGenericHelpMessage())
        }

        val response = getResponseString(event.getStringOption(COMMAND_PARAM_KEY))

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

    @VisibleForTesting
    fun getResponseString(commandName: String): String = try {
        commands.first {
            it.name().lowercase() == commandName.lowercase()
        }.toHelpString()
    } catch (e: NoSuchElementException) {
        String.format(
            COMMAND_NOT_FOUND_TEMPLATE,
            commandName
        )
    }
}
