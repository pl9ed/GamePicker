package com.tubefans.gamepicker.utils

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
}
