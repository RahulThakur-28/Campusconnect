package com.rahul.campusconnect.common.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("campusconnect_prefs", Context.MODE_PRIVATE)

    fun saveCollegeId(collegeId: String) {
        prefs.edit().putString("last_college_id", collegeId).apply()
    }

    fun getCollegeId(): String? {
        return prefs.getString("last_college_id", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
