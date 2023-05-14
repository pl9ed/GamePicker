package com.tubefans.gamepicker.dto

class UserScore(
    val user: BotUser,
    val score: Long
) : Comparable<UserScore> {

    override fun compareTo(other: UserScore): Int {
        return if (this.score != other.score) {
            this.score.compareTo(other.score)
        } else {
            compareValues(this.user.username, other.user.username)
        }
    }
}
