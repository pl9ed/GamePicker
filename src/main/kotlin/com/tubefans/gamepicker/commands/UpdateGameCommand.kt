package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.extensions.getGame
import com.tubefans.gamepicker.extensions.getScore
import com.tubefans.gamepicker.services.UserService
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Component

@Component
class UpdateGameCommand : SlashCommand {

    @Autowired
    lateinit var gateway: GatewayDiscordClient

    @Autowired
    lateinit var userService: UserService

    override val name = "update"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        var botUserResponse: BotUser

        val game = event.getGame()
        val score = event.getScore()

        event.interaction.user.let { user ->
            botUserResponse = try {
                userService.updateGameForUserWithId(user.id.toString(), game, score)
            } catch (e: EmptyResultDataAccessException) {
                val newUser = BotUser(user.id.toString(), user.username, "", mutableMapOf(game to score))
                userService.insertUser(newUser)
            }
        }

        val responseString = StringBuilder("Updated $game with score: $score for ${botUserResponse.username}. ")

        if (botUserResponse.name.isNullOrBlank()) {
            responseString.append(" Note: you do not have a name associated.") // TODO: add update features via cmd
        }

        return event.reply()
            .withEphemeral(true)
            .withContent(
                responseString.toString()
            )
    }
}
