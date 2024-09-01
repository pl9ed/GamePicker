package com.tubefans.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

const val PROJECT_NAME = "GamePicker"
const val APP_NAME = PROJECT_NAME

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = ["com.tubefans.gamepicker", "com.tubefans.arbitragexiv"])
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
