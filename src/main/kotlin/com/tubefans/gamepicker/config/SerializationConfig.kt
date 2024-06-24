package com.tubefans.gamepicker.config

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.common.JacksonResources
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

@Configuration
class SerializationConfig {
    @Bean
    fun pathMatcher(): PathMatchingResourcePatternResolver = PathMatchingResourcePatternResolver()

    @Bean
    fun objectMapper(): ObjectMapper = JacksonResources.create().objectMapper
}
