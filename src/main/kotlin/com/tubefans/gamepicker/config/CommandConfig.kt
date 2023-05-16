package com.tubefans.gamepicker.config

import discord4j.common.JacksonResources
import discord4j.discordjson.json.ApplicationCommandRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

@Configuration
class CommandConfig {

    @Bean
    fun getSlashCommands(): MutableList<ApplicationCommandRequest> {
        val matcher = PathMatchingResourcePatternResolver()
        val d4jMapper = JacksonResources.create()

        // Get our commands json from resources as command data
        val commands: MutableList<ApplicationCommandRequest> = ArrayList()
        for (resource in matcher.getResources("commands/*.json")) {
            val request = d4jMapper.objectMapper
                .readValue(resource.inputStream, ApplicationCommandRequest::class.java)
            commands.add(request)
        }
        return commands
    }
}
