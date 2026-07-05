package com.rahul.campusconnect.presentation.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.rahul.campusconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var loginSuccess by mutableStateOf(false)
        private set

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            val result = repository.login(email, password)

            isLoading = false

            result.onSuccess {
                loginSuccess = true
            }

            result.onFailure {

                errorMessage = when (it) {

                    is FirebaseAuthInvalidUserException -> {
                        "No account found with this email."
                    }

                    is FirebaseAuthInvalidCredentialsException -> {

                        when (it.errorCode) {

                            "ERROR_WRONG_PASSWORD" ->
                                "Incorrect password."

                            "ERROR_INVALID_EMAIL" ->
                                "Invalid email address."

                            else ->
                                "Invalid login credentials."
                        }

                    }

                    is FirebaseNetworkException -> {
                        "No internet connection."
                    }

                    else -> {
                        it.localizedMessage ?: "Something went wrong."
                    }

                }

            }

        }

    }
}