package com.tubefans.gamepicker.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.InputStreamReader
import java.util.Base64
import java.util.Collections

@Configuration
class OAuthConfig {

    companion object {
        const val CREDENTIALS_FILE = "/credentials.json"
        const val TOKENS_DIRECTORY_PATH = "tokens"
        val scopes = Collections.singletonList(
            SheetsScopes.SPREADSHEETS
        )
    }

    @Bean
    fun jsonFactory(): JsonFactory = GsonFactory.getDefaultInstance()

    @Bean
    fun netHttpTransport(): NetHttpTransport =
        GoogleNetHttpTransport.newTrustedTransport()

    @Bean
    fun getCredentials(netHttpTransport: NetHttpTransport): Credential {
        val clientSecrets: GoogleClientSecrets = this::class.java.getResourceAsStream(CREDENTIALS_FILE)?.let {
            GoogleClientSecrets.load(jsonFactory(), InputStreamReader(it))
        } ?: run {
            System.getenv("CREDENTIALS_JSON")?.let {
                jsonFactory().fromString(
                    String(Base64.getDecoder().decode(it)),
                    GoogleClientSecrets::class.java
                )
            }
        } ?: throw NullPointerException("Couldn't set up GoogleClientSecrets from $CREDENTIALS_FILE or CREDENTIALS_JSON")

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
