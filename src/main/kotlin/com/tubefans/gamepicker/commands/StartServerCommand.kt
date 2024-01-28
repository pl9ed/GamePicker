package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.extensions.getStringOption
import com.tubefans.gamepicker.services.EC2Service
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.services.ec2.model.InstanceStateName

@Component
class StartServerCommand
@Autowired
constructor(
    private val ec2Service: EC2Service
) : SlashCommand {
    companion object {
        const val NAME_KEY = "name"

        const val AWS_ERROR_MESSAGE_TEMPLATE = "Failed to start instance: %s"
        const val NO_ELEMENT_MESSAGE_TEMPLATE = "Failed to find EC2 instance associated with %s. Valid values are: %s"
        const val UNHANDLED_ERROR_MESSAGE_TEMPLATE = "Unhandled exception %s: %s"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "start-server"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val serverName = event.getStringOption(NAME_KEY)
        return event.deferReply()
            .then(ec2Service.startInstance(serverName))
            .map { ip ->
                getReplyString(ip)
            }.onErrorResume { e ->
                logger.error("Failed to start instance for $serverName")
                Mono.just(getErrorMessage(serverName, e))
            }.flatMap { message ->
                logger.debug("test - edit reply")
                event.editReply(message)
            }.flatMap { message ->
                logger.debug(" test - send")
                ec2Service.sendConfirmationMessage(serverName, message.channel, InstanceStateName.RUNNING)
            }.then()
    }

    private fun getErrorMessage(serverName: String, e: Throwable): String =
        when (e) {
            is AwsServiceException -> String.format(AWS_ERROR_MESSAGE_TEMPLATE, e.message)
            is NoSuchElementException -> String.format(
                NO_ELEMENT_MESSAGE_TEMPLATE,
                serverName,
                ec2Service.instanceMap.keys.joinToString(
                    ", "
                )
            )

            else -> String.format(UNHANDLED_ERROR_MESSAGE_TEMPLATE, e::class.simpleName, e.message)
        }

    private fun getReplyString(ip: String) = "Starting instance at ip address: $ip"
}
