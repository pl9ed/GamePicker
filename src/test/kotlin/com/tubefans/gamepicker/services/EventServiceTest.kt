package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createRecommendEvent
import discord4j.common.util.Snowflake
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.channel.VoiceChannel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.util.NoSuchElementException

@OptIn(ExperimentalCoroutinesApi::class)
class EventServiceTest {

    private val id0 = Snowflake.of(0)
    private val id1 = Snowflake.of(1)
    private val missing = Snowflake.of(999)

    private val user0 = DiscordUser(id0, "")
    private val user1 = DiscordUser(id1, "")

    private val validVoiceStates: Flux<VoiceState> = Flux.just(
        mockk {
            every { userId } returns id0
        },
        mockk {
            every { userId } returns id1
        }
    )

    private val voiceChannel: VoiceChannel = mockk {
        every { voiceStates } returns validVoiceStates
    }

    private val missingIdVoiceState: Flux<VoiceState> = Flux.just(
        mockk {
            every { userId } returns id0
        },
        mockk {
            every { userId } returns missing
        }
    )
    private val missingIdVoiceChannel: VoiceChannel = mockk {
        every { getVoiceStates() } returns missingIdVoiceState
    }

    private val discordUserService: DiscordUserService = mockk() {
        every { findById(id0) } returns user0
        every { findById(id1) } returns user1
        every { findById(missing) } throws NoSuchElementException()
    }
    private val eventService = EventService(discordUserService)

    @Test
    fun `should get user's current voice channel`() {
        val event = createRecommendEvent(voiceChannel)

        assertEquals(voiceChannel, eventService.getCurrentChannel(event))
    }

    @Test
    fun `should return Null on missing voice state`() {
        val event = createRecommendEvent(null)

        assertEquals(null, eventService.getCurrentChannel(event))
    }

    @Test
    fun `should get users in a voice channel`() = runTest {
        val users = eventService.getUsersInChannel(voiceChannel).block()

        assertEquals(
            setOf(user0, user1),
            users
        )
    }

    @Test
    fun `should exclude entries when findById fails`() = runTest {
        val users = eventService.getUsersInChannel(missingIdVoiceChannel).block()

        assertEquals(
            setOf(user0),
            users
        )
    }
}
