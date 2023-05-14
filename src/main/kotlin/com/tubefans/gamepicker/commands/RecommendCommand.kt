package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.EventService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.channel.VoiceChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
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
                val replyString = if (it.isEmpty()) {
                    "No games found. Are you in a voice channel?"
                } else {
                    "Top games are: ${it.joinToString()}"
                }
                event.editReply(replyString)
            }.then()

}
