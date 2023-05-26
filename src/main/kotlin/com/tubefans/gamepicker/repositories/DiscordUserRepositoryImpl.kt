package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.models.GameScoreMap
import java.util.Optional
import org.springframework.stereotype.Component

@Component
class DiscordUserRepositoryImpl : DiscordUserRepository {
    override fun existsById(id: String): Optional<Boolean> {
        TODO("Not yet implemented")
    }

    override fun findAll(): Optional<Collection<DiscordUser>> {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Optional<DiscordUser> {
        TODO("Not yet implemented")
    }

    override fun findByName(name: String): Optional<DiscordUser> {

    }

    override fun insert(user: DiscordUser): Optional<DiscordUser> {
        TODO("Not yet implemented")
    }

    override fun save(user: DiscordUser): Optional<DiscordUser> {
        TODO("Not yet implemented")
    }

    override fun delete(user: DiscordUser): Optional<Boolean> {
        TODO("Not yet implemented")
    }

}
