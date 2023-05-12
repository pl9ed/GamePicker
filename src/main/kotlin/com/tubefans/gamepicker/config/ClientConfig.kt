package com.tubefans.gamepicker.config

import com.tubefans.gamepicker.BOT_TOKEN
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.gateway.intent.IntentSet
import discord4j.rest.RestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientConfig {

    @Bean
    fun gatewayDiscordClient(): GatewayDiscordClient =
        DiscordClient.create(BOT_TOKEN)
            .gateway()
            .setInitialPresence {
                ClientPresence.online(ClientActivity.listening("to /commands"))
            }
            .setEnabledIntents(IntentSet.all())
            .login()
            .block()!!

    @Bean
    fun discordRestClient(client: GatewayDiscordClient): RestClient =
        client.restClient
}
