package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.BotUserService
import io.mockk.mockk
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val botUserService: BotUserService = mockk()
    private val command = RecommendCommand(botUserService)

    @Test
    fun `should get voice channel for user`() {
        command.getCurrentChannel(event)
    }

}