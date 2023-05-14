package com.tubefans.gamepicker.testlibrary.event

import com.tubefans.gamepicker.commands.PullFromSheetCommand.Companion.SHEET_ID_NAME
import com.tubefans.gamepicker.commands.PullFromSheetCommand.Companion.SHEET_RANGE_NAME
import com.tubefans.gamepicker.services.GameService.Keys.GAME_NAME_KEY
import com.tubefans.gamepicker.services.GameService.Keys.GAME_SCORE_KEY
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.entity.User
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

object TestEventLibrary {

    fun createGatewayDiscordClient(): GatewayDiscordClient = mockk {
        every { rest() } returns mockk()
    }

    fun createPullFromSheetEvent(id: String, range: String): ChatInputInteractionEvent = mockk {
        every { options } returns listOf(
            stringOptionOf(SHEET_ID_NAME, id),
            stringOptionOf(SHEET_RANGE_NAME, range)
        )
    }

    fun createUpdateGameEvent(user: User, game: String, score: Long) = ChatInputInteractionEvent(
        createGatewayDiscordClient(),
        mockk(),
        mockk {
            every { commandInteraction } returns Optional.of(
                mockk {
                    every { options } returns listOf(
                        stringOptionOf(GAME_NAME_KEY, game),
                        longOptionOf(GAME_SCORE_KEY, score)
                    )
                    every { getUser() } returns user
                    every { data } returns mockk() {
                        every { applicationId() } returns mockk() {
                            every { asLong() } returns 0L
                        }
                    }
                }
            )
        }
    )

    private fun stringOptionOf(name: String, value: String): ApplicationCommandInteractionOption = mockk {
        every { getName() } returns name
        every { getValue() } returns Optional.of(
            mockk {
                every { raw } returns value
                every { asString() } returns value
            }
        )
    }

    private fun longOptionOf(name: String, value: Long): ApplicationCommandInteractionOption = mockk {
        every { getName() } returns name
        every { getValue() } returns Optional.of(
            mockk {
                every { raw } returns value.toString()
                every { asLong() } returns value
            }
        )
    }
}
