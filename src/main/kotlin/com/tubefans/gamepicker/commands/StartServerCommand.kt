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
class StartServerCommand @Autowired constructor(
    private val ec2Service: EC2Service
) : SlashCommand {

    companion object {
        const val NAME_KEY = "name"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "start-server"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val serverName: String = event.getStringOption(NAME_KEY)
        val message = try {
            val ip = ec2Service.startInstance(serverName)
            Mono.just(getReplyString(ip))
        } catch (e: AwsServiceException) {
            logger.error("Failed to start instance", e)
            Mono.just("Failed to start instance: ${e.message}")
        } catch (e: NoSuchElementException) {
            logger.error("Failed to find EC2 instance associated with $serverName")
            Mono.just(
                "Failed to find EC2 instance associated with $serverName. Valid values are: ${
                ec2Service.instanceMap.keys.joinToString(
                    ", "
                )
                }"
            )
        }.block()!!
        return event.reply()
            .withContent(message)
    }

    private fun getReplyString(ip: String) = "Starting instance at ip address: $ip"
}