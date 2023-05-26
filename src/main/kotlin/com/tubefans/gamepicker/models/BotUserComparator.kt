package com.tubefans.gamepicker.models

import com.tubefans.gamepicker.dto.DiscordUser

class BotUserComparator(val game: String) : Comparator<DiscordUser> {
    override fun compare(o1: DiscordUser, o2: DiscordUser): Int {
        if (o1.gameMap[game] != o2.gameMap[game]) return compareValues(o1.gameMap[game], o2.gameMap[game])
        return o1.compareTo(o2)
    }
}
