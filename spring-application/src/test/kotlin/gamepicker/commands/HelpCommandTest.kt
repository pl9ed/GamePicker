package com.tubefans.gamepicker.commands

import com.tubefans.gamepicker.commands.HelpCommand.Companion.COMMAND_NOT_FOUND_TEMPLATE
import com.tubefans.gamepicker.commands.HelpCommand.Companion.GENERIC_HELP_HEADER
import com.tubefans.gamepicker.testlibrary.event.TestEventLibrary.createHelpEvent
import com.tubefans.gamepicker.utils.CommandStringFormatter.toHelpString
import com.tubefans.gamepicker.utils.CommandStringFormatter.toRowString
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.possible.Possible
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HelpCommandTest {
    private val option1: ApplicationCommandOptionData =
        mockk {
            every { name() } returns "option1 name"
            every { description() } returns "option1 description"
        }
    private val option2: ApplicationCommandOptionData =
        mockk {
            every { name() } returns "option2 name"
            every { description() } returns "option2 description"
        }

    private val cmd1: ApplicationCommandRequest =
        mockk {
            every { name() } returns "cmd1 name"
            every { description().isAbsent } returns false
            every { description().get() } returns "cmd1 description"
            every { options() } returns Possible.absent()
        }
    private val cmd2: ApplicationCommandRequest =
        mockk {
            every { name() } returns "cmd2 name"
            every { description().isAbsent } returns false
            every { description().get() } returns "cmd2 description"
            every { options() } returns
                Possible.of(
                    listOf(
                        option1,
                        option2,
                    ),
                )
        }
    private val cmd3: ApplicationCommandRequest =
        mockk {
            every { name() } returns "cmd3 name"
            every { description().get() } returns "cmd3 description"
            every { options() } returns Possible.of(emptyList())
        }

    private val commands: List<ApplicationCommandRequest> = listOf(cmd1, cmd2, cmd3)

    private val command = HelpCommand(commands)

    @Test
    fun `should parse ApplicationCommandRequest to {name} {params} {description}`() {
        val expectedString =
            """
            $GENERIC_HELP_HEADER
            ${cmd1.toRowString()}
            ${cmd2.toRowString()}
            ${cmd3.toRowString()}
            """.trimIndent()

        assertEquals(
            expectedString,
            command.getGenericHelpMessage(),
        )
    }

    @Test
    fun `should return different string for generic help command`() {
        val event = createHelpEvent()
        every { event.options.isEmpty() } returns true

        val response = command.handle(event) as InteractionApplicationCommandCallbackReplyMono

        assertEquals(
            command.getGenericHelpMessage(),
            response.content().get(),
        )
    }

    @Test
    fun `should return command not found on invalid command`() {
        val cmd = "not-found"

        assertEquals(
            String.format(
                COMMAND_NOT_FOUND_TEMPLATE,
                cmd,
            ),
            command.getResponseString(cmd),
        )
    }

    @Test
    fun `should return command description and each option on separate lines`() {
        val expectedString = cmd2.toHelpString()

        assertEquals(
            expectedString,
            command.getResponseString(cmd2.name()),
        )
    }

    @Test
    fun `should return only description for no option commands`() {
        val expectedString = cmd1.toHelpString()

        assertEquals(
            expectedString,
            command.getResponseString(cmd1.name()),
        )
    }
}
