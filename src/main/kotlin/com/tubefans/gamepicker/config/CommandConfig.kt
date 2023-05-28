package com.tubefans.gamepicker.config

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.common.JacksonResources
import discord4j.discordjson.json.ApplicationCommandRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

@Configuration
class CommandConfig @Autowired constructor(
    private val matcher: PathMatchingResourcePatternResolver,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun getSlashCommands(): List<ApplicationCommandRequest> {
        // Get our commands json from resources as command data
        val commands: MutableList<ApplicationCommandRequest> = ArrayList()
        for (resource in matcher.getResources("commands/*.json")) {
            val request = objectMapper
                .readValue(resource.inputStream, ApplicationCommandRequest::class.java)
            logger.info("Added ${request.name()} to commands")
            commands.add(request)
        }
        return commands
    }
}
