package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import com.tubefans.gamepicker.services.GoogleDriveService
import com.tubefans.gamepicker.services.GoogleSheetsService
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Optional

class UserCacheTest {
    private val googleDriveService: GoogleDriveService = mockk()
    private val googleSheetCache: GoogleSheetCache =
        mockk {
            every { dataSheet } returns mockk()
        }
    private val googleSheetsService: GoogleSheetsService =
        mockk {
            every { mapToScores(any()) } returns emptyMap()
        }
    private val discordUserRepository: DiscordUserRepository =
        mockk {
            every { findOneByName(any()) } returns Optional.of(DiscordUser(Snowflake.of(1L), "name"))
        }

    @Test
    fun `should update when lastUpdate time is greater than current`() {
        val now = System.currentTimeMillis()
        every { googleDriveService.getLastUpdatedTime() } returnsMany
            listOf(
                DateTime(now - 2000L),
                DateTime(now - 1000L),
                DateTime(now),
            )

        every { googleSheetsService.mapToScores(any()) } returns mapOf("key" to listOf(Pair("game", 0L)))

        val cache =
            UserCache(
                googleDriveService,
                googleSheetCache,
                googleSheetsService,
                discordUserRepository,
            )

        // normally called by Spring
        cache.afterPropertiesSet()

        var users = cache.users
        assertTrue(users.size == 1)

        // test that size remains 1 after calling get() again
        users = cache.users
        assertTrue(users.size == 1)

        // 1 for setup, and 1 for each getter call
        verify(exactly = 3) {
            googleSheetsService.mapToScores(any())
        }
    }

    @Test
    fun `should pull from cache when lastUpdate time is the same`() {
        val now = System.currentTimeMillis()
        every { googleDriveService.getLastUpdatedTime() } returns DateTime(now)

        val cache =
            UserCache(
                googleDriveService,
                googleSheetCache,
                googleSheetsService,
                discordUserRepository,
            )

        cache.afterPropertiesSet()

        val users = cache.users

        verify(exactly = 1) {
            googleSheetsService.mapToScores(any())
        }
    }
}
