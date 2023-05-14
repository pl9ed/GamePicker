package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.dto.BotUser

fun BotUser.updateGame(game: String, score: Long): BotUser = this.apply {
    gameMap[game] = score
}