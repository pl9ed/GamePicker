package com.tubefans.gamepicker.models

data class UserItemList(
    val userId: String? = null,
    val flipItems: List<Int>,
    val priceCheckItems: List<Int>,
    val craftingItems: List<Int>,
)
