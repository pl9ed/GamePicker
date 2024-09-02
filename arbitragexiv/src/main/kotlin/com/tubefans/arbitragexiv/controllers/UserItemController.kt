package com.tubefans.arbitragexiv.controllers

import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.models.UserItemList
import com.tubefans.arbitragexiv.services.UserItemListService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/arbitragexiv/items")
class UserItemController(
    private val userItemListService: UserItemListService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{userId}")
    fun getItems(
        @PathVariable("userId") userId: String,
    ): Mono<UserItemList> {
        log.info("Getting items for user $userId")
        return userItemListService.getUserItemList(userId)
    }

    @PostMapping("/add")
    fun addItems(
        @RequestBody addRequest: AddItemRequest,
    ): Mono<UserItemList> {
        log.info("Adding items for user ${addRequest.userId}, request: $addRequest")
        return userItemListService.addItem(addRequest)
    }
}
