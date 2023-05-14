package com.tubefans.gamepicker.testlibrary.event

import com.tubefans.gamepicker.commands.PullFromSheetCommand.Companion.SHEET_ID_NAME
import com.tubefans.gamepicker.commands.PullFromSheetCommand.Companion.SHEET_RANGE_NAME
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

object TestEventLibrary {

    fun createPullFromSheetEvent(id: String, range: String): ChatInputInteractionEvent = mockk {
        every { options } returns listOf(
            stringOptionOf(SHEET_ID_NAME, id),
            stringOptionOf(SHEET_RANGE_NAME, range)
        )
    }

    private fun stringOptionOf(name: String, value: String): ApplicationCommandInteractionOption = mockk {
        every { getName() } returns name
        every { getValue() } returns Optional.of(
            mockk {
                every { raw } returns value
                every { asString() } returns value
            }
        )
    }
}
