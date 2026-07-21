package com.rahul.campusconnect.data.remote.firestore


import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.common.constant.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePathProvider @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun users(): CollectionReference {
        return firestore.collection(Constants.USERS)
    }

    fun colleges(): CollectionReference {
        return firestore.collection("colleges")
    }

    fun events(collegeId: String): CollectionReference {
        return colleges()
            .document(collegeId)
            .collection(Constants.EVENTS)
    }

    fun placements(collegeId: String): CollectionReference {
        return colleges()
            .document(collegeId)
            .collection(Constants.PLACEMENTS)
    }

    fun announcements(collegeId: String): CollectionReference {
        return colleges()
            .document(collegeId)
            .collection(Constants.ANNOUNCEMENTS)
    }

    fun notes(collegeId: String): CollectionReference {
        return colleges()
            .document(collegeId)
            .collection(Constants.NOTES)
    }

    fun lostFound(collegeId: String): CollectionReference {
        return colleges()
            .document(collegeId)
            .collection(Constants.LOST_FOUND)
    }
}