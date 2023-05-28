package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DiscordUserServiceTest {

    private val id = "id"
    private val username = "username"
    private val name = "name"

    private val missing = "missing"

    private val newGame = "new game"
    private val newScore = 5L

    private var discordUser: DiscordUser = DiscordUser(id, name)

    private val userCache: UserCache = mockk {
    }

    @BeforeEach
    fun setup() {
        discordUser = DiscordUser(id, name)
    }

    private val discordUserService = DiscordUserService(userCache)

    @Test
    fun `should map valid names to users`() {
        val names = listOf("name_a", "name_b", "name_c")
        every {
            userCache.users
        }.returnsMany(
            mutableSetOf(
                DiscordUser("a", "name_a"),
                DiscordUser("b", "name_b"),
                DiscordUser("c", "name_c")
            )
        )

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(names.size, users.size)
    }

    @Test
    fun `should add to failed set when it cannot find user by name`() {
        val names = listOf("a", "b", "c")
        val userA = DiscordUser("a", "a")
        val userC = DiscordUser("c", "c")
        every {
            userCache.users
        }.returnsMany(
            mutableSetOf(
                userA,
                userC
            )
        )

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(setOf(userA, userC), users)
        assertEquals(setOf("b"), failed)
    }
}
