package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.dto.BotUser
import com.tubefans.gamepicker.services.BotUserService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlin.jvm.optionals.getOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AddMeCommand @Autowired constructor(
    private val botUserService: BotUserService
) : SlashCommand {

    companion object {
        const val NAME = "name"
    }

    override val name = "addme"

    @OptIn(ExperimentalStdlibApi::class)
    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val content: String = try {
            val nameField = event.options.first { it.name == NAME }.value.get().asString()
            val username = event.interaction.user.username
            val id = event.interaction.user.id
            botUserService.findById(id.toString()).getOrNull()?.let {
                it.name = nameField
                it.username = username
                botUserService.updateUser(it)
                "Updated user $username with name $name"
            } ?: botUserService.insertUser(BotUser(discordId = id.toString(), username = username, name = nameField))
            "Added new user $username with name $name at id=$id"
        } catch (e: NoSuchElementException) {
            "No name found. Did you forget to pass in your name?"
        }
        return event.reply()
            .withContent(content)
    }

}
