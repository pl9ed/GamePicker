package com.tubefans.arbitragexiv.services

import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.models.UserItemList
import com.tubefans.arbitragexiv.repositories.UserItemListRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

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
                        flipItems = emptyList(),
                        priceCheckItems = emptyList(),
                        craftingItems = emptyList(),
                    ),
                ),
            ).map { originalEntity ->
                var updatedEntity: UserItemList = originalEntity
                for ((listKey, items) in request.additionalItems) {
                    val itemList = getList(updatedEntity, listKey).toMutableList()
                    itemList.addAll(items)

                    val property =
                        updatedEntity::class.memberProperties.firstOrNull { it.name == listKey }
                            ?: throw IllegalArgumentException("No such property: $listKey")

                    property.isAccessible = true
                    val constructor = updatedEntity::class.primaryConstructor!!
                    val args =
                        constructor.parameters.associateWith { param ->
                            if (param.name == listKey) itemList else property.call(updatedEntity)
                        }

                    updatedEntity = updatedEntity::class.primaryConstructor!!.callBy(args)
                }
                updatedEntity
            }.flatMap { updatedEntity ->
                userItemListRepository.save(updatedEntity)
            }

    @Suppress("UNCHECKED_CAST")
    private fun getList(
        list: UserItemList,
        listName: String,
    ): List<Int> = list::class.memberProperties.first { it.name == listName }.call() as List<Int>
}
