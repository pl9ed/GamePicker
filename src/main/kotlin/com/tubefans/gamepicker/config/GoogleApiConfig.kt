package com.tubefans.gamepicker.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.tubefans.gamepicker.APP_NAME
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleApiConfig @Autowired constructor(
    private val credentials: Credential,
    private val jsonFactory: JsonFactory
) {

    @Bean
    fun sheets(): Sheets =
        Sheets
            .Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                jsonFactory,
                credentials
            ).setApplicationName(APP_NAME)
            .build()

}
