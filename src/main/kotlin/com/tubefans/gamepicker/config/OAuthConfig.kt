package com.tubefans.gamepicker.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.util.Collections

@Configuration
class OAuthConfig {

    companion object {
        const val CREDENTIALS_FILE = "service-key.json"
        const val TOKENS_DIRECTORY_PATH = "tokens"
        val scopes = Collections.singletonList(
            SheetsScopes.SPREADSHEETS
        )
    }

    @Bean
    fun gson() = GsonBuilder().create()

    @Bean
    fun jsonFactory(): JsonFactory = GsonFactory.getDefaultInstance()

    @Bean
    fun netHttpTransport(): NetHttpTransport =
        GoogleNetHttpTransport.newTrustedTransport()

    @Bean
    fun getCredentials(netHttpTransport: NetHttpTransport): Credential =
        GoogleCredential.fromStream(FileInputStream(CREDENTIALS_FILE))
            .createScoped(scopes)
}
