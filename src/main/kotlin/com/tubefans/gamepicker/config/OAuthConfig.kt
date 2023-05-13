package com.tubefans.gamepicker.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.Collections
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OAuthConfig {

    companion object {
        const val CREDENTIALS_PATH = "./credentials.json"
        const val TOKENS_DIRECTORY_PATH = "tokens"
        val scopes = Collections.singletonList(
            SheetsScopes.SPREADSHEETS
        )
    }

    @Bean
    fun jsonFactory(): JsonFactory = GsonFactory.getDefaultInstance()

    @Bean
    fun getCredentials(netHttpTransport: NetHttpTransport): Credential {
        val input = this::class.java.getResourceAsStream(CREDENTIALS_PATH) ?: run {
            throw FileNotFoundException("Credentials not found on $CREDENTIALS_PATH")
        }

        val clientSecrets = GoogleClientSecrets.load(jsonFactory(), InputStreamReader(input))

        val flow = GoogleAuthorizationCodeFlow
            .Builder(
                netHttpTransport,
                jsonFactory(),
                clientSecrets,
                scopes
            ).setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}
