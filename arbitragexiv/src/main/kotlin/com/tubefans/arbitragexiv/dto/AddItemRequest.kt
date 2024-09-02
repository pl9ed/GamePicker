package com.tubefans.arbitragexiv.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.tubefans.arbitragexiv.models.ListType

data class AddItemRequest(
    @JsonProperty("user_id") val userId: String,
    @JsonProperty("additional_items") val additionalItems: Map<ListType, Set<Int>>,
)
