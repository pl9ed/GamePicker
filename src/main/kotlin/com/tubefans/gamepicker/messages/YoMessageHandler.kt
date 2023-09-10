package com.tubefans.gamepicker.messages

import com.tubefans.gamepicker.repositories.YoCountRepository
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Component
final class YoMessageHandler @Autowired constructor(
    client: GatewayDiscordClient,
    private val yoCountRepository: YoCountRepository
) : CustomMessageEvent {

    companion object {
        const val MESSAGE_STRING: String = "YO"
        const val RESPONSE_TEMPLATE =
            "Congrats on being the %dth yo! We've averaged %.2f yo's per day since I started counting on %s."
    }

    private val logger = LogManager.getLogger()
    private var initDate: LocalDate = yoCountRepository.serviceInitDate;
    private var count = 0

    init {
        logger.info("Subscribing to message events ${this::class.simpleName}")
        client.eventDispatcher.on(MessageCreateEvent::class.java).filter { shouldRespond(it) }.flatMap { handle(it) }
            .subscribe()
    }

    override fun getMessageString(): String = MESSAGE_STRING

    override fun shouldRespond(message: MessageCreateEvent): Boolean {
        if (message.message.content.uppercase().trim() != getMessageString()) return false
        logger.info("Message matched string, checking if sender is bot")

        if (message.member.get().isBot) return false
        logger.info("Sender was real user, incrementing count")

        count = yoCountRepository.increment()
        logger.info("Count currently at: $count")

        return count != 0 && count % yoCountRepository.getThreshold() == 0
    }

    override fun handle(event: MessageCreateEvent): Mono<Void> {
        return event.message.channel.map {
            logger.info("Creating message in ${it.id}")
            it.createMessage(createResponse())
        }.doOnSuccess {
            logger.info("Sent response message to ${getMessageString()}")
        }.doOnError { e ->
            logger.error("Failed to send response to ${getMessageString()}", e)
        }.block()?.then() ?: Mono.empty()
    }

    private fun createResponse() = String.format(
        RESPONSE_TEMPLATE,
        yoCountRepository.findCount(),
        getYosPerDay(),
        initDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    )

    private fun getYosPerDay(): Double {
        val days = maxOf((LocalDate.now().toEpochDay() - initDate.toEpochDay()).toDouble(), 1.0)

        return yoCountRepository.findCount().toDouble() / days
    }
}
