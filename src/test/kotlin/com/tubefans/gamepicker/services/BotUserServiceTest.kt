package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.repositories.BotUserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class BotUserServiceTest {

    private val id = "id"
    private val username = "username"
    private val name = "name"

    private val missing = "missing"

    private val newGame = "new game"
    private val newScore = 5L

    private var botUser: BotUser = BotUser(id, username, name)

    private val repository: BotUserRepository = mockk {
        every { insert(botUser) } returns botUser
        every { save(botUser) } returns botUser
        every { findOneByName(name) } returns Optional.of(botUser)
        every { findById(id) } returns Optional.of(botUser)
        every { findById(missing) } returns Optional.empty()
        every { findOneByName(missing) } throws NoSuchElementException()
    }

    @BeforeEach
    fun setup() {
        botUser = BotUser(id, username, name)
    }

    private val botUserService = BotUserService(repository)

    @Test
    fun `should update game for user by name`() {
        val returnedUser = botUserService.updateGameForUserWithName(name, newGame, newScore)
        assertTrue(returnedUser.gameMap.keys.contains(newGame))
        assertEquals(newScore, returnedUser.gameMap[newGame])
    }

    @Test
    fun `should update game for user by id`() {
        val returnedUser = botUserService.updateGameForUserWithId(id, newGame, newScore)
        assertTrue(returnedUser.gameMap.keys.contains(newGame))
        assertEquals(newScore, returnedUser.gameMap[newGame])
    }

    @Test
    fun `should throw when user doesn't exist`() {
        assertThrows(NoSuchElementException::class.java) {
            botUserService.updateGameForUserWithName(missing, "", 0L)
        }

        assertThrows(NoSuchElementException::class.java) {
            botUserService.updateGameForUserWithId(missing, "", 0L)
        }
    }
}
