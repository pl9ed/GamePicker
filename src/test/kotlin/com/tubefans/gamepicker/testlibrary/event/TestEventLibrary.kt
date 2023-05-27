package com.tubefans.gamepicker.testlibrary.event

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.discordjson.json.ApplicationCommandRequest
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

object TestEventLibrary {

    fun createAddMeEvent(id: Long, name: String, username: String) = ChatInputInteractionEvent(
        createGatewayDiscordClient(),
        mockk(),
        mockk {
            every { commandInteraction } returns Optional.of(
                mockk {
                    every { options } returns listOf(stringOptionOf("name", name))
                    every { getUser() } returns mockk {
                        every { getId() } returns Snowflake.of(id)
                        every { getUsername() } returns username
                    }
                }
            )
            every { data } returns mockk() {
                every { applicationId() } returns mockk() {
                    every { asLong() } returns 0L
                }
            }
        }
    )

    fun createHelpEvent(command: Optional<ApplicationCommandRequest> = Optional.empty()) = ChatInputInteractionEvent(
        createGatewayDiscordClient(),
        mockk(),
        mockk {
            every { commandInteraction } returns Optional.of(
                mockk {
                    every { user } returns mockk()
                    every { data } returns mockk() {
                        every { applicationId() } returns mockk() {
                            every { asLong() } returns 0L
                        }
                    }
                }
            )
        }
    )

    fun createRecommendEvent(mockVoiceChannel: VoiceChannel?): ChatInputInteractionEvent = mockk {
        every {
            interaction.member.get()
                .voiceState.block()
                ?.channel?.block()
        } returns mockVoiceChannel
    }

    private fun createGatewayDiscordClient(): GatewayDiscordClient = mockk {
        every { rest() } returns mockk() {
            every { webhookService } returns mockk()
        }
    }

    private fun stringOptionOf(name: String, value: String): ApplicationCommandInteractionOption = mockk {
        every { getName() } returns name
        every { getValue() } returns Optional.of(
            mockk {
                every { raw } returns value
                every { asString() } returns value
            }
        )
    }

    private fun longOptionOf(name: String, value: Long): ApplicationCommandInteractionOption = mockk {
        every { getName() } returns name
        every { getValue() } returns Optional.of(
            mockk {
                every { raw } returns value.toString()
                every { asLong() } returns value
            }
        )
    }
}
