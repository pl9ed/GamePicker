import com.tubefans.gamepicker.services.GameScoreMap.Companion.MAX_SCORE

fun Any?.toName(): String? =
    this?.toString()?.takeIf {
        it != "null" && it.isNotBlank()
    }

fun Any?.toScore(): Long? {
    if (this.toString().isBlank()) return null
    if (this.toString() == "null") return null
    return try {
        this.toString().toLong()
    } catch (e: ClassCastException) {
        MAX_SCORE
    }
}