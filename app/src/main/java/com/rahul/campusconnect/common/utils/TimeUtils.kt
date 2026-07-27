package com.rahul.campusconnect.common.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

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
    
    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return ""
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        if (timestamp <= 0) return ""
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}
