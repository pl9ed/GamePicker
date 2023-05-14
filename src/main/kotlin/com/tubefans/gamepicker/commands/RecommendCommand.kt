package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import com.tubefans.gamepicker.services.GameScoreMap
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.channel.VoiceChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

class RecommendCommand @Autowired constructor(
    private val botUserService: BotUserService,
    private val gateway: GatewayDiscordClient
) : SlashCommand {

    override val name = "recommend"

    override fun handle(event: ChatInputInteractionEvent) =
        event.deferReply()
            .then(
                getCurrentChannel(event)?.let {
                    getUsersInChannel(it)
                } ?: Mono.empty()
            ).map {
                GameScoreMap(it).getTopGames(3)
            }.map {
                event.editReply("Top games are: ${it.joinToString()}")
            }.then()

    fun test(event: ChatInputInteractionEvent) = mono {
        event.interaction.member.map {
            it.voiceState.blockOptional().get()
        }.map {
            it.channel
        }
    }

    fun getCurrentChannel(event: ChatInputInteractionEvent): VoiceChannel? =
        event.interaction.member.get()
            .voiceState.block()
            ?.channel?.block()

    fun getUsersInChannel(voiceChannel: VoiceChannel): Mono<Set<BotUser>> = mono {
        voiceChannel.voiceStates.asFlow().map {
            it.userId
        }.map {
            async { botUserService.findById(it.toString()).get() }
        }.toList()
            .awaitAll()
            .toSet()
    }


    fun getUsers(event: ChatInputInteractionEvent) {
        event.interaction.user
    }
}