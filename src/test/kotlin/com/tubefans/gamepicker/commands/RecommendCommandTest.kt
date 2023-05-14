package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.RecommendCommand.Companion.NO_GAMES_RESPONSE
import com.tubefans.gamepicker.services.EventService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RecommendCommandTest {

    private val eventService: EventService = mockk()
    private val command = RecommendCommand(eventService)

    private val responseTemplate = "Top games are: %s"

    @Test
    fun `should generate top games list`() {
        val list = listOf("a", "b", "c")

        assertEquals(
            String.format(responseTemplate, list.joinToString()),
            command.getReplyString(list)
        )
    }

    @Test
    fun `should respond with separate string when no games are found`() {
        assertEquals(NO_GAMES_RESPONSE, command.getReplyString(emptyList()))
    }
}
