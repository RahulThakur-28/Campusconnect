package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.EventRemoteDataSource
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.domain.repository.EventRepository
import com.rahul.campusconnect.domain.repository.NotificationRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val remoteDataSource: EventRemoteDataSource,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository
) : EventRepository {

    private fun getCollegeId(): String {
        return sessionManager.getCollegeId() ?: throw IllegalStateException("College ID not found in session")
    }

    override suspend fun getAllEvents(): Result<List<Event>> = remoteDataSource.getAllEvents(getCollegeId())

    override suspend fun getEventById(eventId: String): Result<Event?> = remoteDataSource.getEventById(getCollegeId(), eventId)

    override suspend fun createEvent(event: Event): Result<String> {
        val result = remoteDataSource.createEvent(getCollegeId(), event)
        result.onSuccess { id ->
            notificationRepository.sendNotification(
                Notification(
                    userId = "ALL",
                    title = "New Event: ${event.title}",
                    message = "At ${event.venue} on ${event.time}",
                    type = NotificationType.EVENT,
                    relatedId = id,
                    collegeId = event.collegeId
                )
            )
        }
        return result
    }

    override suspend fun updateEvent(event: Event): Result<Unit> = remoteDataSource.updateEvent(getCollegeId(), event)

    override suspend fun deleteEvent(eventId: String): Result<Unit> = remoteDataSource.deleteEvent(getCollegeId(), eventId)

    override suspend fun getUpcomingEvents(): Result<List<Event>> = remoteDataSource.getUpcomingEvents(getCollegeId())

    override suspend fun getFeaturedEvents(): Result<List<Event>> = remoteDataSource.getFeaturedEvents(getCollegeId())

    override suspend fun getMyEvents(userId: String): Result<List<Event>> = remoteDataSource.getMyEvents(getCollegeId(), userId)

    override suspend fun uploadEventImage(
        eventId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> = try {
        uploadEventImage(getCollegeId(), eventId, imageUri)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadEventImage(
        collegeId: String,
        eventId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> =
        remoteDataSource.uploadEventImage(collegeId, eventId, imageUri)

    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)

    override suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> =
        remoteDataSource.registerForEvent(getCollegeId(), eventId, userId)

    override suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit> =
        remoteDataSource.unregisterFromEvent(getCollegeId(), eventId, userId)

    override suspend fun isUserRegistered(eventId: String, userId: String): Result<Boolean> =
        remoteDataSource.isUserRegistered(getCollegeId(), eventId, userId)

    override fun generateEventId(): String = remoteDataSource.generateEventId()
}
