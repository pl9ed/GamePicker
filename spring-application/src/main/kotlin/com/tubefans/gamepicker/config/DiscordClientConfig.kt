package com.tubefans.gamepicker.config

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.gateway.intent.IntentSet
import discord4j.rest.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordClientConfig
    @Autowired
    constructor(
        private val secretManagerServiceClient: SecretManagerServiceClient,
    ) {
        companion object {
            const val BOT_TOKEN_KEY = "projects/891049573637/secrets/BOT_TOKEN/versions/latest"
        }

        @Bean
        fun gatewayDiscordClient(): GatewayDiscordClient {
            // TODO: set up environment at some point
            val localBuild = System.getenv("local").toBoolean()
            val token =
                if (localBuild) {
                    System.getenv("BOT_TOKEN")
                } else {
                    secretManagerServiceClient.accessSecretVersion(BOT_TOKEN_KEY)
                        .payload
                        .data
                        .toStringUtf8()
                }

            return DiscordClient.create(token)
                .gateway()
                .setInitialPresence {
                    ClientPresence.online(ClientActivity.listening("to /commands"))
                }
                .setEnabledIntents(IntentSet.all())
                .login()
                .block()!!
        }

        @Bean
        fun discordRestClient(client: GatewayDiscordClient): RestClient = client.restClient
    }
