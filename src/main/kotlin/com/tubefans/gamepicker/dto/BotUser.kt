package com.tubefans.gamepicker.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("user")
data class BotUser(
    @Id
    val discordId: String,
    val username: String?,
    val name: String?,
    val gameMap: MutableMap<String, Long> = mutableMapOf()
) : Comparable<BotUser> {
    override fun compareTo(other: BotUser): Int {
        if (this.name != other.name) return compareValues(this.name, other.name)
        if (this.username != other.username) return compareValues(this.username, other.username)
        return this.discordId.compareTo(other.discordId)
    }
}
