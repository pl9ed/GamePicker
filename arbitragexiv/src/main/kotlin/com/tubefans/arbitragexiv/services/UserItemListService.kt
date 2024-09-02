package com.tubefans.arbitragexiv.services

import com.tubefans.arbitragexiv.dao.UserItemList
import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.dto.RemoveItemRequest
import com.tubefans.arbitragexiv.exceptions.NotFoundException
import com.tubefans.arbitragexiv.repositories.UserItemListRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserItemListService(
    private val userItemListRepository: UserItemListRepository,
) {
    fun getUserItemList(userId: String): Mono<UserItemList> =
        userItemListRepository
            .findById(userId)
            .switchIfEmpty { Mono.error(NotFoundException("No items found for $userId")) }

    fun addItem(request: AddItemRequest): Mono<UserItemList> =
        userItemListRepository
            .findById(request.userId)
            .switchIfEmpty(
                Mono.just(
                    UserItemList(
                        userId = request.userId,
                        itemLists = emptyMap(),
                    ),
                ),
            ).map { originalEntity ->
                val updatedMap = originalEntity.itemLists.toMutableMap()
                for ((listKey, items) in request.additionalItems) {
                    val updatedSet = originalEntity.itemLists[listKey]?.toMutableSet() ?: mutableSetOf()
                    updatedSet.addAll(items)
                    updatedMap[listKey] = updatedSet
                }
                originalEntity.copy(itemLists = updatedMap)
            }.flatMap { updatedEntity ->
                userItemListRepository.save(updatedEntity)
            }

    fun removeItem(request: RemoveItemRequest): Mono<UserItemList> =
        userItemListRepository
            .findById(request.userId)
            .map { originalEntity ->
                val updatedMap = originalEntity.itemLists.toMutableMap()
                for ((listKey, items) in request.itemsToRemove) {
                    val updatedSet = originalEntity.itemLists[listKey]?.toMutableSet() ?: mutableSetOf()
                    updatedSet.removeAll(items)
                    updatedMap[listKey] = updatedSet
                }
                originalEntity.copy(itemLists = updatedMap)
            }.flatMap { updatedEntity ->
                userItemListRepository.save(updatedEntity)
            }.switchIfEmpty(
                Mono.just(
                    UserItemList(
                        userId = request.userId,
                        itemLists = emptyMap(),
                    ),
                ),
            )
}
