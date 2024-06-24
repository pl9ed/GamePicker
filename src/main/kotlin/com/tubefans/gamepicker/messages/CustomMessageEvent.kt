package com.tubefans.gamepicker.messages

import discord4j.core.event.domain.message.MessageCreateEvent
import reactor.core.publisher.Mono

interface CustomMessageEvent {
    fun getMessageString(): String

    fun shouldRespond(message: MessageCreateEvent): Boolean

    fun handle(event: MessageCreateEvent): Mono<Void>
}
