package com.tubefans.gamepicker.testlibrary.infra

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.DeferrableInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.core.`object`.command.ApplicationCommandInteractionOptionValue
import discord4j.core.spec.InteractionCallbackSpec
import discord4j.core.spec.InteractionCallbackSpecDeferReplyMono
import discord4j.core.spec.InteractionReplyEditMono
import discord4j.core.spec.InteractionReplyEditSpec
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import java.util.*

abstract class Discord4JEventTest {

    @Mock
    protected lateinit var inputEvent: ChatInputInteractionEvent

    @Mock
    protected lateinit var outputEvent: DeferrableInteractionEvent

    @Mock
    protected lateinit var option: ApplicationCommandInteractionOption

    @Mock
    protected lateinit var optionValue: ApplicationCommandInteractionOptionValue

    @BeforeEach
    fun eventSetup() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(option.value).thenReturn(Optional.of(optionValue))
        Mockito.`when`(inputEvent.options).thenReturn(listOf(option))
        Mockito.`when`(inputEvent.deferReply()).thenReturn(InteractionCallbackSpecDeferReplyMono.of(outputEvent))
        Mockito.`when`(inputEvent.editReply(ArgumentMatchers.any(InteractionReplyEditSpec::class.java)))
            .thenReturn(Mono.empty())
        Mockito.`when`(inputEvent.editReply(anyString())).thenReturn(InteractionReplyEditMono.of(outputEvent))
        Mockito.`when`(inputEvent.deferReply(ArgumentMatchers.any(InteractionCallbackSpec::class.java)))
            .thenReturn(Mono.empty())

        Mockito.`when`(outputEvent.editReply(anyString())).thenReturn(InteractionReplyEditMono.of(outputEvent))
        Mockito.`when`(outputEvent.editReply(ArgumentMatchers.any(InteractionReplyEditSpec::class.java)))
            .thenReturn(Mono.empty())
        Mockito.`when`(outputEvent.deferReply(ArgumentMatchers.any(InteractionCallbackSpec::class.java)))
            .thenReturn(Mono.empty())
    }
}
