package com.tubefans.arbitragexiv.dto

data class AddItemRequest(
    val userId: String,
    val additionalItems: Map<String, List<Int>>,
)
