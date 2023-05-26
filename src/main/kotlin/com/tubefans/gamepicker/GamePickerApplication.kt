package com.tubefans.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

const val PROJECT_NAME = "GamePicker"
const val APP_NAME = PROJECT_NAME

@EnableMongoRepositories
@SpringBootApplication
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
