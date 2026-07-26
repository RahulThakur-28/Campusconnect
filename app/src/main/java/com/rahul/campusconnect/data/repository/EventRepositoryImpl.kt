package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.EventRemoteDataSource
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val remoteDataSource: EventRemoteDataSource,
    private val sessionManager: SessionManager
) : EventRepository {

    private fun getCollegeId(): String {
        return sessionManager.getCollegeId() ?: throw IllegalStateException("College ID not found in session")
    }

    override suspend fun getAllEvents(): Result<List<Event>> = remoteDataSource.getAllEvents(getCollegeId())

    override suspend fun getEventById(eventId: String): Result<Event?> = remoteDataSource.getEventById(getCollegeId(), eventId)

    override suspend fun createEvent(event: Event): Result<String> = remoteDataSource.createEvent(getCollegeId(), event)

    override suspend fun updateEvent(event: Event): Result<Unit> = remoteDataSource.updateEvent(getCollegeId(), event)

    override suspend fun deleteEvent(eventId: String): Result<Unit> = remoteDataSource.deleteEvent(getCollegeId(), eventId)

    override suspend fun getUpcomingEvents(): Result<List<Event>> = remoteDataSource.getUpcomingEvents(getCollegeId())

    override suspend fun getFeaturedEvents(): Result<List<Event>> = remoteDataSource.getFeaturedEvents(getCollegeId())

    override suspend fun getMyEvents(userId: String): Result<List<Event>> = remoteDataSource.getMyEvents(getCollegeId(), userId)

    override suspend fun uploadEventImage(eventId: String, imageUri: Uri): Result<Pair<String, String>> =
        remoteDataSource.uploadEventImage(getCollegeId(), eventId, imageUri)

    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)

    override suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> =
        remoteDataSource.registerForEvent(getCollegeId(), eventId, userId)

    override suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit> =
        remoteDataSource.unregisterFromEvent(getCollegeId(), eventId, userId)

    override suspend fun isUserRegistered(eventId: String, userId: String): Result<Boolean> =
        remoteDataSource.isUserRegistered(getCollegeId(), eventId, userId)

    override fun generateEventId(): String = remoteDataSource.generateEventId()
}
