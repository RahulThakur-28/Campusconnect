package com.rahul.campusconnect.common.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs =
        context.getSharedPreferences("campusconnect_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COLLEGE_ID = "last_college_id"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun saveCollegeId(collegeId: String) {
        prefs.edit().putString(KEY_COLLEGE_ID, collegeId).apply()
    }

    fun getCollegeId(): String? {
        return prefs.getString(KEY_COLLEGE_ID, null)
    }

    // ---------------- Onboarding ----------------

    fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}