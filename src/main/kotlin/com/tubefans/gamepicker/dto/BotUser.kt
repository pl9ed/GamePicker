package com.tubefans.gamepicker.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("user")
data class BotUser(
    @Id
    val discordId: String,
    val username: String,
    val gameMap: MutableMap<Game, Long>
)
