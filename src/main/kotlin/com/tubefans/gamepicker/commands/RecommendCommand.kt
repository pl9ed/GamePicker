package com.tubefans.gamepicker.commands

import com.mongodb.internal.VisibleForTesting
import com.tubefans.gamepicker.dto.DiscordUser
import com.tubefans.gamepicker.models.GameScoreMap
import com.tubefans.gamepicker.services.EventService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RecommendCommand @Autowired constructor(
    private val eventService: EventService
) : SlashCommand {

    companion object {
        const val DEFAULT_GAME_COUNT = 3
        const val GAME_COUNT_NAME = "game-count"
        const val NO_GAMES_RESPONSE = "No games found. Are you in a voice channel?"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override val name = "recommend"

    override fun handle(event: ChatInputInteractionEvent) =
        event.deferReply()
            .then(
                eventService.getCurrentChannel(event)?.let {
                    eventService.getUsersInChannel(it)
                } ?: Mono.just(emptySet())
            ).map {
                logger.info("Getting top games for {}", it)
                GameScoreMap(it)
            }.map {
                logger.info(it.toString())
                val gameCount = eventService.getNumericOption(event, GAME_COUNT_NAME) ?: DEFAULT_GAME_COUNT
                getReplyString(it, maxOf(10, gameCount.toInt()))
            }.flatMap {
                event.editReply(it)
            }.then()

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    fun getReplyString(gameScoreMap: GameScoreMap, gameCount: Int): String {
        gameScoreMap.apply {
            val topGames = getTopGames(gameCount)

            if (topGames.isEmpty()) return NO_GAMES_RESPONSE

            val replyString = StringBuilder("TOP $gameCount GAMES:\n")

            topGames.forEachIndexed { i, gameScore ->
                val game = gameScore.first
                val score = gameScore.second

                replyString.append(
                    "${i + 1}: ${generateRow(game, score, getTopPlayersForGame(game), getNonPlayersForGame(game))}\n"
                )
            }

            return replyString.toString().trim()
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
    fun generateRow(
        game: String,
        score: Long,
        fans: Collection<DiscordUser>,
        excludes: Collection<DiscordUser>
    ): String = "$game | " +
        "$score | " +
        "Fans: ${fans.map { it.name ?: it.username }.joinToString()} | " +
        "Excludes: ${excludes.map { it.name ?: it.username }.joinToString()}"
}
