package com.tubefans.gamepicker.cache

import com.google.api.client.util.DateTime
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import com.tubefans.gamepicker.services.GoogleSheetsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class UserCacheTest {

    private val googleSheetCache: GoogleSheetCache = mockk() {
        every { getSheet() } returns mockk()
    }
    private val googleSheetsService: GoogleSheetsService = mockk {
        every { mapToScores(any()) } returns emptyMap()
    }
    private val discordUserRepository: DiscordUserRepository = mockk()

    @Test
    fun `should update when lastUpdate time is greater than current`() {
        val now = System.currentTimeMillis()
        every { googleSheetCache.lastUpdateTime() } returnsMany listOf(
            DateTime(now - 1000L),
            DateTime(now)
        )

        val cache = UserCache(
            googleSheetCache,
            googleSheetsService,
            discordUserRepository
        )

        val users = cache.users

        verify(exactly = 2) {
            googleSheetsService.mapToScores(any())
        }
    }

    @Test
    fun `should pull from cache when lastUpdate time is the same`() {
        val now = System.currentTimeMillis()
        every { googleSheetCache.lastUpdateTime() } returns DateTime(now)

        val cache = UserCache(
            googleSheetCache,
            googleSheetsService,
            discordUserRepository
        )

        val users = cache.users

        verify(exactly = 1) {
            googleSheetsService.mapToScores(any())
        }
    }
}
