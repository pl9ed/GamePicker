package com.tubefans.gamepicker.config

import com.tubefans.gamepicker.repositories.DiscordUserRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {

    @Bean
    fun discordUserRepository() = DiscordUserRepositoryImpl()
}
