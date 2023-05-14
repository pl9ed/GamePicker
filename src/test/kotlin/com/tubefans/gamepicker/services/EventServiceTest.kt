package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.dto.BotUser
import discord4j.common.util.Snowflake
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.channel.VoiceChannel
import io.mockk.every
import io.mockk.mockk
import java.util.Optional
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

@OptIn(ExperimentalCoroutinesApi::class)
class EventServiceTest {

    private val id0 = Snowflake.of(0)
    private val id1 = Snowflake.of(1)
    private val missing = Snowflake.of(999)

    private val user0 = BotUser(id0.toString(), "", "")
    private val user1 = BotUser(id1.toString(), "", "")

    private val botUserService: BotUserService = mockk() {
        every { findById(id0.toString()) } returns Optional.of(user0)
        every { findById(id1.toString()) } returns Optional.of(user1)
        every { findById(missing.toString()) } returns Optional.empty()
    }
    private val eventService = EventService(botUserService)

    @Test
    fun `should get users in a voice channel`() = runTest {
        val voiceStates: Flux<VoiceState> = Flux.just(
            mockk {
                every { userId } returns id0
            },
            mockk {
                every { userId } returns id1
            }
        )
        val voiceChannel: VoiceChannel = mockk {
            every { getVoiceStates() } returns voiceStates
        }

        val users = eventService.getUsersInChannel(voiceChannel).block()

        assertEquals(
            setOf(user0, user1),
            users
        )
    }

    @Test
    fun `should exclude entries when findById fails`() {
        val voiceStates: Flux<VoiceState> = Flux.just(
            mockk {
                every { userId } returns id0
            },
            mockk {
                every { userId } returns missing
            }
        )
        val voiceChannel: VoiceChannel = mockk {
            every { getVoiceStates() } returns voiceStates
        }

        val users = eventService.getUsersInChannel(voiceChannel).block()

        assertEquals(
            setOf(user0),
            users
        )
    }

}