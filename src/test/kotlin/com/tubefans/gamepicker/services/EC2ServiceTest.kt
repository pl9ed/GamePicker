package com.tubefans.gamepicker.services

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.test.context.TestPropertySource
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest
import software.amazon.awssdk.services.ec2.model.Ec2Exception
import software.amazon.awssdk.services.ec2.model.InstanceStateChange
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

        assertEquals(ip, service.startInstance(serverName))
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

        assertEquals(ip, service.startInstance(serverName.uppercase()))
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
            service.startInstance(serverName)
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
            service.startInstance(serverName)
        }
    }

    @Test
    fun `should propagate exceptions from ec2client calls`() {
        every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } throws AwsServiceException.builder()
            .build()

        assertThrows(AwsServiceException::class.java) {
            service.startInstance(serverName)
        }
    }

    @Test
    fun `should throw NoSuchElementException when calling startInstance() if map doesn't contain name`() {
        assertThrows(NoSuchElementException::class.java) {
            service.startInstance("aaa")
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

        assertDoesNotThrow { service.stopInstance(serverName) }
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

        assertDoesNotThrow { service.stopInstance(serverName.uppercase()) }
    }

    @Test
    fun `should throw Ec2Exception when stopping instances doesn't contain expected instance id`() {
        every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns mockk {
            every { stoppingInstances() } returns emptyList()
        }

        assertThrows(Ec2Exception::class.java) {
            service.stopInstance(serverName)
        }
    }

    @Test
    fun `should throw NoSuchElementException when calling stopInstance() if map doesn't contain name`() {
        assertThrows(NoSuchElementException::class.java) {
            service.stopInstance("aaa")
        }
    }
}
