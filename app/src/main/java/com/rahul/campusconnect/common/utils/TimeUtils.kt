package com.rahul.campusconnect.common.utils

import android.text.format.DateUtils
import java.util.Date

object TimeUtils {

    /**
     * Converts a timestamp in milliseconds to a relative time string.
     * Examples: "Just now", "5 minutes ago", "2 hours ago", "Yesterday", "3 days ago".
     */
    fun getRelativeTime(timestamp: Long): String {
        if (timestamp <= 0) return ""
        
        val now = System.currentTimeMillis()
        val difference = now - timestamp
        
        return when {
            difference < DateUtils.MINUTE_IN_MILLIS -> "Just now"
            else -> DateUtils.getRelativeTimeSpanString(
                timestamp,
                now,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
    }
    
    /**
     * Formats a timestamp to a readable date.
     */
    fun formatDate(timestamp: Long): String {
        return if (timestamp <= 0) "" else Date(timestamp).toString()
    }
}
