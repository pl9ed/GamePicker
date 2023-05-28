package com.tubefans.gamepicker.dto

data class DiscordUser(
    val discordId: String,
    var username: String?,
    var name: String?,
    @Transient
    var gameMap: MutableMap<String, Long> = mutableMapOf()
) : Comparable<DiscordUser> {
    override fun compareTo(other: DiscordUser): Int {
        if (this.name != other.name) return compareValues(this.name, other.name)
        if (this.username != other.username) return compareValues(this.username, other.username)
        return this.discordId.compareTo(other.discordId)
    }
}
