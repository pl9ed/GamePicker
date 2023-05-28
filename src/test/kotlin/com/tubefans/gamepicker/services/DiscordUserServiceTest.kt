package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import discord4j.common.util.Snowflake
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DiscordUserServiceTest {

    private val id = Snowflake.of(10)
    private val name = "name"

    private var discordUser: DiscordUser = DiscordUser(id, name)

    private val userA = DiscordUser(Snowflake.of(1), "name_a")
    private val userB = DiscordUser(Snowflake.of(2), "name_b")
    private val userC = DiscordUser(Snowflake.of(3), "name_c")

    private val userCache: UserCache = mockk()

    @BeforeEach
    fun setup() {
        discordUser = DiscordUser(id, name)
    }

    private val discordUserService = DiscordUserService(userCache)

    @Test
    fun `should map valid names to users`() {
        val names = listOf("name_a", "name_b", "name_c")
        every { userCache.users } returns mutableSetOf(
            userA,
            userB,
            userC
        )

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(names.size, users.size)
    }

    @Test
    fun `should add to failed set when it cannot find user by name`() {
        val names = listOf(userA, userB, userC).mapNotNull { it.name }
        every { userCache.users } returns mutableSetOf(
            userA,
            userC
        )

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(setOf(userA, userC), users)
        assertEquals(setOf(userB.name), failed)
    }

    @Test
    fun `should handle updating users that already exist in cace`() {
        val userSet = mutableSetOf(userA, userB, userC)
        every { userCache.users } returns userSet
        val newUserA = DiscordUser(userA.discordId, "new name")
        discordUserService.save(newUserA)

        assertTrue(userSet.contains(newUserA))
    }
}
