package com.tubefans.gamepicker.repositories

import org.apache.logging.log4j.LogManager

class InMemoryYoCountRepository : YoCountRepository {
    companion object {
        const val DEFAULT_THRESHOLD = 50
    }
    private val logger = LogManager.getLogger()
    private var count = 0
    private val threshold = try {
        System.getenv("YO_THRESHOLD").toInt()
    } catch (e: Exception) {
        logger.error("Failed to get YO_THRESHOLD. Falling back to default", e)
        DEFAULT_THRESHOLD
    }

    override fun getThreshold(): Int = threshold

    override fun findCount(): Int = count

    override fun increment() {
        count += 1
    }
}
