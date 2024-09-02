package com.tubefans.arbitragexiv.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.tubefans.arbitragexiv.models.ListType

data class RemoveItemRequest(
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("items_to_remove") val itemsToRemove: Map<ListType, Set<Int>>,
)
