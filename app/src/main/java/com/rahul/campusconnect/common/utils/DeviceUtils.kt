package com.rahul.campusconnect.common.utils

import android.os.Build

object DeviceUtils {
    fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
    }
}
