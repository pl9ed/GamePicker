package com.tubefans.gamepicker.services

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

    private val repository: DiscordUserRepository = mockk {
        every { insert(discordUser) } returns discordUser
        every { save(discordUser) } returns discordUser
        every { findByName(name) } returns Optional.of(discordUser)
        every { findById(id) } returns Optional.of(discordUser)
        every { findById(missing) } returns Optional.empty()
        every { findByName(missing) } throws NoSuchElementException()
    }

    @BeforeEach
    fun setup() {
        discordUser = DiscordUser(id, username, name)
    }

    private val discordUserService = DiscordUserService(repository)

    @Test
    fun `should update game for user by name`() {
        val returnedUser = discordUserService.updateGameForUserWithName(name, newGame, newScore)
        assertTrue(returnedUser.gameMap.keys.contains(newGame))
        assertEquals(newScore, returnedUser.gameMap[newGame])
    }

    @Test
    fun `should update game for user by id`() {
        val returnedUser = discordUserService.updateGameForUserWithId(id, newGame, newScore)
        assertTrue(returnedUser.gameMap.keys.contains(newGame))
        assertEquals(newScore, returnedUser.gameMap[newGame])
    }

    @Test
    fun `should throw when user doesn't exist`() {
        assertThrows(NoSuchElementException::class.java) {
            discordUserService.updateGameForUserWithName(missing, "", 0L)
        }

        assertThrows(NoSuchElementException::class.java) {
            discordUserService.updateGameForUserWithId(missing, "", 0L)
        }
    }

    @Test
    fun `should map valid names to users`() {
        val names = listOf("a", "b", "c")
        every {
            repository.findByName(any()).get()
        } returnsMany listOf(
            DiscordUser("a", "username_a", "name_a"),
            DiscordUser("b", "username_b", "name_b"),
            DiscordUser("c", "username_c", "name_b")
        )

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(names.size, users.size)
    }

    @Test
    fun `should add to failed set when it cannot find user by name`() {
        val names = listOf("a", "b", "c")
        val userA = DiscordUser("a", "a", "a")
        val userC = DiscordUser("c", "c", "c")
        every { repository.findByName("a").get() } returns userA
        every { repository.findByName("b").get() } throws NoSuchElementException()
        every { repository.findByName("c").get() } returns userC

        val (users, failed) = discordUserService.getUsersFromNames(names)
        assertEquals(setOf(userA, userC), users)
        assertEquals(setOf("b"), failed)
    }
}
