package com.tubefans.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

const val PROJECT_NAME = "GamePicker"
const val APP_NAME = PROJECT_NAME

@EnableReactiveMongoRepositories(basePackages = ["com.tubefans.arbitragexiv.repositories"])
@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = ["com.tubefans"])
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
