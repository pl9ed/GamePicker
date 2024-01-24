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
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "stop-server"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val serverName = event.getStringOption(NAME_KEY)
        return event.reply()
            .withContent("Stopping EC2 instance for $serverName")
            .then(Mono.just(stopServer(event.getStringOption(NAME_KEY))))
            .flatMap { message ->
                event.editReply(message)
            }.then()
    }

    private fun stopServer(serverName: String): String {
        return try {
            ec2Service.stopInstance(serverName)
            "Stopped EC2 instance for $serverName"
        } catch (e: AwsServiceException) {
            logger.error("Failed to stop instance", e)
            "Failed to stop instance: ${e.message}"
        } catch (e: NoSuchElementException) {
            logger.error("Failed to find EC2 instance associated with $serverName", e)
            "Failed to find EC2 instance associated with $serverName. Valid values are: ${
            ec2Service.instanceMap.keys.joinToString(
                ", "
            )
            }"
        }
    }
}
