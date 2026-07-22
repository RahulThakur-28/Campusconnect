package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.Event

interface EventRemoteDataSource {

    /**
     * Returns all active events.
     */
    suspend fun getAllEvents(): List<Event>

    /**
     * Returns a single event by ID.
     */
    suspend fun getEventById(
        eventId: String
    ): Event?

    /**
     * Creates a new event.
     */

    /**
     * Updates an existing event.
     */
    suspend fun updateEvent(
        event: Event
    )

    /**
     * Soft deletes an event.
     */
    suspend fun deleteEvent(
        eventId: String
    )

    /**
     * Returns featured events.
     */
    suspend fun getFeaturedEvents(): List<Event>

    /**
     * Returns upcoming events.
     */
    suspend fun getUpcomingEvents(): List<Event>

    /**
     * Returns events created by the given user.
     */
    suspend fun getMyEvents(
        userId: String
    ): List<Event>

    /**
     * Uploads an event image and returns its public URL.
     */
    suspend fun uploadEventImage(
        imageUri: Uri
    ): Result<String>

    /**
     * Registers a user for an event.
     */
    suspend fun registerForEvent(
        eventId: String,
        userId: String
    )

    /**
     * Cancels a user's registration.
     */
    suspend fun unregisterFromEvent(
        eventId: String,
        userId: String
    )


    suspend fun isUserRegistered(
        eventId: String,
        userId: String
    ): Boolean


    suspend fun createEvent(
        event: Event
    )


}