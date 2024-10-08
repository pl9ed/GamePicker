package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.dto.DiscordUser
import discord4j.common.util.Snowflake
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class SheetsDiscordUserRepository
    @Autowired
    constructor(
        private val googleSheetCache: GoogleSheetCache,
    ) : DiscordUserRepository {
        override fun findOneByName(name: String): Optional<DiscordUser> =
            try {
                Optional.of(
                    googleSheetCache.userSheet
                        .first {
                            it[0].trim().uppercase() == name.trim().uppercase()
                        }.let {
                            DiscordUser(discordId = Snowflake.of(it[1]), name = it[0].trim().uppercase())
                        },
                )
            } catch (e: RuntimeException) {
                when (e) {
                    is NoSuchElementException, is NumberFormatException -> Optional.empty()
                    else -> throw e
                }
            }
    }
