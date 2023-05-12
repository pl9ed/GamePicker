package com.tubefans.gamepicker.dto

class UserScore(
    val user: BotUser,
    val score: Long
) : Comparable<UserScore> {

    override fun compareTo(other: UserScore): Int {
        val scoreComparison = this.score.compareTo(other.score)
        return if (scoreComparison != 0) scoreComparison
        else this.user.username.compareTo(other.user.username)
    }
}
