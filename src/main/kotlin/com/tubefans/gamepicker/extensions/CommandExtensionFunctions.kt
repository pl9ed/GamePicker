package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.dto.Game
import com.tubefans.gamepicker.services.GameService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent

fun ChatInputInteractionEvent.getGame(): Game =
    Game(
        this.options.first { it.name == GameService.GAME_NAME_KEY }.value.get().asString(),
        this.options.first { it.name == GameService.GAME_GENRE_KEY }.value.get().asString()
    )

fun ChatInputInteractionEvent.getScore(): Long =
    this.options.first { it.name == GameService.GAME_SCORE_KEY }.value.get().asLong()
