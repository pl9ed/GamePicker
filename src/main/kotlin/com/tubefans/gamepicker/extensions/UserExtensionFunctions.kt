package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.dto.DiscordUser

fun DiscordUser.updateGame(game: String, score: Long): DiscordUser = this.apply {
    gameMap[game] = score
}
