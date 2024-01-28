package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.EC2Service
import com.tubefans.gamepicker.testlibrary.infra.Discord4JEventTest
import discord4j.core.spec.InteractionCallbackSpecDeferReplyMono
import discord4j.core.spec.InteractionReplyEditMono
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.atMostOnce
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import software.amazon.awssdk.awscore.exception.AwsServiceException

class StartCommandTest : Discord4JEventTest() {
    private val serverName = "name1"

    private val mockMap = mutableMapOf(Pair(serverName, "id1"))
    private val mockService: EC2Service = mockk() {
        every { instanceMap } returns mockMap
    }
    private val command = StartServerCommand(mockService)

    private val ip = "ip address"

    @BeforeEach
    fun setup() {
        `when`(optionValue.asString()).thenReturn(serverName)
        `when`(option.name).thenReturn("name")
    }

    @Test
    fun `should return ip when starting server`() {
        val expectedMessage = "Starting instance at ip address: $ip"
        every { mockService.startInstance(serverName) } returns Mono.just(ip)

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.startInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).options
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }

    @Test
    fun `should return relevant error message when instance fails to start due to aws exception`() {
        val errorMessage = "error message"
        val expectedMessage = "Failed to start instance: $errorMessage"
        every { mockService.startInstance(serverName) }.returns(
            Mono.error(
                AwsServiceException.builder()
                    .message(errorMessage)
                    .build()
            )
        )

        `when`(inputEvent.options).thenReturn(listOf(option))
        `when`(inputEvent.deferReply()).thenReturn(InteractionCallbackSpecDeferReplyMono.of(inputEvent))
        `when`(inputEvent.editReply(expectedMessage)).thenReturn(InteractionReplyEditMono.of(inputEvent))

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.startInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).options
        Mockito.verify(inputEvent, atMostOnce()).deferReply()
        Mockito.verify(inputEvent, atMostOnce()).editReply(expectedMessage)
    }

    @Test
    fun `should return relevant error message when no instances are found`() {
        val errorMessage = "error message"
        val expectedMessage = String.format(
            StartServerCommand.NO_ELEMENT_MESSAGE_TEMPLATE,
            serverName,
            mockMap.keys.joinToString(
                ", "
            )
        )
        every { mockService.startInstance(serverName) } returns Mono.error(NoSuchElementException(errorMessage))

        `when`(inputEvent.options).thenReturn(listOf(option))
        `when`(inputEvent.deferReply()).thenReturn(InteractionCallbackSpecDeferReplyMono.of(inputEvent))
        `when`(inputEvent.editReply(expectedMessage)).thenReturn(InteractionReplyEditMono.of(inputEvent))

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.startInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).options
        Mockito.verify(inputEvent, atMostOnce()).deferReply()
        Mockito.verify(inputEvent, atMostOnce()).editReply(expectedMessage)
    }

    @Test
    fun `should return relevant error message when server name is not found`() {
        val nameString = mockMap.keys.joinToString(",")
        val expectedMessage = "Failed to find EC2 instance associated with $serverName. Valid values are: $nameString"

        every { mockService.startInstance(serverName) }.returns(
            Mono.error(
                NoSuchElementException()
            )
        )

        `when`(inputEvent.options).thenReturn(listOf(option))
        `when`(inputEvent.deferReply()).thenReturn(InteractionCallbackSpecDeferReplyMono.of(inputEvent))
        `when`(inputEvent.editReply(expectedMessage)).thenReturn(InteractionReplyEditMono.of(inputEvent))

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.startInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).options
        Mockito.verify(inputEvent, atMostOnce()).deferReply()
        Mockito.verify(inputEvent, atMostOnce()).editReply(expectedMessage)
    }
}
