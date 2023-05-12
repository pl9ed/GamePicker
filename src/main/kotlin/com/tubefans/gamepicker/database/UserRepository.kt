package com.tubefans.gamepicker.database

import com.tubefans.gamepicker.dto.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String>
