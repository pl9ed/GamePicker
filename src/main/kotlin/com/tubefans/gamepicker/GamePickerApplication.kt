package com.tubefans.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

val TEST_TOKEN = System.getenv()["TEST_BOT_TOKEN"] ?: ""

@EnableMongoRepositories
@SpringBootApplication
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
