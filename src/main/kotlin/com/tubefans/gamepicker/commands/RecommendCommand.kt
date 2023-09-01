package com.tubefans.gamepicker.commands

import com.google.common.annotations.VisibleForTesting
import com.tubefans.gamepicker.cache.UserCache
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.extensions.getLongOption
import com.tubefans.gamepicker.extensions.getStringOption
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.ChatInputInteractionEventService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RecommendCommand @Autowired constructor(
    private val chatInputInteractionEventService: ChatInputInteractionEventService,
    private val userCache: UserCache
) : SlashCommand {

    companion object {
        const val DEFAULT_GAME_COUNT = 5

        const val INCLUDE_KEY = "include"
        const val EXCLUDE_KEY = "exclude"
        const val DISPLAY_COUNT_KEY = "display-count"

        const val NO_GAMES_RESPONSE = "No games found. Are you in a voice channel?"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "recommend"

    override fun handle(event: ChatInputInteractionEvent) =
        event.deferReply()
            .then(
                chatInputInteractionEventService.getCurrentChannel(event)?.let {
                    chatInputInteractionEventService.getUsersInChannel(it)
                } ?: Mono.just(emptySet())
            ).map { users ->
                val exclude = try {
                    event.getStringOption(EXCLUDE_KEY).split(",")
                } catch (e: NoSuchElementException) {
                    emptySet()
                }
                val include = try {
                    event.getStringOption(INCLUDE_KEY).split(",")
                } catch (e: NoSuchElementException) {
                    emptySet()
                }
                users.toMutableSet()
                    .removeExclusions(exclude)
                    .addInclusions(include)
                    .toSet()
            }.map {
                logger.info(
                    "Getting top games for {}",
                    it.joinToString { user ->
                        user.name ?: user.discordId.asString()
                    }
                )
                GameScoreMap(it)
            }.map {
                val count = try {
                    event.getLongOption(DISPLAY_COUNT_KEY).toInt()
                } catch (e: RuntimeException) {
                    when (e) {
                        is NumberFormatException, is NoSuchElementException -> DEFAULT_GAME_COUNT
                        else -> throw e
                    }
                }
                getReplyString(it, count)
            }.flatMap {
                event.editReply(it)
            }.then()

    @VisibleForTesting
    fun getReplyString(gameScoreMap: GameScoreMap, rawGameCount: Int): String {
        val (table, maxWidth) = generateTable(gameScoreMap, rawGameCount)
        if (table.isEmpty()) return NO_GAMES_RESPONSE

        val replyString = StringBuilder(
            "```\n" +
                "TOP ${table.size} GAMES:\n"
        )

        table.forEach { row ->
            val rank = row[0].padEnd(maxWidth[0], ' ')
            val game = row[1].padEnd(maxWidth[1], ' ')
            val score = row[2].padEnd(maxWidth[2], ' ')
            val fans = row[3].padEnd(maxWidth[3], ' ')
            val excludes = row[4].padEnd(maxWidth[4], ' ')

            replyString.append("| $rank | $game | $score | $fans | $excludes |\n")
        }

        replyString.append("```")

        return replyString.toString()
    }

    @VisibleForTesting
    fun generateTable(gameScoreMap: GameScoreMap, rawGameCount: Int): Pair<List<List<String>>, Array<Int>> {
        val maxWidth = Array(5) { 0 }
        val table = mutableListOf<List<String>>()

        gameScoreMap.apply {
            // 1 < gamecount < 10
            val gameCount = maxOf(minOf(10, rawGameCount), 1)
            val topGames = getTopGames(gameCount)
            logger.info("Top games: {}", topGames.joinToString { it.first })

            topGames.forEachIndexed { i, gameScore ->
                val rank = i + 1
                val game = gameScore.first
                val score = gameScore.second

                val rowData = generateRowData(rank, game, score, getTopPlayersForGame(game), getNonPlayersForGame(game))
                table.add(rowData)

                for (j in maxWidth.indices) {
                    maxWidth[j] = maxOf(maxWidth[j], rowData[j].length)
                }
            }

            return Pair(table, maxWidth)
        }
    }

    @VisibleForTesting
    fun generateRowData(
        rank: Int,
        game: String,
        score: Long,
        fans: Collection<DiscordUser>,
        excludes: Collection<DiscordUser>
    ): List<String> = listOf(
        "$rank.",
        game,
        score.toString(),
        "Fans: ${fans.joinToString { it.name ?: it.discordId.asString() }}",
        "Excludes: ${excludes.joinToString { it.name ?: it.discordId.asString() }}"
    )

    private fun MutableCollection<DiscordUser>.removeExclusions(excludeNames: Collection<String>): MutableCollection<DiscordUser> {
        excludeNames.forEach { raw ->
            this.removeIf {
                it.name?.trim()?.uppercase() == raw.trim().uppercase()
            }
        }
        return this
    }

    private fun MutableCollection<DiscordUser>.addInclusions(includeNames: Collection<String>): MutableCollection<DiscordUser> {
        includeNames.forEach { name ->
            try {
                userCache.users.first {
                    it.name?.trim()?.uppercase() == name.trim().uppercase()
                }.let {
                    this.add(it)
                }
            } catch (e: NoSuchElementException) {
                logger.error("Could not find user with name $name", e)
            }
        }
        return this
    }
}
