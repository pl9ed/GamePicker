package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface DiscordUserRepository : MongoRepository<DiscordUser, String> {
    fun findOneByName(name: String): Optional<DiscordUser>
}
