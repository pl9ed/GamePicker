package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import java.util.Optional

interface DiscordUserRepository {
    fun findOneByName(name: String): Optional<DiscordUser>
}
