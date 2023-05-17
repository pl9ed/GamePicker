package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.AddMeCommand.Companion.MESSAGE_TEMPLATE
import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createAddMeEvent
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddMeCommandTest {

    private val id = Snowflake.of(123).toString()
    private val missingId = Snowflake.of(111).toString()
    private val name = "name"
    private val username = "username"
    private val botUser = BotUser(id, username, name)

    private val botUserService: BotUserService = mockk() {
        every { findById(id) } returns Optional.of(botUser)
        every { findById(missingId) } returns Optional.empty()
    }
    private val command = AddMeCommand(botUserService)

    @Test
    fun `should return correct message upon success`() {
        val event = createAddMeEvent(id, name, username)

        command.handle(event)

        assertEquals(
            event.reply.block()!!.content,
            String.format(
                MESSAGE_TEMPLATE,
                name,
                username,
                id
            )
        )
    }

}