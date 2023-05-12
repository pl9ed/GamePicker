package com.tubefans.gamepicker.database

import com.tubefans.gamepicker.dto.BotUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<BotUser, String>
