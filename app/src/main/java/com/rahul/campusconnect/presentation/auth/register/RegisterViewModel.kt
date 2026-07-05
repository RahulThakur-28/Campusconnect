package com.rahul.campusconnect.presentation.auth.register

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var studentId by mutableStateOf("")
    var department by mutableStateOf("")

    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var profileImage: Uri? by mutableStateOf(null)

    var isLoading by mutableStateOf(false)
        private set

    var registerSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun registerUser(){

        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            val result = repository.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                collegeId = studentId,
                branch = department,
                imageUri = profileImage
            )

            isLoading = false

            result.onSuccess {
                registerSuccess = true
            }

            result.onFailure {

                errorMessage = when (it) {

                    is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                        "An account with this email already exists."

                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                        "Password must be at least 6 characters."

                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                        "Please enter a valid email address."

                    is com.google.firebase.FirebaseNetworkException ->
                        "No internet connection."

                    else ->
                        it.localizedMessage ?: "Registration failed."
                }

            }
        }
    }
}