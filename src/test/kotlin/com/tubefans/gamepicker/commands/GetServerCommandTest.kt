package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.GetServerCommand.Companion.ERROR_TEMPLATE
import com.tubefans.gamepicker.extensions.toDisplayString
import com.tubefans.gamepicker.services.EC2Service
import com.tubefans.gamepicker.testlibrary.infra.Discord4JEventTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.ec2.model.InstanceStatus

class GetServerCommandTest : Discord4JEventTest() {
    private val serverName = "name1"
    private val serverId = "id1"
    private val mockMap = mutableMapOf(Pair(serverName, serverId))
    private val mockService: EC2Service = mockk {
        every { instanceMap } returns mockMap
    }
    private val command = GetServerCommand(mockService)

    private val instanceStatusString = "instance status"
    private val instanceStateString = "instance state"
    private val mockStatus: InstanceStatus = mockk {
        every { instanceId() } returns serverId
        every { instanceStatus() } returns mockk {
            every { this@mockk.toString() } returns instanceStatusString
        }
        every { instanceState() } returns mockk {
            every { this@mockk.toString() } returns instanceStateString
        }
    }

    @BeforeEach
    fun setup() {
        Mockito.`when`(optionValue.asString()).thenReturn(serverName)
        Mockito.`when`(option.name).thenReturn("name")
        every { mockService.instanceMap }
    }

    @Test
    fun `should get server status`() {
        val expectedMessage = mockStatus.toDisplayString(serverName)
        every { mockService.getInstanceStatus(serverName) } returns Mono.just(mockStatus)

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.getInstanceStatus(serverName) }
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }

    @Test
    fun `should display error message if getInstanceStatus() fails`() {
        val exceptionMessage = "Exception message"
        val exceptionClass = RuntimeException::class.simpleName
        val expectedMessage = String.format(ERROR_TEMPLATE, serverName, exceptionClass, exceptionMessage)
        every { mockService.getInstanceStatus(serverName) } returns Mono.error(RuntimeException(exceptionMessage))

        command.handle(inputEvent).block()

        verify(exactly = 1) { mockService.getInstanceStatus(serverName) }
        Mockito.verify(inputEvent, times(1)).deferReply()
        Mockito.verify(inputEvent, times(1)).editReply(expectedMessage)
    }
}
