package com.tubefans.gamepicker.utils

import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest

object CommandStringFormatter {

    fun ApplicationCommandRequest.toRowString(): String {
        val row = StringBuilder("/${this.name()} ")

        if (!this.options().isAbsent) {
            this.options().get().forEach {
                row.append("{${it.name()}} ")
            }
        }

        row.append(": ${this.description().get()}")

        return row.toString()
    }

    fun ApplicationCommandRequest.toHelpString(): String {
        val strBuilder = StringBuilder()

        if (!this.options().isAbsent) {
            this.options().get().forEach {
                strBuilder.append("${it.name()}: ${it.description()}\n")
            }

            strBuilder.trimEnd()
        } else {
            strBuilder.append(this.description())
        }

        return strBuilder.toString()
    }

}
