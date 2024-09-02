package com.tubefans.arbitragexiv.repositories

import com.tubefans.arbitragexiv.models.UserItemList
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserItemListRepository : ReactiveMongoRepository<UserItemList, String>
