package com.tubefans.gamepicker.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest
import software.amazon.awssdk.services.ec2.model.Ec2Exception
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest

@ConfigurationProperties(prefix = "ec2")
@Service
class EC2Service @Autowired constructor(
    private val ec2Client: Ec2Client
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    val instanceMap: Map<String, String> = HashMap()

    fun startInstance(name: String): String {
        val instanceId = instanceMap[name.lowercase()] ?: throw NoSuchElementException("No instance found with name $name")

        val startRequest = StartInstancesRequest.builder().instanceIds(instanceId).build()

        val startingInstances = ec2Client.startInstances(startRequest)
            .startingInstances()
            .map { it.instanceId() }

        logger.debug("starting instances: ${startingInstances.joinToString(",")}")

        if (!startingInstances.contains(instanceId)) {
            throwEc2Exception("Failed to start instance with name $name and id $instanceId")
        }

        val describeRequest = DescribeInstancesRequest.builder().instanceIds(instanceId).build()
        val describeResponse = ec2Client.describeInstances(describeRequest)

        if (describeResponse.reservations().isEmpty() ||
            !describeResponse.reservations()[0].hasInstances()
        ) {
            logger.warn("reservations: ${describeResponse.reservations().joinToString(",")}")
            throwEc2Exception("Failed to get starting instances")
        }
        val ip = describeResponse.reservations()[0].instances()[0].publicIpAddress()
        logger.debug("Starting instance at $ip")
        return ip
    }

    fun stopInstance(name: String) {
        val instanceId = instanceMap[name]

        val stopRequest = StopInstancesRequest.builder().instanceIds(instanceId).build()
        val stoppingInstances = ec2Client.stopInstances(stopRequest).stoppingInstances().map { it.instanceId() }
        logger.debug("stopping instances: ${stoppingInstances.joinToString(",")}")

        if (!stoppingInstances.contains(instanceId)) {
            throwEc2Exception("Failed to stop instance with name $name and id $instanceId")
        }
    }

    private fun throwEc2Exception(message: String) {
        throw Ec2Exception.builder()
            .message(message)
            .build()
    }
}
