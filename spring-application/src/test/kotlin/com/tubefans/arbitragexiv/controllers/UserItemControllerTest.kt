package com.tubefans.arbitragexiv.controllers

import com.tubefans.arbitragexiv.dao.UserItemList
import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.dto.RemoveItemRequest
import com.tubefans.arbitragexiv.models.ListType
import com.tubefans.arbitragexiv.repositories.UserItemListRepository
import com.tubefans.arbitragexiv.services.UserItemListService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@WebFluxTest(UserItemController::class)
@Import(UserItemListService::class)
@ContextConfiguration(classes = [UserItemController::class])
@DisplayName("UserItemController")
class UserItemControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var userItemListRepository: UserItemListRepository

    @BeforeEach
    fun setup() {
        doAnswer { invocation ->
            Mono.just(invocation.arguments[0])
        }.`when`(userItemListRepository).save(any(UserItemList::class.java))
    }

    @Test
    @DisplayName("should get items for a user")
    fun `test getItems endpoint`() {
        val userId = "testUser"
        val userItemList = UserItemList(userId, mapOf(ListType.PRICE_CHECK to setOf(1, 2)))

        Mockito.`when`(userItemListRepository.findById(userId)).thenReturn(Mono.just(userItemList))

        webTestClient
            .get()
            .uri("/arbitragexiv/items/$userId")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<UserItemList>()
            .consumeWith { assertEquals(userItemList, it.responseBody) }
    }

    @Test
    @DisplayName("should add items to a user's list")
    fun `test addItems endpoint`() {
        val userId = "testUser"
        val originalItemList = UserItemList(userId, mapOf(ListType.FLIP to setOf(1, 2)))
        val addItemRequest = AddItemRequest(userId, mapOf(ListType.FLIP to setOf(3, 4)))
        val expectedResponse = UserItemList(addItemRequest.userId, mapOf(ListType.FLIP to setOf(1, 2, 3, 4)))

        Mockito.`when`(userItemListRepository.findById(userId)).thenReturn(Mono.just(originalItemList))

        webTestClient
            .post()
            .uri("/arbitragexiv/items")
            .bodyValue(addItemRequest)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<UserItemList>()
            .consumeWith { assertEquals(expectedResponse, it.responseBody) }
    }

    @Test
    @DisplayName("should remove items from a user's list")
    fun `test deleteItems endpoint`() {
        val userId = "testUser"
        val originalItemList = UserItemList(userId, mapOf(ListType.CRAFTING to setOf(1, 2)))
        val removeItemRequest = RemoveItemRequest(userId, mapOf(ListType.CRAFTING to setOf(1)))
        val expectedResponse = UserItemList(removeItemRequest.userId, mapOf(ListType.CRAFTING to setOf(2)))

        Mockito.`when`(userItemListRepository.findById(removeItemRequest.userId)).thenReturn(Mono.just(originalItemList))

        webTestClient
            .post()
            .uri("/arbitragexiv/items/remove")
            .bodyValue(removeItemRequest)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<UserItemList>()
            .consumeWith { assertEquals(expectedResponse, it.responseBody) }
    }
}
