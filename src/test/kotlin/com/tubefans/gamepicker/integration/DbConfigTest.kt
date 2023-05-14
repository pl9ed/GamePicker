package com.tubefans.gamepicker.integration

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.repositories.BotUserRepository
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
    private lateinit var botUserRepository: BotUserRepository

    private val user = BotUser("id", "username", "name")

    @BeforeEach
    fun setup() {
        botUserRepository.save(user)
    }

    @AfterEach
    fun teardown() {
        botUserRepository.delete(user)
    }

    @Test
    fun `can save entries`() {
        val updatedName = "new name"

        val response = botUserRepository.save(user.copy(name = updatedName))
        assertEquals(response.name, updatedName)
    }

    @Test
    fun `can find single entry by field`() {
        val response = botUserRepository.findOneByName(user.name)
        assertEquals(user, response)
    }
}
