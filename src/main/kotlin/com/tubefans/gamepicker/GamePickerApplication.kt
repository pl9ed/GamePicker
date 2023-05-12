package com.tubefans.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

const val TEST_TOKEN = "MTEwNjEyNjY2MjkzMzk1NDU4MQ.GrV-zz.S7bHFH9wSIcVDx38F0vueBEtnvpESGWqGr7NAQ"

@EnableMongoRepositories
@SpringBootApplication
class GamePickerApplication

fun main(args: Array<String>) {
    runApplication<GamePickerApplication>(*args)
}
