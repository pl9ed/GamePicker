package com.tubefans.gamepicker.repositories

import java.time.LocalDate

interface YoCountRepository {
    fun getThreshold(): Int
    fun getStartDate(): LocalDate
    fun findCount(): Int
    fun increment(): Int
}
