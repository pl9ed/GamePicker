package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import java.util.Optional

interface DiscordUserRepository {
    fun existsById(id: String): Optional<Boolean>
    fun findAll(): Optional<Collection<DiscordUser>>
    fun findById(id: String): Optional<DiscordUser>
    fun findByName(name: String): Optional<DiscordUser>
    fun insert(user: DiscordUser): Optional<DiscordUser>
    fun save(user: DiscordUser): Optional<DiscordUser>

}
