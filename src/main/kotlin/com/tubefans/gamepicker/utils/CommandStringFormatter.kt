package com.tubefans.gamepicker.utils

import discord4j.discordjson.json.ApplicationCommandRequest

object CommandStringFormatter {

    fun ApplicationCommandRequest.toRowString(includeOptions: Boolean = true): String {
        val row = StringBuilder("/${this.name()} ")

        if (includeOptions) {
            this.options().get().forEach {
                row.append("{${it.name()}} ")
            }
        }

        row.append(": ${this.description().get()}")

        return row.toString()
    }
}
