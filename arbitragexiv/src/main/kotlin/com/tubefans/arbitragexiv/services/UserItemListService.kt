package com.tubefans.arbitragexiv.services

import com.tubefans.arbitragexiv.dao.UserItemList
import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.repositories.UserItemListRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserItemListService(
    private val userItemListRepository: UserItemListRepository,
) {
    fun getUserItemList(userId: String): Mono<UserItemList> = userItemListRepository.findById(userId)

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
                    val updatedList = originalEntity.itemLists[listKey]?.toMutableSet() ?: mutableSetOf()
                    updatedList.addAll(items)
                    updatedMap[listKey] = updatedList
                }
                originalEntity.copy(itemLists = updatedMap)
            }.flatMap { updatedEntity ->
                userItemListRepository.save(updatedEntity)
            }
}
