import com.tubefans.gamepicker.models.GameScoreMap.Companion.MAX_SCORE

fun Any?.toName(): String? =
    this?.toString()?.takeIf {
        it != "null" && it.isNotBlank()
    }

fun Any?.toScore(): Long? {
    if (this.toString().isBlank()) return null
    if (this.toString() == "null") return null
    return try {
        /*
        We need to handle this separately so we know which
        toLong() implementation to use
         */
        val number: Long = when (this) {
            is Float -> this.toLong()
            is Double -> this.toLong()
            else -> this.toString().toLong()
        }
        when {
            number > 10L -> 10L
            number < 0L -> 0L
            else -> number
        }
    } catch (e: NumberFormatException) {
        MAX_SCORE
    }
}

fun List<List<Any>>.mapToString(): List<List<String>> =
    this.map { row ->
        row.map {
            it.toString()
        }
    }