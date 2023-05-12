package com.tubefans.gamepicker.config

import discord4j.discordjson.json.ApplicationCommandRequest

object CommandConfig {
    private val ping = ApplicationCommandRequest.builder().name("ping").description("Ping Pong!").build()
    private val createEvent = ApplicationCommandRequest.builder().name("create").description("Create").build()

    val commands = listOf<ApplicationCommandRequest>(
        ping,
        createEvent
    )
}
