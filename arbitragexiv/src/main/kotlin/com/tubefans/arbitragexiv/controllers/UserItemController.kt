package com.tubefans.arbitragexiv.controllers

import com.tubefans.arbitragexiv.dto.AddItemRequest
import com.tubefans.arbitragexiv.services.UserItemListService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class UserItemController(
    private val userItemListService: UserItemListService,
) {
    @GetMapping("/arbitragexiv/{userId}/items")
    fun getItems(
        @PathVariable("userId") userId: String,
    ) = userItemListService.getUserItemList(userId)

    @PostMapping("/arbitragexiv/items")
    fun addItems(
        @RequestBody addRequest: AddItemRequest,
    ) = userItemListService.addItem(addRequest)
}
