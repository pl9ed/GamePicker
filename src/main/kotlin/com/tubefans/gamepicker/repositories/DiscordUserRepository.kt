package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import java.util.Optional
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DiscordUserRepository : MongoRepository<DiscordUser, String> {
    fun findOneByName(name: String): Optional<DiscordUser>

}
