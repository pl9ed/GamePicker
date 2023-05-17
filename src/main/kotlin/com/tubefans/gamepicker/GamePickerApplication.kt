package com.tubefans.gamepicker

import discord4j.common.util.Snowflake
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

val BOT_TOKEN = System.getenv()["BOT_TOKEN"] ?: ""
val GOOGLE_API_KEY = System.getenv()["GOOGLE_API_KEY"] ?: ""
val OAUTH_CLIENT_ID = System.getenv()["OAUTH_CLIENT_ID"] ?: ""
val OAUTH_CLIENT_SECRET = System.getenv()["OAUTH_CLIENT_SECRET"] ?: ""

const val APP_NAME = "GamePicker"

@EnableMongoRepositories
@SpringBootApplication
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
