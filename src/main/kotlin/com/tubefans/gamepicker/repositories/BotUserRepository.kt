package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.BotUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BotUserRepository : MongoRepository<BotUser, String> {
    fun findOneByName(name: String): Optional<BotUser>
}
