package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.DiscordUserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AddMeCommand @Autowired constructor(
    private val discordUserService: DiscordUserService
) : SlashCommand {

    companion object {
        const val NAME = "name"
        const val MESSAGE_TEMPLATE = "Updated name=%s, username=%s for user with id=%s"
        val noMatchingNameTemplate = """No name matching %s found. You need to add yourself to the sheet 
            first before updating your name or username.
            """.trimIndent()
    }

    override val name = "add-me"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val nameField = event.options.first { it.name == NAME }.value.get().asString()

        val content: String = try {
            val username = event.interaction.user.username
            val id = event.interaction.user.id.toString()
            val user = discordUserService.findById(id)
            user.name = nameField.uppercase()
            user.username = username
            discordUserService.save(user)

            String.format(
                MESSAGE_TEMPLATE,
                nameField,
                username,
                id
            )
        } catch (e: NoSuchElementException) {
            String.format(
                noMatchingNameTemplate,
                nameField
            )
        }
        return event.reply()
            .withEphemeral(true)
            .withContent(content)
    }
}
