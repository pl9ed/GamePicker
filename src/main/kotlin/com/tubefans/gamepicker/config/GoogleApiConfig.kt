package com.tubefans.gamepicker.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.auth.Credentials
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings
import com.tubefans.gamepicker.APP_NAME
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleApiConfig @Autowired constructor(
    private val netHttpTransport: NetHttpTransport,
    private val credentialV2: Credential,
    private val credentials: Credentials,
    private val jsonFactory: JsonFactory
) {

    @Bean
    fun sheets(): Sheets =
        Sheets
            .Builder(
                netHttpTransport,
                jsonFactory,
                credentialV2
            ).setApplicationName(APP_NAME)
            .build()

    @Bean
    fun secretManagerServiceClient(): SecretManagerServiceClient =
        SecretManagerServiceClient.create(
            SecretManagerServiceSettings.newBuilder()
                .setCredentialsProvider {
                    credentials
                }.build()
        )
}
