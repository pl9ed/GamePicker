package com.tubefans.gamepicker.utils

import com.tubefans.gamepicker.utils.CommandStringFormatter.toHelpString
import com.tubefans.gamepicker.utils.CommandStringFormatter.toRowString
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.possible.Possible
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommandStringFormatterTest {
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
    private val cmd: ApplicationCommandRequest =
        mockk {
            every { name() } returns "name"
            every { description().get() } returns "description"
            every { description().isAbsent } returns false
            every { options() } returns
                Possible.of(
                    listOf(
                        option1,
                        option2,
                    ),
                )
        }

    private val noOptionsCmd: ApplicationCommandRequest =
        mockk {
            every { name() } returns "name"
            every { description().get() } returns "description"
            every { description().isAbsent } returns false
            every { options() } returns Possible.absent()
        }

    private val emptyOptionsCmd: ApplicationCommandRequest =
        mockk {
            every { name() } returns "name"
            every { description().get() } returns "description"
            every { options() } returns Possible.absent()
        }

    @Test
    fun `row string should format ApplicationCommandRequest to readable string`() {
        val expectedString =
            "/${cmd.name()} " +
                "{${option1.name()}} {${option2.name()}} : " +
                cmd.description().get()

        assertEquals(
            expectedString,
            cmd.toRowString(),
        )
    }

    @Test
    fun `row string should handle no option commands`() {
        val expectedString = "/${noOptionsCmd.name()} : ${noOptionsCmd.description().get()}"

        assertEquals(
            expectedString,
            noOptionsCmd.toRowString(),
        )
    }

    @Test
    fun `row string should handle empty options`() {
        val expectedString = "/${emptyOptionsCmd.name()} : ${emptyOptionsCmd.description().get()}"

        assertEquals(
            expectedString,
            emptyOptionsCmd.toRowString(),
        )
    }

    @Test
    fun `help string should list command description with options`() {
        val expectedString =
            """
            ${cmd.description().get()}
            ${option1.name()}: ${option1.description()}
            ${option2.name()}: ${option2.description()}
            """.trimIndent()

        assertEquals(
            expectedString,
            cmd.toHelpString(),
        )
    }

    @Test
    fun `help string should handle no options`() {
        val expectedString =
            """
            ${noOptionsCmd.description().get()}
            """.trimIndent()

        assertEquals(
            expectedString,
            noOptionsCmd.toHelpString(),
        )
    }
}
