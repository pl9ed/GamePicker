package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createAddMeEvent
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.Optional

class AddMeCommandTest {

    private val numericId = 123L
    private val missingNumericId = 111L
    private val id = Snowflake.of(numericId).toString()
    private val missingId = Snowflake.of(missingNumericId).toString()
    private val name = "name"
    private val username = "username"
    private val botUser = BotUser(id, username, name)
    private val newUser = BotUser(missingId, username, name)

    private val botUserService: BotUserService = mockk() {
        every { findById(id) } returns Optional.of(botUser)
        every { findById(missingId) } returns Optional.empty()
        every { save(botUser) } returns botUser
        every { save(newUser) } returns newUser
    }
    private val command = AddMeCommand(botUserService)

    @Test
    fun `should add user to database`() {
        val event = createAddMeEvent(numericId, name, username)

        command.handle(event)

        verify {
            botUserService.save(botUser)
        }
    }

    @Test
    fun `should add new user to database`() {
        val event = createAddMeEvent(missingNumericId, name, username)

        command.handle(event)

        verify {
            botUserService.save(newUser)
        }
    }
}
