package com.tubefans.gamepicker.utils

import com.tubefans.gamepicker.utils.CommandStringFormatter.toRowString
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommandStringFormatterTest {
    private val option1: ApplicationCommandOptionData = mockk {
        every { name() } returns "option1 name"
        every { description() } returns "option1 description"
    }
    private val option2: ApplicationCommandOptionData = mockk {
        every { name() } returns "option2 name"
        every { description() } returns "option2 description"
    }
    private val cmd: ApplicationCommandRequest = mockk {
        every { name() } returns "name"
        every { description().get() } returns "description"
        every { options().isAbsent } returns false
        every { options().get() } returns listOf(
            option1,
            option2
        )
    }

    private val noOptionsCmd: ApplicationCommandRequest = mockk {
        every { name() } returns "name"
        every { description().get() } returns "description"
        every { options().isAbsent } returns true
    }

    private val emptyOptionsCmd: ApplicationCommandRequest = mockk {
        every { name() } returns "name"
        every { description().get() } returns "description"
        every { options().isAbsent } returns true
    }


    @Test
    fun `should format ApplicationCommandRequest to readable string`() {
        val expectedString = "/${cmd.name()} " +
            "{${option1.name()}} {${option2.name()}} : " +
            cmd.description().get()

        assertEquals(
            expectedString,
            cmd.toRowString()
        )
    }

    @Test
    fun `should handle no option commands`() {
        val expectedString = "/${noOptionsCmd.name()} : ${noOptionsCmd.description().get()}"

        assertEquals(
            expectedString,
            noOptionsCmd.toRowString()
        )
    }

    @Test
    fun `should handle empty options`() {
        val expectedString = "/${emptyOptionsCmd.name()} : ${emptyOptionsCmd.description().get()}"

        assertEquals(
            expectedString,
            emptyOptionsCmd.toRowString()
        )
    }
}
