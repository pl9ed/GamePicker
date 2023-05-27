package com.tubefans.gamepicker.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("user")
data class DiscordUser(
    @Id
    val discordId: String,
    var username: String?,
    var name: String?,
    @Transient
    var gameMap: MutableMap<String, Long>
) : Comparable<DiscordUser> {
    override fun compareTo(other: DiscordUser): Int {
        if (this.name != other.name) return compareValues(this.name, other.name)
        if (this.username != other.username) return compareValues(this.username, other.username)
        return this.discordId.compareTo(other.discordId)
    }
}
