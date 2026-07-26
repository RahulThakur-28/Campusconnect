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

    fun colleges(): CollectionReference {
        return firestore.collection("colleges")
    }

    fun college(collegeId: String) = colleges().document(collegeId)

    fun users(collegeId: String): CollectionReference {
        return college(collegeId).collection(Constants.USERS)
    }

    fun events(collegeId: String): CollectionReference {
        return college(collegeId).collection(Constants.EVENTS)
    }

    fun placements(collegeId: String): CollectionReference {
        return college(collegeId).collection(Constants.PLACEMENTS)
    }

    fun announcements(collegeId: String): CollectionReference {
        return college(collegeId).collection(Constants.ANNOUNCEMENTS)
    }

    fun notes(collegeId: String): CollectionReference {
        return college(collegeId).collection(Constants.NOTES)
    }

    fun lostFound(collegeId: String): CollectionReference {
        return college(collegeId).collection(Constants.LOST_FOUND)
    }

    fun notifications(collegeId: String): CollectionReference {
        return college(collegeId).collection("notifications")
    }

    fun discussions(collegeId: String): CollectionReference {
        return college(collegeId).collection("discussions")
    }

    fun reports(collegeId: String): CollectionReference {
        return college(collegeId).collection("reports")
    }
}
