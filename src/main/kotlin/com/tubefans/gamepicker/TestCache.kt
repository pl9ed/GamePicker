package com.tubefans.gamepicker

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestCache @Autowired constructor(
    private val discordUserRepository: DiscordUserRepository
) {

    private val users = mutableSetOf<DiscordUser>()

    @PostConstruct
    fun init() {
        users.add(discordUserRepository.findOneByName("ANDREW").get())
    }
}
