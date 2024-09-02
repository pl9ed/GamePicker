package com.tubefans.arbitragexiv.models

data class UserItemList(
    val userId: String? = null,
    val itemLists: Map<ListType, List<Int>>,
)
