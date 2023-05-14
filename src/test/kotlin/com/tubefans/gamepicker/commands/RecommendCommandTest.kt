package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.EventService
import io.mockk.mockk
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val eventService: EventService = mockk()
    private val command = RecommendCommand(eventService)

}