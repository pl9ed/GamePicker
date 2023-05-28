package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.dto.DiscordUser
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SheetsDiscordUserRepositoryTest {

    private val googleSheetCache: GoogleSheetCache = mockk()
    private val user = DiscordUser(Snowflake.of(1), "name")

    private val repository = SheetsDiscordUserRepository(googleSheetCache)

    @Test
    fun `should find user by name`() {
        every { googleSheetCache.userSheet } returns listOf(
            listOf(user.name!!, user.discordId.asLong().toString())
        )

        assertEquals(
            user,
            repository.findOneByName(user.name!!).get()
        )
    }

    @Test
    fun `should return empty Optional when no user is found`() {
        every { googleSheetCache.userSheet } returns emptyList()

        assertTrue(repository.findOneByName(user.name!!).isEmpty)
    }
}
