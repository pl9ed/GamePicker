package com.tubefans.arbitragexiv.repositories

import com.tubefans.gamepicker.models.UserItemList
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserItemListRepository : MongoRepository<UserItemList, String>
