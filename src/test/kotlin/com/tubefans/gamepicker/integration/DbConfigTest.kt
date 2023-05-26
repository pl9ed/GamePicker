package com.tubefans.gamepicker.integration

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.repositories.DiscordUserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled("GHA connection/configuration issue with test DB")
@SpringBootTest
class DbConfigTest {

    @Autowired
    private lateinit var discordUserRepository: DiscordUserRepository

    private val user = DiscordUser("id", "username", "name")

    @BeforeEach
    fun setup() {
        discordUserRepository.save(user)
    }

    @AfterEach
    fun teardown() {
        discordUserRepository.delete(user)
    }

    @Test
    fun `can save entries`() {
        val updatedName = "new name"

        val response = discordUserRepository.save(user.copy(name = updatedName))
        assertEquals(response.name, updatedName)
    }

    @Test
    fun `can find single entry by field`() {
        val response = user.name?.let { discordUserRepository.findOneByName(it) }
        assertEquals(user, response)
    }
}
