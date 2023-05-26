package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.services.DiscordUserService
import com.tubefans.gamepicker.services.GoogleSheetsService
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createPullFromSheetEvent
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException

class PullFromSheetCommandTest {

    private val validSheet = "valid"
    private val emptySheet = "empty"
    private val missingSheet = "missing-sheet"

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

    private val discordUserService: DiscordUserService = mockk {
        every {
            updateGameForUserWithName(name, any(), any())
        } returns DiscordUser("id", "username", "name", mutableMapOf())
        every {
            updateGameForUserWithName(missingName, any(), any())
        } throws NoSuchElementException()
    }

    private val googleSheetsService: GoogleSheetsService = mockk() {
        every { getSheet(validSheet, any()) } returns mockSheet
        every { getSheet(emptySheet, any()) } returns emptyList()
        every { getSheet(missingSheet, any()) } throws IOException()
        every { mapToScores(mockSheet) } returns mockScores
        every { mapToScores(emptyList()) } returns emptyMap()
    }

    private val command = PullFromSheetCommand(discordUserService, googleSheetsService)

    @Test
    fun `should only update valid users`() {
        val event = createPullFromSheetEvent(validSheet, "range")
        val message = command.updateDbFromSheet(event).block()

        assertEquals(
            String.format(templateResponse, 1, " Failed to update for $missingName"),
            message
        )
    }

    @Test
    fun `should handle empty sheet`() {
        val event = createPullFromSheetEvent(emptySheet, "range")
        val message = command.updateDbFromSheet(event).block()

        verify { discordUserService wasNot called }

        assertEquals(
            String.format(templateResponse, 0, ""),
            message
        )
    }

    @Test
    fun `should handle missing sheet`() {
        val event = createPullFromSheetEvent(missingSheet, "range")
        val message = command.updateDbFromSheet(event).block()

        assertEquals(
            String.format(templateResponse, 0, ""),
            message
        )
    }
}
