package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.EventRemoteDataSource
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val remoteDataSource: EventRemoteDataSource
) : EventRepository {

    override suspend fun getAllEvents(): Result<List<Event>> = remoteDataSource.getAllEvents()

    override suspend fun getEventById(eventId: String): Result<Event?> = remoteDataSource.getEventById(eventId)

    override suspend fun createEvent(event: Event): Result<String> = remoteDataSource.createEvent(event)

    override suspend fun updateEvent(event: Event): Result<Unit> = remoteDataSource.updateEvent(event)

    override suspend fun deleteEvent(eventId: String): Result<Unit> = remoteDataSource.deleteEvent(eventId)

    override suspend fun getUpcomingEvents(): Result<List<Event>> = remoteDataSource.getUpcomingEvents()

    override suspend fun getFeaturedEvents(): Result<List<Event>> = remoteDataSource.getFeaturedEvents()

    override suspend fun getMyEvents(userId: String): Result<List<Event>> = remoteDataSource.getMyEvents(userId)

    override suspend fun uploadEventImage(eventId: String, imageUri: Uri): Result<Pair<String, String>> =
        remoteDataSource.uploadEventImage(eventId, imageUri)

    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)

    override suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> =
        remoteDataSource.registerForEvent(eventId, userId)

    override suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit> =
        remoteDataSource.unregisterFromEvent(eventId, userId)

    override suspend fun isUserRegistered(eventId: String, userId: String): Result<Boolean> =
        remoteDataSource.isUserRegistered(eventId, userId)

    override fun generateEventId(): String = remoteDataSource.generateEventId()
}
