package com.tubefans.gamepicker.repositories

import java.time.LocalDate

interface YoCountRepository {
    var serviceInitDate: LocalDate
    fun getThreshold(): Int
    fun findCount(): Int
    fun increment(): Int
}
