package com.tubefans.arbitragexiv.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.data.mongodb")
data class MongoConfigurationProperties(
    val username: String,
    val uri: String,
)
