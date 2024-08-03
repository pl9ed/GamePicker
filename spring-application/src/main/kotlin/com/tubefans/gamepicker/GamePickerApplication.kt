package com.tubefans.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val PROJECT_NAME = "GamePicker"
const val APP_NAME = PROJECT_NAME

@SpringBootApplication
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
