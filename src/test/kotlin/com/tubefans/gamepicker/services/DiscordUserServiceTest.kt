package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class DiscordUserServiceTest {

    private val id = "id"
    private val username = "username"
    private val name = "name"

    private val missing = "missing"

    private val newGame = "new game"
    private val newScore = 5L

    private var discordUser: DiscordUser = DiscordUser(id, username, name)

    private val userCache: UserCache = mockk {

    }

    @BeforeEach
    fun setup() {
        discordUser = DiscordUser(id, username, name)
    }

    private val discordUserService = DiscordUserService(userCache)

    @Test
    fun `should map valid names to users`() {
        val names = listOf("a", "b", "c")
        every {
            userCache.users
        }.returnsMany(
            mutableSetOf(
                DiscordUser("a", "username_a", "name_a"),
                DiscordUser("b", "username_b", "name_b"),
                DiscordUser("c", "username_c", "name_b")
            )
        )

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(names.size, users.size)
    }

    @Test
    fun `should add to failed set when it cannot find user by name`() {
        val names = listOf("a", "b", "c")
        val userA = DiscordUser("a", "a", "a")
        val userC = DiscordUser("c", "c", "c")
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
