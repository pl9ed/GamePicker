package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.StopServerCommand.Companion.AWS_ERROR_TEMPLATE
import com.tubefans.gamepicker.services.EC2Service
import com.tubefans.gamepicker.testlibrary.infra.Discord4JEventTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import software.amazon.awssdk.awscore.exception.AwsServiceException

class StopCommandTest : Discord4JEventTest() {
    private val serverName = "name1"
    private val serverId = "id1"
    private val mockMap = mutableMapOf(Pair(serverName, serverId))
    private val mockService: EC2Service =
        mockk {
            every { instanceMap } returns mockMap
        }

    private val command = StopServerCommand(mockService)

    @BeforeEach
    fun setup() {
        `when`(optionValue.asString()).thenReturn(serverName)
        `when`(option.name).thenReturn("name")
    }

    @Test
    fun `should call stopInstance()`() {
        val expectedMessage = "Stopping $serverName running on $serverId"
        every { mockService.stopInstance(serverName) } returns Mono.just(serverId)

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.stopInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }

    @Test
    fun `should propagate exceptions from ec2client`() {
        val exceptionMessage = "exception message"
        val expectedMessage =
            String.format(AWS_ERROR_TEMPLATE, serverName, AwsServiceException::class.simpleName, exceptionMessage)
        every { mockService.stopInstance(serverName) } returns
            Mono.error(
                AwsServiceException.builder().message(exceptionMessage).build(),
            )

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.stopInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }

    @Test
    fun `should respond with appropriate error message when serverName is not found`() {
        val expectedMessage =
            String.format(StopServerCommand.SERVER_NOT_FOUND_TEMPLATE, serverName, mockMap.keys.joinToString(","))
        every { mockService.stopInstance(serverName) } returns
            Mono.error(
                NoSuchElementException(),
            )

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.stopInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }

    @Test
    fun `should respond with appropriate message for uncaught exceptions`() {
        val exceptionMessage = "exception message"
        val expectedMessage =
            String.format(
                StopServerCommand.UNHANDLED_ERROR_TEMPLATE,
                serverName,
                RuntimeException::class.simpleName,
                exceptionMessage,
            )
        every { mockService.stopInstance(serverName) } returns
            Mono.error(
                RuntimeException(exceptionMessage),
            )

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.stopInstance(serverName) }
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }
}
