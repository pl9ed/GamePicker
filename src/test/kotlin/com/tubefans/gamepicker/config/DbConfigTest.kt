package com.tubefans.gamepicker.config

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.repositories.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DbConfigTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    private val user = BotUser("id", "username", "name")

    @BeforeEach
    fun setup() {
        userRepository.save(user)
    }

    @AfterEach
    fun teardown() {
        userRepository.delete(user)
    }

    @Test
    fun `can save entries`() {
        val updatedName = "new name"

        val response = userRepository.save(user.copy(name = updatedName))
        assertEquals(response.name, updatedName)
    }

    @Test
    fun `can find single entry by field`() {
        val response = userRepository.findOneByName(user.name)
        assertEquals(user, response)
    }
}
