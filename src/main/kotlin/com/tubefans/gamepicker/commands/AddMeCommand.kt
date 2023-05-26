package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.services.DiscordUserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrElse

@Component
class AddMeCommand @Autowired constructor(
    private val discordUserService: DiscordUserService
) : SlashCommand {

    companion object {
        const val NAME = "name"
        const val MESSAGE_TEMPLATE = "Updated name=%s, username=%s for user with id=%s"
        const val NO_NAME_PARAM_RESPONSE = "No name found. Did you forget to pass in your name?"
    }

    override val name = "add-me"

    @OptIn(ExperimentalStdlibApi::class)
    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val content: String = try {
            val nameField = event.options.first { it.name == NAME }.value.get().asString()
            val username = event.interaction.user.username
            val id = event.interaction.user.id.toString()
            val user = discordUserService.findById(id).getOrElse {
                DiscordUser(discordId = id, username = username, name = nameField)
            }
            discordUserService.save(user)

            String.format(
                MESSAGE_TEMPLATE,
                nameField,
                username,
                id
            )
        } catch (e: NoSuchElementException) {
            NO_NAME_PARAM_RESPONSE
        }
        return event.reply()
            .withEphemeral(true)
            .withContent(content)
    }
}
