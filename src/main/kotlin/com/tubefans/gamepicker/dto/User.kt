package com.tubefans.gamepicker.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("user")
data class User(
    @Id
    val discordId: String,
    val username: String,
    val gameMap: MutableMap<Game, Int>
)
