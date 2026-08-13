package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Event
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class EventRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager
) : EventRemoteDataSource {

    override suspend fun getAllEvents(collegeId: String): Result<List<Event>> = try {
        Log.d("EVENT_QUERY", "Fetching all events for college: $collegeId")
        val snapshot = pathProvider.events(collegeId)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .await()

        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Log.e("EVENT_QUERY", "Error fetching events", e)
        Result.failure(e)
    }

    override suspend fun getEventById(collegeId: String, eventId: String): Result<Event?> = try {
        val snapshot = pathProvider.events(collegeId).document(eventId).get().await()
        val event = snapshot.toObject(Event::class.java)?.copy(id = snapshot.id)
        Result.success(event)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createEvent(collegeId: String, event: Event): Result<String> = try {
        val document = pathProvider.events(collegeId).document(event.id.ifBlank { generateEventId() })
        val finalEvent = event.copy(
            id = document.id,
            collegeId = collegeId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            deleted = false
        )
        document.set(finalEvent).await()
        Result.success(document.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateEvent(collegeId: String, event: Event): Result<Unit> = try {
        val updatedEvent = event.copy(updatedAt = System.currentTimeMillis())
        pathProvider.events(collegeId).document(event.id).set(updatedEvent).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteEvent(collegeId: String, eventId: String): Result<Unit> = try {
        val eventRef = pathProvider.events(collegeId).document(eventId)
        val eventSnapshot = eventRef.get().await()
        val event = eventSnapshot.toObject(Event::class.java)

        // 1. Delete image from storage if exists
        event?.imageStoragePath?.let { path ->
            if (path.isNotBlank()) {
                storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
            }
        }

        // 2. Delete registrations subcollection
        val registrations = eventRef.collection(Constants.REGISTRATIONS).get().await()
        val batch = firestore.batch()
        for (doc in registrations.documents) {
            batch.delete(doc.reference)
        }
        
        // 3. Delete the event document
        batch.delete(eventRef)
        batch.commit().await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getFeaturedEvents(collegeId: String): Result<List<Event>> = try {
        val snapshot = pathProvider.events(collegeId)
            .whereEqualTo("isFeatured", true)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .await()
        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUpcomingEvents(collegeId: String): Result<List<Event>> = try {
        val snapshot = pathProvider.events(collegeId)
            .whereGreaterThanOrEqualTo("startDate", System.currentTimeMillis())
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .await()
        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMyEvents(collegeId: String, userId: String): Result<List<Event>> = try {
        val snapshot = pathProvider.events(collegeId)
            .whereEqualTo("organizerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadEventImage(collegeId: String, eventId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.eventBanner(collegeId, eventId)
        storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = try {
        storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun registerForEvent(collegeId: String, eventId: String, userId: String): Result<Unit> = try {
        val eventRef = pathProvider.events(collegeId).document(eventId)
        firestore.runTransaction { transaction ->
            val eventSnapshot = transaction.get(eventRef)
            if (!eventSnapshot.exists()) throw IllegalStateException("Event not found")
            if (eventSnapshot.getBoolean("isRegistrationOpen") == false) throw IllegalStateException("Registration closed")

            val registrationRef = eventRef.collection(Constants.REGISTRATIONS).document(userId)
            if (transaction.get(registrationRef).exists()) throw IllegalStateException("Already registered")

            val registeredCount = eventSnapshot.getLong("registeredCount")?.toInt() ?: 0
            val maxParticipants = eventSnapshot.getLong("maxParticipants")?.toInt() ?: Int.MAX_VALUE

            if (registeredCount >= maxParticipants) throw IllegalStateException("Event full")

            val currentTime = System.currentTimeMillis()
            transaction.set(registrationRef, mapOf("userId" to userId, "registeredAt" to currentTime))
            transaction.update(eventRef, mapOf(
                "registeredCount" to registeredCount + 1,
                "updatedAt" to currentTime
            ))
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unregisterFromEvent(collegeId: String, eventId: String, userId: String): Result<Unit> = try {
        val eventRef = pathProvider.events(collegeId).document(eventId)
        firestore.runTransaction { transaction ->
            val registrationRef = eventRef.collection(Constants.REGISTRATIONS).document(userId)
            if (!transaction.get(registrationRef).exists()) return@runTransaction

            val eventSnapshot = transaction.get(eventRef)
            val registeredCount = eventSnapshot.getLong("registeredCount")?.toInt() ?: 0

            transaction.delete(registrationRef)
            transaction.update(eventRef, "registeredCount", maxOf(0, registeredCount - 1))
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun isUserRegistered(collegeId: String, eventId: String, userId: String): Result<Boolean> = try {
        val snapshot = pathProvider.events(collegeId).document(eventId).collection(Constants.REGISTRATIONS).document(userId).get().await()
        Result.success(snapshot.exists())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun generateEventId(): String = UUID.randomUUID().toString()
}
