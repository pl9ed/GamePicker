package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DiscordUserRepository : MongoRepository<DiscordUser, String> {
    fun findOneByName(name: String): Optional<DiscordUser>
}
