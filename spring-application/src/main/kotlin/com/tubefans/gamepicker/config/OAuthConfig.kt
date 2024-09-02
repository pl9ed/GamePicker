package com.tubefans.gamepicker.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
import java.io.FileInputStream

@Configuration
class OAuthConfig {
    companion object {
        const val CREDENTIALS_FILE_PATH = "service-key.json"
        val scopes: List<String> =
            listOf(
                SheetsScopes.SPREADSHEETS,
                "https://www.googleapis.com/auth/cloud-platform",
                "https://www.googleapis.com/auth/drive",
            )
    }

    @Bean
    fun jsonFactory(): JsonFactory = GsonFactory.getDefaultInstance()

    @Bean
    fun netHttpTransport(): NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    // TODO: use a single credential implementation xd

    @Bean
    fun getCredentialsV2(netHttpTransport: NetHttpTransport): Credential =
        GoogleCredential
            .fromStream(FileInputStream(ResourceUtils.getFile("classpath:$CREDENTIALS_FILE_PATH")))
            .createScoped(scopes)

    @Bean
    fun getCredentials(): Credentials =
        GoogleCredentials
            .fromStream(FileInputStream(ResourceUtils.getFile("classpath:$CREDENTIALS_FILE_PATH")))
            .createScoped(scopes)
}
