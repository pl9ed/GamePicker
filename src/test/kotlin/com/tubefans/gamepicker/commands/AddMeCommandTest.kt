package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.services.DiscordUserService
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createAddMeEvent
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class AddMeCommandTest {

    private val numericId = 123L
    private val missingNumericId = 111L
    private val id = Snowflake.of(numericId).toString()
    private val missingId = Snowflake.of(missingNumericId).toString()
    private val name = "name"
    private val username = "username"
    private val discordUser = DiscordUser(id, username, name)
    private val newUser = DiscordUser(missingId, username, name)

    private val discordUserService: DiscordUserService = mockk() {
        every { findById(id) } returns discordUser
        every { findById(missingId) } throws NoSuchElementException()
        every { save(discordUser) } returns discordUser
        every { save(newUser) } returns newUser
    }
    private val command = AddMeCommand(discordUserService)

    @Test
    fun `should add user to database`() {
        val event = createAddMeEvent(numericId, name, username)

        command.handle(event)

        verify {
            discordUserService.save(discordUser)
        }
    }

    @Test
    fun `should not add user if not present on sheet`() {
        val event = createAddMeEvent(missingNumericId, name, username)

        command.handle(event)
        verify(exactly = 0) {
            discordUserService.save(newUser)
        }
    }
}
