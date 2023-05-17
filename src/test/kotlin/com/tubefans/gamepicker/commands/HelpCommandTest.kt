package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.HelpCommand.Companion.GENERIC_HELP_HEADER
import com.tubefans.gamepicker.utils.CommandStringFormatter.toRowString
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.possible.Possible
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
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
        every { options() } returns Possible.absent()
    }
    private val cmd2: ApplicationCommandRequest = mockk {
        every { name() } returns "cmd1 name"
        every { description().get() } returns "cmd1 description"
        every { options() } returns Possible.of(
            listOf(
                option1,
                option2
            )
        )
    }
    private val cmd3: ApplicationCommandRequest = mockk {
        every { name() } returns "cmd1 name"
        every { description().get() } returns "cmd1 description"
        every { options() } returns Possible.of(emptyList())
    }

    private val commands: List<ApplicationCommandRequest> = listOf(cmd1, cmd2, cmd3)

    private val command = HelpCommand(commands)

    @Test
    fun `should parse ApplicationCommandRequest to {name} {params} {description}`() {
        val expectedString = """
            $GENERIC_HELP_HEADER
            ${cmd1.toRowString()}
            ${cmd2.toRowString()}
            ${cmd3.toRowString()}
        """.trimIndent()

        assertEquals(
            expectedString,
            command.getGenericHelpMessage()
        )
    }
}
