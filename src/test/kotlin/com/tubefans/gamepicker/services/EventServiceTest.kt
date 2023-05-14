package com.tubefans.gamepicker.services

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventServiceTest {

    private val botUserService: BotUserService = mockk()
    private val eventService = EventService(botUserService)

    @Test
    fun `should get users in a voice channel`() = runTest {

    }

}