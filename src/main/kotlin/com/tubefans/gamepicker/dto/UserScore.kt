package com.tubefans.gamepicker.dto

class UserScore(
    val username: String,
    val score: Long
) : Comparable<UserScore> {

    override fun compareTo(other: UserScore): Int {
        val scoreComparison = this.score.compareTo(other.score)
        return if (scoreComparison != 0) scoreComparison
        else this.username.compareTo(other.username)
    }
}
