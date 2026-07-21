package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.Event

interface EventRepository {

    /**
     * Fetch all active events.
     */
    suspend fun getAllEvents(): Result<List<Event>>

    /**
     * Fetch a single event.
     */
    suspend fun getEventById(
        eventId: String
    ): Result<Event?>

    /**
     * Create a new event.
     */
    suspend fun createEvent(
        event: Event
    ): Result<Unit>

    /**
     * Update an existing event.
     */
    suspend fun updateEvent(
        event: Event
    ): Result<Unit>

    /**
     * Soft delete an event.
     */
    suspend fun deleteEvent(
        eventId: String
    ): Result<Unit>

    /**
     * Upcoming events.
     */
    suspend fun getUpcomingEvents(): Result<List<Event>>

    /**
     * Featured events.
     */
    suspend fun getFeaturedEvents(): Result<List<Event>>

    /**
     * Events created by a user.
     */
    suspend fun getMyEvents(
        userId: String
    ): Result<List<Event>>

    /**
     * Upload event banner and return its URL.
     */
    suspend fun uploadEventImage(
        imageUri: Uri
    ): Result<String>

    /**
     * Register current user for an event.
     */
    suspend fun registerForEvent(
        eventId: String,
        userId: String
    ): Result<Unit>

    /**
     * Cancel registration.
     */
    suspend fun unregisterFromEvent(
        eventId: String,
        userId: String
    ): Result<Unit>
}