package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.services.GameService
import com.tubefans.gamepicker.services.GameService.Keys.GAME_NAME_KEY
import com.tubefans.gamepicker.services.GameService.Keys.GAME_SCORE_KEY
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent

fun ChatInputInteractionEvent.getGame(): String = getStringOption(GAME_NAME_KEY)

fun ChatInputInteractionEvent.getScore(): Long =
    this.options.first { it.name == GAME_SCORE_KEY }.value.get().asLong()

fun ChatInputInteractionEvent.getStringOption(name: String): String =
    this.options.first { it.name == name }.value.get().asString()
