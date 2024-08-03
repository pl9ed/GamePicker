package com.tubefans.gamepicker.config

import com.google.api.gax.rpc.ApiException
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ec2.Ec2Client

@Configuration
class AWSConfig
    @Autowired
    constructor(
        private val googleSecretsManager: SecretManagerServiceClient,
    ) {
        companion object {
            const val ACCESS_ID_KEY = "projects/891049573637/secrets/AWS_ACCESS_KEY_ID/versions/latest"
            const val SECRET_KEY = "projects/891049573637/secrets/AWS_SECRET_ACCESS_KEY/versions/latest"
        }

        @Bean
        fun ec2Client(): Ec2Client =
            Ec2Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider { basicCredentials() }
                .build()

        @Bean
        fun basicCredentials(): AwsCredentials =
            try {
                val accessId =
                    googleSecretsManager.accessSecretVersion(ACCESS_ID_KEY)
                        .payload
                        .data
                        .toStringUtf8()
                val secret =
                    googleSecretsManager.accessSecretVersion(SECRET_KEY)
                        .payload
                        .data
                        .toStringUtf8()
                AwsBasicCredentials.create(
                    accessId,
                    secret,
                )
            } catch (e: ApiException) {
                DefaultCredentialsProvider.create().resolveCredentials()
            }
    }
