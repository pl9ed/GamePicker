package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.DiscordUser
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
import java.util.Optional

@Service
class ChatInputInteractionEventService @Autowired constructor(
    private val discordUserService: DiscordUserService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getCurrentChannel(event: ChatInputInteractionEvent): VoiceChannel? =
        event.interaction.member.get()
            .voiceState.block()
            ?.channel?.block()

    fun getUsersInChannel(voiceChannel: VoiceChannel): Mono<Set<DiscordUser>> = mono {
        voiceChannel.voiceStates.asFlow()
            .map {
                it.userId
            }.map { snowflake ->
                async {
                    try {
                        Optional.of(
                            discordUserService.findById(snowflake).also {
                                logger.info("Getting user with id {}", snowflake.toString())
                            }
                        )
                    } catch (e: NoSuchElementException) {
                        Optional.empty()
                    }
                }
            }.map {
                it.await()
            }.filter {
                it.isPresent
            }.map {
                it.get()
            }.map {
                it.also { user ->
                    logger.info("Fetched user {}", user)
                }
            }.toList().toSet()
    }
}
