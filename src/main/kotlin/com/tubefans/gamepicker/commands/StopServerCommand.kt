package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.extensions.getStringOption
import com.tubefans.gamepicker.services.EC2Service
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.awscore.exception.AwsServiceException

@Component
class StopServerCommand
@Autowired
constructor(
    private val ec2Service: EC2Service
) : SlashCommand {
    companion object {
        const val NAME_KEY = "name"

        const val AWS_ERROR_TEMPLATE = "Failed to stop instance %s, %s: %s"
        const val SERVER_NOT_FOUND_TEMPLATE = "Failed to find EC2 instance associated with %s. Valid values are: %s"
        const val UNHANDLED_ERROR_TEMPLATE = "Uncaught exception when stopping instance %s, %s: %s"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "stop-server"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val serverName = event.getStringOption(NAME_KEY)
        return event.deferReply()
            .then(ec2Service.stopInstance(serverName))
            .map { instanceId ->
                "Stopping $serverName running on $instanceId"
            }.onErrorResume { e ->
                Mono.just(getErrorMessage(serverName, e))
            }.flatMap { message ->
                event.editReply(message)
            }.then()
    }

    private fun getErrorMessage(serverName: String, e: Throwable) =
        when (e) {
            is AwsServiceException -> {
                logger.error("Failed to stop instance", e)
                String.format(AWS_ERROR_TEMPLATE, serverName, e::class.simpleName, e.message)
            }

            is NoSuchElementException -> {
                logger.error("Failed to find EC2 instance associated with $serverName", e)
                String.format(SERVER_NOT_FOUND_TEMPLATE, serverName, ec2Service.instanceMap.keys.joinToString(","))
            }

            else -> {
                logger.error("Uncaught exception for server $serverName", e)
                String.format(UNHANDLED_ERROR_TEMPLATE, serverName, e::class.simpleName, e.message)
            }
        }
}
