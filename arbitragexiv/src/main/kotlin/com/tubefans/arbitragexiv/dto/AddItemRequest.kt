package com.tubefans.arbitragexiv.dto

import com.tubefans.arbitragexiv.models.ListType

data class AddItemRequest(
    val userId: String,
    val additionalItems: Map<ListType, List<Int>>,
)
