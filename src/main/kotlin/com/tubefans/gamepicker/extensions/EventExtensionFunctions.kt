package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.commands.PullFromSheetCommand
import com.tubefans.gamepicker.services.GameService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent

fun ChatInputInteractionEvent.getGame(): String =
    this.options.first { it.name == GameService.GAME_NAME_KEY }.value.get().asString()

fun ChatInputInteractionEvent.getScore(): Long =
    this.options.first { it.name == GameService.GAME_SCORE_KEY }.value.get().asLong()

fun ChatInputInteractionEvent.getSheetId(): String = try {
    this.options
        .first { it.name == PullFromSheetCommand.SHEET_ID_NAME }
        .value
        .get()
        .asString()
} catch (e: NoSuchElementException) {
    PullFromSheetCommand.DEFAULT_SHEET
}

fun ChatInputInteractionEvent.getSheetRange(): String = try {
    this.options
        .first { it.name == PullFromSheetCommand.SHEET_RANGE_NAME }
        .value
        .get()
        .asString()
} catch (e: NoSuchElementException) {
    PullFromSheetCommand.DEFAULT_RANGE
}