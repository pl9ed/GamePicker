package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import java.util.Optional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SheetsDiscordUserRepository @Autowired constructor(
    private val userCache: UserCache
) : DiscordUserRepository {

    override fun findOneByName(name: String): Optional<DiscordUser> = try {
        Optional.of(userCache.users.first { it.name == name })
    } catch (e: NoSuchElementException) {
        Optional.empty()
    }
}
