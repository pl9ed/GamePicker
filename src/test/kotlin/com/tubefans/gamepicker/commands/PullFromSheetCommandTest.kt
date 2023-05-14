package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.services.UserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException

class PullFromSheetCommandTest {

    private val name = "NAME"
    private val missingName = "MISSING"

    private val mockSheet = mutableListOf(
        listOf("", "game1", "game2", "game3"),
        listOf("", "genre1", "genre1", "genre2"),
        listOf(name, "10", "10", "10"),
        listOf(missingName, "", "", "")
    )

    private val mockScores = mutableMapOf(
        name to listOf(Pair("game1", 10L), Pair("game2", 10L), Pair("game3", 10L)),
        missingName to listOf(Pair("game1", 0L), Pair("game2", 0L), Pair("game3", 0L))
    )

    private val templateResponse = "Updated DB with scores for %d users.%s"

    private val event: ChatInputInteractionEvent = mockk() {
        every { options } throws NoSuchElementException()
    }

    @Test
    fun `should only update valid users`() {
        val userService: UserService = mockk {
            every {
                updateGameForUserWithName(name, any(), any())
            } returns BotUser("id", "username", "name", mutableMapOf())
            every {
                updateGameForUserWithName(missingName, any(), any())
            } throws EmptyResultDataAccessException(1)
        }

        val googleSheetsService: GoogleSheetsService = mockk() {
            every { getSheet(any(), any()) } returns mockSheet
            every { mapToScores(mockSheet) } returns mockScores
        }

        val command = PullFromSheetCommand(
            userService,
            googleSheetsService
        )

        val message = command.updateDbFromSheet(event).block()

        assertEquals(
            String.format(templateResponse, 1, " Failed to update for $missingName"),
            message
        )
    }

    @Test
    fun `should handle empty sheet`() {
        val userService: UserService = mockk()

        val googleSheetsService: GoogleSheetsService = mockk() {
            every { getSheet(any(), any()) } returns emptyList()
            every { mapToScores(any()) } returns emptyMap()
        }

        val command = PullFromSheetCommand(
            userService,
            googleSheetsService
        )

        val message = command.updateDbFromSheet(event).block()

        verify { userService wasNot called }

        assertEquals(
            String.format(templateResponse, 0, ""),
            message
        )
    }
}
