package com.tubefans.gamepicker.repositories

interface YoCountRepository {
    fun getThreshold(): Int
    fun findCount(): Int
    fun increment()
}
