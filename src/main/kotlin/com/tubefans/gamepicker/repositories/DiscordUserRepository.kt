package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DiscordUserRepository {
    fun findOneByName(name: String): Optional<DiscordUser>
}
