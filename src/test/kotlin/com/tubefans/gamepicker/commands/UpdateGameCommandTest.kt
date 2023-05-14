package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createUpdateGameEvent
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.User
import discord4j.discordjson.Id
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdateGameCommandTest {

    private val id = Snowflake.of(123)
    private val missingId = Snowflake.of(0)
    private val username = "username"
    private val game = "game"
    private val score = 10L

    private val botUser = BotUser(id.toString(), username, "")

    private val botUserService: BotUserService = mockk {
        every { updateGameForUserWithId(id.toString(), any(), any()) } returns botUser
        every { updateGameForUserWithId(missingId.toString(), any(), any()) } throws NoSuchElementException()
    }

    private val responseTemplate = "Updated %s with score: %d for %s."

    private val command = UpdateGameCommand(botUserService)

    @Test
    fun `should update user scores`() {
        val user = User(
            mockk(),
            mockk {
                every { id() } returns Id.of(123)
            }
        )

        val event = createUpdateGameEvent(user, game, score)
        val response = command.handle(event)

        verify {
            botUserService.updateGameForUserWithId(id.toString(), game, score)
            event.reply()
        }

        assertTrue(response.isEphemeralPresent)

        assertEquals(
            String.format(
                responseTemplate,
                game,
                score,
                username
            ),
            response.content().get()
        )
    }

    @Test
    fun `should create new entry if id doesn't exist`() {
        val user = User(
            mockk(),
            mockk {
                every { id() } returns Id.of(0)
                every { username() } returns username
            }
        )
        val botUser = BotUser(missingId.toString(), username, "name")

        every { botUserService.insertUser(any()) } returns botUser

        val event = createUpdateGameEvent(user, game, score)
        val response = command.handle(event)

        verify {
            botUserService.updateGameForUserWithId(missingId.toString(), game, score)
            botUserService.insertUser(botUser)
            event.reply()
        }

        assertTrue(response.isEphemeralPresent)

        assertEquals(
            String.format(
                responseTemplate,
                game,
                score,
                username
            ),
            response.content().get()
        )
    }
}
