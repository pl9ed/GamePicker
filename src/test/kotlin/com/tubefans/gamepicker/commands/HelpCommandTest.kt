package com.tubefans.gamepicker.commands

import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test


class HelpCommandTest {

    private val option1: ApplicationCommandOptionData = mockk {
        every { name() } returns "option1 name"
        every { description() } returns "option1 description"
    }
    private val option2: ApplicationCommandOptionData = mockk {
        every { name() } returns "option2 name"
        every { description() } returns "option2 description"
    }

    private val cmd1: ApplicationCommandRequest = mockk {
        every { name() } returns "cmd1 name"
        every { description().get() } returns "cmd1 description"
    }
    private val cmd2: ApplicationCommandRequest = mockk {
        every { name() } returns "cmd1 name"
        every { description().get() } returns "cmd1 description"
        every { options().get() } returns listOf(
            option1,
            option2
        )
    }

    private val commands: List<ApplicationCommandRequest> = listOf(cmd1, cmd2)

    private val command = HelpCommand(commands)

    @Test
    fun `should parse ApplicationCommandRequest to {name} {params} {description}`() {
    }

}