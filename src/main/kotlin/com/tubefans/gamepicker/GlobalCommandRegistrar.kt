package com.tubefans.gamepicker

import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class GlobalCommandRegistrar @Autowired constructor(
    private val client: RestClient,
    private val commands: List<ApplicationCommandRequest>
) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    // This method will run only once on each start up and is automatically called with Spring so blocking is okay.
    @Throws(IOException::class)
    override fun run(args: ApplicationArguments) {
        // Create an ObjectMapper that supported Discord4J classes

        // Convenience variables for the sake of easier to read code below.
        val applicationService = client.applicationService
        val applicationId = client.applicationId.block()!!

        /* Bulk overwrite commands. This is now idempotent, so it is safe to use this even when only 1 command
        is changed/added/removed
        */applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
            .doOnNext { applicationCommandData ->
                val optionString = applicationCommandData.options()
                    .toOptional()
                    .takeIf { options ->
                        options.isPresent
                    }?.map { list ->
                        list.joinToString { it.name() }
                    }
                logger.debug(
                    "Successfully registered command {} with options {}",
                    applicationCommandData.name(),
                    optionString
                )
            }.doOnError { e: Throwable? -> logger.error("Failed to register global commands", e) }
            .subscribe()
    }
}
