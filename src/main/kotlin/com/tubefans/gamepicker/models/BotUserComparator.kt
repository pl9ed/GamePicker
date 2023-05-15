package com.tubefans.gamepicker.models

import com.tubefans.gamepicker.dto.BotUser

class BotUserComparator(val game: String) : Comparator<BotUser> {
    override fun compare(o1: BotUser, o2: BotUser): Int =
        compareValues(o1.gameMap[game], o2.gameMap[game])
}
