package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.services.DiscordUserService
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

    private val discordUser = DiscordUser(id.toString(), username, "")

    private val discordUserService: DiscordUserService = mockk {
        every { updateGameForUserWithId(id.toString(), any(), any()) } returns discordUser
        every { updateGameForUserWithId(missingId.toString(), any(), any()) } throws NoSuchElementException()
    }

    private val responseTemplate = "Updated %s with score: %d for %s."

    private val command = UpdateGameCommand(discordUserService)

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
            discordUserService.updateGameForUserWithId(id.toString(), game, score)
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
        val discordUser = DiscordUser(missingId.toString(), username, "", mutableMapOf(game to score))

        every { discordUserService.insert(any()) } returns discordUser

        val event = createUpdateGameEvent(user, game, score)
        val response = command.handle(event)

        verify {
            discordUserService.updateGameForUserWithId(missingId.toString(), game, score)
            discordUserService.insert(discordUser)
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
