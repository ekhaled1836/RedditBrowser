package me.ekhaled1836.tp.reddit.util

import kotlin.math.roundToInt

class PostUtils {
    companion object {
        fun getTime(created_utc: Long): String {
            val timeSinceCreation = System.currentTimeMillis() / 1000 - created_utc
            return when {
                timeSinceCreation < 60 -> "${(timeSinceCreation).toInt()}s"
                timeSinceCreation < 3600 -> "${(timeSinceCreation / 60).toInt()}min"
                timeSinceCreation < 86400 -> "${(timeSinceCreation / 3600).toInt()}h"
                timeSinceCreation < 2629800 -> "${(timeSinceCreation / 86400).toInt()}d"
                timeSinceCreation < 31557600 -> "${(timeSinceCreation / 2629800).toInt()}mon"
                else -> "${(timeSinceCreation / 31557600).toInt()}y"
            }
        }

        fun getShortNumber(number: Int): String {
            return if (number < 1000) "$number" else "${(number / 1000.0).roundToInt()}k"
        }
    }
}