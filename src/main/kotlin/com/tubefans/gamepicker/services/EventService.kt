package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.channel.VoiceChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EventService @Autowired constructor(
    private val botUserService: BotUserService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getCurrentChannel(event: ChatInputInteractionEvent): VoiceChannel? =
        event.interaction.member.get()
            .voiceState.block()
            ?.channel?.block()

    fun getUsersInChannel(voiceChannel: VoiceChannel): Mono<Set<BotUser>> = mono {
        voiceChannel.voiceStates.asFlow()
            .map {
                it.userId
            }.map { snowflake ->
                async {
                    botUserService.findById(snowflake.toString()).also {
                        logger.info("Getting user with id {}", snowflake.toString())
                    }
                }
            }.map {
                it.await()
            }.filter {
                it.isPresent
            }.map {
                it.get().also { user ->
                    logger.info("Fetched user {}", user)
                }
            }.toList().toSet()
    }
}
