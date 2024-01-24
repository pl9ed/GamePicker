package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.services.EC2Service
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.amazon.awssdk.awscore.exception.AwsServiceException

class StartServiceTest {
    private val mockMap = mutableMapOf(Pair("name1", "id1"))
    private val mockService: EC2Service = mockk() {
        every { instanceMap } returns mockMap
    }
    private val command = StartServerCommand(mockService)

    @Test
    fun `should return ip when starting server`() {
        val serverName = "name1"
        val ip = "ip address"
        every { mockService.startInstance(serverName) } returns ip

        assertEquals("Starting instance at ip address: $ip", command.startServer(serverName))
        verify(exactly = 1) { mockService.startInstance(serverName) }
    }

    @Test
    fun `should return relevant error message when instance fails to start`() {
        val serverName = "name1"
        val errorMessage = "error message"
        every { mockService.startInstance(serverName) } throws AwsServiceException.builder().message(errorMessage)
            .build()

        assertEquals("Failed to start instance: $errorMessage", command.startServer(serverName))
    }

    @Test
    fun `should return relevant error message when server name is not found`() {
        val serverName = "name1"
        val nameString = mockMap.keys.joinToString(",")
        val expectedMessage = "Failed to find EC2 instance associated with $serverName. Valid values are: $nameString"

        every { mockService.startInstance(serverName) } throws NoSuchElementException()

        assertEquals(expectedMessage, command.startServer(serverName))
    }
}
