package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.Event

interface EventRepository {

    suspend fun getAllEvents(): Result<List<Event>>

    suspend fun getEventById(eventId: String): Result<Event?>

    suspend fun createEvent(event: Event): Result<String>

    suspend fun updateEvent(event: Event): Result<Unit>

    suspend fun deleteEvent(eventId: String): Result<Unit>

    suspend fun getUpcomingEvents(): Result<List<Event>>

    suspend fun getFeaturedEvents(): Result<List<Event>>

    suspend fun getMyEvents(userId: String): Result<List<Event>>

    suspend fun uploadEventImage(eventId: String, imageUri: Uri): Result<Pair<String, String>>

    suspend fun uploadEventImage(collegeId: String, eventId: String, imageUri: Uri): Result<Pair<String, String>>

    suspend fun deleteFile(path: String): Result<Unit>

    suspend fun registerForEvent(eventId: String, userId: String): Result<Unit>

    suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit>

    suspend fun isUserRegistered(eventId: String, userId: String): Result<Boolean>

    fun generateEventId(): String
}
