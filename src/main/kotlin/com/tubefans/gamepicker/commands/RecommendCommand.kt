package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import com.tubefans.gamepicker.services.GameScoreMap
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
    private val botUserService: BotUserService
) : SlashCommand {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "recommend"

    override fun handle(event: ChatInputInteractionEvent) =
        event.deferReply()
            .then(
                getCurrentChannel(event)?.let {
                    getUsersInChannel(it)
                } ?: Mono.just(emptySet())
            ).map {
                logger.debug("Getting top games for {} users", it.size)
                GameScoreMap(it).getTopGames(3)
            }.flatMap {
                val replyString = if (it.isEmpty()) {
                    "No games found. Are you in a voice channel?"
                } else {
                    "Top games are: ${it.joinToString()}"
                }
                event.editReply(replyString)
            }.then()

    fun getCurrentChannel(event: ChatInputInteractionEvent): VoiceChannel? =
        event.interaction.member.get()
            .voiceState.block()
            ?.channel?.block()

    fun getUsersInChannel(voiceChannel: VoiceChannel): Mono<Set<BotUser>> = mono {
        voiceChannel.voiceStates.asFlow().map {
            it.userId
        }.map {
            async {
                logger.debug("Getting user with id {}", it.toString())
                botUserService.findById(it.toString()).get()
            }
        }.toList()
            .awaitAll()
            .toSet()
    }
}
