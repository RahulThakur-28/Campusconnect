package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.Event

interface EventRemoteDataSource {

    suspend fun getAllEvents(collegeId: String): Result<List<Event>>

    suspend fun getEventById(collegeId: String, eventId: String): Result<Event?>

    suspend fun createEvent(collegeId: String, event: Event): Result<String>

    suspend fun updateEvent(collegeId: String, event: Event): Result<Unit>

    suspend fun deleteEvent(collegeId: String, eventId: String): Result<Unit>

    suspend fun getFeaturedEvents(collegeId: String): Result<List<Event>>

    suspend fun getUpcomingEvents(collegeId: String): Result<List<Event>>

    suspend fun getMyEvents(collegeId: String, userId: String): Result<List<Event>>

    suspend fun uploadEventImage(collegeId: String, eventId: String, imageUri: Uri): Result<Pair<String, String>>

    suspend fun deleteFile(path: String): Result<Unit>

    suspend fun registerForEvent(collegeId: String, eventId: String, userId: String): Result<Unit>

    suspend fun unregisterFromEvent(collegeId: String, eventId: String, userId: String): Result<Unit>

    suspend fun isUserRegistered(collegeId: String, eventId: String, userId: String): Result<Boolean>

    fun generateEventId(): String
}
