package com.tubefans.arbitragexiv.dao

import com.tubefans.arbitragexiv.models.ListType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user_item_list")
data class UserItemList(
    @Id val userId: String? = null,
    val itemLists: Map<ListType, Set<Int>>,
)
