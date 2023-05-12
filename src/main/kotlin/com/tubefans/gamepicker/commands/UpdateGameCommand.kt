package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.database.UserRepository
import com.tubefans.gamepicker.dto.User
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UpdateGameCommand : SlashCommand {

    @Autowired
    lateinit var gateway: GatewayDiscordClient

    @Autowired
    lateinit var userRepository: UserRepository

    override val name = "update"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        var returnedUser: User? = null

        event.interaction.user.let { user ->
            // TODO: implement actual update
            returnedUser = userRepository.save(User(user.id.toString(), user.username, mutableMapOf()))
        }

        return event.reply()
            .withEphemeral(true)
            .withContent(
                returnedUser?.toString() ?: "Null user"
            )
    }
}
