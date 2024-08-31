package com.tubefans.gamepicker.services

import com.tubefans.gamepicker.services.EC2Service.Companion.CONFIRMATION_MESSAGE_TEMPLATE
import com.tubefans.gamepicker.services.EC2Service.Companion.MAX_RETRY_ATTEMPTS
import com.tubefans.gamepicker.services.EC2Service.Companion.RETRIES_EXHAUSTED_TEMPLATE
import com.tubefans.gamepicker.services.EC2Service.Companion.RETRY_INTERVAL
import com.tubefans.gamepicker.services.EC2Service.Companion.TOTAL_RETRY_DURATION
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.MessageCreateMono
import discord4j.core.spec.MessageCreateSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.springframework.test.context.TestPropertySource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusRequest
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusResponse
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest
import software.amazon.awssdk.services.ec2.model.Ec2Exception
import software.amazon.awssdk.services.ec2.model.InstanceStateChange
import software.amazon.awssdk.services.ec2.model.InstanceStateName
import software.amazon.awssdk.services.ec2.model.InstanceStatus
import software.amazon.awssdk.services.ec2.model.Reservation
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest
import java.time.Duration

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

    @Nested
    inner class StartInstances {
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
                            },
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
                            },
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
                            },
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
            every { mockEc2Client.startInstances(ofType(StartInstancesRequest::class)) } throws
                AwsServiceException
                    .builder()
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
    }

    @Nested
    inner class StopInstance {
        @Test
        fun `should complete on stop success`() {
            val id = "id1"
            val mockInstance: InstanceStateChange =
                mockk {
                    every { instanceId() } returns id
                }

            every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns
                mockk {
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

            every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns
                mockk {
                    every { stoppingInstances() } returns listOf(mockInstance)
                }

            assertDoesNotThrow { service.stopInstance(serverName.uppercase()).block() }
        }

        @Test
        fun `should throw Ec2Exception when stopping instances doesn't contain expected instance id`() {
            every { mockEc2Client.stopInstances(ofType(StopInstancesRequest::class)) } returns
                mockk {
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
    }

    @Nested
    inner class DescribeInstanceStatus {
        @Test
        fun `should get instance status for matching servers`() {
            val mockInstanceStatus: InstanceStatus =
                mockk {
                    every { instanceState() } returns
                        mockk {
                            every { this@mockk.toString() } returns "state"
                        }
                }

            val expectedRequest =
                DescribeInstanceStatusRequest
                    .builder()
                    .instanceIds(serverId)
                    .includeAllInstances(true)
                    .build()

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns
                mockk {
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
            val expectedRequest =
                DescribeInstanceStatusRequest
                    .builder()
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
            val expectedRequest =
                DescribeInstanceStatusRequest
                    .builder()
                    .instanceIds(serverId)
                    .includeAllInstances(true)
                    .build()

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns
                mockk {
                    every { hasInstanceStatuses() } returns false
                }

            assertThrows(AwsServiceException::class.java) {
                service.getInstanceStatus(serverName).block()
            }

            verify(exactly = 1) { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) }
        }

        @Test
        fun `should throw AwsServiceException if response list is empty when getting server status`() {
            val expectedRequest =
                DescribeInstanceStatusRequest
                    .builder()
                    .instanceIds(serverId)
                    .includeAllInstances(true)
                    .build()

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns
                mockk {
                    every { hasInstanceStatuses() } returns true
                    every { instanceStatuses() } returns emptyList()
                }

            assertThrows(AwsServiceException::class.java) {
                service.getInstanceStatus(serverName).block()
            }

            verify(exactly = 1) { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) }
        }
    }

    @Nested
    inner class SendConfirmationMessage {
        private val desiredState = InstanceStateName.STOPPED
        private val otherState = InstanceStateName.RUNNING

        private val mockNonMatchingInstanceStatus: InstanceStatus =
            mockk {
                every { instanceState() } returns
                    mockk {
                        every { name() } returns otherState
                        every { this@mockk.toString() } returns "other-state"
                    }
            }

        private val mockMatchingInstanceStatus: InstanceStatus =
            mockk {
                every { instanceState() } returns
                    mockk {
                        every { name() } returns desiredState
                        every { this@mockk.toString() } returns "state"
                    }
            }

        private val expectedRequest: DescribeInstanceStatusRequest =
            DescribeInstanceStatusRequest
                .builder()
                .instanceIds(serverId)
                .includeAllInstances(true)
                .build()

        private val failureResponse: DescribeInstanceStatusResponse =
            mockk {
                every { hasInstanceStatuses() } returns false
                every { instanceStatuses() } returns emptyList()
            }
        private val successResponse: DescribeInstanceStatusResponse =
            mockk {
                every { hasInstanceStatuses() } returns true
                every { instanceStatuses() } returns listOf(mockMatchingInstanceStatus)
            }

        private val channel: MessageChannel = mock(MessageChannel::class.java)
        private val outputChannel: MessageChannel = mock(MessageChannel::class.java)

        @BeforeEach
        fun setup() {
            `when`(channel.createMessage(anyString())).thenReturn(MessageCreateMono.of(outputChannel))
            `when`(outputChannel.createMessage(any(MessageCreateSpec::class.java)))
                .thenReturn(Mono.empty())
        }

        @Test
        fun `should respond with status`() {
            val expectedMessage = String.format(CONFIRMATION_MESSAGE_TEMPLATE, serverName, desiredState)

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns successResponse

            StepVerifier
                .create(
                    service.sendConfirmationMessage(
                        serverName,
                        Mono.just(channel),
                        desiredState,
                    ),
                ).verifyComplete()

            verify(exactly = 1) { mockEc2Client.describeInstanceStatus(expectedRequest) }
            Mockito.verify(channel, times(1)).createMessage(expectedMessage)
        }

        @Test
        fun `should retry if state does not match`() {
            val expectedMessage = String.format(CONFIRMATION_MESSAGE_TEMPLATE, serverName, desiredState)
            val notMatchingResponse: DescribeInstanceStatusResponse =
                mockk {
                    every { hasInstanceStatuses() } returns true
                    every { instanceStatuses() } returns listOf(mockNonMatchingInstanceStatus)
                }
            val responses =
                generateSequence { notMatchingResponse }
                    .take(MAX_RETRY_ATTEMPTS.toInt() - 1)
                    .toMutableList()

            responses.add(successResponse)

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returnsMany responses

            val stepVerifier =
                StepVerifier
                    .withVirtualTime {
                        service.sendConfirmationMessage(
                            serverName,
                            Mono.just(channel),
                            InstanceStateName.STOPPED,
                        )
                    }.expectSubscription()

            var attempts = 0

            while (attempts < MAX_RETRY_ATTEMPTS - 1) {
                stepVerifier.expectNoEvent(RETRY_INTERVAL)
                attempts++
            }

            stepVerifier.expectNext().verifyComplete()

            verify(exactly = MAX_RETRY_ATTEMPTS.toInt()) { mockEc2Client.describeInstanceStatus(expectedRequest) }
            Mockito.verify(channel, times(1)).createMessage(expectedMessage)
        }

        @Test
        fun `should not send follow up if getInstanceStatus() fails due no matching server name`() {
            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } throws NoSuchElementException()

            StepVerifier
                .create(
                    service.sendConfirmationMessage(
                        serverName,
                        Mono.just(channel),
                        InstanceStateName.STOPPED,
                    ),
                ).verifyComplete()

            verify(exactly = 1) { mockEc2Client.describeInstanceStatus(expectedRequest) }
            Mockito.verifyNoInteractions(channel)
        }

        @Test
        fun `should propagate original exception if getInstanceStatus() fails`() {
            val exceptionMessage = "test exception"
            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } throws RuntimeException(exceptionMessage)

            StepVerifier
                .create(
                    service.sendConfirmationMessage(
                        serverName,
                        Mono.just(channel),
                        InstanceStateName.STOPPED,
                    ),
                ).verifyComplete()

            verify(exactly = 1) { mockEc2Client.describeInstanceStatus(expectedRequest) }
            Mockito.verify(channel, times(1)).createMessage(exceptionMessage)
        }

        @Test
        fun `should retry when getInstanceState fails with AwsServiceException`() {
            val expectedMessage = String.format(CONFIRMATION_MESSAGE_TEMPLATE, serverName, desiredState)

            val responses =
                generateSequence { failureResponse }
                    .take(MAX_RETRY_ATTEMPTS.toInt() - 1)
                    .toMutableList()

            responses.add(successResponse)

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returnsMany responses

            val stepVerifier =
                StepVerifier
                    .withVirtualTime {
                        service.sendConfirmationMessage(
                            serverName,
                            Mono.just(channel),
                            InstanceStateName.STOPPED,
                        )
                    }.expectSubscription()

            var attempts = 0

            while (attempts < MAX_RETRY_ATTEMPTS - 1) {
                stepVerifier.expectNoEvent(RETRY_INTERVAL)
                attempts++
            }

            stepVerifier.expectNext().verifyComplete()

            verify(exactly = MAX_RETRY_ATTEMPTS.toInt()) { mockEc2Client.describeInstanceStatus(expectedRequest) }
            Mockito.verify(channel, times(1)).createMessage(expectedMessage)
        }

        @Test
        fun `should propagate AwsServiceException once retries are exhausted`() {
            val expectedMessage =
                String.format(RETRIES_EXHAUSTED_TEMPLATE, serverName, desiredState, TOTAL_RETRY_DURATION)

            every { mockEc2Client.describeInstanceStatus(eq(expectedRequest)) } returns failureResponse

            StepVerifier
                .withVirtualTime {
                    service.sendConfirmationMessage(
                        serverName,
                        Mono.just(channel),
                        InstanceStateName.STOPPED,
                    )
                }.expectSubscription()
                .expectNoEvent(Duration.ofSeconds(TOTAL_RETRY_DURATION))
                .expectNext()
                .verifyComplete()

            // initial call + # retries
            verify(exactly = MAX_RETRY_ATTEMPTS.toInt() + 1) { mockEc2Client.describeInstanceStatus(expectedRequest) }
            Mockito.verify(channel, times(1)).createMessage(expectedMessage)
        }
    }
}
