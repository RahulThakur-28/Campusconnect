package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.EventRemoteDataSource
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val remoteDataSource: EventRemoteDataSource
) : EventRepository {

    override suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            Result.success(remoteDataSource.getAllEvents())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEventById(
        eventId: String
    ): Result<Event?> {
        return try {
            Result.success(remoteDataSource.getEventById(eventId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createEvent(
        event: Event
    ): Result<Unit> {
        return try {
            remoteDataSource.createEvent(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(
        event: Event
    ): Result<Unit> {
        return try {
            remoteDataSource.updateEvent(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(
        eventId: String
    ): Result<Unit> {
        return try {
            remoteDataSource.deleteEvent(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingEvents(): Result<List<Event>> {
        return try {
            Result.success(remoteDataSource.getUpcomingEvents())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFeaturedEvents(): Result<List<Event>> {
        return try {
            Result.success(remoteDataSource.getFeaturedEvents())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyEvents(
        userId: String
    ): Result<List<Event>> {
        return try {
            Result.success(remoteDataSource.getMyEvents(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadEventImage(
        imageUri: Uri
    ): Result<String> {
        return try {
            remoteDataSource.uploadEventImage(imageUri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerForEvent(
        eventId: String,
        userId: String
    ): Result<Unit> {
        return try {
            remoteDataSource.registerForEvent(eventId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unregisterFromEvent(
        eventId: String,
        userId: String
    ): Result<Unit> {
        return try {
            remoteDataSource.unregisterFromEvent(eventId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}