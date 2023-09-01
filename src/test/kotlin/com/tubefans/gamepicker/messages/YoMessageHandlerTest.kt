package com.tubefans.gamepicker.messages

import com.tubefans.gamepicker.messages.YoMessageHandler.Companion.MESSAGE_STRING
import com.tubefans.gamepicker.repositories.YoCountRepository
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Member
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.util.Optional
import kotlin.random.Random

class YoMessageHandlerTest {

    private var client: GatewayDiscordClient = mockk {
        every { eventDispatcher } returns mockk() {
            every { on(MessageCreateEvent::class.java) } returns Flux.empty()
        }
    }

    private lateinit var yoCountRepository: YoCountRepository

    private val yoMessage: MessageCreateEvent = mockk {
        every { message } returns mockk {
            every { content } returns MESSAGE_STRING
        }
    }

    private val otherMessage: MessageCreateEvent = mockk {
        every { message } returns mockk {
            every { content } returns "abc"
        }
    }

    private val botUser: Member = mockk() {
        every { isBot } returns true
    }
    private val user: Member = mockk() {
        every { isBot } returns false
    }

    private lateinit var handler: YoMessageHandler

    @BeforeEach
    fun setup() {
        yoCountRepository = mockk {
            every { increment() } just (runs)
            every { getThreshold() } returns 5
        }
        handler = YoMessageHandler(client, yoCountRepository)
    }

    @Test
    @DisplayName("should increment count when message string matches")
    fun shouldIncrementOnMatch() {
        every { yoMessage.member } returns Optional.of(user)
        every { yoCountRepository.findCount() } returns 0

        handler.shouldRespond(yoMessage)

        verify { yoCountRepository.increment() }
    }

    @Test
    @DisplayName("should use case insensitive string matching")
    fun shouldMatchAnyCase() {
        every { yoCountRepository.findCount() } returns 0

        val message: MessageCreateEvent = mockk {
            every { message } returns mockk {
                every { content } returns "yo"
            }
            every { member } returns Optional.of(user)
        }

        handler.shouldRespond(message)

        verify { yoCountRepository.increment() }
    }

    @Test
    @DisplayName("should disregard whitespace for matching")
    fun shouldNotCheckWhitespace() {
        every { yoCountRepository.findCount() } returns 0

        val message: MessageCreateEvent = mockk {
            every { message } returns mockk {
                every { content } returns "       \nyo     \n     "
            }
            every { member } returns Optional.of(user)
        }

        handler.shouldRespond(message)

        verify { yoCountRepository.increment() }
    }

    @Test
    @DisplayName("should not increment when message string doesn't match")
    fun shouldNotIncrementOnNonMatchingString() {
        every { otherMessage.member } returns Optional.of(user)
        every { yoCountRepository.findCount() } returns 0
        every { yoCountRepository.increment() } answers { throw AssertionError("increment() was called") }

        handler.shouldRespond(otherMessage)
    }

    @Test
    @DisplayName("should not increment for bot user message string matches")
    fun shouldNotIncrementForBots() {
        every { yoMessage.member } returns Optional.of(botUser)
        every { yoCountRepository.findCount() } returns 0
        every { yoCountRepository.increment() } answers { throw AssertionError("increment() was called") }

        handler.shouldRespond(yoMessage)
    }

    @Test
    @DisplayName("should respond to 'yo' when count is multiple of threshold")
    fun shouldRespondOnCorrectCount() {
        val message: MessageCreateEvent = mockk {
            every { member } returns Optional.of(user)
            every { message } returns mockk {
                every { content } returns MESSAGE_STRING
            }
        }

        every { yoCountRepository.findCount() } returns yoCountRepository.getThreshold() * Random.nextInt(10)

        assertTrue(handler.shouldRespond(message))
    }

    @Test
    @DisplayName("should not respond to 'yo' when count is not multiple of threshold")
    fun shouldNotRespondOnIncorrectCount() {
        val message: MessageCreateEvent = mockk {
            every { member } returns Optional.of(user)
            every { message } returns mockk {
                every { content } returns MESSAGE_STRING
            }
        }

        every { yoCountRepository.findCount() } returns (yoCountRepository.getThreshold() * Random.nextInt(10)) + Random.nextInt(1, 49)

        assertFalse(handler.shouldRespond(message))
    }
}
