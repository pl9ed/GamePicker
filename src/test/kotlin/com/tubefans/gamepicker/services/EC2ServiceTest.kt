package com.tubefans.gamepicker.services

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.eq
import org.springframework.test.context.TestPropertySource
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusRequest
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest
import software.amazon.awssdk.services.ec2.model.Ec2Exception
import software.amazon.awssdk.services.ec2.model.InstanceStateChange
import software.amazon.awssdk.services.ec2.model.InstanceStatus
import software.amazon.awssdk.services.ec2.model.Reservation
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest

@TestPropertySource(locations = ["/application.properties", "/application.yml"])
class EC2ServiceTest {
    private val mockEc2Client: Ec2Client = mockk()
    private lateinit var service: EC2Service
    private val serverName = "name1"
    private val serverId = "id1"

    @BeforeEach
    fun setup() {
        service = EC2Service(mockEc2Client)
        service.instanceMap[serverName] = serverId
    }

    @Test
    fun `should return string with ip on success`() {
        val ip = "ip1"
        val mockInstance: InstanceStateChange =
            mockk {
                every { instanceId() } returns serverId
            }
        val mockReservation: Reservation =
            mockk {
                every { hasInstances() } returns true
                every { instances() } returns
                    listOf(
                        mockk {
                            every { publicIpAddress() } returns ip
                        }
                    )
            }
        every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } returns
            mockk {
                every { startingInstances() } returns listOf(mockInstance)
            }
        every { mockEc2Client.describeInstances(ofType(DescribeInstancesRequest::class)) } returns
            mockk {
                every { hasReservations() } returns true
                every { reservations() } returns listOf(mockReservation)
            }

        assertEquals(ip, service.startInstance(serverName).block())
    }

    @Test
    fun `should start instances regardless of server name case`() {
        val id = "id1"
        val ip = "ip1"
        val mockInstance: InstanceStateChange =
            mockk {
                every { instanceId() } returns id
            }
        val mockReservation: Reservation =
            mockk {
                every { hasInstances() } returns true
                every { instances() } returns
                    listOf(
                        mockk {
                            every { publicIpAddress() } returns ip
                        }
                    )
            }
        every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } returns
            mockk {
                every { startingInstances() } returns listOf(mockInstance)
            }
        every { mockEc2Client.describeInstances(ofType(DescribeInstancesRequest::class)) } returns
            mockk {
                every { hasReservations() } returns true
                every { reservations() } returns listOf(mockReservation)
            }

        assertEquals(ip, service.startInstance(serverName.uppercase()).block())
    }

    @Test
    fun `should throw Ec2Exception when starting instances doesn't contain expected instance id`() {
        val ip = "ip1"
        val mockReservation: Reservation =
            mockk {
                every { hasInstances() } returns true
                every { instances() } returns
                    listOf(
                        mockk {
                            every { publicIpAddress() } returns ip
                        }
                    )
            }
        every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } returns
            mockk {
                every { startingInstances() } returns listOf()
            }
        every { mockEc2Client.describeInstances(ofType(DescribeInstancesRequest::class)) } returns
            mockk {
                every { hasReservations() } returns true
                every { reservations() } returns listOf(mockReservation)
            }

        assertThrows(Ec2Exception::class.java) {
            service.startInstance(serverName).block()
        }
    }

    @Test
    fun `should throw Ec2Exception when reservations doesn't have any instances`() {
        every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } returns
            mockk {
                every { startingInstances() } returns emptyList()
            }
        every { mockEc2Client.describeInstances(ofType(DescribeInstancesRequest::class)) } returns
            mockk {
                every { hasReservations() } returns false
                every { reservations() } returns emptyList()
            }

        assertThrows(Ec2Exception::class.java) {
            service.startInstance(serverName).block()
        }
    }

    @Test
    fun `should propagate exceptions from ec2client calls`() {
        every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } throws AwsServiceException.builder()
            .build()

        assertThrows(AwsServiceException::class.java) {
            service.startInstance(serverName).block()
        }
    }

    @Test
    fun `should throw NoSuchElementException when calling startInstance() if map doesn't contain name`() {
        assertThrows(NoSuchElementException::class.java) {
            service.startInstance("aaa").block()
        }
    }

    @Test
    fun `should complete on stop success`() {
        val id = "id1"
        val mockInstance: InstanceStateChange =
            mockk {
                every { instanceId() } returns id
            }

        every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns mockk {
            every { stoppingInstances() } returns listOf(mockInstance)
        }

        assertDoesNotThrow { service.stopInstance(serverName).block() }
    }

    @Test
    fun `should stop instances regardless of name case`() {
        val id = "id1"
        val mockInstance: InstanceStateChange =
            mockk {
                every { instanceId() } returns id
            }

        every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns mockk {
            every { stoppingInstances() } returns listOf(mockInstance)
        }

        assertDoesNotThrow { service.stopInstance(serverName.uppercase()).block() }
    }

    @Test
    fun `should throw Ec2Exception when stopping instances doesn't contain expected instance id`() {
        every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns mockk {
            every { stoppingInstances() } returns emptyList()
        }

        assertThrows(Ec2Exception::class.java) {
            service.stopInstance(serverName).block()
        }
    }

    @Test
    fun `should throw NoSuchElementException when calling stopInstance() if map doesn't contain name`() {
        assertThrows(NoSuchElementException::class.java) {
            service.stopInstance("aaa").block()
        }
    }

    @Test
    fun `should get instance status for matching servers`() {
        val mockInstanceStatus: InstanceStatus = mockk {
            every { instanceState() } returns mockk {
                every { this@mockk.toString() } returns "state"
            }
        }

        val expectedRequest = DescribeInstanceStatusRequest.builder()
            .instanceIds(serverId)
            .includeAllInstances(true)
            .build()

        every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns mockk {
            every { hasInstanceStatuses() } returns true
            every { instanceStatuses() } returns listOf(mockInstanceStatus)
        }

        assertEquals(mockInstanceStatus, service.getInstanceStatus(serverName).block())
    }

   @Test
   fun `should throw NoSuchElementException if serverName doesn't match any known servers`() {
       assertThrows(NoSuchElementException::class.java) {
           service.getInstanceStatus("not a server").block()
       }

       verify { mockEc2Client wasNot Called }
   }

    @Test
    fun `should propagate exception if describeInstanceStatus() fails`() {
        val expectedRequest = DescribeInstanceStatusRequest.builder()
            .instanceIds(serverId)
            .includeAllInstances(true)
            .build()

        every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } throws
                AwsServiceException.builder().build()

        assertThrows(AwsServiceException::class.java) {
            service.getInstanceStatus(serverName).block()
        }

        verify(exactly = 1) { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) }
    }

    @Test
    fun `should throw AwsServiceException if response does not contain instance status when getting server status`() {
        val expectedRequest = DescribeInstanceStatusRequest.builder()
            .instanceIds(serverId)
            .includeAllInstances(true)
            .build()

        every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns mockk {
            every { hasInstanceStatuses() } returns false
        }

        assertThrows(AwsServiceException::class.java) {
            service.getInstanceStatus(serverName).block()
        }

        verify(exactly = 1) { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) }
    }

    @Test
    fun `should throw AwsServiceException if response list is empty when getting server status`() {
        val expectedRequest = DescribeInstanceStatusRequest.builder()
            .instanceIds(serverId)
            .includeAllInstances(true)
            .build()

        every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns mockk {
            every { hasInstanceStatuses() } returns true
            every { instanceStatuses() } returns emptyList()
        }

        assertThrows(AwsServiceException::class.java) {
            service.getInstanceStatus(serverName).block()
        }

        verify(exactly = 1) { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) }
    }
}
