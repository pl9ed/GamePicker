package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.extensions.getGame
import com.tubefans.gamepicker.extensions.getScore
import com.tubefans.gamepicker.services.BotUserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UpdateGameCommand @Autowired constructor(
    private val botUserService: BotUserService
) : SlashCommand {

    override val name = "update"

    override fun handle(event: ChatInputInteractionEvent): InteractionApplicationCommandCallbackReplyMono {
        var discordUserResponse: DiscordUser

        val game = event.getGame()
        val score = event.getScore()

        event.interaction.user.let { user ->
            discordUserResponse = try {
                botUserService.updateGameForUserWithId(user.id.toString(), game, score)
            } catch (e: NoSuchElementException) {
                val newUser = DiscordUser(user.id.toString(), user.username, "", mutableMapOf(game to score))
                botUserService.insert(newUser)
            }
        }

        return event.reply()
            .withEphemeral(true)
            .withContent(
                "Updated $game with score: $score for ${discordUserResponse.username}."
            )
    }
}
