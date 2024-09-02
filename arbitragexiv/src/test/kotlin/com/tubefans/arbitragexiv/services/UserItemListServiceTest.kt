package com.tubefans.arbitragexiv.services

import com.tubefans.arbitragexiv.dao.UserItemList
import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.models.ListType
import com.tubefans.arbitragexiv.repositories.UserItemListRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@DisplayName("UserItemListService")
class UserItemListServiceTest {
    private lateinit var userItemListRepository: UserItemListRepository
    private lateinit var userItemListService: UserItemListService

    @BeforeEach
    fun setup() {
        userItemListRepository = mock(UserItemListRepository::class.java)
        userItemListService = UserItemListService(userItemListRepository)
    }

    @Test
    @DisplayName("should get user item list")
    fun getUserItemListReturnsExistingList() {
        val userId = "user123"
        val userItemList = UserItemList(userId, mapOf(ListType.FLIP to setOf(1, 2, 3)))
        `when`(userItemListRepository.findById(userId)).thenReturn(Mono.just(userItemList))

        val result = userItemListService.getUserItemList(userId)

        StepVerifier
            .create(result)
            .expectNext(userItemList)
            .verifyComplete()
    }

    @Test
    @DisplayName("should return empty when user item list not found")
    fun getUserItemListReturnsEmptyWhenNotFound() {
        val userId = "user123"
        `when`(userItemListRepository.findById(userId)).thenReturn(Mono.empty())

        val result = userItemListService.getUserItemList(userId)

        StepVerifier
            .create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("should add items to user item list")
    fun addItemAddsNewItemsToExistingList() {
        val userId = "user123"
        val existingList = UserItemList(userId, mapOf(ListType.FLIP to setOf(1, 2, 3)))
        val request = AddItemRequest(userId, mapOf(ListType.CRAFTING to setOf(4, 5)))
        val expectedList = UserItemList(userId, mapOf(ListType.FLIP to setOf(1, 2, 3), ListType.CRAFTING to setOf(4, 5)))
        `when`(userItemListRepository.findById(userId)).thenReturn(Mono.just(existingList))
        doAnswer { invocation ->
            val saveArg = invocation.getArgument(0) as UserItemList
            Mono.just(saveArg)
        }.`when`(userItemListRepository).save(any(UserItemList::class.java))

        val result = userItemListService.addItem(request)

        StepVerifier
            .create(result)
            .assertNext { assertEquals(expectedList, it) }
            .verifyComplete()
    }

    @Test
    @DisplayName("add operation should create new lists when not found")
    fun addItemCreatesNewListWhenNotFound() {
        val userId = "user123"
        val request = AddItemRequest(userId, mapOf(ListType.FLIP to setOf(1, 2, 3)))
        val newList = UserItemList(userId, mapOf(ListType.FLIP to setOf(1, 2, 3)))
        `when`(userItemListRepository.findById(userId)).thenReturn(Mono.empty())
        `when`(userItemListRepository.save(any(UserItemList::class.java))).thenReturn(Mono.just(newList))

        val result = userItemListService.addItem(request)

        StepVerifier
            .create(result)
            .assertNext { assertEquals(newList, it) }
            .verifyComplete()
    }
}
