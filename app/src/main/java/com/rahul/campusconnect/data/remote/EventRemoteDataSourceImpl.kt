package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Event
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class EventRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageManager: StorageManager
) : EventRemoteDataSource {

    private suspend fun getCollegeId(): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in.")

        val snapshot = firestore
            .collection(Constants.USERS)
            .document(uid)
            .get()
            .await()

        return snapshot.getString("collegeId")
            ?: throw IllegalStateException("College ID not found.")
    }

    private suspend fun eventsCollection(): CollectionReference {
        return firestore.collection(Constants.COLLEGES)
            .document(getCollegeId())
            .collection(Constants.EVENTS)
    }

    override suspend fun getAllEvents(): Result<List<Event>> = try {
        Log.d("EVENT_QUERY", "Fetching all events...")
        val snapshot = eventsCollection()
            .whereEqualTo("deleted", false)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .await()

        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: FirebaseFirestoreException) {
        Log.e("EVENT_QUERY", "Firestore Error: ${e.message}", e)
        Result.failure(e)
    } catch (e: Exception) {
        Log.e("EVENT_QUERY", "Error: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun getEventById(eventId: String): Result<Event?> = try {
        val snapshot = eventsCollection().document(eventId).get().await()
        val event = snapshot.toObject(Event::class.java)?.copy(id = snapshot.id)
        if (event?.deleted == true) Result.success(null) else Result.success(event)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createEvent(event: Event): Result<String> = try {
        val document = eventsCollection().document(event.id.ifBlank { generateEventId() })
        val collegeId = getCollegeId()
        val finalEvent = event.copy(
            id = document.id,
            collegeId = collegeId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            deleted = false
        )
        document.set(finalEvent).await()
        Log.d("EVENT_CREATE", "Event created: ${document.id}")
        Result.success(document.id)
    } catch (e: Exception) {
        Log.e("EVENT_CREATE", "Error creating event", e)
        Result.failure(e)
    }

    override suspend fun updateEvent(event: Event): Result<Unit> = try {
        val updatedEvent = event.copy(updatedAt = System.currentTimeMillis())
        eventsCollection().document(event.id).set(updatedEvent).await()
        Log.d("EVENT_UPDATE", "Event updated: ${event.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("EVENT_UPDATE", "Error updating event", e)
        Result.failure(e)
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> = try {
        eventsCollection().document(eventId).update(
            mapOf(
                "deleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
        Log.d("EVENT_DELETE", "Event soft deleted: $eventId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("EVENT_DELETE", "Error deleting event", e)
        Result.failure(e)
    }

    override suspend fun getFeaturedEvents(): Result<List<Event>> = try {
        val snapshot = eventsCollection()
            .whereEqualTo("isFeatured", true)
            .whereEqualTo("deleted", false)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .await()
        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUpcomingEvents(): Result<List<Event>> = try {
        val snapshot = eventsCollection()
            .whereGreaterThanOrEqualTo("startDate", System.currentTimeMillis())
            .whereEqualTo("deleted", false)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .await()
        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMyEvents(userId: String): Result<List<Event>> = try {
        val snapshot = eventsCollection()
            .whereEqualTo("organizerId", userId)
            .whereEqualTo("deleted", false)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
        Result.success(events)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadEventImage(eventId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.eventBanner(getCollegeId(), eventId)
        storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Log.e("EVENT_UPLOAD", "Error uploading image", e)
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = try {
        storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> = try {
        val eventRef = eventsCollection().document(eventId)
        firestore.runTransaction { transaction ->
            val eventSnapshot = transaction.get(eventRef)
            if (!eventSnapshot.exists()) throw IllegalStateException("Event not found")
            if (eventSnapshot.getBoolean("deleted") == true) throw IllegalStateException("Event deleted")
            if (eventSnapshot.getBoolean("isRegistrationOpen") == false) throw IllegalStateException("Registration closed")

            val registrationRef = eventRef.collection(Constants.REGISTRATIONS).document(userId)
            if (transaction.get(registrationRef).exists()) throw IllegalStateException("Already registered")

            val registeredCount = eventSnapshot.getLong("registeredCount")?.toInt() ?: 0
            val maxParticipants = eventSnapshot.getLong("maxParticipants")?.toInt() ?: Int.MAX_VALUE

            if (registeredCount >= maxParticipants) throw IllegalStateException("Event full")

            transaction.set(registrationRef, mapOf("userId" to userId, "registeredAt" to System.currentTimeMillis()))
            transaction.update(eventRef, "registeredCount", registeredCount + 1)
        }.await()
        Log.d("EVENT_REGISTER", "User $userId registered for $eventId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("EVENT_REGISTER", "Registration failed", e)
        Result.failure(e)
    }

    override suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit> = try {
        val eventRef = eventsCollection().document(eventId)
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

    override suspend fun isUserRegistered(eventId: String, userId: String): Result<Boolean> = try {
        val snapshot = eventsCollection().document(eventId).collection(Constants.REGISTRATIONS).document(userId).get().await()
        Result.success(snapshot.exists())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun generateEventId(): String = UUID.randomUUID().toString()
}
