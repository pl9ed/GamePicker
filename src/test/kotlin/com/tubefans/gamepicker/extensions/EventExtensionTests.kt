package com.tubefans.gamepicker.extensions

import com.tubefans.gamepicker.services.GameService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class EventExtensionTests {

    private val gameName = "test-game"
    private val gameScore = 10L

    private val gameNameOption: ApplicationCommandInteractionOption = mockk {
        every { name } returns GameService.GAME_NAME_KEY
        every { value.get() } returns mockk {
            every { asString() } returns gameName
        }
    }
    private val gameScoreOptions: ApplicationCommandInteractionOption = mockk {
        every { name } returns GameService.GAME_SCORE_KEY
        every { value.get() } returns mockk {
            every { asLong() } returns gameScore
        }
    }

    private val mockOptions: List<ApplicationCommandInteractionOption> = mutableListOf(
        gameNameOption,
        gameScoreOptions
    )

    private val event: ChatInputInteractionEvent = mockk() {
        every { options } returns mockOptions
    }


    @Test
    fun `should get first game from options`() {
        Assertions.assertEquals(gameName, event.getGame())
    }

    @Test
    fun `should get score from options`() {
        Assertions.assertEquals(gameScore, event.getScore())
    }

    @Test
    fun `should throw on empty options`() {
        val emptyEvent: ChatInputInteractionEvent = mockk() {
            every { options } returns emptyList()
        }

        assertThrows(NoSuchElementException::class.java) {
            Assertions.assertEquals(null, emptyEvent.getGame())
        }

        assertThrows(NoSuchElementException::class.java) {
            Assertions.assertEquals(null, emptyEvent.getScore())
        }
    }

}
