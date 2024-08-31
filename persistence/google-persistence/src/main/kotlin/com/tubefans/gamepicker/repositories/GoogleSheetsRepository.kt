package com.tubefans.gamepicker.repositories

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.apache.http.client.HttpResponseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class GoogleSheetsRepository
    @Autowired
    constructor(
        private val sheets: Sheets,
    ) {
        companion object {
            private val log = LoggerFactory.getLogger(this::class.java)
        }

        fun getValueRange(
            id: String,
            range: String,
        ): ValueRange =
            try {
                sheets.spreadsheets().values()[id, range].execute()
            } catch (e: HttpResponseException) {
                log.error("Failed to get value range from Google API", e)
                ValueRange()
            }

        /**
         * @param id Google Sheet id
         * @param range String representation of range, e.x. 'A1:A5'
         * @return 2D array of objects from the sheet
         */
        fun getSheet(
            id: String,
            range: String,
        ): List<List<Any>> =
            try {
                sheets
                    .spreadsheets()
                    .values()[id, range]
                    .execute()
                    .getValues()
            } catch (e: HttpResponseException) {
                log.error("Failed to get sheet from Google API", e)
                emptyList()
            }

        fun writeRange(
            id: String,
            range: String,
            values: List<List<String>>,
        ) {
            log.info("Attempting to write to sheet at range {} for values {}", range, values.joinToString { it.joinToString() })
            val body = ValueRange().setValues(values)
            try {
                sheets
                    .spreadsheets()
                    .values()
                    .update(id, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute()
            } catch (e: GoogleJsonResponseException) {
                log.error("Failed to write to google sheet", e)
            }
        }
    }
