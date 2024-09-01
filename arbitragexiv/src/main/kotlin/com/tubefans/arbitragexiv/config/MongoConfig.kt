package com.tubefans.arbitragexiv.config

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration

@Configuration
@EnableConfigurationProperties(MongoConfigurationProperties::class)
open class MongoConfig(
    private val googleSecretsManager: SecretManagerServiceClient,
    mongoConfigurationProperties: MongoConfigurationProperties,
) : AbstractReactiveMongoConfiguration() {
    companion object {
        const val MONGO_TOKEN_KEY = "projects/891049573637/secrets/ARBITRAGEXIV_MONGO_TOKEN/versions/latest"
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    private val username = mongoConfigurationProperties.username
    private val uri = mongoConfigurationProperties.uri

    override fun getDatabaseName(): String = "arbitragexiv"

    @Bean
    override fun mongoClientSettings(): MongoClientSettings {
        val mongoToken: String =
            googleSecretsManager
                .accessSecretVersion(MONGO_TOKEN_KEY)
                .payload.data
                .toStringUtf8()
        val connectionString = String.format(uri, username, mongoToken)

        return MongoClientSettings
            .builder()
            .applyConnectionString(ConnectionString(connectionString))
            .build()
    }

    @Bean
    open fun mongoClient(mongoClientSettings: MongoClientSettings): MongoClient =
        MongoClients.create(mongoClientSettings).also { mongoClient ->
            val database = mongoClient.getDatabase("arbitragexiv")
            runBlocking {
                log.info("Checking mongodb connection")
                val response =
                    database
                        .runCommand(Document("ping", 1))
                        .awaitSingle()
                log.info("Received response from mongodb: $response")
            }
        }
}
