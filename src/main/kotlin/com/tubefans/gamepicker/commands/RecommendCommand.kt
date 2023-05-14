package com.tubefans.gamepicker.commands

import com.mongodb.internal.VisibleForTesting
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.EventService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RecommendCommand @Autowired constructor(
    private val eventService: EventService
) : SlashCommand {

    companion object {
        const val DEFAULT_GAME_COUNT = 3

        const val NO_GAMES_RESPONSE = "No games found. Are you in a voice channel?"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "recommend"

    override fun handle(event: ChatInputInteractionEvent) =
        event.deferReply()
            .then(
                eventService.getCurrentChannel(event)?.let {
                    eventService.getUsersInChannel(it)
                } ?: Mono.just(emptySet())
            ).map {
                logger.debug("Getting top games for {} users", it.size)
                GameScoreMap(it).getTopGames(DEFAULT_GAME_COUNT)
            }.flatMap {
                event.editReply(getReplyString(it))
            }.then()

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    fun getReplyString(games: List<String>) = if (games.isEmpty()) {
        NO_GAMES_RESPONSE
    } else {
        "Top games are: ${games.joinToString()}"
    }
}
