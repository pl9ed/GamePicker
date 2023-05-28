package com.tubefans.gamepicker.repositories

import com.tubefans.gamepicker.cache.GoogleSheetCache
import com.tubefans.gamepicker.dto.DiscordUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class SheetsDiscordUserRepository @Autowired constructor(
    private val googleSheetCache: GoogleSheetCache
) : DiscordUserRepository {

    override fun findOneByName(name: String): Optional<DiscordUser> = try {
        Optional.of(
            googleSheetCache.userSheet.first {
                it[0].uppercase() == name.uppercase()
            }.let {
                DiscordUser(discordId = it[0], name = it[1])
            }
        )
    } catch (e: NoSuchElementException) {
        Optional.empty()
    }
}
