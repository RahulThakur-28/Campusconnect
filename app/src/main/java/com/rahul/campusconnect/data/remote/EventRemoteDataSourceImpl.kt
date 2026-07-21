package com.rahul.campusconnect.data.remote


import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
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

    /**
     * Returns the current user's collegeId.
     */
    private suspend fun getCollegeId(): String {

        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in.")

        val snapshot = firestore
            .collection(Constants.USERS)
            .document(uid)
            .get()
            .await()

        val collegeId = snapshot.getString("collegeId")

        if (collegeId.isNullOrBlank()) {
            throw IllegalStateException("College ID not found.")
        }

        return collegeId
    }
    override suspend fun getAllEvents(): List<Event> {

        val snapshot = eventsCollection()
            .whereEqualTo(Constants.IS_DELETED, false)
            .orderBy(Constants.START_DATE)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(Event::class.java)
        }
    }

    override suspend fun getEventById(
        eventId: String
    ): Event? {

        val snapshot = eventsCollection()
            .document(eventId)
            .get()
            .await()

        if (!snapshot.exists()) {
            return null
        }

        return snapshot.toObject(Event::class.java)
    }

    override suspend fun getFeaturedEvents(): List<Event> {

        val snapshot = eventsCollection()
            .whereEqualTo(Constants.IS_FEATURED, true)
            .whereEqualTo(Constants.IS_DELETED, false)
            .orderBy(Constants.START_DATE)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(Event::class.java)
        }
    }

    override suspend fun getUpcomingEvents(): List<Event> {

        val snapshot = eventsCollection()
            .whereGreaterThanOrEqualTo(
                Constants.START_DATE,
                System.currentTimeMillis()
            )
            .whereEqualTo(Constants.IS_DELETED, false)
            .orderBy(Constants.START_DATE)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(Event::class.java)
        }
    }

    private suspend fun eventsCollection(): CollectionReference {
        return firestore.collection(Constants.COLLEGES)
            .document(getCollegeId())
            .collection(Constants.EVENTS)
    }

    override suspend fun createEvent(
        event: Event
    ) {

        val eventsRef = eventsCollection()

        val document = if (event.id.isBlank()) {
            eventsRef.document()
        } else {
            eventsRef.document(event.id)
        }

        document.set(
            event.copy(
                id = document.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isDeleted = false
            )
        ).await()
    }



    override suspend fun updateEvent(
        event: Event
    ) {

        eventsCollection()
            .document(event.id)
            .set(
                event.copy(
                    updatedAt = System.currentTimeMillis()
                )
            )
            .await()
    }



    override suspend fun deleteEvent(
        eventId: String
    ) {

        eventsCollection()
            .document(eventId)
            .update(
                mapOf(
                    Constants.IS_DELETED to true,
                    Constants.UPDATED_AT to System.currentTimeMillis()
                )
            )
            .await()
    }



    override suspend fun getMyEvents(
        userId: String
    ): List<Event> {

        val snapshot = eventsCollection()
            .whereEqualTo(Constants.ORGANIZER_ID, userId)
            .whereEqualTo(Constants.IS_DELETED, false)
            .orderBy(Constants.CREATED_AT)
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            it.toObject(Event::class.java)
        }
    }

    override suspend fun uploadEventImage(
        imageUri: Uri
    ):  Result<String> {

        val path = "${StorageConstants.EVENTS}/${UUID.randomUUID()}.jpg"

        return storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        )
    }

    override suspend fun registerForEvent(
        eventId: String,
        userId: String
    ) {

        val eventCollection = eventsCollection()

        firestore.runTransaction { transaction ->

            val eventRef = eventCollection.document(eventId)

            val registrationRef = eventRef
                .collection(Constants.REGISTRATIONS)
                .document(userId)

            val eventSnapshot = transaction.get(eventRef)

            if (!eventSnapshot.exists()) {
                throw Exception("Event not found.")
            }

            val alreadyRegistered = transaction.get(registrationRef)

            if (alreadyRegistered.exists()) {
                throw Exception("User already registered.")
            }

            val registeredCount =
                eventSnapshot.getLong(Constants.REGISTERED_COUNT)?.toInt() ?: 0

            val maxParticipants =
                eventSnapshot.getLong("maxParticipants")?.toInt() ?: Int.MAX_VALUE

            if (registeredCount >= maxParticipants) {
                throw Exception("Event is full.")
            }

            transaction.set(
                registrationRef,
                mapOf(
                    Constants.USER_ID to userId,
                    Constants.REGISTERED_AT to System.currentTimeMillis()
                )
            )

            transaction.update(
                eventRef,
                Constants.REGISTERED_COUNT,
                registeredCount + 1
            )

            null
        }.await()
    }

    override suspend fun unregisterFromEvent(
        eventId: String,
        userId: String
    ) {

        val eventCollection = eventsCollection()

        firestore.runTransaction { transaction ->

            val eventRef = eventCollection.document(eventId)

            val registrationRef = eventRef
                .collection(Constants.REGISTRATIONS)
                .document(userId)

            val registrationSnapshot = transaction.get(registrationRef)

            if (!registrationSnapshot.exists()) {
                return@runTransaction null
            }

            val eventSnapshot = transaction.get(eventRef)

            val registeredCount =
                eventSnapshot.getLong(Constants.REGISTERED_COUNT)?.toInt() ?: 0

            transaction.delete(registrationRef)

            transaction.update(
                eventRef,
                Constants.REGISTERED_COUNT,
                maxOf(registeredCount - 1, 0)
            )

            null
        }.await()
    }

}