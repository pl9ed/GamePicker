package com.tubefans.gamepicker.models

import com.tubefans.gamepicker.dto.BotUser

class BotUserComparator(val game: String) : Comparator<BotUser> {
    override fun compare(o1: BotUser, o2: BotUser): Int {
        if (o1.gameMap[game] != o2.gameMap[game]) return compareValues(o1.gameMap[game], o2.gameMap[game])
        return o1.compareTo(o2)
    }
}
